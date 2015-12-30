package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by Dominik on 30.12.2015.
 */
public class ArchetypeCatalogFileChooserDescriptor extends FileChooserDescriptor {

    public ArchetypeCatalogFileChooserDescriptor() {
        super(true, false, false, false, false, false);
    }

    @Override
    public boolean isFileSelectable(VirtualFile file) {
        return "archetype-catalog.xml".equals(file.getName());
    }
}
