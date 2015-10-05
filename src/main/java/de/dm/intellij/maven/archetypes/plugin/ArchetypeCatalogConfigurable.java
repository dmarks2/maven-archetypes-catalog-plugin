package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.InputValidator;
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
                if (StringUtil.isNotEmpty(text)) {
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
                if (StringUtil.isNotEmpty(text)) {
                    listModel.setElementAt(text, index);
                }
            }
        });

        JPanel panel = decorator.createPanel();
        UIUtil.addBorder(panel, IdeBorderFactory.createTitledBorder("Archetype Catalogs"));
        return panel;
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

    private static class ArchetypeRepositoryURLValidator implements InputValidator {
        @Override
        public boolean checkInput(String inputString) {
            try {
                final URL url = new URL(inputString);
                if (StringUtil.isNotEmpty(url.getHost())) {
                    boolean result = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(url);
                    return result;
                }
            }
            catch (MalformedURLException e) {
                return false;
            } catch (SAXException e) {
                return false;
            } catch (JAXBException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
            return false;
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }
    }
}
