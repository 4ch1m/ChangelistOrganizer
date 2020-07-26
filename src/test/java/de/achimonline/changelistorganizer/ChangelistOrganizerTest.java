package de.achimonline.changelistorganizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.util.containers.EmptyIterator;
import de.achimonline.changelistorganizer.settings.ProjectSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import javax.swing.Icon;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({ProjectSettings.class, ChangeListManager.class, ChangelistOrganizerIcons.class, Messages.class})
public class ChangelistOrganizerTest {

    // *************************************************************************************
    private static final boolean ENABLED_1 = true;
    private static final String CHANGELIST_NAME_1 = "images";
    private static final String FILE_PATTERN_1 = "*.jp*g";
    private static final boolean CHECK_FULL_PATH_1 = false;
    private static final boolean CONFIRMATION_DIALOG_1 = false;
    // ---
    private static final String FILE_NAME_1 = "test.jpg";
    private static final String FILE_PATH_1 =  "/" + FILE_NAME_1;
    // *************************************************************************************
    private static final boolean ENABLED_2 = true;
    private static final String CHANGELIST_NAME_2 = "config";
    private static final String FILE_PATTERN_2 = "*.xml";
    private static final boolean CHECK_FULL_PATH_2 = false;
    private static final boolean CONFIRMATION_DIALOG_2 = true;
    // ---
    private static final String FILE_NAME_2 = "build.xml";
    private static final String FILE_PATH_2 = "/test/path/" + FILE_NAME_2;
    // *************************************************************************************
    private static final boolean ENABLED_3 = true;
    private static final String CHANGELIST_NAME_3 = "images";
    private static final String FILE_PATTERN_3 = "*.p?g";
    private static final boolean CHECK_FULL_PATH_3 = false;
    private static final boolean CONFIRMATION_DIALOG_3 = false;
    // ---
    private static final String FILE_NAME_3 = "test.png";
    private static final String FILE_PATH_3 = "/path/path/" + FILE_NAME_3;
    // *************************************************************************************
    private static final boolean ENABLED_4 = true;
    private static final String CHANGELIST_NAME_4 = "HTML-templates";
    private static final String FILE_PATTERN_4 = "*WEB-INF*.html";
    private static final boolean CHECK_FULL_PATH_4 = true;
    private static final boolean CONFIRMATION_DIALOG_4 = false;
    // ---
    private static final String FILE_NAME_4 = "test.html";
    private static final String FILE_PATH_4 = "/test/path/WEB-INF/templates/" + FILE_NAME_4;
    // *************************************************************************************
    private static final boolean ENABLED_5 = true;
    private static final String CHANGELIST_NAME_5 = "resources";
    private static final String FILE_PATTERN_5 = "*/resources/*";
    private static final boolean CHECK_FULL_PATH_5 = true;
    private static final boolean CONFIRMATION_DIALOG_5 = false;
    // ---
    private static final String FILE_NAME_5 = "test.properties";
    private static final String FILE_PATH_5 = "/test/resources/props/" + FILE_NAME_5;
    // *************************************************************************************

    @Mock
    private Project project;

    @Mock
    private ChangeListManager changeListManager;

    @Mock
    private LocalChangeList localChangeList;

    @Mock
    private List<LocalChangeList> localChangeLists;

    @Parameterized.Parameter(value = 0)
    public List<ChangelistOrganizerItem> changelistOrganizerItems;

    @Parameterized.Parameter(value = 1)
    public List<VirtualFile> virtualFiles;

    @Parameterized.Parameter(value = 2)
    public boolean allFilesOnDefaultChangelist;

    @Parameterized.Parameter(value = 3)
    public boolean stopApplyingItemsAfterFirstMatch;

    @Parameterized.Parameter(value = 4)
    public boolean onlyApplyItemsOnDefaultChangelist;

    @Parameterized.Parameter(value = 5)
    public boolean removeEmptyChangelists;

