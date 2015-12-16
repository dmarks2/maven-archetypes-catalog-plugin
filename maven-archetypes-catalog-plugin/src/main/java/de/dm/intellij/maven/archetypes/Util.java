package de.dm.intellij.maven.archetypes;

import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import de.dm.intellij.maven.archetypes.plugin.ArchetypeCatalogDefinition;
import de.dm.intellij.maven.archetypes.plugin.ArchetypeCatalogProjectComponent;
import de.dm.intellij.maven.archetypes.plugin.ArchetypeCatalogSettings;
import de.dm.intellij.maven.model.ArchetypeCatalogModel;
import de.dm.intellij.maven.model.ArchetypeCatalogType;
import org.jetbrains.idea.maven.project.MavenProjectSettings;
import org.jetbrains.idea.maven.utils.MavenUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dominik on 10.10.2015.
 */
public class Util {

    public static final Logger LOG = Logger.getInstance(Util.class);

    public static final String NOTIFICATION_GROUP_ID = "maven-archetypes-catalog-plugin";
    public static final String COMPONENT_NAME = "maven-archetype-catalog-plugin";

    public static List<ArchetypeCatalogModel> getArchetypeCatalogModels() {
        List<ArchetypeCatalogModel> result = new ArrayList<ArchetypeCatalogModel>();
        Set<String> customUrls = ArchetypeCatalogSettings.getInstance().getUrls();
        Set<String> extensionUrls = ArchetypeCatalogDefinition.getArchetypeCatalogDefinitionsURLs();
        result.addAll(getUrlsWithType(customUrls, ArchetypeCatalogType.CUSTOM));
        result.addAll(getUrlsWithType(extensionUrls, ArchetypeCatalogType.EXTENSION));
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects != null) {
            for (Project project : openProjects) {
                ArchetypeCatalogProjectComponent component = project.getComponent(ArchetypeCatalogProjectComponent.class);
                if (component != null) {
                    result.addAll(getUrlsWithType(component.getMavenArchetypeCatalogUrls(), ArchetypeCatalogType.PROJECT));
                }
            }
        }

        return result;
    }

    public static void saveArchetypeCatalogModels(List<ArchetypeCatalogModel> archetypeCatalogs) {
        Set<String> customUrls = new HashSet<String>();
        for (ArchetypeCatalogModel archetypeCatalogModel : archetypeCatalogs) {
            if (ArchetypeCatalogType.CUSTOM.equals(archetypeCatalogModel.getType())) {
                customUrls.add(archetypeCatalogModel.getUrl());
            }
        }
        ArchetypeCatalogSettings.getInstance().setUrls(customUrls);
    }

    private static List<ArchetypeCatalogModel> getUrlsWithType(Set<String> urls, ArchetypeCatalogType type) {
        List<ArchetypeCatalogModel> result = new ArrayList<ArchetypeCatalogModel>();
        for (String url : urls) {
            if (
                    (url.toLowerCase().startsWith("http://")) ||
                    (url.toLowerCase().startsWith("https://"))
                ) {
                result.add(new ArchetypeCatalogModel(url, type));
            }
        }
        return result;
    }
}
