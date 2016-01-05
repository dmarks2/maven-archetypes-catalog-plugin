package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import org.jdom.Element;
import org.jetbrains.idea.maven.importing.MavenImporter;
import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.jetbrains.idea.maven.project.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Dominik on 16.12.2015.
 */
public class ArchetypeCatalogMavenImporter extends MavenImporter {

    public static final Logger LOG = Logger.getInstance(ArchetypeCatalogMavenImporter.class);

    public static final String APACHE_MAVEN_GROUP_ID = "org.apache.maven.plugins";
    public static final String ARCHETYPE_ARTIFACT_ID = "maven-archetype-plugin";

    public static final String ARCHETYPE_CATALOG = "archetypeCatalog";

    public ArchetypeCatalogMavenImporter() {
        super(APACHE_MAVEN_GROUP_ID, ARCHETYPE_ARTIFACT_ID);
    }


    @Override
    public void preProcess(Module module, MavenProject mavenProject, MavenProjectChanges mavenProjectChanges, MavenModifiableModelsProvider mavenModifiableModelsProvider) {

    }

    @Override
    public void process(MavenModifiableModelsProvider mavenModifiableModelsProvider, Module module, MavenRootModelAdapter mavenRootModelAdapter, MavenProjectsTree mavenProjectsTree, MavenProject mavenProject, MavenProjectChanges mavenProjectChanges, Map<MavenProject, String> map, List<MavenProjectsProcessorTask> list) {
        MavenPlugin plugin = mavenProject.findPlugin(myPluginGroupID, myPluginArtifactID);

        if (plugin != null) {
            Element config = plugin.getConfigurationElement();
            if (config != null) {
                Element child = config.getChild(ARCHETYPE_CATALOG);
                String archetypeCatalog = child.getText();

                ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
                if (component != null) {
                    component.addMavenArchetypeCatalogs(archetypeCatalog);
                }

            }
        } else {
            String archetypeCatalog = mavenProject.getProperties().getProperty(ARCHETYPE_CATALOG);
            if ( (archetypeCatalog != null) && (archetypeCatalog.length() > 0) ) {
                ArchetypeCatalogProjectComponent component = module.getProject().getComponent(ArchetypeCatalogProjectComponent.class);
                if (component != null) {
                    component.addMavenArchetypeCatalogs(archetypeCatalog);
                }
            }
        }
    }

    @Override
    public boolean isApplicable(MavenProject mavenProject) {
        boolean applicable = super.isApplicable(mavenProject);
        if (! applicable) {
            applicable = mavenProject.getProperties().containsKey(ARCHETYPE_CATALOG);
        }
        return applicable;
    }

    @Override
    public void getSupportedPackagings(Collection<String> result) {
        result.add(MavenConstants.TYPE_JAR);
        result.add(MavenConstants.TYPE_WAR);
        result.add(MavenConstants.TYPE_POM);
    }

    @Override
    public void getSupportedDependencyTypes(Collection<String> result, SupportedRequestType type) {
        result.add(MavenConstants.TYPE_JAR);
        result.add(MavenConstants.TYPE_WAR);
        result.add(MavenConstants.TYPE_POM);
    }

    @Override
    public boolean processChangedModulesOnly() {
        return false;
    }
}
