package test.webcrawler;

/**
 *
 */
public interface SiteValidityEvaluator {

    /**
     * Check weather the page content
     * @param siteContent
     * @return
     */
    public boolean isSiteValid(WebSite siteContent);
}
