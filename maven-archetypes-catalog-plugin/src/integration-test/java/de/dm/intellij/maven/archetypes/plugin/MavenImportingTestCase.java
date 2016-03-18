package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.idea.maven.model.MavenExplicitProfiles;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.project.MavenProjectsTree;
import org.jetbrains.idea.maven.project.MavenWorkspaceSettingsComponent;
import org.jetbrains.idea.maven.server.MavenServerManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dominik on 13.02.2016.
 */
public abstract class MavenImportingTestCase extends MavenTestCase {

    protected MavenProjectsTree myProjectsTree;
    protected MavenProjectsManager myProjectsManager;

    @Override
    protected void setUp() throws Exception {
        VfsRootAccess.allowRootAccess(PathManager.getConfigPath());

        super.setUp();

        File settingsFile = MavenWorkspaceSettingsComponent.getInstance(myProject).getSettings().generalSettings.getEffectiveGlobalSettingsIoFile();
        if (settingsFile != null) {
            VfsRootAccess.allowRootAccess(settingsFile.getAbsolutePath());
        }
    }

    @Override
    protected void setUpInWriteAction() throws Exception {
        super.setUpInWriteAction();
        myProjectsManager = MavenProjectsManager.getInstance(myProject);
    }

    protected void importProject(@NonNls String xml) throws IOException {
        createProjectPom(xml);
        importProject();
    }

    protected void importProject() {
        importProjectWithProfiles();
    }

    protected void importProjectWithProfiles(String... profiles) {
        doImportProjects(true, Collections.singletonList(myProjectPom), profiles);
    }

    private void doImportProjects(boolean useMaven2, final List<VirtualFile> files, String... profiles) {
        MavenServerManager.getInstance().setUseMaven2(useMaven2);
        initProjectsManager(false);

        readProjects(files, profiles);

        UIUtil.invokeAndWaitIfNeeded(new Runnable() {
            @Override
            public void run() {
                myProjectsManager.waitForResolvingCompletion();
                myProjectsManager.scheduleImportInTests(files);
                myProjectsManager.importProjects();
            }
        });

        for (MavenProject each : myProjectsTree.getProjects()) {
            if (each.hasReadingProblems()) {
                System.out.println(each + " has problems: " + each.getProblems());
            }
        }
    }

    protected void initProjectsManager(boolean enableEventHandling) {
        myProjectsManager.initForTests();
        myProjectsTree = myProjectsManager.getProjectsTreeForTests();
        if (enableEventHandling) myProjectsManager.listenForExternalChanges();
    }

    protected void readProjects() {
        readProjects(myProjectsManager.getProjectsFiles());
    }

    protected void readProjects(List<VirtualFile> files, String... profiles) {
        myProjectsManager.resetManagedFilesAndProfilesInTests(files, new MavenExplicitProfiles(Arrays.asList(profiles)));
        waitForReadingCompletion();
    }

    protected void waitForReadingCompletion() {
        UIUtil.invokeAndWaitIfNeeded(new Runnable() {
            @Override
            public void run() {
                try {
                    myProjectsManager.waitForReadingCompletion();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
