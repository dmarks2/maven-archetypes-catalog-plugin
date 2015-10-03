package de.dm.intellij.maven.archetypes.plugin;

import de.dm.intellij.maven.model.ArchetypeCatalogFactoryUtil;
import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jetbrains.idea.maven.indices.MavenArchetypesProvider;
import org.jetbrains.idea.maven.model.MavenArchetype;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogProvider implements MavenArchetypesProvider {

    @Override
    public Collection<MavenArchetype> getArchetypes() {
        try {
            URL url = new URL("https://repository.liferay.com/nexus/content/repositories/liferay-ce/archetype-catalog.xml");

            ArchetypeCatalog catalog = ArchetypeCatalogFactoryUtil.getArchetypeCatalog(url);

            Collection<MavenArchetype> result = new ArrayList<MavenArchetype>();

            for (Archetype archetype : catalog.getArchetypes().getArchetype()) {
                result.add(new MavenArchetype(archetype.getGroupId(), archetype.getArtifactId(), archetype.getVersion(), archetype.getRepository(), archetype.getDescription()));
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
