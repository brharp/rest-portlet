package ca.uoguelph.ccs.portal.rest;

import java.util.*;
import javax.portlet.*;
import org.w3c.dom.*;
import ca.uoguelph.ccs.ws.*;

/**
 * Base class of portlet methods.
 *
 * <P>Portlet method objects encapsulate the state of a transaction
 * with a remote REST portlet.
 */
abstract class PortletMethod
    implements Constants
{
    private PortletContext context;
    private String httpMethod;
    private boolean async;
    private Header[] requestHeaders;
    private Header[] responseHeaders;

    /**
     * Constructs a new portlet method given an HTTP method,
     * synchronization flag, and sets of request and response headers.
     *
     * @param context a portlet context.
     * @param httpMethod the HTTP method used to invoke the remote
     * portlet for this method---one of "GET" or "POST".
     * @param requestHeaders a set of request headers to send.
     * @param responseHeaders a set of response headers to process.
     */
    PortletMethod (PortletContext context,
                   String httpMethod,
                   boolean async,
                   Header[] requestHeaders,
                   Header[] responseHeaders)
    {
        this.context = context;
        this.httpMethod = httpMethod;
        this.async = async;
        this.requestHeaders = requestHeaders;
        this.responseHeaders = responseHeaders;
    }
    
    /**
     * Constructs a new portlet method using the default set of
     * response headers.
     */
    PortletMethod (PortletContext context,
                   String httpMethod,
                   boolean async,
                   Header[] requestHeaders)
    {
        this(context,
             httpMethod,
             async,
             requestHeaders,
             Header.responseHeaders);
    }

    /**
     * Invokes this method in the remote portlet.
     *
     * <P>Subclasses that override this method should extract a
     * transport and endpoint (as a String) from the portlet argument
     * and call {@link
     * #execute(RestRemotePortlet,XMLHttpRequestObject,String)} for
     * further processing.
     *
     * @param portlet a REST remote portlet.
     */
    void execute (RestRemotePortlet portlet)
        throws java.io.IOException, 
               PortletException
    {
        execute(portlet, portlet.getTransport(), 
                portlet.getInitParameter(ENDPOINT));
    }
    
    /**
     * Invokes this method, given a portlet, transport, and endpoint
     * (as a String).
     *
     * <P>Subclasses that override this method should create an {@link
     * Endpoint} object and call {@link
     * #execute(RestRemotePortlet,XMLHttpRequestObject,Endpoint)} for
     * further processing.
     *
     * <P>Concrete implementations override this method to control
     * construction of the Endpoint object.
     *
     * @param portlet a REST remote portlet.
     * @param transport an HTTP transport.
     * @param endpoint a REST portlet endpoint (as a String).
     */
    void execute (RestRemotePortlet portlet,
                  XMLHttpRequestObject transport,
                  String endpoint)
        throws java.io.IOException,
               PortletException
    {
        execute(portlet, transport, new Endpoint(endpoint));
    }
    
    /**
     * Invokes this method on a REST portlet, given a portlet
     * instance, transport, and endpoint (as an {@link Endpoint}
     * object).
     *
     * <P>Subclasses that override this method should deconstruct the
     * Endpoint object and call {@link
     * #execute(RestRemotePortlet,XMLHttpRequestObject,String,String)}
     * for further processing.
     *
     * <P>Concrete implementations override this method to control
     * placement of any parameter string (in the query string versus
     * the request body, for example).
     *
     * <P>The default implementation appends any parameters to the
     * endpoint URL as a query string.
     *
     * <P>Subclasses may override this method to perform pre- or
     * post-processing of the HTTP request.
     * 
     * @param portlet a REST remote portlet.
     * @param transport an HTTP transport.
     * @param endpoint an REST portlet endpoint (as an Endpoint object).
     */
    void execute (RestRemotePortlet portlet,
                  XMLHttpRequestObject transport,
                  Endpoint endpoint)
        throws java.io.IOException,
               PortletException
    {
        execute(portlet, transport, 
                endpoint.getURL(endpoint.getParameterString()),
                null);
    }
    
    /**
     * Invokes this method on a remote REST portlet, given a
     * transport, endpoint URL, and request body.
     *
     * <P>This method performs the actual work of making the HTTP
     * request and processing the response, formatting all request
     * headers, and processing all response headers.
     *
     * @param portlet a REST remote portlet.
     * @param transport an HTTP transport.
     * @param url an endpoint URL.
     * @param content the HTTP request body.
     */
    final void execute (RestRemotePortlet portlet,
                        XMLHttpRequestObject transport,
                        String url,
                        String content)
        throws java.io.IOException, 
               PortletException
    {
        context.log(getHttpMethod() + " " + url);
        transport.open(getHttpMethod(), url, isAsync());
        // Format request headers.
        Iterator i = requestHeaders();
        while (i.hasNext()) {
            Header h = (Header)i.next();
            String v = format(h);
            if (v != null && v.length() > 0) {
                context.log(h.getName()+": "+v);
                transport.setRequestHeader(h.getName(), v);
            }
        }
        // Send request.
        context.log("Body: "+content);
        transport.send(content);
        context.log(transport.getStatus() + " " + transport.getStatusText());
        // Process response headers.
        Iterator j = responseHeaders();
        while (j.hasNext()) {
            Header h = (Header)j.next();
            String v = transport.getResponseHeader(h.getName());
            if (v != null && v.length() > 0) {
                context.log(h.getName()+": "+v);
                process(h, v);
            }
        }
    }

    /**
     * Returns the portlet context.
     */
    protected PortletContext getContext()
    {
        return context;
    }

    /**
     * Returns the HTTP method.
     */
    protected String getHttpMethod()
    {
        return httpMethod;
    }

    /**
     * Returns <code>true</code> if the executing thread should block
     * and wait for a response from the remote portlet.
     */
    protected boolean isAsync ()
    {
        return async;
    }

    /**
     * Returns the set of request headers.
     */
    protected Iterator requestHeaders ()
    {
        return Arrays.asList(requestHeaders).iterator();
    }

    /**
     * Returns the set of response headers.
     */
    protected Iterator responseHeaders ()
    {
        return Arrays.asList(responseHeaders).iterator();
    }

    /**
     * Formats the value of an HTTP request header.
     */
    abstract String format (Header header) 
        throws java.io.IOException, PortletException;

    /**
     * Processes the value of an HTTP response header.
     */
    abstract void process (Header header, String value) 
        throws java.io.IOException, PortletException;
}
