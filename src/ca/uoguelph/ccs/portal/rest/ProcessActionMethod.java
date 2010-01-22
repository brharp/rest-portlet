package ca.uoguelph.ccs.portal.rest;

import java.io.*;
import java.util.*;
import javax.portlet.*;
import ca.uoguelph.ccs.ws.*;

/**
 * Invokes the processAction method of a remote REST portlet.
 *
 * @author M. Brent Harp
 */
class ProcessActionMethod extends PortletRequestMethod
{
    private ActionRequest request;
    private ActionResponse response;

    ProcessActionMethod (PortletContext context,
                         ActionRequest request,
                         ActionResponse response)
    {
        super(context, "POST", false, Header.processActionRequestHeaders);
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
     * If the content type of the captured portlet request (as
     * determined by calling {@link ActionRequest#getContentType}) is
     * "application/x-www-form-urlencoded", this method calls {@link
     * #execute(RestRemotePortlet,XMLHttpRequestObject,String,String)
     * execute} with the {@link Endpoint} endpoint parameter string in
     * the HTTP request body. Otherwise, it calls execute with
     * parameters in the query string and a request body read from
     * {@link ActionRequest#getReader()}.
     *
     * <P>After executing the HTTP method, this method calls {@link
     * PortletPreferences#store()} to save any preferences set by the
     * HTTP response headers.
     *
     * @param portlet a portlet instance.
     * @param transport an HTTP transport.
     * @param endpoint a REST portlet endpoint.
     */
    void execute (RestRemotePortlet portlet,
                  XMLHttpRequestObject transport,
                  Endpoint endpoint)
        throws java.io.IOException,
               PortletException
    {
        String contentType = request.getContentType();
        String params = endpoint.getParameterString();
        if ("application/x-www-form-urlencoded".equals(contentType)) {
            // If Content-Type is form-urlencoded, post parameters in
            // request body.
            transport.setRequestHeader
                ("Content-Type", "application/x-www-form-urlencoded");
            transport.setRequestHeader
                ("Content-Length", Integer.toString(params.length()));
            execute(portlet, transport,
                    endpoint.getURL(),
                    params);
        }
        else {
            // Otherwise, send parameters in query string and post
            // buffered input in request body.
            int read = 0; 
            StringBuffer buf = new StringBuffer();
            Reader input = request.getReader();
            while ((read = input.read()) > -1) {
                buf.append(read);
            }
            transport.setRequestHeader
                ("Content-Type", request.getContentType());
            transport.setRequestHeader
                ("Content-Length",
                 Integer.toString(request.getContentLength()));
            execute(portlet, transport,
                    endpoint.getURL(params),
                    buf.toString());
        }
        
        request.getPreferences().store();
    }
}
