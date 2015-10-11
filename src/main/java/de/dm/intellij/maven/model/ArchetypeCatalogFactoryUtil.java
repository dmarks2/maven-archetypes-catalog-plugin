package de.dm.intellij.maven.model;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.apache.maven.archetype.catalog.ObjectFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogFactoryUtil {

    public static final String ARCHETYPE_CATALOG_SCHEMA_FILE = "archetype-catalog-1.0.0.xsd";
    public static final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    public static Schema schema;
    public static Validator validator;

    static {
        schema = null;
        try {
            schema = sf.newSchema(new StreamSource(ArchetypeCatalogFactoryUtil.class.getResourceAsStream("/" + ARCHETYPE_CATALOG_SCHEMA_FILE)));
            validator = schema.newValidator();
        } catch (SAXException e) {
        }
    }


    public static ArchetypeCatalog getArchetypeCatalog(URL url) throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<ArchetypeCatalog> result = (JAXBElement) unmarshaller.unmarshal(url.openStream());
        return result.getValue();
    }

    public static boolean validateArchetypeCatalog(URL url) throws IOException, SAXException, JAXBException {
        Source source = new StreamSource(url.openStream());

        MyErrorHandler myErrorHandler = new MyErrorHandler();
        validator.setErrorHandler(myErrorHandler);
        validator.validate(source);
        return myErrorHandler.isValid;

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