    @Parameterized.Parameter(value = 6)
    public int confirmationDialogResult;

    @Parameterized.Parameter(value = 7)
    public int expectedNumberOfCalls;

    @Before
    public void setUp() {
        mockStatic(ProjectSettings.class);
        mockStatic(ChangeListManager.class);
        mockStatic(ChangelistOrganizerIcons.class);
        mockStatic(Messages.class);

        when(ChangeListManager.getInstance(project)).thenReturn(changeListManager);
        when(ChangelistOrganizerIcons.get(anyString())).thenReturn(null);
        when(changeListManager.getChangeList(any(VirtualFile.class))).thenReturn(localChangeList);
        when(changeListManager.getChangeLists()).thenReturn(Collections.singletonList(localChangeList));
        when(changeListManager.addChangeList(anyString(), anyString())).thenReturn(localChangeList);
        doNothing().when(changeListManager).moveChangesTo(any(LocalChangeList.class), any(Change.class));
    }

    @Test
    public void testOrganize() {
        ProjectSettings projectSettings = new ProjectSettings();
        projectSettings.setChangelistOrganizerItems(changelistOrganizerItems);
        projectSettings.setStopApplyingItemsAfterFirstMatch(stopApplyingItemsAfterFirstMatch);
        projectSettings.setOnlyApplyItemsOnDefaultChangelist(onlyApplyItemsOnDefaultChangelist);
        projectSettings.setRemoveEmptyChangelists(removeEmptyChangelists);

        when(ProjectSettings.storedSettings(project)).thenReturn(projectSettings);
        when(changeListManager.getAffectedFiles()).thenReturn(virtualFiles);
        when(changeListManager.getChangeLists()).thenReturn(localChangeLists);
        when(localChangeList.isDefault()).thenReturn(allFilesOnDefaultChangelist);
        when(localChangeList.getName()).thenReturn(UUID.randomUUID().toString());
        when(localChangeLists.iterator()).thenReturn(EmptyIterator.getInstance());
        when(Messages.showOkCancelDialog(any(Project.class), anyString(), anyString(), anyString(), anyString(), any(Icon.class))).thenReturn(confirmationDialogResult);

        ChangelistOrganizer.organize(project);

        verify(changeListManager, times(expectedNumberOfCalls)).moveChangesTo(any(LocalChangeList.class));

        if (removeEmptyChangelists) {
            verify(localChangeLists).iterator();
        } else {
            verify(localChangeLists, never()).iterator();
        }
    }

    private static ChangelistOrganizerItem buildChanglistOrganizerItem(boolean enabled, String changeListName, String filePattern, boolean checkFullPath, boolean confirmationDialog) {
        ChangelistOrganizerItem changelistOrganizerItem = new ChangelistOrganizerItem();
        changelistOrganizerItem.setEnabled(enabled);
        changelistOrganizerItem.setChangeListName(changeListName);
        changelistOrganizerItem.setFilePattern(filePattern);
        changelistOrganizerItem.setCheckFullPath(checkFullPath);
        changelistOrganizerItem.setConfirmationDialog(confirmationDialog);

        return changelistOrganizerItem;
    }

    private static List<ChangelistOrganizerItem> buildChangelistOrganizerItems() {
        return Arrays.asList(buildChanglistOrganizerItem(ENABLED_1, CHANGELIST_NAME_1, FILE_PATTERN_1, CHECK_FULL_PATH_1, CONFIRMATION_DIALOG_1),
                             buildChanglistOrganizerItem(ENABLED_2, CHANGELIST_NAME_2, FILE_PATTERN_2, CHECK_FULL_PATH_2, CONFIRMATION_DIALOG_2),
                             buildChanglistOrganizerItem(ENABLED_3, CHANGELIST_NAME_3, FILE_PATTERN_3, CHECK_FULL_PATH_3, CONFIRMATION_DIALOG_3),
                             buildChanglistOrganizerItem(ENABLED_4, CHANGELIST_NAME_4, FILE_PATTERN_4, CHECK_FULL_PATH_4, CONFIRMATION_DIALOG_4),
                             buildChanglistOrganizerItem(ENABLED_5, CHANGELIST_NAME_5, FILE_PATTERN_5, CHECK_FULL_PATH_5, CONFIRMATION_DIALOG_5));
    }

