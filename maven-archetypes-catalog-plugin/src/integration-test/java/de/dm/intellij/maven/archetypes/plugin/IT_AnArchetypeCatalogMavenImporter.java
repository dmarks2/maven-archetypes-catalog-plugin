package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Test;

import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dominik on 13.02.2016.
 */
public class IT_AnArchetypeCatalogMavenImporter extends MavenImportingTestCase {

    static {
        System.out.println(System.getProperties());
    }

    @Test
    public void testImportNone() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(0));
        }
    }

    @Test
    public void testImportLocalFromProperties() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>" +

                "<properties>" +
                "  <archetypeCatalog>local</archetypeCatalog>" +
                "</properties>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(1));
            String catalogUrl = mavenArchetypeCatalogUrls.iterator().next();
            assertThat(catalogUrl, IsNull.notNullValue());
        }
    }

    @Test
    public void testImportRemoteFromProperties() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>" +

                "<properties>" +
                "  <archetypeCatalog>remote</archetypeCatalog>" +
                "</properties>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(1));
            String catalogUrl = mavenArchetypeCatalogUrls.iterator().next();
            assertThat(catalogUrl, IsNull.notNullValue());
            assertThat(catalogUrl, IsEqual.equalTo("http://repo.maven.apache.org/maven2/archetype-catalog.xml"));
        }
    }

    @Test
    public void testImportUrlFromProperties() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>" +

                "<properties>" +
                "  <archetypeCatalog>http://www.foo.com/archetype-catalog.xml</archetypeCatalog>" +
                "</properties>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(1));
            String catalogUrl = mavenArchetypeCatalogUrls.iterator().next();
            assertThat(catalogUrl, IsNull.notNullValue());
            assertThat(catalogUrl, IsEqual.equalTo("http://www.foo.com/archetype-catalog.xml"));
        }
    }

    @Test
    public void testImportLocalFromPlugin() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>" +

                "<build>" +
                "  <plugins>" +
                "    <plugin>" +
                "      <artifactId>maven-archetype-plugin</artifactId>" +
                "      <configuration>" +
                "        <archetypeCatalog>local</archetypeCatalog>" +
                "      </configuration>" +
                "    </plugin>" +
                "  </plugins>" +
                "</build>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(1));
            String catalogUrl = mavenArchetypeCatalogUrls.iterator().next();
            assertThat(catalogUrl, IsNull.notNullValue());
        }
    }

    @Test
    public void testImportRemoteFromPlugin() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>" +

                "<build>" +
                "  <plugins>" +
                "    <plugin>" +
                "      <artifactId>maven-archetype-plugin</artifactId>" +
                "      <configuration>" +
                "        <archetypeCatalog>remote</archetypeCatalog>" +
                "      </configuration>" +
                "    </plugin>" +
                "  </plugins>" +
                "</build>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(1));
            String catalogUrl = mavenArchetypeCatalogUrls.iterator().next();
            assertThat(catalogUrl, IsNull.notNullValue());
            assertThat(catalogUrl, IsEqual.equalTo("http://repo.maven.apache.org/maven2/archetype-catalog.xml"));
        }
    }

    @Test
    public void testImportUrlFromPlugin() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>" +

                "<build>" +
                "  <plugins>" +
                "    <plugin>" +
                "      <artifactId>maven-archetype-plugin</artifactId>" +
                "      <configuration>" +
                "        <archetypeCatalog>http://www.foo.com/archetype-catalog.xml</archetypeCatalog>" +
                "      </configuration>" +
                "    </plugin>" +
                "  </plugins>" +
                "</build>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(1));
            String catalogUrl = mavenArchetypeCatalogUrls.iterator().next();
            assertThat(catalogUrl, IsNull.notNullValue());
            assertThat(catalogUrl, IsEqual.equalTo("http://www.foo.com/archetype-catalog.xml"));
        }
    }

    @Test
    public void testImportNoneFromPlugin() throws IOException {
        importProject("<groupId>test</groupId>" +
                "<artifactId>project</artifactId>" +
                "<version>1</version>" +

                "<build>" +
                "  <plugins>" +
                "    <plugin>" +
                "      <artifactId>maven-archetype-plugin</artifactId>" +
                "    </plugin>" +
                "  </plugins>" +
                "</build>");

        Module[] actual = ModuleManager.getInstance(myProject).getModules();
        for (Module module : actual) {
            ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
            assertThat(component, IsNull.notNullValue());
            Set<String> mavenArchetypeCatalogUrls = component.getMavenArchetypeCatalogUrls();
            assertThat(mavenArchetypeCatalogUrls.size(), IsEqual.equalTo(0));
        }
    }

}
