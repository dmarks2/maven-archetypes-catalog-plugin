package de.dm.intellij.maven.model;

/**
 * Created by Dominik on 06.12.2015.
 */

import com.intellij.util.net.HttpConfigurable;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpConfigurable.class)
public class AnArchetypeCatalogFactoryUtil {

    @Before
    public void setup() {
        PowerMockito.mockStatic(HttpConfigurable.class);
        Mockito.when(HttpConfigurable.getInstance()).thenReturn(
                new HttpConfigurable() {
                    @NotNull
                    public URLConnection openConnection(@NotNull String location) throws IOException {
                        return new URL(location).openConnection();
                    }
                }
        );
    }

    @Test
    public void should_return_false_on_validateArchetypeCatalog_for_non_xml_file() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(new URL("http://www.google.de"));
        assertThat(valid, equalTo(false));
    }

    @Test
    public void should_return_false_on_validateArchetypeCatalog_for_invalid_xml_file() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(new URL("http://www.w3schools.com/xml/note.xml"));
        assertThat(valid, equalTo(false));
    }

    @Test
    public void should_return_true_on_validateArchetypeCatalog_for_valid_xml_file_with_namespace() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(new URL("https://repository.liferay.com/nexus/content/repositories/liferay-ce/archetype-catalog.xml"));
        assertThat(valid, equalTo(true));
    }

    @Test
    public void should_return_true_on_validateArchetypeCatalog_for_valid_xml_file_without_namespace() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(new URL("http://repo1.maven.org/maven2/archetype-catalog.xml"));
        assertThat(valid, equalTo(true));
    }

    @Test
    public void should_return_valid_ArchetypeCatalog_on_getArchetypeCatalog_for_valid_xml_file_with_namespace() throws IOException, JAXBException, SAXException {
        ArchetypeCatalog archetypeCatalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(new URL("https://repository.liferay.com/nexus/content/repositories/liferay-ce/archetype-catalog.xml"));
        assertThat(archetypeCatalog, not(nullValue()));
    }

    @Test
    public void should_return_valid_ArchetypeCatalog_on_getArchetypeCatalog_for_valid_xml_file_without_namespace() throws IOException, JAXBException, SAXException {
        ArchetypeCatalog archetypeCatalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(new URL("http://repo1.maven.org/maven2/archetype-catalog.xml"));
        assertThat(archetypeCatalog, not(nullValue()));
    }

}
