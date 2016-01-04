package de.dm.intellij.maven.archetypes;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import de.dm.intellij.maven.archetypes.plugin.ArchetypeCatalogDefinition;
import de.dm.intellij.maven.archetypes.plugin.ArchetypeCatalogProjectComponent;
import de.dm.intellij.maven.archetypes.plugin.ArchetypeCatalogSettings;
import de.dm.intellij.maven.model.ArchetypeCatalogModel;
import de.dm.intellij.maven.model.ArchetypeCatalogType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
        result.addAll(getUrlsWithType(customUrls, ArchetypeCatalogType.CUSTOM, null));
        result.addAll(getUrlsWithType(extensionUrls, ArchetypeCatalogType.EXTENSION, null));
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects != null) {
            for (Project project : openProjects) {
                ArchetypeCatalogProjectComponent component = project.getComponent(ArchetypeCatalogProjectComponent.class);
                if (component != null) {
                    result.addAll(getUrlsWithType(component.getMavenArchetypeCatalogUrls(), ArchetypeCatalogType.PROJECT, project));
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

    private static List<ArchetypeCatalogModel> getUrlsWithType(Set<String> urls, ArchetypeCatalogType type, @Nullable Project project) {
        List<ArchetypeCatalogModel> result = new ArrayList<ArchetypeCatalogModel>();
        for (String url : urls) {
            try {
                String resolvedUrl = resolveCatalogUrl(url, project);
                if (resolvedUrl != null) {
                    result.add(new ArchetypeCatalogModel(url, type));
                }
            } catch (MalformedURLException e) {
                LOG.warn("URL is malformed: " + url + " (" + e.getMessage() + ")");
            }
        }
        return result;
    }

    public static String resolveCatalogUrl(String catalogUrl, @Nullable Project project) throws MalformedURLException {
        if (catalogUrl == null) {
            return null;
        }
        if (
                (catalogUrl.toLowerCase().startsWith("http://")) ||
                (catalogUrl.toLowerCase().startsWith("https://"))
                ) {
            URL url = new URL(catalogUrl);
            String filePart = url.getFile();
            if ( (filePart == null) || (filePart.length() == 0) || ("/".equals(filePart)) ) {
                url = new URL(url, "archetype-catalog.xml");
            }

            return url.toString();
        } else if ("remote".equals(catalogUrl)) {
            return "http://repo.maven.apache.org/maven2/archetype-catalog.xml";
        } else if ("local".equals(catalogUrl)) {
            if (project != null) {
                MavenProjectsManager instance = MavenProjectsManager.getInstance(project);
                File mavenHome = instance.getLocalRepository().getParentFile();
                File archetypeCatalogFile = new File(mavenHome, "archetype-catalog.xml");
                if (archetypeCatalogFile.exists()) {
                    return archetypeCatalogFile.toURI().toURL().toString();
                }
            }
        } else if (catalogUrl.toLowerCase().startsWith("file:/")) {
            URL url = new URL(catalogUrl);
            String filePart = url.getFile();
            if ( (filePart == null) || (filePart.length() == 0) || ("/".equals(filePart)) || (filePart.endsWith("/")) ) {
                url = new URL(url, "archetype-catalog.xml");
            }

            return url.toString();
        }

        return null;
    }

    @Nullable
    public static String getFileUrl(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                return file.toURI().toURL().toString();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static String getFilePath(@Nullable String url) {
        if (url != null) {
            if (url.startsWith("file:/")) {
                try {
                    URL urlValue = new URL(url);
                    File file;
                    try {
                        file = new File(urlValue.toURI());
                    } catch (URISyntaxException e) {
                        file = new File(urlValue.getPath());
                    }
                    return file.getAbsolutePath();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
