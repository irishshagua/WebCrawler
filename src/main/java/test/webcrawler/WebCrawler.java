package test.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 */
public final class WebCrawler implements AutoCloseable {

    private ExecutorService _executor;
    private ScheduledExecutorService _scheduler;
    private Set<URL> _sites;
    private Set<CompletableFuture<Optional<WebSite>>> _relaventSites;

    private final long SLEEP_BETWEEN_CRAWL_SITE_POLLS   = 2L;
    private final int  FIXED_THREAD_POOL_SIZE           = 4;

    /*
     * Default Validator
     */
    private SiteValidityEvaluator _siteValidator =
            (siteContent) -> siteContent.matches(".*\\<[^>]+>.*");


    public WebCrawler() {
        _scheduler = Executors.newSingleThreadScheduledExecutor();
        _executor = Executors.newFixedThreadPool(FIXED_THREAD_POOL_SIZE);
        _sites = new HashSet<>();
        _relaventSites = Collections.newSetFromMap(new ConcurrentHashMap<>()); // Is there a better way to do this?

        // Schedule crawl to run at intervals to
        // allow addition of new sites over time
        _scheduler.scheduleAtFixedRate(
                this::crawlSites,
                0L,
                SLEEP_BETWEEN_CRAWL_SITE_POLLS,
                TimeUnit.MILLISECONDS);
    }
 

    /*
     *******************************************
     * PUBLIC METHODS
     *******************************************
     */

    /**
     * @param siteValidator
     */
    public void setSiteValidator(SiteValidityEvaluator siteValidator) {
        _siteValidator = siteValidator;
    }

    /**
     * No guaranteed order
     *
     * @param sites
     */
    public void addSitesToCrawl(Set<String> sites) {
        for (String site : sites) {
            addSiteToCrawl(site);
        }
    }

    /**
     * Invalid URLs are ignored
     *
     * @param site
     */
    public void addSiteToCrawl(String site) {
        try {
            _sites.add(new URL(validateSiteUrl(site)));
        } catch (IOException e) {} // We dont add non valid URL's
    }

    /**
     * @return
     */
    public List<WebSite> getValidSites() {
        List<WebSite> validSites =
                _relaventSites.stream()
                        .filter(future -> future.isDone())
                        .map(future -> future.join())
                        .filter(optional -> optional.isPresent())
                        .map(optional -> optional.get())
                        .collect(Collectors.toList());

        _relaventSites.removeIf(site ->
                site.isDone() && (!site.join().isPresent()
                        || validSites.contains(site.join().get())
                )
        );

        return validSites;
    }

    /**
     *
     */
    public void close() {
        _executor.shutdown();
        _scheduler.shutdown();
    }

    /*
      **************************************
      * PRIVATE METHODS
      **************************************
      */
    private void crawlSites() {
        _relaventSites.addAll(_sites.stream()
                .map(site -> CompletableFuture.supplyAsync(() -> scrapeSite(site), _executor))
                .map(contentFuture -> contentFuture.thenCompose(this::calculateRelevance))
                .collect(Collectors.<CompletableFuture<Optional<WebSite>>>toSet()));

        _sites.clear();
    }

    private String validateSiteUrl(String url) {
        if (!url.toLowerCase().startsWith("http")) {
            url = "http://" + url; // Assuming standard http
        }

        return url;
    }

    private WebSite scrapeSite(URL site) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(site.openStream()))) {
            StringBuilder sb = new StringBuilder();
            in.lines().forEachOrdered(sb::append);

            return new WebSite(site, sb.toString());
        } catch (IOException e) {
            return WebSite.INVALID_SITE; // Cannot open stream
        }
    }

    private CompletableFuture<Optional<WebSite>> calculateRelevance(WebSite site) {
        return CompletableFuture.supplyAsync(()
                -> _siteValidator.isSiteValid(site.getContent())
                ? Optional.of(site)
                : Optional.<WebSite>empty());
    }
}
