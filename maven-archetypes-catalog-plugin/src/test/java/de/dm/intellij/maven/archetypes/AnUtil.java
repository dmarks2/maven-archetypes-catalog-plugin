package de.dm.intellij.maven.archetypes;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by Dominik on 30.12.2015.
 */
public class AnUtil {

    private static final String URL_WITH_ARCHETYPE_CATALOG = "http://foo.com/archetype-catalog.xml";
    private static final String URL_WITHOUT_ARCHETYPE_CATALOG = "http://foo.com";
    private static final String URL_WITHOUT_ARCHETYPE_CATALOG_PLUS_SLASH = "http://foo.com/";
    private static final String URL_WITHOUT_ARCHETYPE_CATALOG_SUBDIRECTORY_PLUS_SLASH = "http://foo.com/bar/";

    private static final String FILE_WITH_ARCHETYPE_CATALOG = "file:/c:/archetype-catalog.xml";
    private static final String FILE_WITHOUT_ARCHETYPE_CATALOG = "file:/c:/";

    private static final String REMOTE = "remote";
    private static final String REMOTE_ARCHETYPE_CATALOG_URL = "http://repo.maven.apache.org/maven2/archetype-catalog.xml";

    private static final String LOCAL = "local";

    @Test
    public void should_not_change_url_with_archetype_catalog_xml_for_resolveCatalogUrl() throws MalformedURLException {
        String result = Util.resolveCatalogUrl(URL_WITH_ARCHETYPE_CATALOG, null);

        assertThat(result, equalTo(URL_WITH_ARCHETYPE_CATALOG));
    }

    @Test
    public void should_add_archetype_catalog_xml_for_url_without_for_resolveCatalogUrl() throws MalformedURLException {
        String result = Util.resolveCatalogUrl(URL_WITHOUT_ARCHETYPE_CATALOG, null);

        assertThat(result, equalTo(URL_WITH_ARCHETYPE_CATALOG));
    }

    @Test
    public void should_add_archetype_catalog_xml_for_url_without_but_with_slash_for_resolveCatalogUrl() throws MalformedURLException {
        String result = Util.resolveCatalogUrl(URL_WITHOUT_ARCHETYPE_CATALOG_PLUS_SLASH, null);

        assertThat(result, equalTo(URL_WITH_ARCHETYPE_CATALOG));
    }

    @Test
    public void should_add_archetype_catalog_xml_for_url_with_subdirectory_and_slash_for_resolveCatalogUrl() throws MalformedURLException {
        String result = Util.resolveCatalogUrl(URL_WITHOUT_ARCHETYPE_CATALOG_PLUS_SLASH, null);

        assertThat(result, equalTo(URL_WITH_ARCHETYPE_CATALOG));
    }

    @Test
    public void should_not_change_file_with_archetype_catalog_xml_for_resolveCatalogUrl() throws MalformedURLException {
        String result = Util.resolveCatalogUrl(FILE_WITH_ARCHETYPE_CATALOG, null);

        assertThat(result, equalTo(FILE_WITH_ARCHETYPE_CATALOG));
    }

    @Test
    public void should_add_archetype_catalog_xml_for_file_without_for_resolveCatalogUrl() throws MalformedURLException {
        String result = Util.resolveCatalogUrl(FILE_WITHOUT_ARCHETYPE_CATALOG, null);

        assertThat(result, equalTo(FILE_WITH_ARCHETYPE_CATALOG));
    }

    @Test
    public void should_resolve_remote_for_resolveCatalogUrl() throws MalformedURLException {
        String result = Util.resolveCatalogUrl(REMOTE, null);

        assertThat(result, equalTo(REMOTE_ARCHETYPE_CATALOG_URL));
    }



}
