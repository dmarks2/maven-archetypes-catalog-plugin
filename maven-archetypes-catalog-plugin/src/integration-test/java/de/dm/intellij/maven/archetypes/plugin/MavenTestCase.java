package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl;
import com.intellij.util.ui.UIUtil;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.indices.MavenIndicesManager;
import org.jetbrains.idea.maven.project.*;
import org.jetbrains.idea.maven.server.MavenServerManager;
import org.jetbrains.idea.maven.utils.MavenProgressIndicator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominik on 13.02.2016.
 */
public abstract class MavenTestCase extends UsefulTestCase {

    protected static final MavenConsole NULL_MAVEN_CONSOLE = new NullMavenConsole();
    // should not be static
    //protected static MavenProgressIndicator EMPTY_MAVEN_PROCESS = new MavenProgressIndicator(new EmptyProgressIndicator());

    protected IdeaProjectTestFixture myTestFixture;

    private File ourTempDir;
    protected File myDir;

    protected Project myProject;

    protected VirtualFile myProjectRoot;

    protected VirtualFile myProjectPom;
    protected List<VirtualFile> myAllPoms = new ArrayList<VirtualFile>();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ensureTempDirCreated();

        myDir = new File(ourTempDir, getTestName(false));
        FileUtil.ensureExists(myDir);

        setUpFixtures();

        myProject = myTestFixture.getProject();

        MavenWorkspaceSettingsComponent mavenWorkspaceSettingsComponent = MavenWorkspaceSettingsComponent.getInstance(myProject);
        mavenWorkspaceSettingsComponent.loadState(new MavenWorkspaceSettings());

        String home = getTestMavenHome();
        if (home != null) {
            getMavenGeneralSettings().setMavenHome(home);
        }

        UIUtil.invokeAndWaitIfNeeded(new Runnable() {
            @Override
            public void run() {
                try {
                    restoreSettingsFile();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setUpInWriteAction();
                        }
                        catch (Throwable e) {
                            try {
                                tearDown();
                            }
                            catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            MavenServerManager.getInstance().shutdown(true);
            //MavenArtifactDownloader.awaitQuiescence(100, TimeUnit.SECONDS);
            myProject = null;
            UIUtil.invokeAndWaitIfNeeded(new Runnable() {
                @Override
                public void run() {
                    try {
                        tearDownFixtures();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            MavenIndicesManager.getInstance().clear();
        }
        finally {
            super.tearDown();
            FileUtil.delete(myDir);
            // cannot use reliably the result of the com.intellij.openapi.util.io.FileUtil.delete() method
            // because com.intellij.openapi.util.io.FileUtilRt.deleteRecursivelyNIO() does not honor this contract
            if (myDir.exists()) {
                System.err.println("Cannot delete " + myDir);
                //printDirectoryContent(myDir);
                myDir.deleteOnExit();
            }
            resetClassFields(getClass());
        }
    }

    protected void tearDownFixtures() throws Exception {
        myTestFixture.tearDown();
        myTestFixture = null;
    }

    private void resetClassFields(final Class<?> aClass) {
        if (aClass == null) return;

        final Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            final int modifiers = field.getModifiers();
            if ((modifiers & Modifier.FINAL) == 0
                    && (modifiers & Modifier.STATIC) == 0
                    && !field.getType().isPrimitive()) {
                field.setAccessible(true);
                try {
                    field.set(this, null);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        if (aClass == MavenTestCase.class) return;
        resetClassFields(aClass.getSuperclass());
    }

    private void ensureTempDirCreated() throws IOException {
        if (ourTempDir != null) return;

        ourTempDir = new File(FileUtil.getTempDirectory(), "mavenTests");
        FileUtil.delete(ourTempDir);
        FileUtil.ensureExists(ourTempDir);
    }

    protected void setUpFixtures() throws Exception {
        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder(getName());
        myTestFixture = fixtureBuilder.getFixture();
        myTestFixture.setUp();
    }

    protected void setUpInWriteAction() throws Exception {
        File projectDir = new File(myDir, "project");
        projectDir.mkdirs();
        myProjectRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(projectDir);
    }

    protected MavenGeneralSettings getMavenGeneralSettings() {
        return MavenProjectsManager.getInstance(myProject).getGeneralSettings();
    }

    protected void restoreSettingsFile() throws IOException {
        updateSettingsXml("");
    }

    protected VirtualFile updateSettingsXml(String content) throws IOException {
        return updateSettingsXmlFully(createSettingsXmlContent(content));
    }

    protected VirtualFile updateSettingsXmlFully(@NonNls @Language("XML") String content) throws IOException {
        File ioFile = new File(myDir, "settings.xml");
        ioFile.createNewFile();
        VirtualFile f = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ioFile);
        setFileContent(f, content, true);
        getMavenGeneralSettings().setUserSettingsFile(f.getPath());
        return f;
    }

    protected VirtualFile createProjectPom(@NonNls String xml) throws IOException {
        return myProjectPom = createPomFile(myProjectRoot, xml);
    }

    protected VirtualFile createPomFile(final VirtualFile dir, String xml) throws IOException {
        VirtualFile f = dir.findChild("pom.xml");
        if (f == null) {
            f = new WriteAction<VirtualFile>() {
                @Override
                protected void run(@NotNull Result<VirtualFile> result) throws Throwable {
                    VirtualFile res = dir.createChildData(null, "pom.xml");
                    result.setResult(res);
                }
            }.execute().getResultObject();
            myAllPoms.add(f);
        }
        setFileContent(f, createPomXml(xml), true);
        return f;
    }

    protected boolean hasMavenInstallation() {
        boolean result = getTestMavenHome() != null;
        return result;
    }

    @NonNls @Language(value="XML")
    public static String createPomXml(@NonNls @Language(value="XML", prefix="<xml>", suffix="</xml>") String xml) {
        return "<?xml version=\"1.0\"?>" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" +
                "  <modelVersion>4.0.0</modelVersion>" +
                xml +
                "</project>";
    }

    private static String getTestMavenHome() {
        return System.getProperty("idea.maven.test.home");
    }

    private static String createSettingsXmlContent(String content) {
        String mirror = System.getProperty("idea.maven.test.mirror",
                // use JB maven proxy server for internal use by default, see details at
                // https://confluence.jetbrains.com/display/JBINT/Maven+proxy+server
                "http://maven.labs.intellij.net/repo1");
        return "<settings>" +
                content +
                "<mirrors>" +
                "  <mirror>" +
                "    <id>jb-central-proxy</id>" +
                "    <url>" + mirror + "</url>" +
                "    <mirrorOf>external:*,!flex-repository</mirrorOf>" +
                "  </mirror>" +
                "</mirrors>" +
                "</settings>";
    }

    private static void setFileContent(final VirtualFile file, final String content, final boolean advanceStamps) throws IOException {
        new WriteAction<VirtualFile>() {
            @Override
            protected void run(@NotNull Result<VirtualFile> result) throws Throwable {
                if (advanceStamps) {
                    file.setBinaryContent(content.getBytes(), -1, file.getTimeStamp() + 4000);
                }
                else {
                    file.setBinaryContent(content.getBytes(), file.getModificationStamp(), file.getTimeStamp());
                }
            }
        }.execute().getResultObject();
    }
}
