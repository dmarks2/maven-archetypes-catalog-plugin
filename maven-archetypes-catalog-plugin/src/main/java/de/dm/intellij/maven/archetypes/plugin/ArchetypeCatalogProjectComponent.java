package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import de.dm.intellij.maven.archetypes.Util;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik on 16.12.2015.
 */
public class ArchetypeCatalogProjectComponent implements ProjectComponent {

    public static final Logger LOG = Logger.getInstance(ArchetypeCatalogProjectComponent.class);

    private Project project;

    private Set<String> mavenArchetypeCatalogs = new HashSet<String>();

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

    public void addMavenArchetypeCatalogs(String mavenArchetypeCatalogs) {
        String[] catalogs = mavenArchetypeCatalogs.split(",");
        for (String catalogUrl : catalogs) {
            try {
                String url = Util.resolveCatalogUrl(catalogUrl, project);
                this.mavenArchetypeCatalogs.add(url);
            } catch (MalformedURLException e) {
                LOG.warn("Malformed URL " + catalogUrl + " (" + e.getMessage() + ")");
            }

        }
    }

    public Set<String> getMavenArchetypeCatalogUrls() {
        return mavenArchetypeCatalogs;
    }
}
