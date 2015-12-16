package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik on 16.12.2015.
 */
public class ArchetypeCatalogProjectComponent implements ProjectComponent {

    private Project project;

    private String mavenArchetypeCatalogs;

    public ArchetypeCatalogProjectComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "Maven Archetypes Catalog";
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getMavenArchetypeCatalogs() {
        return mavenArchetypeCatalogs;
    }

    public void setMavenArchetypeCatalogs(String mavenArchetypeCatalogs) {
        this.mavenArchetypeCatalogs = mavenArchetypeCatalogs;
    }

    public Set<String> getMavenArchetypeCatalogUrls() {
        Set<String> result = new HashSet<String>();
        if (mavenArchetypeCatalogs != null) {
            String[] catalogs = mavenArchetypeCatalogs.split(",");
            //TODO: only http / https urls supported (no local, remote, file://)
            for (String url : catalogs) {
                if (
                        (url.toLowerCase().startsWith("http://")) ||
                        (url.toLowerCase().startsWith("https://"))
                ) {
                    result.add(url);
                }
            }
        }
        return result;
    }
}
