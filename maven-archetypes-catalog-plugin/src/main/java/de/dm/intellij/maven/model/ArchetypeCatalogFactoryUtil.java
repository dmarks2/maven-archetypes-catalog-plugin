package de.dm.intellij.maven.model;

import com.intellij.util.net.HttpConfigurable;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.apache.maven.archetype.catalog.ObjectFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.*;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogFactoryUtil {

    public static final String ARCHETYPE_CATALOG_SCHEMA_FILE = "archetype-catalog-1.0.0.xsd";
    public static final String ARCHETYPE_SCHEMA_URL = "http://maven.apache.org/plugins/maven-archetype-plugin/archetype-catalog/1.0.0";

    public static final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    public static XMLFilter xmlFilter;

    static {
        try {
            xmlFilter = new ArchetypeSchemaURLNamespaceFilter();
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xmlFilter.setParent(xr);

        } catch (SAXException e) {
        } catch (ParserConfigurationException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public static ArchetypeCatalog getArchetypeCatalog(URL url) throws IOException, JAXBException, SAXException {
        if (url != null) {
            InputStream inputStream = getInputStream(url);
            if (inputStream != null) {
                return getArchetypeCatalog(inputStream);
            }
        }
        return null;
    }

    private static ArchetypeCatalog getArchetypeCatalog(InputStream inputStream) throws JAXBException, IOException, SAXException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        xmlFilter.setContentHandler(unmarshaller.getUnmarshallerHandler());

        InputSource inputSource = new InputSource(inputStream);
        xmlFilter.parse(inputSource);

        JAXBElement<ArchetypeCatalog> result = (JAXBElement<ArchetypeCatalog>) unmarshaller.getUnmarshallerHandler().getResult();
        return result.getValue();
    }

    public static boolean validateArchetypeCatalog(URL url) throws IOException, SAXException, JAXBException {
        if (url != null) {
            InputStream inputStream = getInputStream(url);
            if (inputStream != null) {
                return validateArchetypeCatalog(inputStream);
            }
        }

        return false;
    }

    private static boolean validateArchetypeCatalog(InputStream inputStream) throws IOException {
        InputSource inputSource = new InputSource(inputStream);
        MyErrorHandler myErrorHandler = new MyErrorHandler();
        xmlFilter.setErrorHandler(myErrorHandler);

        try {
            xmlFilter.parse(inputSource);
            return myErrorHandler.isValid;
        } catch (SAXException e) {
            return false;
        }
    }

    private static InputStream getInputStream(@NotNull URL url) throws IOException {
        String protocol = url.getProtocol();
        if (
                ("http".equals(protocol)) ||
                        ("https".equals(protocol))
                ) {
            //Use IntelliJ proxy settings for http / https URLs

            URLConnection urlConnection = HttpConfigurable.getInstance().openConnection(url.toString());
            return urlConnection.getInputStream();
        } else if ("file".equals(protocol)) {
            URLConnection urlConnection = url.openConnection();
            return urlConnection.getInputStream();
        } else if ("jar".equals(protocol)) {
            URLConnection urlConnection = url.openConnection();
            return urlConnection.getInputStream();
        }
        return null;
    }

    private static class MyErrorHandler implements ErrorHandler {

        protected boolean isValid = true;

        @Override
        public void warning(SAXParseException exception) throws SAXException {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            isValid = false;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            isValid = false;
        }
    }

}
