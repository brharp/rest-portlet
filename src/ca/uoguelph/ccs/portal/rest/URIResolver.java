package ca.uoguelph.ccs.portal.rest;

/**
 * Resolves URIs. An object implementing this interface is used by a
 * {@link ContentFilter} to resolve URIs in a portlet response.
 *
 * @author M. Brent Harp
 */
public interface URIResolver
{
    /**
     * Resolves a URI. If the URI is already absolute it is returned
     * unchanged.
     */
    public String resolveURI(String uri);
}
