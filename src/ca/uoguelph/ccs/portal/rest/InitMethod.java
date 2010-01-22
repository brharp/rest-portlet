package ca.uoguelph.ccs.portal.rest;

import java.util.*;
import javax.portlet.*;
import org.w3c.dom.*;

/**
 * Invokes the init method of a remote REST portlet.
 *
 * @author M. Brent Harp
 */
class InitMethod extends PortletMethod
{
    private PortletConfig config;

    InitMethod (PortletContext context, PortletConfig config)
    {
        super(context, "GET", false, Header.initRequestHeaders);
        this.config = config;
    }

    /**
     * Formats the given request header by calling {@link
     * Header#format(PortletContext,PortletConfig)}.
     *
     * @param header a portlet header.
     * @return an HTTP header value.
     */
    String format (Header header) 
        throws java.io.IOException, PortletException
    {
        return header.format(getContext(), config);
    }

    void process (Header header, String value) 
    {
    }
}
