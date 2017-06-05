package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import de.dm.intellij.maven.model.ArchetypeCatalogFactoryUtil;
import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jetbrains.idea.maven.indices.MavenArchetypesProvider;
import org.jetbrains.idea.maven.model.MavenArchetype;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogProvider implements MavenArchetypesProvider {

    @Override
    public Collection<MavenArchetype> getArchetypes() {
        Set<String> urls = new HashSet<String>();
        urls.addAll(ArchetypeCatalogSettings.getInstance().getUrls());
        urls.addAll(ArchetypeCatalogDefinition.getArchetypeCatalogDefinitionsURLs());

        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects != null) {
            for (Project project : openProjects) {
                ArchetypeCatalogProjectComponent component = project.getComponent(ArchetypeCatalogProjectComponent.class);
                if (component != null) {
                    urls.addAll(component.getMavenArchetypeCatalogUrls());
                }
            }
        }

        Collection<MavenArchetype> result = new HashSet<MavenArchetype>();

        boolean skipRepository = ArchetypeCatalogSettings.getInstance().isSkipRepository();

        for (String url : urls) {
            try {

                ArchetypeCatalog catalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(new URL(url));

                if (catalog != null) {
                    if (catalog.getArchetypes() != null) {
                        for (Archetype archetype : catalog.getArchetypes().getArchetype()) {
                            result.add(new MavenArchetype(archetype.getGroupId(), archetype.getArtifactId(), archetype.getVersion(), (skipRepository ? null : archetype.getRepository()), archetype.getDescription()));
                        }
                    }
                }
            } catch (IOException e) {
                handleException(e, url);
            } catch (JAXBException e) {
                handleException(e, url);
            } catch (SAXException e) {
                handleException(e, url);
            }
        }
        return result;
    }

    private static void handleException(Exception e, String url) {
        ArchetypeCatalogApplicationComponent.notify("Unable to load Archetype Catalog " + url + ": " + e.getClass().getSimpleName(), NotificationType.WARNING);
        ArchetypeCatalogApplicationComponent.LOG.warn("Unable to load Archetype Catalog " + url + ": " + e.getClass().getSimpleName());
    }

}
