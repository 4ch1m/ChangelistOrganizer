package de.achimonline.changelistorganizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import de.achimonline.changelistorganizer.settings.ProjectSettings;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.Icon;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangelistOrganizerTest {
    @Mock
    private Project project;

    @Mock
    private ChangeListManager changeListManager;

    @Mock
    private LocalChangeList localChangeList;

    @Mock
    private Change change;

    @Test
    public void organize() {
        for (TestData testData : buildTestData()) {
            ProjectSettings projectSettings = new ProjectSettings();
            projectSettings.setChangelistOrganizerItems(testData.getChangelistOrganizerItems());
            projectSettings.setStopApplyingItemsAfterFirstMatch(testData.isStopApplyingItemsAfterFirstMatch());
            projectSettings.setOnlyApplyItemsOnDefaultChangelist(testData.isOnlyApplyItemsOnDefaultChangelist());
            projectSettings.setRemoveEmptyChangelists(testData.isRemoveEmptyChangelists());

            try (
                    MockedStatic<ProjectSettings> projectSettingsMockedStatic = mockStatic(ProjectSettings.class);
                    MockedStatic<ChangeListManager> changeListManagerMockedStatic = mockStatic(ChangeListManager.class);
                    MockedStatic<Messages> messagesMockedStatic = mockStatic(Messages.class, CALLS_REAL_METHODS);
            ) {
                projectSettingsMockedStatic.when(() -> ProjectSettings.storedSettings(any(Project.class))).thenReturn(projectSettings);
                changeListManagerMockedStatic.when(() -> ChangeListManager.getInstance(any(Project.class))).thenReturn(changeListManager);
                messagesMockedStatic.when(() -> Messages.showOkCancelDialog(any(Project.class), anyString(), anyString(), anyString(), anyString(), any(Icon.class))).thenReturn(testData.getConfirmationDialogResult());

                when(changeListManager.getAffectedFiles()).thenReturn(testData.files.stream().map(VirtualFileDummy::getVirtualFile).collect(Collectors.toList()));
                when(localChangeList.isDefault()).thenReturn(testData.isAllFilesOnDefaultChangelist());
                when(changeListManager.getChangeList(any(VirtualFile.class))).thenReturn(localChangeList);
                when(localChangeList.getName()).thenReturn(UUID.randomUUID().toString());
                when(changeListManager.addChangeList(anyString(), anyString())).thenReturn(localChangeList);
                when(changeListManager.getChangesIn(any(VirtualFile.class))).thenReturn(List.of(change));
                doNothing().when(changeListManager).moveChangesTo(any(LocalChangeList.class), any(Change.class));

                ChangelistOrganizer.organize(project);

                verify(changeListManager, times(testData.getExpectedNumberOfMoves())).moveChangesTo(any(LocalChangeList.class), any());

                if (testData.isRemoveEmptyChangelists()) {
                    verify(changeListManager).getChangeLists();
                } else {
                    verify(changeListManager, never()).getChangeLists();
                }

                reset(changeListManager);
            }
        }
    }

    public static List<TestData> buildTestData() {
        return List.of(
                //         | testFiles         | changelistOrganizerItems         | allFilesOnDefaultChangelist | stopApplyingItemsAfterFirstMatch | onlyApplyItemsOnDefaultChangelist | removeEmptyChangelists | confirmationDialogResult | expectedNumberOfMoves
                new TestData(buildTestFiles(),   buildChangelistOrganizerItems(),   true,                         true,                              true,                               true,                    Messages.OK,               5),
                new TestData(buildTestFiles(),   buildChangelistOrganizerItems(),   true,                         true,                              true,                               false,                   Messages.OK,               5),
                new TestData(buildTestFiles(),   buildChangelistOrganizerItems(),   false,                        true,                              true,                               true,                    Messages.OK,               0),
                new TestData(buildTestFiles(),   buildChangelistOrganizerItems(),   false,                        true,                              false,                              true,                    Messages.OK,               5),
                new TestData(buildTestFiles(),   buildChangelistOrganizerItems(),   true,                         false,                             true,                               true,                    Messages.OK,               5),
                new TestData(buildTestFiles(),   buildChangelistOrganizerItems(),   true,                         false,                             true,                               true,                    Messages.CANCEL,           4)
        );
    }

    private static List<ChangelistOrganizerItem> buildChangelistOrganizerItems() {
        return List.of(
            //                        | enabled | changelistName    | filePattern       | checkFullPath | confirmationDialog
            new ChangelistOrganizerItem(true,     "images",           "*.jp*g",           false,          false),
            new ChangelistOrganizerItem(true,     "config",           "*.xml",            false,          true),
            new ChangelistOrganizerItem(true,     "images",           "*.p?g",            false,          false),
            new ChangelistOrganizerItem(true,     "HTML-templates",   "*WEB-INF*.html",   true,           false),
            new ChangelistOrganizerItem(true,     "resources",        "*/resources/*",    true,           false),
            new ChangelistOrganizerItem(false,    "markdown",         "*.md",             true,           false)
        );
    }

    private static List<VirtualFileDummy> buildTestFiles() {
        return List.of(
            new VirtualFileDummy("test.jpg",        "/"),
            new VirtualFileDummy("build.xml",       "/test/path"),
            new VirtualFileDummy("test.png",        "/path/path"),
            new VirtualFileDummy("test.html",       "/test/path/WEB-INF/templates"),
            new VirtualFileDummy("test.properties", "/test/resources/props"),
            new VirtualFileDummy("README.md",       "/")
        );
    }

    @Data
    protected static class TestData implements Serializable {
        private final List<VirtualFileDummy> files;
        private final List<ChangelistOrganizerItem> changelistOrganizerItems;

        private final boolean allFilesOnDefaultChangelist;
        private final boolean stopApplyingItemsAfterFirstMatch;
        private final boolean onlyApplyItemsOnDefaultChangelist;
        private final boolean removeEmptyChangelists;
        private final int confirmationDialogResult;
        private final int expectedNumberOfMoves;
    }
}
