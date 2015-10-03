package de.dm.intellij.maven.model;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.apache.maven.archetype.catalog.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogFactoryUtil {

    public static ArchetypeCatalog getArchetypeCatalog(URL url) throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        JAXBElement<ArchetypeCatalog> result = (JAXBElement) unmarshaller.unmarshal(url.openStream());
        return result.getValue();
    }

    public static void main(String[] args) throws IOException, JAXBException {
        URL url = new URL("https://repository.liferay.com/nexus/content/repositories/liferay-ce/archetype-catalog.xml");

        ArchetypeCatalog catalog = getArchetypeCatalog(url);
        for (Archetype archetype : catalog.getArchetypes().getArchetype()) {
            System.out.println(archetype.getGroupId() + ":" + archetype.getArtifactId() + ":" + archetype.getVersion() + " (" + archetype.getDescription() + ") - " + archetype.getRepository());
        }
    }
}
