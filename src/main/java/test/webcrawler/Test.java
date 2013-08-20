package test.webcrawler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Test {

    public static void main(String... args) {
        Set<String> sites = new HashSet<>(Arrays.asList(
                "www.paddypower.com",
                "Facebook.com",
                "Google.com",
                "youtube.com",
                "yahoo.com",
                "baidu.com"
        ));

        try (WebCrawler crawler = WebCrawler.getInstance()) {
            crawler.addSitesToCrawl(sites);
            int numVerifiedSites = 0;

            while (numVerifiedSites < sites.size()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                for (WebSite site : crawler.getValidSites()) {
                    System.out.println(site.getUrl().toString()
                            + " is a valid site");
                    numVerifiedSites++;
                }
            }
        }
    }
}
