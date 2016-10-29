package de.achimonline.changelistorganizer.component;

import com.intellij.openapi.project.Project;
import de.achimonline.changelistorganizer.ChangelistOrganizerItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectSettings implements Serializable {
    private List<ChangelistOrganizerItem> changelistOrganizerItems = new ArrayList<ChangelistOrganizerItem>();
    boolean stopApplyingItemsAfterFirstMatch = true;
    boolean removeEmptyChangelists = false;

    public static ProjectSettings storedSettings(Project project) {
        ProjectSettingsComponent projectSettingsComponent = project.getComponent(ProjectSettingsComponent.class);

        if (projectSettingsComponent == null) {
            return new ProjectSettings();
        }

        return projectSettingsComponent.getState();
    }

    public List<ChangelistOrganizerItem> getChangelistOrganizerItems() {
        return changelistOrganizerItems;
    }

    public void setChangelistOrganizerItems(List<ChangelistOrganizerItem> changelistOrganizerItems) {
        this.changelistOrganizerItems = changelistOrganizerItems;
    }

    public boolean isStopApplyingItemsAfterFirstMatch() {
        return stopApplyingItemsAfterFirstMatch;
    }

    public void setStopApplyingItemsAfterFirstMatch(boolean stopApplyingItemsAfterFirstMatch) {
        this.stopApplyingItemsAfterFirstMatch = stopApplyingItemsAfterFirstMatch;
    }

    public boolean isRemoveEmptyChangelists() {
        return removeEmptyChangelists;
    }

    public void setRemoveEmptyChangelists(boolean removeEmptyChangelists) {
        this.removeEmptyChangelists = removeEmptyChangelists;
    }
}
