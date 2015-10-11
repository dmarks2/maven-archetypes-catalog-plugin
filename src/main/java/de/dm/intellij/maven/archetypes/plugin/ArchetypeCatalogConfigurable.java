package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.InputValidatorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.UIUtil;
import de.dm.intellij.maven.model.ArchetypeCatalogFactoryUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogConfigurable implements Configurable {

    private JBList lstCatalogs;
    private CollectionListModel<String> listModel;

    @Nls
    @Override
    public String getDisplayName() {
        return "Archetype Catalogs";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        listModel = new CollectionListModel<String>(ArchetypeCatalogSettings.getInstance().getUrls());
        lstCatalogs = new JBList(listModel);
        lstCatalogs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(lstCatalogs);
        decorator.setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                final String value = (String)lstCatalogs.getSelectedValue();
                final String text = Messages.showInputDialog(
                        "Archetype Catalog URL",
                        "Add Archetype Catalog URL",
                        Messages.getQuestionIcon(),
                        value == null ? "http://" : value,
                        new ArchetypeRepositoryURLValidator());
                if (validateInput(text)) {
                    listModel.add(text);
                    lstCatalogs.setSelectedValue(text, true);
                }
            }
        });
        decorator.setEditAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                final int index = lstCatalogs.getSelectedIndex();
                final String text = Messages.showInputDialog(
                        "Archetype Catalog URL",
                        "Edit Archetype Catalog URL",
                        Messages.getQuestionIcon(),
                        listModel.getElementAt(index),
                        new ArchetypeRepositoryURLValidator());
                if (validateInput(text)) {
                    listModel.setElementAt(text, index);
                }
            }
        });

        JPanel panel = decorator.createPanel();
        UIUtil.addBorder(panel, IdeBorderFactory.createTitledBorder("Archetype Catalogs"));
        return panel;
    }

    private boolean validateInput(String text) {
        String errorText = null;
        if (StringUtil.isNotEmpty(text)) {

            try {
                final URL url = new URL(text);
                if (StringUtil.isNotEmpty(url.getHost())) {
                    boolean result = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(url);
                    if (result == false) {
                        errorText = "URL does not point to a valid archetype-catalog.xml";
                    } else {
                        errorText = null;
                    }
                    return result;
                } else {
                    errorText = null;
                    return false;
                }
            } catch (MalformedURLException e) {
                errorText = "Invalid URL";
                return false;
            } catch (SAXException e) {
                errorText = "URL does not point to a valid XML file";
                return false;
            } catch (JAXBException e) {
                errorText = "URL does not point to a valid archetype-catalog.xml";
                return false;
            } catch (IOException e) {
                errorText = "Error reading from given URL";
                return false;
            } finally {
                if (errorText != null) {
                    Messages.showErrorDialog(errorText, "Archetype Catalog URL");
                }
            }
        }
        return false;
    }

    @Override
    public boolean isModified() {
        return (! (listModel.getItems().equals(ArchetypeCatalogSettings.getInstance().getUrls())) );
    }

    @Override
    public void apply() throws ConfigurationException {
        ArchetypeCatalogSettings.getInstance().setUrls(new HashSet<>(listModel.getItems()));
    }

    @Override
    public void reset() {
        listModel.removeAll();
        listModel.add(new ArrayList<String>(ArchetypeCatalogSettings.getInstance().getUrls()));
    }

    @Override
    public void disposeUIResources() {

    }

    private static class ArchetypeRepositoryURLValidator implements InputValidatorEx {

        private String errorText;

        @Override
        public boolean canClose(String inputString) {
            try {
                final URL url = new URL(inputString);
                boolean result = StringUtil.isNotEmpty(url.getHost());
                errorText = null;
                return result;
            } catch (MalformedURLException e) {
                errorText = "Invalid URL";
            }
            return false;
        }

        @Override
        public boolean checkInput(String inputString) {
            return canClose(inputString);
        }

        @Nullable
        @Override
        public String getErrorText(String s) {
            return errorText;
        }
    }
}
