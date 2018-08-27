package de.dm.intellij.maven.archetypes.plugin;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.*;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import de.dm.intellij.maven.archetypes.Util;
import de.dm.intellij.maven.model.ArchetypeCatalogFactoryUtil;
import de.dm.intellij.maven.model.ArchetypeCatalogModel;
import de.dm.intellij.maven.model.ArchetypeCatalogType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Dominik on 03.10.2015.
 */
public class ArchetypeCatalogConfigurable implements SearchableConfigurable, Configurable.NoScroll {

    private JBTable lstCatalogs;
    private TableModel listModel;
    private JCheckBox checkboxSkipRepository;

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
        listModel = new ArchetypeCatalogTableModel(Util.getArchetypeCatalogModels());
        lstCatalogs = new JBTable(listModel);
        lstCatalogs.getEmptyText().setText("No Archetype Catalogs defined");
        lstCatalogs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCatalogs.setDefaultRenderer(Object.class, new ArchetypeCatalogCellRenderer());
        //lstCatalogs.getColumnModel().getColumn(0).setPreferredWidth(400);
        //lstCatalogs.getColumnModel().getColumn(1).setPreferredWidth(100);
        lstCatalogs.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(lstCatalogs);
        decorator.setAddAction(new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                FileChooserDescriptor descriptor = new ArchetypeCatalogFileChooserDescriptor();

                descriptor.setDescription("Archetype Catalog URL");

                SelectArchetypeCatalogDialog dialog = new SelectArchetypeCatalogDialog(null, descriptor, "Add Archetype Catalog URL");

                dialog.show();
                if (dialog.isOK()) {
                    final String text = dialog.getUrl();

                    ProgressManager.getInstance().run(new Task.Modal(null, "Reading Archetype Catalog", true) {

                        private boolean isValid;

                        @Override
                        public void run(ProgressIndicator progressIndicator) {
                            progressIndicator.setIndeterminate(true);
                            isValid = validateInput(text);
                        }

                        @Override
                        public void onSuccess() {
                            if (isValid) {
                                ArchetypeCatalogModel archetypeCatalogModel = new ArchetypeCatalogModel(text, ArchetypeCatalogType.CUSTOM);
                                addRow(archetypeCatalogModel);
                                lstCatalogs.getSelectionModel().setSelectionInterval(listModel.getRowCount() - 1, listModel.getRowCount() - 1);
                            }
                        }
                    });
                }
            }
        });
        final AnActionButtonRunnable editAction = new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                final int index = lstCatalogs.getSelectedRow();
                final ArchetypeCatalogModel archetypeCatalogModel = getArchetypeCatalogModel(index);
                if (ArchetypeCatalogType.CUSTOM.equals(archetypeCatalogModel.getType())) {

                    FileChooserDescriptor descriptor = new ArchetypeCatalogFileChooserDescriptor();

                    descriptor.setDescription("Archetype Catalog URL");

                    final SelectArchetypeCatalogDialog dialog = new SelectArchetypeCatalogDialog(null, descriptor, "Edit Archetype Catalog URL");
                    dialog.setUrl(archetypeCatalogModel.getUrl());

                    dialog.show();
                    if (dialog.isOK()) {
                        final String text = dialog.getUrl();

                        ProgressManager.getInstance().run(new Task.Modal(null, "Reading Archetype Catalog", true) {

                            private boolean isValid;

                            @Override
                            public void run(ProgressIndicator progressIndicator) {
                                progressIndicator.setIndeterminate(true);
                                isValid = validateInput(text);
                            }

                            @Override
                            public void onSuccess() {
                                if (isValid) {
                                    archetypeCatalogModel.setUrl(dialog.getUrl());
                                    updateRow(index, archetypeCatalogModel);
                                }
                            }
                        });
                    }
                }
            }
        };
        decorator.setEditAction(editAction);

        final AnActionButtonRunnable deleteAction = new AnActionButtonRunnable() {
            @Override
            public void run(AnActionButton anActionButton) {
                final int index = lstCatalogs.getSelectedRow();
                ArchetypeCatalogModel archetypeCatalogModel = getArchetypeCatalogModel(index);
                if (archetypeCatalogModel != null) {
                    if (ArchetypeCatalogType.CUSTOM.equals(archetypeCatalogModel.getType())) {
                        deleteRow(index);
                    }
                }
            }
        };
        decorator.setRemoveAction(deleteAction);

        JPanel panel = decorator.createPanel();

        JPanel component = new JPanel(new BorderLayout());
        component.setPreferredSize(JBUI.size(300, 500));
        panel.setBorder(IdeBorderFactory.createTitledBorder("Archetype Catalogs", false, new Insets(10, 0, 0, 0)));
        component.add(panel, BorderLayout.CENTER);

        final AnActionButton removeButton = ToolbarDecorator.findRemoveButton(component);
        final AnActionButton editButton = ToolbarDecorator.findEditButton(component);

        lstCatalogs.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                final int index = lstCatalogs.getSelectedRow();
                ArchetypeCatalogModel archetypeCatalogModel = getArchetypeCatalogModel(index);
                if (archetypeCatalogModel != null) {
                    if (! (ArchetypeCatalogType.CUSTOM.equals(archetypeCatalogModel.getType())) ) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                removeButton.setEnabled(false);
                                editButton.setEnabled(false);
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                removeButton.setEnabled(true);
                                editButton.setEnabled(true);
                            }
                        });
                    }
                }
            }
        });

        lstCatalogs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel options = new JPanel(new BorderLayout());
        options.setBorder(IdeBorderFactory.createTitledBorder("Options", false, new Insets(10, 0, 0, 0)));
        component.add(options, BorderLayout.NORTH);
        checkboxSkipRepository = new JBCheckBox("Skip Repository Definition");
        checkboxSkipRepository.setSelected(ArchetypeCatalogSettings.getInstance().isSkipRepository());

        JPanel optionsInside = new JPanel(new GridLayout(0,1));
        optionsInside.add(checkboxSkipRepository);
        optionsInside.add(new JBLabel("Suppresses -DarchetypeRepository parameter when creating new projects. Be sure to define required repositories in your settings.xml.", UIUtil.ComponentStyle.REGULAR, UIUtil.FontColor.BRIGHTER));

        options.add(optionsInside, BorderLayout.NORTH);

        return component;
    }

    private boolean validateInput(String text) {
        String errorText = null;
        if (StringUtil.isNotEmpty(text)) {

            try {
                final URL url = new URL(text);
                boolean result = ArchetypeCatalogFactoryUtil.validateArchetypeCatalog(url);
                if (result == false) {
                    errorText = "URL does not point to a valid archetype-catalog.xml";
                } else {
                    errorText = null;
                }
                return result;
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
                    final String message = errorText;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Messages.showErrorDialog(message, "Archetype Catalog URL");
                        }
                    });
                }
            }
        }
        return false;
    }

    @Override
    public boolean isModified() {
        return ((ArchetypeCatalogTableModel)listModel).isModified() ||
                (ArchetypeCatalogSettings.getInstance().isSkipRepository() != checkboxSkipRepository.isSelected());
    }

    @Override
    public void apply() throws ConfigurationException {
        Util.saveArchetypeCatalogModels(((ArchetypeCatalogTableModel)listModel).getArchetypeCatalogModels());
        ArchetypeCatalogSettings.getInstance().setSkipRepository(checkboxSkipRepository.isSelected());
        ((ArchetypeCatalogTableModel)listModel).setModified(false);
    }

    @Override
    public void reset() {
        ((ArchetypeCatalogTableModel)listModel).updateArchetypeCatalogModels(Util.getArchetypeCatalogModels());
        checkboxSkipRepository.setSelected(ArchetypeCatalogSettings.getInstance().isSkipRepository());
    }

    @Override
    public void disposeUIResources() {

    }

    private ArchetypeCatalogModel getArchetypeCatalogModel(int i) {
        return ((ArchetypeCatalogTableModel)listModel).getArchetypeCatalogModel(i);
    }

    private void addRow(ArchetypeCatalogModel archetypeCatalogModel) {
        ((ArchetypeCatalogTableModel)listModel).addRow(archetypeCatalogModel);
    }

    private void updateRow(int row, ArchetypeCatalogModel archetypeCatalogModel) {
        ((ArchetypeCatalogTableModel)listModel).setRow(row, archetypeCatalogModel);
    }

    private void deleteRow(int row) {
        ((ArchetypeCatalogTableModel)listModel).removeRow(row);
    }

    @NotNull
    @Override
    public String getId() {
        return "de.dm.intellij.maven.archetypes.plugin.ArchetypeCatalogConfigurable";
    }

    private class ArchetypeCatalogTableModel extends AbstractTableModel {

        private final String[] COLUMNS = { "URL", "Type" };
        private final List<ArchetypeCatalogModel> archetypeCatalogModels;

        boolean modified;

        public ArchetypeCatalogTableModel(List<ArchetypeCatalogModel> archetypeCatalogModels) {
            this.archetypeCatalogModels = archetypeCatalogModels;
            modified = false;
        }

        public List<ArchetypeCatalogModel> getArchetypeCatalogModels() {
            return archetypeCatalogModels;
        }

        public void updateArchetypeCatalogModels(List<ArchetypeCatalogModel> archetypeCatalogModels) {
            this.archetypeCatalogModels.clear();
            this.archetypeCatalogModels.addAll(archetypeCatalogModels);
            fireTableDataChanged();
            modified = false;
        }

        @Override
        public int getRowCount() {
            return archetypeCatalogModels.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int i) {
            return COLUMNS[i];
        }

        @Override
        public Object getValueAt(int row, int col) {
            ArchetypeCatalogModel archetypeCatalogModel = getArchetypeCatalogModel(row);
            switch (col) {
                case 0: return archetypeCatalogModel.getUrl();
                case 1: return archetypeCatalogModel.getType().getDisplayName();
            }
            throw new RuntimeException("Invalid column " + col);
        }

        public ArchetypeCatalogModel getArchetypeCatalogModel(int index) {
            if (index > -1) {
                return archetypeCatalogModels.get(index);
            } else {
                return null;
            }
        }

        public void setRow(int row, ArchetypeCatalogModel archetypeCatalogModel) {
            this.archetypeCatalogModels.set(row, archetypeCatalogModel);
            fireTableRowsUpdated(row, row);
            modified = true;
        }
        public void addRow(ArchetypeCatalogModel archetypeCatalogModel) {
            this.archetypeCatalogModels.add(archetypeCatalogModel);
            fireTableRowsInserted(archetypeCatalogModels.size() - 1, archetypeCatalogModels.size() - 1);
            modified = true;
        }
        public void removeRow(int row) {
            this.archetypeCatalogModels.remove(row);
            fireTableRowsDeleted(row, row);
            modified = true;
        }

        public boolean isModified() {
            return modified;
        }

        public void setModified(boolean modified) {
            this.modified = modified;
        }
    }

    private class ArchetypeCatalogCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(null);
            setBackground(null);

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            ArchetypeCatalogModel archetypeCatalogModel = ArchetypeCatalogConfigurable.this.getArchetypeCatalogModel(row);
            if (archetypeCatalogModel.getType() == ArchetypeCatalogType.EXTENSION) {
                if (isSelected) {
                    setForeground(JBColor.LIGHT_GRAY);
                } else {
                    setBackground(JBColor.LIGHT_GRAY);
                }
            }
            if (archetypeCatalogModel.getType() == ArchetypeCatalogType.PROJECT) {
                if (isSelected) {
                    setForeground(JBColor.LIGHT_GRAY);
                } else {
                    setBackground(JBColor.LIGHT_GRAY);
                }
            }
            return c;
        }
    }
}
