package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.extensions.ExtensionPointName;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Marks on 05.10.2015.
 */
public abstract class ArchetypeCatalogDefinition {

    public static ExtensionPointName<ArchetypeCatalogDefinition> EXTENSION_POINT_NAME = ExtensionPointName.create("de.dm.intellij.maven-archetypes-catalog-plugin.archetypeCatalogDefinition");

    public static Set<String> getArchetypeCatalogDefinitionsURLs() {
        Set<String> result = new HashSet<String>();
        ArchetypeCatalogDefinition[] archetypeCatalogDefinitions = (ArchetypeCatalogDefinition[])EXTENSION_POINT_NAME.getExtensions();
        for (ArchetypeCatalogDefinition archetypeCatalogDefinition : archetypeCatalogDefinitions) {
            result.addAll(archetypeCatalogDefinition.getArchetypeCatalogURLs());
        }
        return result;
    }

    public abstract Set<String> getArchetypeCatalogURLs();

}
