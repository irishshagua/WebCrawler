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

    @Override
    public String toString() {
        String toString;
        if (_url == null)
            toString = "INVALID_SITE";
        else
            toString = "[" + _url + "] : "
                    + (_pageContent.length() < 25
                        ? _pageContent
                        : _pageContent.substring(0, 25));

        return toString;
    }
}
