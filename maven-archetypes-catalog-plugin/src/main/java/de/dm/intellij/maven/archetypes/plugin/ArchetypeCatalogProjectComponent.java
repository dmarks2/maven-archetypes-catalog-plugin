package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import de.dm.intellij.maven.archetypes.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dominik on 16.12.2015.
 */
@State(
        name = "ArchetypeCatalogProjectComponent",
        storages = {
                @Storage(
                        id = "default",
                        file = StoragePathMacros.PROJECT_FILE
                ),
                @Storage(
                        id = "dir",
                        file = StoragePathMacros.PROJECT_CONFIG_DIR + "/archetypeCatalog.xml",
                        scheme = StorageScheme.DIRECTORY_BASED)
                }
)
public class ArchetypeCatalogProjectComponent implements ProjectComponent, PersistentStateComponent<ArchetypeCatalogProjectComponentStateWrapper> {

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

    @Nullable
    public ArchetypeCatalogProjectComponentStateWrapper getState() {
        ArchetypeCatalogProjectComponentStateWrapper stateWrapper = new ArchetypeCatalogProjectComponentStateWrapper();
        stateWrapper.mavenArchetypeCatalogs = this.mavenArchetypeCatalogs;
        return stateWrapper;
    }

    public void loadState(ArchetypeCatalogProjectComponentStateWrapper stateWrapper) {
        this.mavenArchetypeCatalogs = stateWrapper.mavenArchetypeCatalogs;
    }
}
