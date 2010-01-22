package ca.uoguelph.ccs.portal.rest;

import java.io.*;
import java.util.*;
import javax.portlet.*;

/**
 * Type safe enumeration of portlet headers.
 *
 * <P>Each portlet header is represented by an instance of this
 * class. Every header object has methods to format a request header
 * given a portlet request-response object pair, a portlet config
 * object, or the portlet context, and a method to process response
 * headers given a request-response object pair. Default
 * implementations of these methods throw
 * UnsupportedOperationException.
 *
 * <P>Subclasses are specialized to write portlet state as HTTP
 * request headers, and modify portlet state in response to HTTP
 * response headers.
 *
 * @author M. Brent Harp
 */
public abstract class Header
{
    /**
     * The header name (ie. "X-Portlet-Method").
     */
    private String name;

    /**
     * Constructor. Protected to keep the set of portlet headers
     * closed.
     *
     * @param name the header name.
     */
    protected Header (String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of this header.
     *
     * @return the header name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Formats the value of this header as a request header, given
     * a portlet context, and portlet request and response objects.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context the portlet context.
     * @param request the portlet request.
     * @param response the portlet response.
     * @return an HTTP header value.
     */
    public String format (PortletContext context,
                          PortletRequest request,
                          PortletResponse response)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Formats the value of this header as a request header, given a
     * portlet context and portlet configuration.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context the portlet context.
     * @param config the portlet configuration.
     * @return an HTTP header value.
     */
    public String format (PortletContext context,
                          PortletConfig config)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Formats the value of this header as a request header, given
     * only a portlet context.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context the portlet context.
     * @return an HTTP header value.
     */
    public String format (PortletContext context)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Processes a response header given a portlet context, portlet
     * request, portlet response, and header value.
     *
     * <P>Subclasses that override this method are expected to modify
     * the portlet objects according to the header value.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     * 
     * @param context a portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @param value the response header value.
     */
    public void process (PortletContext context,
                         PortletRequest request,
                         PortletResponse response,
                         String value)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }


    /* REST Portlet Headers */

