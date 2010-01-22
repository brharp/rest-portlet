package ca.uoguelph.ccs.portal.rest;

import java.util.*;
import javax.portlet.*;

/**
 * Invokes the destroy method of a remote REST portlet.
 *
 * @author M. Brent Harp
 */
class DestroyMethod extends PortletMethod
{
    DestroyMethod (PortletContext context)
    {
        super(context, "GET", true, Header.destroyRequestHeaders);
    }

    /**
     * Formats the value of a header by calling {@link
     * Header#format(PortletContext)}.
     *
     * @param header a header to format.
     * @return an HTTP header value.
     */
    String format (Header header) 
        throws java.io.IOException, PortletException
    {
        return header.format(getContext());
    }

    void process (Header header, String value) 
    {
    }
}
