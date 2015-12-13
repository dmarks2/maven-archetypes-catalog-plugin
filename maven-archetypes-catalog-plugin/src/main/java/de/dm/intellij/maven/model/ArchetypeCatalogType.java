package de.dm.intellij.maven.model;

/**
 * Created by Dominik on 13.12.2015.
 */
public enum ArchetypeCatalogType {

    CUSTOM("custom"),
    EXTENSION("extension"),
    SYSTEM("system");

    private String displayName;

    private ArchetypeCatalogType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
