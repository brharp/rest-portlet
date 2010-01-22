package ca.uoguelph.ccs.portal.rest;

import org.apache.xerces.xni.*;
import org.apache.xerces.xni.parser.*;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.filters.Writer;

/**
 * Filters a text/html content stream.
 *
 * <P>This class parses the input stream using the Neko HTML parser,
 * resolving the following attribute values using the configured
 * {@link URIResolver}.
 *
 * <UL>
 *   <LI>action</LI>
 *   <LI>cite</LI>
 *   <LI>classid</LI>
 *   <LI>codebase</LI>
 *   <LI>data</LI>
 *   <LI>datasrc</LI>
 *   <LI>href</LI>
 *   <LI>longdesc</LI>
 *   <LI>profile</LI>
 *   <LI>src</LI>
 *   <LI>usemap</LI>
 * </UL>
 *
 * @author M. Brent Harp
 */
public class HTMLContentFilter implements ContentFilter
{
    protected static final String FILTERS =
        "http://cyberneko.org/html/properties/filters";

    protected static final String BALANCE_TAGS =
        "http://cyberneko.org/html/features/balance-tags";
    
    protected static final String DOCUMENT_FRAGMENT =
        "http://cyberneko.org/html/features/balance-tags/document-fragment";

    protected static final String NAMES_ELEMS =
        "http://cyberneko.org/html/properties/names/elems";

    protected static final String NAMES_ATTRS =
        "http://cyberneko.org/html/properties/names/attrs";
    
    protected static final String UPPER = "upper";
    protected static final String LOWER = "lower";

    protected URIResolver resolver;
    
    public void setURIResolver(URIResolver resolver)
    {
        this.resolver = resolver;
    }

    public void filter (java.io.Reader reader, java.io.Writer writer)
        throws java.io.IOException
    {
        XMLDocumentFilter rewrite = new URLRewriteFilter();
        XMLDocumentFilter serialize = new Writer(writer, "UTF-8");
        XMLDocumentFilter[] filters = { rewrite, serialize };

        XMLParserConfiguration p = new HTMLConfiguration();
        p.setFeature(BALANCE_TAGS, false);
        p.setFeature(DOCUMENT_FRAGMENT, true);
        p.setProperty(FILTERS, filters);
        p.setProperty(NAMES_ELEMS, UPPER);
        p.setProperty(NAMES_ATTRS, LOWER);
        
        p.parse(new XMLInputSource(null, null, null, reader, null));
    }
    
    protected String resolveURI (String uri)
    {
        return resolver.resolveURI(uri);
    }
    
    class URLRewriteFilter extends DefaultFilter
    {
        public void startElement(QName element,
                                 XMLAttributes a,
                                 Augmentations augs)
            throws XNIException
        {
            int i;

            if ((i = a.getIndex("action")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("cite")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("classid")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("codebase")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("data")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("datasrc")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("href")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("longdesc")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("profile")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("src")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            if ((i = a.getIndex("usemap")) > -1) {
                a.setValue(i, resolveURI(a.getValue(i)));
            }

            super.startElement(element, a, augs);
        }
    }
}
