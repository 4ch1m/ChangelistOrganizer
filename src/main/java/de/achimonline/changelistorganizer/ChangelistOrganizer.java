package de.achimonline.changelistorganizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import de.achimonline.changelistorganizer.component.ProjectSettings;

import java.util.Iterator;

public class ChangelistOrganizer {
    public synchronized static void organize(Project project) {
        if (project == null) {
            return;
        }

        ProjectSettings projectSettings = ProjectSettings.storedSettings(project);
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);

        // iterate all affected files
        for (VirtualFile virtualFile : changeListManager.getAffectedFiles()) {
            // check if the settings tell us to only consider the default-changelist, and check if the file is in it
            if (projectSettings.isOnlyApplyItemsOnDefaultChangelist() && !changeListManager.getChangeList(virtualFile).isDefault()) {
                continue;
            }

            // apply every organizer-item on the file
            for (ChangelistOrganizerItem changelistOrganizerItem : projectSettings.getChangelistOrganizerItems()) {
                // check if item is enabled
                if (changelistOrganizerItem.isEnabled()) {
                    // check if file is already in target changelist
                    if (!changeListManager.getChangeList(virtualFile).getName().equals(changelistOrganizerItem.getChangeListName())) {
                        // check if target changelist-name is valid
                        if (changelistOrganizerItem.getChangeListName() != null && !changelistOrganizerItem.getChangeListName().trim().isEmpty()) {
                            // build regex-pattern from user-input-filepattern
                            String filenamePatternRegEx = changelistOrganizerItem.getFilePattern().replace(".", "\\.").replace("?", ".?").replace("*", ".*");
                            String compareValue = changelistOrganizerItem.isCheckFullPath() ? virtualFile.getPath() : virtualFile.getName();

                            // check if it matches
                            if (compareValue.matches(filenamePatternRegEx)) {
                                boolean performMove = true;

                                // check if we need to show a confirmation-dialog
                                if (changelistOrganizerItem.isConfirmationDialog()) {
                                    // show the confirmation-dialog and set the flag accordingly
                                    performMove = Messages.showOkCancelDialog(project, ChangelistOrganizerStrings.message("organize.confirmation.dialog.message", virtualFile.getName(), changelistOrganizerItem.getChangeListName()), ChangelistOrganizerStrings.message("organize.confirmation.dialog.title"), Messages.OK_BUTTON, Messages.CANCEL_BUTTON, ChangelistOrganizerIcons.get("icon_32x32.png")) == Messages.OK;
                                }

                                if (performMove) {
                                    LocalChangeList localChangeList = changeListManager.addChangeList(changelistOrganizerItem.getChangeListName(), ChangelistOrganizerStrings.message("organize.changelist.comment"));
                                    changeListManager.moveChangesTo(localChangeList, ArrayUtil.toObjectArray(changeListManager.getChangesIn(virtualFile), Change.class));
                                }

                                // skip all following items if the setting says so
                                if (projectSettings.isStopApplyingItemsAfterFirstMatch()) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // remove empty changelists
        if (projectSettings.isRemoveEmptyChangelists()) {
            Iterator<LocalChangeList> changeListIterator = changeListManager.getChangeLists().iterator();

            while (changeListIterator.hasNext()) {
                LocalChangeList changeList = changeListIterator.next();

                if (!changeList.isDefault() && changeList.getChanges().isEmpty()) {
                    changeListManager.removeChangeList(changeList);
                }
            }
        }
    }
}