    static Header ContentType = 
        new Header("Content-Type")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
            {
                if (response instanceof RenderResponse) {
                    ((RenderResponse)response).setContentType(value);
                }
            }
            
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return ((ActionRequest)request).getContentType();
            }
        };

    static Header XPortletCharacterEncoding = 
        new Header("X-Portlet-Character-Encoding")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
            {
                // should do something here.
            }
            
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return ((ActionRequest)request).getCharacterEncoding();
            }
        };

    static Header ContentLength = 
        new Header("Content-Length")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return Integer.toString
                    (((ActionRequest)request).getContentLength());
            }

            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
            {
                // ignore
            }
        };

    static Header XPortletInitParameter = 
        new CookieHeader("X-Portlet-Init-Parameter")
        {
            public Enumeration getNames (PortletContext context,
                                         PortletConfig config)
            {
                return config.getInitParameterNames();
            }

            public String getValue (PortletContext context,
                                    PortletConfig config,
                                    String name)
            {
                return config.getInitParameter(name);
            }
        };

    static Header XPortletName = 
        new Header("X-Portlet-Name")
        {
            public String format (PortletContext context,
                                  PortletConfig config)
            {
                return config.getPortletName();
            }
        };

    
    static Header XPortletAttribute =
        new CookieHeader("X-Portlet-Attribute")
        {
            public Enumeration getNames (PortletContext context,
                                         PortletConfig config)
            {
                return config.getPortletContext().getAttributeNames();
            }

            public String getValue (PortletContext context,
                                    PortletConfig config,
                                    String name)
            {
                return config.getPortletContext()
                    .getAttribute(name).toString();
            }
        };

    static Header XPortletMajorVersion =
        new Header("X-Portlet-Major-Version")
        {
            public String format (PortletContext context,
                                  PortletConfig config)
            {
                return Integer.toString
                    (config.getPortletContext().getMajorVersion());
            }
        };

    static Header XPortletMinorVersion =
        new Header("X-Portlet-Minor-Version")
        {
            public String format (PortletContext context,
                                  PortletConfig config)
            {
                return Integer.toString
                    (config.getPortletContext().getMinorVersion());
            }
        };

    static Header XPortletMethod =
        new Header("X-Portlet-Method")
        {
            public String format (PortletContext context,
                                  PortletConfig config)
            {
                return "init";
            }

            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                if (request instanceof RenderRequest)
                    return "render";
                else
                    return "processAction";
            }

            public String format ()
            {
                return "destroy";
            }
        };

    static Header XPortletAuthType =
        new Header("X-Portlet-Auth-Type")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getAuthType();
            }
        };

    static Header XPortletContextPath =
        new Header("X-Portlet-Context-Path")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getContextPath();
            }
        };

    static Header XPortletLocale =
        new Header("X-Portlet-Locale")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getLocale().toString();
            }
        };

    static Header XPortletParameter =
        new CookieHeader("X-Portlet-Parameter")
        {
            public Enumeration getNames (PortletContext context,
                                         PortletRequest request,
                                         PortletResponse response)
            {
                return request.getParameterNames();
            }

            public String getValue (PortletContext context,
                                    PortletRequest request,
                                    PortletResponse response,
                                    String name)
            {
                return request.getParameter(name);
            }
        };

    static Header XPortletSetPortletMode = 
        new Header("X-Portlet-Set-Portlet-Mode")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
                throws PortletException, IOException
            {
                ((ActionResponse)response)
                    .setPortletMode(new PortletMode(value));
            }
        };

    static Header XPortletSetPreference =
        new CookieHeader("X-Portlet-Set-Preference")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String name, String value)
                throws PortletException, IOException
            {
                if (value != null && value.length() > 0) {
                    request.getPreferences().setValue(name, value);
                } else {
                    request.getPreferences().reset(name);
                }
            }
        };
                
    static Header XPortletPreference =
        new CookieHeader("X-Portlet-Preference")
        {
            public Enumeration getNames (PortletContext context,
                                         PortletRequest request,
                                         PortletResponse response)
            {
                return request.getPreferences().getNames();
            }

            public String getValue (PortletContext context,
                                    PortletRequest request,
                                    PortletResponse response,
                                    String name)
            {
                return request.getPreferences().getValue(name, null);
            }
        };

    static Header XPortletSetTitle =
        new Header("X-Portlet-Set-Title")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
            {
                ((RenderResponse)response).setTitle(value);
            }
        };

    static Header XPortletSetWindowState = 
        new Header("X-Portlet-Set-Window-State")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
                throws PortletException, IOException
            {
                ((ActionResponse)response)
                    .setWindowState(new WindowState(value));
            }
        };

    static Header XPortletProperty =
        new CookieHeader("X-Portlet-Property")
        {
            public Enumeration getNames (PortletContext context,
                                         PortletRequest request,
                                         PortletResponse response)
            {
                return request.getPropertyNames();
            }
            
            public String getValue (PortletContext context,
                                    PortletRequest request,
                                    PortletResponse response,
                                    String name)
            {
                return request.getProperty(name);
            }
        };

    static Header XPortletSetProperty =
        new CookieHeader("X-Portlet-Set-Property")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String name, String value)
                throws PortletException, IOException
            {
                response.setProperty(name, value);
            }
        };
                
    static Header XPortletAddProperty =
        new CookieHeader("X-Portlet-Add-Property")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String name, String value)
                throws PortletException, IOException
            {
                response.addProperty(name, value);
            }
        };
                
    static Header XPortletRemoteUser =
        new Header("X-Portlet-Remote-User")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getRemoteUser();
            }
        };

    static Header XPortletSecure =
        new Header("X-Portlet-Secure")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return Boolean.toString(request.isSecure());
            }
        };

    static Header XPortletSessionId =
        new Header("X-Portlet-Session-Id")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getPortletSession().getId();
            }
        };

    static Header XPortletServerName =
        new Header("X-Portlet-Server-Name")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getServerName();
            }
        };

    static Header XPortletServerPort =
        new Header("X-Portlet-Server-Port")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return Integer.toString(request.getServerPort());
            }
        };

    static Header XPortletScheme =
        new Header("X-Portlet-Scheme")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getScheme();
            }
        };

    static Header XPortletWindowState =
        new Header("X-Portlet-Window-State")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getWindowState().toString();
            }
        };

    static Header XPortletPortletMode =
        new Header("X-Portlet-Portlet-Mode")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
            {
                return request.getPortletMode().toString();
            }
        };

    static Header XPortletUserInfo =
        new CookieHeader("X-Portlet-User-Info")
        {
            public Enumeration getNames (PortletContext context,
                                         PortletRequest request,
                                         PortletResponse response)
            {
                return Collections.enumeration
                    (((Map)request.getAttribute(request.USER_INFO))
                     .keySet());
            }

            public String getValue (PortletContext context,
                                    PortletRequest request,
                                    PortletResponse response,
                                    String name)
            {
                return (String)(((Map)request.getAttribute(request.USER_INFO))
                                .get(name));
            }
        };
                
    static Header XPortletSendRedirect = 
        new Header("X-Portlet-Send-Redirect")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
                throws PortletException, IOException
            {
                ((ActionResponse)response).sendRedirect(value);
            }
        };

    static Header XPortletNamespace =
        new Header("X-Portlet-Namespace")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
                throws PortletException, IOException
            {
                return ((RenderResponse)response).getNamespace();
            }
        };

    static Header XPortletResponseContentType =
        new Header("X-Portlet-Response-Content-Type")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
                throws PortletException, IOException
            {
                return request.getResponseContentType();
            }
        };

    static Header XPortletResponseContentTypes =
        new ListHeader("X-Portlet-Response-Content-Types")
        {
            public Enumeration getValues (PortletContext context,
                                          PortletRequest request,
                                          PortletResponse response)
                throws PortletException, IOException
            {
                return request.getResponseContentTypes();
            }
        };    

    static Header XPortletSetRenderParameter =
        new CookieHeader("X-Portlet-Set-Render-Parameter")
        {
            public void process (PortletContext context,
                                 PortletRequest request, 
                                 PortletResponse response,
                                 String name, String value)
                throws PortletException, IOException
            {
                if (response instanceof ActionResponse) {
                    ((ActionResponse)response).setRenderParameter(name, value);
                } else {
                    super.process(context, request, response, name, value);
                }
            }
        };

    static Header XPortletLog =
        new Header("X-Portlet-Log")
        {
            public void process (PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response,
                                 String value)
                throws PortletException, IOException
            {
                context.log(value);
            }
        };

    static Header Cookie =
        new Header("Cookie")
        {
            public String format (PortletContext context,
                                  PortletRequest request,
                                  PortletResponse response)
                throws PortletException, IOException
            {
                return request.getProperty("cookie");
            }
        };

    /* Header Enumeration */

    protected static Header[] responseHeaders =
        new Header[] {
            ContentLength,
            ContentType,
            Cookie,
            XPortletAddProperty,
            XPortletAttribute,
            XPortletAuthType,
            XPortletContextPath,
            XPortletInitParameter,
            XPortletLocale,
            XPortletLog,
            XPortletMajorVersion,
            XPortletMethod,
            XPortletMinorVersion,
            XPortletName,
            XPortletNamespace,
            XPortletParameter,
            XPortletPortletMode,
            XPortletPreference,
            XPortletProperty,
            XPortletRemoteUser,
            XPortletResponseContentType,
            XPortletResponseContentTypes,
            XPortletScheme,
            XPortletSecure,
            XPortletSendRedirect,
            XPortletServerName,
            XPortletServerPort,
            XPortletSessionId,
            XPortletSetPortletMode,
            XPortletSetPreference,
            XPortletSetProperty,
            XPortletSetRenderParameter,
            XPortletSetTitle,
            XPortletSetWindowState,
            XPortletUserInfo,
            XPortletWindowState
        };            

    public static Header[] initRequestHeaders = 
        new Header[] {
            XPortletMethod,
            XPortletInitParameter,
            XPortletName,
            XPortletAttribute,
            XPortletMajorVersion,
            XPortletMinorVersion
        };

    public static Header[] renderRequestHeaders = 
        new Header[] {
            Cookie,
            XPortletMethod,
            XPortletAuthType,
            XPortletContextPath,
            XPortletLocale,
            XPortletNamespace,
            XPortletPortletMode,
            XPortletParameter,
            XPortletPreference,
            XPortletProperty,
            XPortletRemoteUser,
            XPortletResponseContentType,
            XPortletResponseContentTypes,
            XPortletSecure,
            XPortletSessionId,
            XPortletServerName,
            XPortletServerPort,
            XPortletScheme,
            XPortletUserInfo,
            XPortletWindowState
        };

    public static Header[] processActionRequestHeaders = 
        new Header[] {
            ContentLength,
            ContentType,
            Cookie,
            XPortletMethod,
            XPortletAuthType,
            XPortletContextPath,
            XPortletLocale,
            XPortletPortletMode,
            XPortletParameter,
            XPortletPreference,
            XPortletProperty,
            XPortletRemoteUser,
            XPortletResponseContentType,
            XPortletResponseContentTypes,
            XPortletSecure,
            XPortletSessionId,
            XPortletServerName,
            XPortletServerPort,
            XPortletScheme,
            XPortletUserInfo,
            XPortletWindowState
        };

    public static Header[] destroyRequestHeaders =
        new Header[] {
            XPortletMethod
        };
}

