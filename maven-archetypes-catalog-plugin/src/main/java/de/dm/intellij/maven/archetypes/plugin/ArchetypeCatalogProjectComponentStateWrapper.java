package de.dm.intellij.maven.archetypes.plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik on 30.01.2016.
 */
public class ArchetypeCatalogProjectComponentStateWrapper {

    public Set<String> mavenArchetypeCatalogs;

    public ArchetypeCatalogProjectComponentStateWrapper() {
        this.mavenArchetypeCatalogs = new HashSet<String>();
    }

}
