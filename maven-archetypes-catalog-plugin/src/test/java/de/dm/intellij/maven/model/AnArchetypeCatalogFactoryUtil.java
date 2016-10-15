package de.dm.intellij.maven.model;

/**
 * Created by Dominik on 06.12.2015.
 */

import com.intellij.util.net.HttpConfigurable;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpConfigurable.class)
public class AnArchetypeCatalogFactoryUtil {

    private static final URL ARBITRARY_TXT = AnArchetypeCatalogFactoryUtil.class.getResource("/archetype-catalogs/arbitrary.txt");
    private static final URL ARBITRARY_XML = AnArchetypeCatalogFactoryUtil.class.getResource("/archetype-catalogs/arbitrary.xml");
    private static final URL XML_WITH_NAMESPACE = AnArchetypeCatalogFactoryUtil.class.getResource("/archetype-catalogs/liferay/archetype-catalog.xml");
    private static final URL XML_WITHOUT_NAMESPACE = AnArchetypeCatalogFactoryUtil.class.getResource("/archetype-catalogs/maven2/archetype-catalog.xml");

    private static URL XML_INSIDE_JAR;

    static {
        try {
            XML_INSIDE_JAR = new URL(
                    "jar:" +
                            AnArchetypeCatalogFactoryUtil.class.getResource("/archetype-catalogs/jar/archetype-catalog.jar").toString() +
                            "!/archetype-catalog.xml");
        } catch (MalformedURLException e) {
            XML_INSIDE_JAR = null;
        }
    }

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
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(ARBITRARY_TXT);
        assertThat(valid, equalTo(false));
    }

    @Test
    @Ignore
    public void should_return_false_on_validateArchetypeCatalog_for_invalid_xml_file() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(ARBITRARY_XML);
        assertThat(valid, equalTo(false));
    }

    @Test
    public void should_return_true_on_validateArchetypeCatalog_for_valid_xml_file_with_namespace() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(XML_WITH_NAMESPACE);
        assertThat(valid, equalTo(true));
    }

    @Test
    public void should_return_true_on_validateArchetypeCatalog_for_valid_xml_file_without_namespace() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(XML_WITHOUT_NAMESPACE);
        assertThat(valid, equalTo(true));
    }

    @Test
    public void should_return_valid_ArchetypeCatalog_on_getArchetypeCatalog_for_valid_xml_file_with_namespace() throws IOException, JAXBException, SAXException {
        ArchetypeCatalog archetypeCatalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(XML_WITH_NAMESPACE);
        assertThat(archetypeCatalog, not(nullValue()));
    }

    @Test
    public void should_return_valid_ArchetypeCatalog_on_getArchetypeCatalog_for_valid_xml_file_without_namespace() throws IOException, JAXBException, SAXException {
        ArchetypeCatalog archetypeCatalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(XML_WITHOUT_NAMESPACE);
        assertThat(archetypeCatalog, not(nullValue()));
    }

    @Test
    public void should_return_true_on_validateArchetypeCatalog_for_valid_xml_file_within_jar() throws IOException, JAXBException, SAXException {
        boolean valid = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(XML_INSIDE_JAR);
        assertThat(valid, equalTo(true));
    }

    @Test
    public void should_return_valid_ArchetypeCatalog_on_getArchetypeCatalog_for_xml_file_within_jar() throws IOException, JAXBException, SAXException {
        ArchetypeCatalog archetypeCatalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(XML_INSIDE_JAR);
        assertThat(archetypeCatalog, not(nullValue()));
    }

}
