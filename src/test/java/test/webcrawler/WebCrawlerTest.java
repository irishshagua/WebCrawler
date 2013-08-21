package test.webcrawler;


import org.junit.Assert;
import org.junit.Test;

public class WebCrawlerTest {

    @Test
    public void test() {
        WebCrawler crawler = WebCrawler.getInstance();
        Assert.assertNotNull(crawler);
    }
}
