package de.achimonline.changelistorganizer.settings;

import com.intellij.openapi.project.Project;
import de.achimonline.changelistorganizer.ChangelistOrganizerItem;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectSettings implements Serializable {

    private List<ChangelistOrganizerItem> changelistOrganizerItems = new ArrayList<>();
    private boolean onlyApplyItemsOnDefaultChangelist = true;
    private boolean stopApplyingItemsAfterFirstMatch = true;
    private boolean removeEmptyChangelists = false;
    private boolean automaticallyOrganize = false;

    public static ProjectSettings storedSettings(Project project) {
        ProjectSettingsService projectSettingsService = project.getService(ProjectSettingsService.class);

        if (projectSettingsService == null) {
            return new ProjectSettings();
        }

        return projectSettingsService.getState();
    }

 }
