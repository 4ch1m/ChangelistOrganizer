package de.achimonline.changelistorganizer.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import de.achimonline.changelistorganizer.ChangelistOrganizer;
import de.achimonline.changelistorganizer.ChangelistOrganizerIcons;
import de.achimonline.changelistorganizer.ChangelistOrganizerStrings;

public class SelectedChangelistPopupAction extends AnAction {
    public SelectedChangelistPopupAction() {
        super(ChangelistOrganizerStrings.message("action.popup.text"), ChangelistOrganizerStrings.message("action.popup.description"), ChangelistOrganizerIcons.get("icon_16x16.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        ChangelistOrganizer.organize(project);
    }
}
