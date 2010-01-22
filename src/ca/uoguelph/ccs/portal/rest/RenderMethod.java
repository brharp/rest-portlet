package ca.uoguelph.ccs.portal.rest;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.portlet.*;
import ca.uoguelph.ccs.ws.*;

/**
 * A closure object that invokes the render method of a remote portlet
 * with a captured render request and render response.
 */
class RenderMethod extends PortletRequestMethod
{
    private RenderRequest request;
    private RenderResponse response;

    /**
     * Constructs a new, synchronous portlet method with the given
     * context, and the HTTP method "GET", that formats all render
     * request headers.
     *
     * @param context a portlet context.
     * @param request a portlet render request.
     * @param response a portlet render response.
     */
    RenderMethod (PortletContext context,
                  RenderRequest request,
                  RenderResponse response)
    {
        super(context, "GET", false, Header.renderRequestHeaders);
        this.request = request;
        this.response = response;
    }
    
    protected PortletRequest getRequest()
    {
        return request;
    }

    protected PortletResponse getResponse()
    {
        return response;
    }

    /**
     * Invokes the render method of a remote REST portlet given a
     * portlet instance, an HTTP transport, and an endpoint.
     *
     * <P>This method calls
     * PortletMethod#execute(RestRemotePortlet,XMLHttpRequestObject,Endpoint)
     * and filters the response body through an appropriate {@link
     * ContentFilter} to the captured render response's writer.
     *
     * <P>The content filter (acquired from {@link
     * RestRemotePortlet#getContentFilter}) is configured with a
     * {@link PortletURIResolver} that resolves relative URIs against
     * the endpoint URL, and delegates to the captured render response
     * object to create portlet URLs.
     *
     * @param portlet a REST remote portlet.
     * @param transport an HTTP transport.
     * @param endpoint a REST portlet endpoint.
     */
    void execute (RestRemotePortlet portlet,
                  XMLHttpRequestObject transport,
                  Endpoint endpoint)
        throws IOException, 
               PortletException
    {
        super.execute(portlet, transport, endpoint);

        switch (transport.getStatus()) {
        case 200:
            sendResponse(portlet, transport, endpoint);
            break;
        default:
            sendError(portlet, transport, endpoint);
            break;
        }
    }
    
    private void sendResponse (RestRemotePortlet portlet,
                               XMLHttpRequestObject transport,
                               Endpoint endpoint)
        throws IOException, 
               PortletException
    {
        send(portlet, endpoint,
             request.getResponseContentType(),
             transport.getResponseText());
    }

    private void sendError (RestRemotePortlet portlet, 
                            XMLHttpRequestObject transport, 
                            Endpoint endpoint)
        throws IOException, 
               PortletException
    {
        String path = "resources/"+transport.getStatus()+".html";
        InputStream in = getClass().getResourceAsStream(path);

        if (null != in)
            send(portlet, endpoint, "text/html",
                 new InputStreamReader(in));
        else
            send(portlet, endpoint, "text/html",
                 transport.getStatus() + " " + 
                 transport.getStatusText());
    }
    
    private void send (RestRemotePortlet portlet,
                       Endpoint endpoint,
                       String type,
                       String content)
        throws IOException, 
               PortletException
    {
        send(portlet, endpoint, type, new StringReader(content));
    }

    private void send (RestRemotePortlet portlet,
                       final Endpoint endpoint,
                       String type,
                       Reader reader)
        throws IOException, 
               PortletException
    {
        response.setContentType(type);

        ContentFilter filter = portlet.getContentFilter(type);
        
        filter.setURIResolver(new PortletURIResolver()
            {
                final String baseURI = endpoint.getURL();
                
                public String getBaseURI() {
                    return baseURI;
                }
                
                public PortletURL createRenderURL() {
                    return response.createRenderURL();
                }
                
                public PortletURL createActionURL() {
                    return response.createActionURL();
                }
            });
        
        try {
            filter.filter(reader, response.getWriter());
        }
        catch (Exception e) {
            Writer writer = response.getWriter();
            copy(reader, writer);
            writer.write("<!--WARNING: " + e + "-->");
        }
    }

    private static void copy (Reader r, Writer w) 
        throws IOException
    {
        char[] buf = new char[1024];
        int    i;
        
        while ((i = r.read(buf)) > -1) {
            w.write(buf, 0, i);
        }
    }
}
