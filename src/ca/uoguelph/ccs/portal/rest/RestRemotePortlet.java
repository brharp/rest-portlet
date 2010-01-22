package ca.uoguelph.ccs.portal.rest;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.portlet.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import ca.uoguelph.ccs.ws.*;
import org.apache.commons.logging.*;

/**
 * Portlet class implementing the REST Remote Portlet API.
 *
 * @see <a href="http://www.uoguelph.ca/~brharp/portal/rest/spec.html">The REST Portlet Specification</a>
 * @author M. Brent Harp
 */
public class RestRemotePortlet extends GenericPortlet
{
    /**
     * Default constructor.
     */
    public RestRemotePortlet()
    {
        super();
    }
    
    /**
     * Invokes the init method of the remote portlet. This method is
     * optional.
     *
     * @param config the portlet configuration
     * @see InitMethod
     */
    public void init(PortletConfig config)
        throws PortletException
    {
        initInternal(config);
        
        try {
            new InitMethod(getPortletContext(), config)
                .execute(this);
        }
        catch (Exception e) {
            log(e.getMessage());
        }
    }

    /**
     * Invokes the render method of the remote portlet.
     *
     * @param request the render request object
     * @param response the render response object
     * @see RenderMethod
     */
    public void render(RenderRequest request, 
                       RenderResponse response)
        throws PortletException, 
               IOException
    {
        renderInternal(request, response);

        new RenderMethod(getPortletContext(), request, response)
            .execute(this);
    }
    
    /** 
     * Invokes the processAction method of the remote portlet.
     *
     * @param request the action request object.
     * @param response the action response object.
     * @see ProcessActionMethod
     */
    public void processAction(ActionRequest request,
                              ActionResponse response)
        throws PortletException, 
               IOException
    {
        processActionInternal(request, response);

        new ProcessActionMethod(getPortletContext(), request, response)
            .execute(this);
    }
    
    /**
     * Invokes the destroy method of the remote portlet. Implementing
     * this method in the remote portlet is optional.
     *
     * @see DestroyMethod
     */
    public void destroy()
    {
        destroyInternal();
        
        try {
            new DestroyMethod(getPortletContext())
                .execute(this);
        }
        catch (Exception e) {
            log(e.getMessage());
        }
    }

    /**
     * Performs internal handling of the init method.
     *
     * This method is called before the init method of the remote
     * portlet is invoked.
     *
     * Subclasses that override this method must be sure to call
     * super.initInternal(config).
     *
     * @param config the portlet configuration object
     */
    protected void initInternal (PortletConfig config)
        throws PortletException
    {
        super.init(config);
    }

    /**
     * Performs internal handling of the render method.
     *
     * This method is called before the render method of the remote
     * portlet is invoked.
     *
     * Subclasses that override this method must be sure to call
     * super.renderInternal(request, response).
     *
     * @param request the render request object.
     * @param response the render response object.
     */
    protected void renderInternal (RenderRequest request,
                                   RenderResponse response)
        throws PortletException,
               IOException
    {
    }

    /**
     * Performs internal handling of the processAction method.
     *
     * This method is called before the processAction method of the
     * remote portlet is invoked.
     *
     * Subclasses that override this method must be sure to call
     * super.processActionInternal(request, response).
     *
     * @param request the action request object.
     * @param response the action response object.
     */
    protected void processActionInternal (ActionRequest request,
                                          ActionResponse response)
        throws PortletException,
               IOException
    {
    }

    /**
     * Performs internal handling of the destroy method.
     *
     * This method is called before the destroy method of the remote
     * portlet is invoked.
     *
     * Subclasses that override this method must be sure to call
     * super.destroyInternal().
     */
    protected void destroyInternal ()
    {
    }

    /**
     * Logs a message to the portlet log.
     *
     * @param msg the message to log.
     */
    protected void log (String msg)
    {
        getPortletContext().log(msg);
    }

    /**
     * Returns a transport for communicating with the remote portlet.
     *
     * @return a transport object.
     */
    protected XMLHttpRequestObject getTransport()
    {
        return new XMLHttpRequestObject();
    }

    /**
     * Returns a content filter for the given content type.
     *
     * @param type a mime content type.
     * @return a content filter.
     */
    public ContentFilter getContentFilter (String type)
    {
        return new HTMLContentFilter();
    }
}
