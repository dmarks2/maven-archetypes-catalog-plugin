package de.dm.intellij.maven.model;

/**
 * Created by Dominik on 13.12.2015.
 */
public class ArchetypeCatalogModel {

    private String url;
    private ArchetypeCatalogType type;

    public ArchetypeCatalogModel(String url, ArchetypeCatalogType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArchetypeCatalogType getType() {
        return type;
    }

    public void setType(ArchetypeCatalogType type) {
        this.type = type;
    }
}
