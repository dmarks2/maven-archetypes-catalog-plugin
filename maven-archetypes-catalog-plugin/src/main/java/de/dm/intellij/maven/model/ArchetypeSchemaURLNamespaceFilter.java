package de.dm.intellij.maven.model;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class ArchetypeSchemaURLNamespaceFilter extends XMLFilterImpl {
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("".equals(uri)) {
            uri = ArchetypeCatalogFactoryUtil.ARCHETYPE_SCHEMA_URL;
        }
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if ("".equals(uri)) {
            uri = ArchetypeCatalogFactoryUtil.ARCHETYPE_SCHEMA_URL;
        }
        super.startElement(uri, localName, qName, atts);
    }

}

