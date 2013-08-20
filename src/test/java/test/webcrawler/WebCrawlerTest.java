package test.webcrawler;


import org.junit.Assert;
import org.junit.Test;

public class WebCrawlerTest {

    @Test
    public void test() {
        WebCrawler crawler = WebCrawler.getInstance();
        Assert.assertNotNull(crawler);
    }

    @Test
    public void failTest() {
        Assert.assertEquals(1, 2);
    }
}