    private static List<ChangelistOrganizerItem> buildChangelistOrganizerItems_twoItemsDisabled() {
        List<ChangelistOrganizerItem> changelistOrganizerItems = buildChangelistOrganizerItems();
        changelistOrganizerItems.get(0).setEnabled(false);
        changelistOrganizerItems.get(2).setEnabled(false);

        return changelistOrganizerItems;
    }

    private static List<VirtualFile> buildVirtualFiles() {
        List<VirtualFile> virtualFiles = new ArrayList<>();
        virtualFiles.add(createVirtualFile(FILE_NAME_1, FILE_PATH_1));
        virtualFiles.add(createVirtualFile(FILE_NAME_2, FILE_PATH_2));
        virtualFiles.add(createVirtualFile(FILE_NAME_3, FILE_PATH_3));
        virtualFiles.add(createVirtualFile(FILE_NAME_4, FILE_PATH_4));
        virtualFiles.add(createVirtualFile(FILE_NAME_5, FILE_PATH_5));

        return virtualFiles;
    }

    private static VirtualFile createVirtualFile(final String name, final String path) {
        return new VirtualFile() {
            @NotNull
            @Override
            public String getName() {
                return name;
            }

            @NotNull
            @Override
            public VirtualFileSystem getFileSystem() {
                return null;
            }

            @NotNull
            @Override
            public String getPath() {
                return path;
            }

            @Override
            public boolean isWritable() {
                return false;
            }

            @Override
            public boolean isDirectory() {
                return false;
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public VirtualFile getParent() {
                return null;
            }

            @Override
            public VirtualFile[] getChildren() {
                return new VirtualFile[0];
            }

            @NotNull
            @Override
            public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) {
                return null;
            }

            @NotNull
            @Override
            public byte[] contentsToByteArray() {
                return new byte[0];
            }

            @Override
            public long getTimeStamp() {
                return 0;
            }

            @Override
            public long getLength() {
                return 0;
            }

            @Override
            public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
            }

            @Override
            public InputStream getInputStream() {
                return null;
            }
        };
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // changelistOrganizerItems,                         virtualFiles,        allFilesOnDefaultChangelist, stopApplyingItemsAfterFirstMatch, onlyApplyItemsOnDefaultChangelist, removeEmptyChangelists, confirmationDialogResult, expectedNumberOfCalls
                {  buildChangelistOrganizerItems(),                  buildVirtualFiles(), true,                        true,                             true,                              true,                   Messages.OK,              5 },
                {  buildChangelistOrganizerItems(),                  buildVirtualFiles(), true,                        true,                             true,                              false,                  Messages.OK,              5 },
                {  buildChangelistOrganizerItems(),                  buildVirtualFiles(), false,                       true,                             true,                              true,                   Messages.OK,              0 },
                {  buildChangelistOrganizerItems(),                  buildVirtualFiles(), false,                       true,                             false,                             true,                   Messages.OK,              5 },
                {  buildChangelistOrganizerItems(),                  buildVirtualFiles(), true,                        false,                            true,                              true,                   Messages.OK,              5 },
// FIXME - the static mock of "Message.class" doesn't seem to work with 2.0.0-RC.3 anymore :-(
//                {  buildChangelistOrganizerItems(),                  buildVirtualFiles(), true,                        true,                             true,                              true,                   Messages.CANCEL,          4 },
                {  buildChangelistOrganizerItems_twoItemsDisabled(), buildVirtualFiles(), true,                        true,                             true,                              true,                   Messages.OK,              3 }
        });
    }
}
