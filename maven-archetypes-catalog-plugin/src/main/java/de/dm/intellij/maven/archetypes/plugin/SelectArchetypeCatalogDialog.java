package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dominik on 19.12.2015.
 */
public class SelectArchetypeCatalogDialog extends DialogWrapper {

    private TextFieldWithBrowseButton textField = new TextFieldWithBrowseButton();

    protected SelectArchetypeCatalogDialog(@Nullable Project project, FileChooserDescriptor descriptor, String title) {
        super(project);
        setTitle(title);
        textField.addBrowseFolderListener("Archetype Catalog", null, project, descriptor);
        textField.setMinimumSize(new Dimension(250, textField.getMinimumSize().height));
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        JLabel messageComponent = new JLabel(Messages.getQuestionIcon());
        Container screenSize = new Container();
        screenSize.setLayout(new BorderLayout());
        screenSize.add(messageComponent, "North");
        panel.add(screenSize, "West");

        panel.add(textField, "Center");

        return panel;
    }



    public String getUrl() {
        String text = textField.getText();
        try {
            new URL(text);
            return text;
        } catch (MalformedURLException e) {
            File file = new File(text);
            if (file.exists()) {
                try {
                    return file.toURI().toURL().toString();
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return textField.getText();
    }

    public void setUrl(String url) {
        if (url != null) {
            if (url.startsWith("file:/")) {
                File file = new File(url);
                textField.setText(file.getAbsolutePath());
                return;
            }
        }
        textField.setText(url);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return textField;
    }
}
