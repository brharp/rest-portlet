package ca.uoguelph.ccs.portal.rest;

import java.net.*;
import javax.portlet.*;

/**
 * Resolves portlet scheme URIs.
 *
 * @author M. Brent Harp
 */
public abstract class PortletURIResolver
    implements URIResolver
{
    /**
     * Constructs a new portlet URI resolver.
     */
    public PortletURIResolver()
    {
    }

    /**
     * Returns a portlet render URL.
     *
     * @return a portlet render URL.
     */
    public abstract PortletURL createRenderURL();

    /**
     * Returns an action URL.
     *
     * @return a portlet action URL.
     */
    public abstract PortletURL createActionURL();

    /**
     * Returns the base URI against which to resolve relative
     * URIs. This is typically the endpiont URL.
     *
     * @return an URI.
     */
    public abstract String getBaseURI();

    /**
     * Resolves a relative URI against the base URI.
     *
     * @param rel a URI to resolve
     * @return the resolved URI
     */
    public String resolveURI (String rel)
    {
        return resolveURI(rel, getBaseURI());
    }
    
    /**
     * Resolves a URI against a base URI. If URI to resolve is already
     * an absolute URI, it is returned unchanged.
     *
     * <p>This method also transforms "portlet:" scheme URIs into
     * PortletURLs.
     *
     * @param relativeURI a URI to resolve
     * @param baseURI a base URI to resolve against
     * @return the resolved URI
     */
    public String resolveURI (String relativeURI, String baseURI)
    {
         try {
             URI rel  = new URI(relativeURI);
             URI base = new URI(baseURI);
             URI uri  = base.resolve(rel);
             
             if ("portlet".equals(uri.getScheme())) {
                 return createPortletURL(uri).toString();
             } else {
                 return uri.toString();
             }
         }
         catch (Exception e) {
             e.printStackTrace();
             return relativeURI;
         }
    }

    /**
     * Creates a PortletURL from a "portlet:" URI, a render request,
     * and a render response.
     *
     * @param uri a portlet: scheme URI
     * @return a PortletURL
     */
    private PortletURL createPortletURL(URI uri) 
        throws Exception
    {
        // The path component of a portlet URI is either "render" or
        // "processAction", followed (optionally) by one or more of
        // ";mode=<mode>", ";state=<state>", ";secure=<true|false>".
        
        PortletURL portletURL = null;
        
        String schemeSpecificPart = uri.getRawSchemeSpecificPart();
        String schemeSpecificParts[] =
            schemeSpecificPart.split("\\?", 2);
        
        if (schemeSpecificParts.length > 0) {
            String pathComponent = schemeSpecificParts[0];
            String path[] = pathComponent.split(";", 2);
            String method = path[0];
            if ("render".equals(method)) {
                portletURL = createRenderURL();
            }
            else if ("processAction".equals(method)) {
                portletURL = createActionURL();
            }
            else {
                throw new IllegalArgumentException
                    (method + " is not a valid portlet method.");
            }
            
            if (path.length > 1) {
                String params[] = path[1].split(";");
                for (int i = 0; i < params.length; i++) {
                    String[] nv = params[i].split("=");
                    if (nv.length == 2) {
                        String name = nv[0].toLowerCase();
                        String value = nv[1].toLowerCase();
                        if (name.equals("mode"))
                            portletURL.setPortletMode(new PortletMode(value));
                        else if (name.equals("state"))
                            portletURL.setWindowState(new WindowState(value));
                        else if (name.equals("secure"))
                            portletURL.setSecure(Boolean.parseBoolean(value));
                    }
                }
            }
        }
        else {
            portletURL = createRenderURL();
        }

        if (schemeSpecificParts.length == 2) {
            String rawQuery = schemeSpecificParts[1];
            String[] pairs = rawQuery.split("&");
            for (int i = 0; i < pairs.length; i++) {
                String[] nv = pairs[i].split("=");
                if (nv.length == 2) {
                    portletURL.setParameter(URLDecoder.decode(nv[0], "UTF-8"),
                                            URLDecoder.decode(nv[1], "UTF-8"));
                }
            }
        }
        
        return portletURL;
    }
}
