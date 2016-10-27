package de.achimonline.changelistorganizer.component;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.table.JBTable;
import de.achimonline.changelistorganizer.ChangelistOrganizerItem;
import de.achimonline.changelistorganizer.ChangelistOrganizerStrings;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ProjectSettingsPane implements Disposable {
    private JPanel panel;
    private JBTable table;
    private JButton addButton;
    private JButton deleteButton;

    private TableModel tableModel = new TableModel(new ArrayList<ChangelistOrganizerItem>());

    public ProjectSettingsPane() {
        super();

        table.setModel(tableModel);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                deleteButton.setEnabled(table.getSelectedRowCount() > 0);
            }
        });

        addButton.setIcon(IconLoader.getIcon("/icons/add.png"));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!tableAlreadyContainsEmptyItem()) {
                    tableModel.getData().add(new ChangelistOrganizerItem());
                    tableModel.fireTableDataChanged();
                }
            }
        });

        deleteButton.setIcon(IconLoader.getIcon("/icons/delete.png"));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.getData().remove(table.getSelectedRow());
                tableModel.fireTableDataChanged();
            }
        });
    }

    public void setData(ProjectSettings projectSettings) {
        tableModel.getData().clear();
        tableModel.getData().addAll(projectSettings.getChangelistOrganizerItems() == null ? new ArrayList<ChangelistOrganizerItem>() : projectSettings.getChangelistOrganizerItems());
    }

    public void storeSettings(ProjectSettings projectSettings) {
        List<ChangelistOrganizerItem> cleansedChangelistOrganizerItems = new ArrayList<ChangelistOrganizerItem>();

        for (ChangelistOrganizerItem changelistOrganizerItem : tableModel.getData()) {
            if (changelistOrganizerItem.getChangeListName() != null && !changelistOrganizerItem.getChangeListName().trim().isEmpty()) {
                cleansedChangelistOrganizerItems.add(changelistOrganizerItem);
            }
        }

        projectSettings.setChangelistOrganizerItems(cleansedChangelistOrganizerItems);
    }

    @Override
    public void dispose() {
    }

    public JPanel getPanel() {
        return panel;
    }

    public boolean isModified() {
        return tableModel.isModified();
    }

    public static class TableModel extends AbstractTableModel
    {
        private final String[] columnNames = new String[] { ChangelistOrganizerStrings.message("settings.table.column.enabled"),
                                                            ChangelistOrganizerStrings.message("settings.table.column.changelist.name"),
                                                            ChangelistOrganizerStrings.message("settings.table.column.file.pattern"),
                                                            ChangelistOrganizerStrings.message("settings.table.column.check.full.path"),
                                                            ChangelistOrganizerStrings.message("settings.table.column.confirmation.dialog") };

        private java.util.List<ChangelistOrganizerItem> data = new ArrayList<ChangelistOrganizerItem>();
        private boolean modified = false;

        public TableModel(List<ChangelistOrganizerItem> data) {
            this.data.addAll(data);
        }

        public List<ChangelistOrganizerItem> getData() {
            return data;
        }

        public boolean isModified() {
            return modified;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ChangelistOrganizerItem rowChangelistOrganizerItem = data.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return rowChangelistOrganizerItem.isEnabled();
                case 1:
                    return rowChangelistOrganizerItem.getChangeListName() != null ? rowChangelistOrganizerItem.getChangeListName() : "";
                case 2:
                    return rowChangelistOrganizerItem.getFilePattern() != null ? rowChangelistOrganizerItem.getFilePattern() : "";
                case 3:
                    return rowChangelistOrganizerItem.isCheckFullPath();
                case 4:
                    return rowChangelistOrganizerItem.isConfirmationDialog();
            }

            throw new IllegalArgumentException();
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Object value = getValueAt(0, columnIndex);
            return (value != null ? value.getClass() : String.class);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
            if (newValue == null) {
                return;
            }

            ChangelistOrganizerItem rowChangelistOrganizerItem = data.get(rowIndex);

            Object oldValue = new Object();

            switch (columnIndex) {
                case 0:
                    oldValue = rowChangelistOrganizerItem.isEnabled();
                    rowChangelistOrganizerItem.setEnabled((Boolean) newValue);
                    break;
                case 1:
                    oldValue = rowChangelistOrganizerItem.getChangeListName();
                    rowChangelistOrganizerItem.setChangeListName((String) newValue);
                    break;
                case 2:
                    oldValue = rowChangelistOrganizerItem.getFilePattern();
                    rowChangelistOrganizerItem.setFilePattern((String) newValue);
                    break;
                case 3:
                    oldValue = rowChangelistOrganizerItem.isCheckFullPath();
                    rowChangelistOrganizerItem.setCheckFullPath((Boolean) newValue);
                    break;
                case 4:
                    oldValue = rowChangelistOrganizerItem.isConfirmationDialog();
                    rowChangelistOrganizerItem.setConfirmationDialog((Boolean) newValue);
                    break;
            }

            if (!objectValuesAreEqual(newValue, oldValue)) {
                modified = true;
            }
        }

        private boolean objectValuesAreEqual(Object value1, Object value2) {
            if (value1.getClass() == Boolean.class && value2.getClass() == Boolean.class) {
                return ((Boolean) value1).equals((Boolean) value2);
            }

            if (value1.getClass() == String.class && value2.getClass() == String.class) {
                return ((String) value1).equals((String) value2);
            }

            return false;
        }
    }

    private boolean tableAlreadyContainsEmptyItem() {
        boolean containsEmptyItem = false;

        for (ChangelistOrganizerItem changelistOrganizerItem : tableModel.getData()) {
            if (!changelistOrganizerItem.isEnabled() &
                (changelistOrganizerItem.getChangeListName() == null || changelistOrganizerItem.getChangeListName().trim().isEmpty()) &
                (changelistOrganizerItem.getFilePattern() == null || changelistOrganizerItem.getFilePattern().trim().isEmpty()) &
                !changelistOrganizerItem.isCheckFullPath() &
                !changelistOrganizerItem.isConfirmationDialog()) {
                containsEmptyItem = true;
                break;
            }
        }

        return containsEmptyItem;
    }
}