/**
 * A subclass for handling cookie-formatted headers.
 *
 * <P>A number of portlet headers use "cookie" format---that is, a
 * list of name-value pairs, separated by equal signs, delimited by
 * semi-colons. For example:
 *
 * <PRE>NAME1=VALUE1; NAME2=VALUE2; ... NAMEN=VALUEN;</PRE>
 *
 * <P>This class simplifies handling of cookie-valued headers by
 * providing default implementations of format and process that format
 * a list of values and parse a cookie formatted string.
 *
 * <P>Subclasses of this class need only to override a few template
 * methods to either enumerate name-value pairs (for request headers),
 * or process a sequence of name-value pairs (for response headers).
 *
 * @author M. Brent Harp
 */        
abstract class CookieHeader extends Header
{
    /**
     * Constructs a new cookie header.
     *
     * @param name the header name.
     */
    protected CookieHeader (String name)
    {
        super(name);
    }

    /**
     * Formats a cookie-valued header given a portlet context and
     * portlet configuration.
     *
     * @param context the portlet context.
     * @param config the portlet configuration.
     * @return a cookie-formatted string.
     */
    public String format (PortletContext context,
                          PortletConfig config)
        throws PortletException, IOException
    {
        StringBuffer sb = new StringBuffer();
        Enumeration e = getNames(context, config);
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            sb.append(name).append("=")
                .append(getValue(context, config, name));
            if (e.hasMoreElements()) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }

