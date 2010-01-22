package ca.uoguelph.ccs.portal.rest;

import java.net.*;
import java.util.*;
import javax.portlet.*;
import ca.uoguelph.ccs.ws.XMLHttpRequestObject;

/**
 * Base class of methods that capture a portlet request and portlet
 * response.
 *
 * <P>This class provides an implementation of {@link
 * #execute(RestRemotePortlet,XMLHttpRequestObject,String) execute}
 * that allows the endpoint init parameter to be overridden by a
 * preference value of the same name. If no such preference is set,
 * the endpoint falls back on the init parameter value.
 *
 * <P>This class also provides default implementations of process and
 * format that take portlet request and response objects as arguments.
 *
 * <P>Finally, this class provides a method for collecting portlet
 * request parameters as a URL encoded parameter string.
 *
 * @author M. Brent Harp
 */
abstract class PortletRequestMethod extends PortletMethod
    implements Constants
{
    protected abstract PortletRequest getRequest();
    protected abstract PortletResponse getResponse();

    PortletRequestMethod (PortletContext context,
                          String httpMethod,
                          boolean async,
                          Header[] requestHeaders,
                          Header[] responseHeaders)
    {
        super(context, httpMethod, async, requestHeaders, responseHeaders);
    }
    
    PortletRequestMethod (PortletContext context,
                          String httpMethod,
                          boolean async,
                          Header[] requestHeaders)
    {
        super(context, httpMethod, async, requestHeaders);
    }

    /**
     * Constructs an {@link Endpoint} object and calls {@link
     * #execute(RestRemotePortlet,XMLHttpRequestObject,Endpoint)} to
     * continue processing.
     *
     * <P>The endpoint URL will be taken from the portlet preferences,
     * if available. Otherwise the given endpoint will be used. The
     * Endpoint will be constructed with parameters gathered from the
     * portlet request parameters.
     *
     * @param portlet a portlet instance.
     * @param transport an HTTP transport.
     * @param endpoint a REST portlet endpoint.
     */
    void execute (RestRemotePortlet portlet,
                  XMLHttpRequestObject transport,
                  String endpoint)
        throws java.io.IOException,
               PortletException
    {
        execute(portlet, transport,
                new Endpoint(getPreference(ENDPOINT, endpoint),
                             getParameterString()));
    }
    
    /**
     * Collects the portlet request parameters as an URL encoded
     * parameter string.
     *
     * @return an URL encoded parameter string.
     */
    protected String getParameterString()
    {
        // Collect parameters.
        StringBuffer sbuf = new StringBuffer();
        try {
            Enumeration i = getRequest().getParameterNames();
            while (i.hasMoreElements()) {
                String name = (String)i.nextElement();
                String[] values = getRequest().getParameterValues(name);
                int j = 0;
                while (j < values.length) {
                    sbuf.append(URLEncoder.encode(name, "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(values[j++], "UTF-8"));
                }
		if (i.hasMoreElements()) {
		    sbuf.append("&");
		}
            }
        }
        catch (java.io.UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
        return sbuf.toString();
    }

    /**
     * Formats an HTTP request header by calling {@link
     * Header#format(PortletContext,PortletRequest,PortletResponse)}.
     *
     * @param header a portlet header.
     * @return an HTTP header value.
     */
    protected String format (Header header) 
        throws java.io.IOException, PortletException
    {
        return header.format(getContext(), getRequest(), getResponse());
    }
    
    /**
     * Processes an HTTP response header value by calling {@link
     * Header#process(PortletContext,PortletRequest,PortletResponse,String)}.
     *
     * @param header a portlet header.
     * @param value an HTTP response header value.
     */
    protected void process (Header header, String value) 
        throws java.io.IOException, PortletException
    {
        header.process(getContext(), getRequest(), getResponse(), value);
    }

    /**
     * Returns a portlet preference value from the captured portlet
     * request.
     *
     * @param name the preference to retrieve.
     * @param def the default value to return if name is not
     * defined. May be null.
     */
    private String getPreference (String name, String def)
    {
        return getRequest().getPreferences().getValue(name, def);
    }
}
