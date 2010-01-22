package ca.uoguelph.ccs.portal.rest;

/**
 * Parses endpoint URLs.
 *
 * <P>
 *
 * This class provides easy access to an endpoint URL, with or without
 * a query string appended.
 *
 * <P>
 *
 * If the endpoint URL contains a query string, these parameters will
 * become default parameters. When retrieving an endpoint URL with a
 * query string, default parameters will be merged with the supplied
 * parameter string.
 *
 * @author M. Brent Harp
 */
public class Endpoint
{
    /**
     * The bare endpoint URL, without a query string.
     */
    private String url;

    /**
     * The parameter string.
     */
    private String par;
    
    /**
     * Constructs a new Endpoint given a URL. The URL may contain a
     * query string, in which case parameters in the query string will
     * be returned by #getParameterString.
     *
     * @param url an endpoint URL.
     * @throws NullPointerException if the URL parameter is null.
     */
    public Endpoint (String url)
    {
        this(url, new String());
    }
    
    /**
     * Constructs a new Endpoint given a URL and parameter string. The
     * URL may contain a query string, in which case parameters from
     * the query string will be merged with the supplied parameter
     * string. The parameter string argument must be properly URL
     * encoded.
     *
     * @param url an endpoint URL.
     * @param par a parameter string.
     * @throws NullPointerException if the URL parameter is null.
     * @throws NullPointerException if the parameter string is null.
     */
    public Endpoint (String url, String par)
    {
        if (url == null)
            throw new NullPointerException();

        if (par == null)
            throw new NullPointerException();

        int q = url.indexOf('?');

        this.url = q < 0 ? url : url.substring(0, q);
        this.par = q < 0 ? par : par + "&" + url.substring(q+1);
    }
    
    /**
     * Returns the endpoint URL, sans parameters.
     *
     * @return an URL sans query string.
     */
    public String getURL()
    {
        return url;
    }

    /**
     * Returns the endpoint URL, with the supplied parameter string as
     * a query string. The parameter string need not begin with '?',
     * this method will insert one between the URL and query string.
     *
     * <P>
     * 
     * If the parameter string is an empty string, this method returns
     * the same value as #getURL().
     *
     * @param par a parameter string. May be empty.
     * @return an URL, possibly with a query string.
     * @throws NullPointerException if the parameter string is null.
     */
    public String getURL(String par)
    {
        if (par == null)
            throw new NullPointerException();

        return par.length() > 0 ? url + "?" + par : url;
    }
    
    /**
     * Returns the parameter string. This will include any parameters
     * from the endpoint URL query string, as well as any parameters
     * passed to the constructor.
     *
     * @return a parameter string, URL encoded.
     */
    public String getParameterString()
    {
        return par;
    }
}