    /**
     * Formats a cookie-valued header given a portlet context, portlet
     * request, and portlet response.
     *
     * @param context the portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @return a cookie-formatted string.
     */
    public String format (PortletContext context,
                          PortletRequest request,
                          PortletResponse response)
        throws PortletException, IOException
    {
        StringBuffer sb = new StringBuffer();
        Enumeration e = getNames(context, request, response);
        while (e.hasMoreElements()) {
            String name = (String)e.nextElement();
            sb.append(name).append("=")
                .append(getValue(context, request, response, name));
            if (e.hasMoreElements()) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }
    
    /**
     * Parses and processes a cookie-valued header given a portlet
     * context, portlet request, portlet response, and header value.
     *
     * <P>This method calls the {@link
     * #process(PortletContext,PortletRequest,PortletResponse,String,String)
     * template method} to process each name-value pair.
     *
     * @param context the portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @param value a cooke-formatted string.
     */
    public void process (PortletContext context,
                         PortletRequest request,
                         PortletResponse response,
                         String value)
        throws PortletException, IOException
    {
        String[] cookies = value.split("; *");
        for (int i = 0; i < cookies.length; i++) {
            String[] pair = cookies[i].split("=");
            if (pair.length == 2) {
                process(context, request, response, pair[0], pair[1]);
            } else {
                process(context, request, response, pair[0], null);
            }
        }
    }
    
    /** 
     * A template method to process name-value pairs from a
     * cookie-formatted header given a portlet context, portlet
     * request, and portlet response.
     * 
     * <P>Subclasses that process response headers should override
     * this method and provide a concrete implementation.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context
     * @param request a portlet request
     * @param response a portlet response
     * @param name the name of this name-value pair.
     * @param value the value of this name-value pair.
     */
    public void process (PortletContext context,
                         PortletRequest request,
                         PortletResponse response,
                         String name, String value)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Enumerates over the names of name-value pairs for this request
     * header.
     *
     * <P>Subclasses that format request headers derived from the
     * portlet configuration should override this method with a
     * concrete implementation.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context
     * @param config a portlet configuration.
     * @return an enumeration of name-value pair names, as Strings.
     */
    public Enumeration getNames(PortletContext context,
                                PortletConfig config)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the value of a name-value pair given the name.
     *
     * <P>Subclasses that override {@link
     * #getNames(PortletContext,PortletConfig)} must override this
     * method to return values.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context.
     * @param config a portlet configuration.
     * @param name the name of the value to retrieve.
     * @return the value associated with name.
     */
    public String getValue(PortletContext context,
                           PortletConfig config,
                           String name)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Enumerates over the names of name-value pairs for this request
     * header.
     *
     * <P>Subclasses that format request headers derived from the
     * portlet request and response objects should override this
     * method with a concrete implementation.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @return an enumeration of name-value pair names, as Strings.
     */
    public Enumeration getNames(PortletContext context,
                                PortletRequest request,
                                PortletResponse response)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns the value of a name-value pair given the name.
     *
     * <P>Subclasses that override {@link
     * #getNames(PortletContext,PortletRequest,PortletResponse)} must
     * override this method to return the values.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @param name the name of the value to retrieve.
     * @return the value associated with name.
     */
    public String getValue(PortletContext context,
                           PortletRequest request,
                           PortletResponse response,
                           String name)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }
}

/**
 * A subclass for handling list-formatted headers.
 *
 * <P>A number of portlet headers use "list" format---that is, a
 * list of values delimited by commas. For example:
 *
 * <PRE>VALUE1, VALUE2, ..., VALUEN</PRE>
 *
 * <P>This class simplifies handling of list-valued headers by
 * providing default implementations of format and process that format
 * a list of values and parse a list formatted string.
 *
 * <P>Subclasses of this class need only override a few template
 * methods to either enumerate values (for request headers), or
 * process a sequence of values (for response headers).
 *
 * @author M. Brent Harp
 */        
abstract class ListHeader extends Header
{
    /**
     * Constructs a list header.
     *
     * @param name this header's name.
     */
    protected ListHeader (String name)
    {
        super(name);
    }

    /**
     * Formats a list-valued header given a portlet context and
     * portlet configuration.
     *
     * @param context the portlet context.
     * @param config the portlet configuration.
     * @return a list-formatted string.
     */
    public String format (PortletContext context,
                          PortletConfig config)
        throws PortletException, IOException
    {
        StringBuffer sb = new StringBuffer();
        Enumeration e = getValues(context, config);
        while (e.hasMoreElements()) {
            String value = (String)e.nextElement();
            sb.append(value);
            if (e.hasMoreElements()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Formats a list-valued header given a portlet context, portlet
     * request, and portlet response.
     *
     * @param context the portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @return a list-formatted string.
     */
    public String format (PortletContext context,
                          PortletRequest request,
                          PortletResponse response)
        throws PortletException, IOException
    {
        StringBuffer sb = new StringBuffer();
        Enumeration e = getValues(context, request, response);
        while (e.hasMoreElements()) {
            String value = (String)e.nextElement();
            sb.append(value);
            if (e.hasMoreElements()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Parses and processes a list-valued header given a portlet
     * context, portlet request, portlet response, and header value.
     *
     * <P>This method calls the {@link
     * #processValue(PortletContext,PortletRequest,PortletResponse,String)
     * template method} to process each value.
     *
     * @param context the portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @param value a list-formatted string.
     */
    public void process (PortletContext context,
                         PortletRequest request,
                         PortletResponse response,
                         String value)
        throws PortletException, IOException
    {
        String[] values = value.split(", *");
        for (int i = 0; i < values.length; i++) {
            processValue(context, request, response, values[i]);
        }
    }

    /** 
     * A template method to process values from a list-formatted
     * header given a portlet context, portlet request, and portlet
     * response.
     * 
     * <P>Subclasses that process response headers should override
     * this method and provide a concrete implementation.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context.
     * @param request a portlet request.
     * @param response a portlet response.
     * @param value a value.
     */
    public void processValue (PortletContext context,
                              PortletRequest request,
                              PortletResponse response,
                              String value)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Enumerates over the values for this request header.
     *
     * <P>Subclasses that format request headers derived from the
     * portlet configuration should override this method with a
     * concrete implementation.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context
     * @param config a portlet configuration.
     * @return an enumeration of String values.
     */
    public Enumeration getValues(PortletContext context,
                                 PortletConfig config)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Enumerates over the values for this request header.
     *
     * <P>Subclasses that format request headers derived from the
     * portlet request and portlet response should override this
     * method with a concrete implementation.
     *
     * <P>The default implementation throws
     * UnsupportedOperationException.
     *
     * @param context a portlet context
     * @param request a portlet request.
     * @param response a portlet response.
     * @return an enumeration of String values.
     */
    public Enumeration getValues(PortletContext context,
                                 PortletRequest request,
                                 PortletResponse response)
        throws PortletException, IOException
    {
        throw new UnsupportedOperationException();
    }
}
