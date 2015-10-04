package de.dm.intellij.maven.archetypes.plugin;

import de.dm.intellij.maven.model.ArchetypeCatalogFactoryUtil;
import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jetbrains.idea.maven.indices.MavenArchetypesProvider;
import org.jetbrains.idea.maven.model.MavenArchetype;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogProvider implements MavenArchetypesProvider {

    @Override
    public Collection<MavenArchetype> getArchetypes() {
        try {
            List<String> urls = ArchetypeCatalogSettings.getInstance().getUrls();

            Collection<MavenArchetype> result = new HashSet<>();

            for (String url : urls) {

                ArchetypeCatalog catalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(new URL(url));

                for (Archetype archetype : catalog.getArchetypes().getArchetype()) {
                    result.add(new MavenArchetype(archetype.getGroupId(), archetype.getArtifactId(), archetype.getVersion(), archetype.getRepository(), archetype.getDescription()));
                }
            }

            return result;
        } catch (IOException e) {
            //??
        } catch (JAXBException e) {
            //??
        }

        return Collections.emptyList();
    }

}
