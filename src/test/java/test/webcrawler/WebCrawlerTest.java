package test.webcrawler;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WebCrawlerTest {

    private Set<String> TEST_SITES1;
    private Set<String> TEST_SITES2;

    @Before
    public void setuptestData() {
        TEST_SITES1 = new HashSet<>(Arrays.asList(
                "www.paddypower.com",
                "www.google.com",
                "youtube.com",
                "yahoo.com",
                "baidu.com"
        ));

        TEST_SITES2 = new HashSet<>(Arrays.asList(
                "www.football365.com",
                "www.stpatsfc.com",
                "www.bbc.co.uk"
        ));
    }

    @Test
    public void testGetInstance() {
        try(WebCrawler crawler = new WebCrawler()) {
            Assert.assertNotNull(crawler);
        }
    }

    @Test
    public void testSiteCrawlSuccess() {
        Set<WebSite> verifiedSites = new HashSet<>();

        try (WebCrawler crawler = new WebCrawler()) {
            crawler.addSitesToCrawl(TEST_SITES1);
            int ticks = 0;

            while (verifiedSites.size() < TEST_SITES1.size()
                    && ticks++ < 15) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                for (WebSite site : crawler.getValidSites()) {
                    verifiedSites.add(site);
                }
            }
        }

        Assert.assertEquals(TEST_SITES1.size(), verifiedSites.size());
    }

    @Test
    public void testSiteCrawlAddSiteInProgress() {
        Set<WebSite> verifiedSites = new HashSet<>();

        try (WebCrawler crawler = new WebCrawler()) {
            crawler.addSitesToCrawl(TEST_SITES2);
            crawler.addSiteToCrawl("www.rte.ie");
            int ticks = 0;

            while (verifiedSites.size() < TEST_SITES2.size() + 1
                    && ticks++ < 15) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                for (WebSite site : crawler.getValidSites()) {
                    verifiedSites.add(site);
                }
            }
        }

        Assert.assertEquals(TEST_SITES2.size() + 1, verifiedSites.size());
    }
}
