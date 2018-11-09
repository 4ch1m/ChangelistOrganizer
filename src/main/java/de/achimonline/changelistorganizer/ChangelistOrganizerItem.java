package de.achimonline.changelistorganizer;

import java.io.Serializable;

public class ChangelistOrganizerItem implements Serializable {
    private boolean enabled;
    private String changeListName;
    private String filePattern;
    private boolean checkFullPath;
    private boolean confirmationDialog;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getChangeListName() {
        return changeListName;
    }

    public void setChangeListName(String changeListName) {
        this.changeListName = changeListName;
    }

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public boolean isCheckFullPath() {
        return checkFullPath;
    }

    public void setCheckFullPath(boolean checkFullPath) {
        this.checkFullPath = checkFullPath;
    }

    public boolean isConfirmationDialog() {
        return confirmationDialog;
    }

    public void setConfirmationDialog(boolean confirmationDialog) {
        this.confirmationDialog = confirmationDialog;
    }
}
