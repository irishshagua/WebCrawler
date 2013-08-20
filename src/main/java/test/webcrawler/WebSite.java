package test.webcrawler;

import java.net.URL;

class WebSite {

    private URL _url;
    private String _pageContent;

    public static final WebSite INVALID_SITE = new WebSite(null, "");

    public WebSite(URL url, String pageContent) {
        _url = url;
        _pageContent = pageContent;
    }

    public URL getUrl() {
        return _url;
    }

    public String getContent() {
        return _pageContent;
    }
}
