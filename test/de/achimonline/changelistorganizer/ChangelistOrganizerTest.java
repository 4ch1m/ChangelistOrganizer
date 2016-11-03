package de.achimonline.changelistorganizer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import de.achimonline.changelistorganizer.component.ProjectSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProjectSettings.class, ChangeListManager.class})
public class ChangelistOrganizerTest {

    private static final boolean ENABLED_1 = true;
    private static final String CHANGELIST_NAME_1 = "images";
    private static final String FILE_PATTERN_1 = "*.jp*g";
    private static final String FILE_NAME_1 = "test.jpg";
    private static final String FILE_PATH_1 =  "/" + FILE_NAME_1;
    private static final boolean CHECK_FULL_PATH_1 = false;
    private static final boolean CONFIRMATION_DIALOG_1 = false;

    private static final boolean ENABLED_2 = false;
    private static final String CHANGELIST_NAME_2 = "config";
    private static final String FILE_PATTERN_2 = "*.xml";
    private static final String FILE_NAME_2 = "build.xml";
    private static final String FILE_PATH_2 = "/test/path/" + FILE_NAME_2;
    private static final boolean CHECK_FULL_PATH_2 = false;
    private static final boolean CONFIRMATION_DIALOG_2 = true;

    private static final boolean ENABLED_3 = true;
    private static final String CHANGELIST_NAME_3 = "images";
    private static final String FILE_PATTERN_3 = "*.p?g";
    private static final String FILE_NAME_3 = "test.png";
    private static final String FILE_PATH_3 = "/path/path/" + FILE_NAME_3;
    private static final boolean CHECK_FULL_PATH_3 = false;
    private static final boolean CONFIRMATION_DIALOG_3 = false;

    private static final boolean ENABLED_4 = true;
    private static final String CHANGELIST_NAME_4 = "HTML-templates";
    private static final String FILE_PATTERN_4 = "*WEB-INF*.html";
    private static final String FILE_NAME_4 = "test.html";
    private static final String FILE_PATH_4 = "/test/path/WEB-INF/templates/" + FILE_NAME_4;
    private static final boolean CHECK_FULL_PATH_4 = true;
    private static final boolean CONFIRMATION_DIALOG_4 = false;

    private static final boolean ENABLED_5 = true;
    private static final String CHANGELIST_NAME_5 = "resources";
    private static final String FILE_PATTERN_5 = "*/resources/*";
    private static final String FILE_NAME_5 = "test.properties";
    private static final String FILE_PATH_5 = "/test/resources/props/" + FILE_NAME_5;
    private static final boolean CHECK_FULL_PATH_5 = true;
    private static final boolean CONFIRMATION_DIALOG_5 = false;

    @Mock
    private Project project;

    @Mock
    private ChangeListManager changeListManager;

    @Mock
    private LocalChangeList localChangeList;

    @Before
    public void setUp() throws Exception {
        mockStatic(ProjectSettings.class);
        mockStatic(ChangeListManager.class);

        when(ChangeListManager.getInstance(project)).thenReturn(changeListManager);
        when(changeListManager.getChangeList(any(VirtualFile.class))).thenReturn(localChangeList);
        when(changeListManager.getChangeLists()).thenReturn(Arrays.asList(localChangeList));
        when(changeListManager.addChangeList(anyString(), anyString())).thenReturn(localChangeList);
        doNothing().when(changeListManager).moveChangesTo(any(LocalChangeList.class), any(Change.class));
    }

    @Test
    public void testOrganize() throws Exception {
        ProjectSettings projectSettings = buildProjectSettingsWithItems();
        projectSettings.setStopApplyingItemsAfterFirstMatch(true);
        projectSettings.setOnlyApplyItemsOnDefaultChangelist(true);
        projectSettings.setRemoveEmptyChangelists(false);

        List<VirtualFile> virtualFiles = buildVirtualFiles();

        when(ProjectSettings.storedSettings(project)).thenReturn(projectSettings);
        when(changeListManager.getAffectedFiles()).thenReturn(virtualFiles);
        when(localChangeList.isDefault()).thenReturn(true);
        when(localChangeList.getName()).thenReturn(UUID.randomUUID().toString());

        ChangelistOrganizer.organize(project);

        verify(changeListManager, times(4)).moveChangesTo(any(LocalChangeList.class));
    }

    /*
        TODO

        improve/complete tests for ...

        - global settings (stopApplyingItemsAfterFirstMatch, onlyApplyItemsOnDefaultChangelist, removeEmptyChangelists)
        - "checkFullPath" and "showConfirmationDialog"
        - general pattern matching
        - etc.
     */

    private ProjectSettings buildProjectSettingsWithItems() {
        ChangelistOrganizerItem changelistOrganizerItem1 = new ChangelistOrganizerItem();
        changelistOrganizerItem1.setEnabled(ENABLED_1);
        changelistOrganizerItem1.setChangeListName(CHANGELIST_NAME_1);
        changelistOrganizerItem1.setFilePattern(FILE_PATTERN_1);
        changelistOrganizerItem1.setCheckFullPath(CHECK_FULL_PATH_1);
        changelistOrganizerItem1.setConfirmationDialog(CONFIRMATION_DIALOG_1);

        ChangelistOrganizerItem changelistOrganizerItem2 = new ChangelistOrganizerItem();
        changelistOrganizerItem2.setEnabled(ENABLED_2);
        changelistOrganizerItem2.setChangeListName(CHANGELIST_NAME_2);
        changelistOrganizerItem2.setFilePattern(FILE_PATTERN_2);
        changelistOrganizerItem2.setCheckFullPath(CHECK_FULL_PATH_2);
        changelistOrganizerItem2.setConfirmationDialog(CONFIRMATION_DIALOG_2);

        ChangelistOrganizerItem changelistOrganizerItem3 = new ChangelistOrganizerItem();
        changelistOrganizerItem3.setEnabled(ENABLED_3);
        changelistOrganizerItem3.setChangeListName(CHANGELIST_NAME_3);
        changelistOrganizerItem3.setFilePattern(FILE_PATTERN_3);
        changelistOrganizerItem3.setCheckFullPath(CHECK_FULL_PATH_3);
        changelistOrganizerItem3.setConfirmationDialog(CONFIRMATION_DIALOG_3);

        ChangelistOrganizerItem changelistOrganizerItem4 = new ChangelistOrganizerItem();
        changelistOrganizerItem4.setEnabled(ENABLED_4);
        changelistOrganizerItem4.setChangeListName(CHANGELIST_NAME_4);
        changelistOrganizerItem4.setFilePattern(FILE_PATTERN_4);
        changelistOrganizerItem4.setCheckFullPath(CHECK_FULL_PATH_4);
        changelistOrganizerItem4.setConfirmationDialog(CONFIRMATION_DIALOG_4);

        ChangelistOrganizerItem changelistOrganizerItem5 = new ChangelistOrganizerItem();
        changelistOrganizerItem5.setEnabled(ENABLED_5);
        changelistOrganizerItem5.setChangeListName(CHANGELIST_NAME_5);
        changelistOrganizerItem5.setFilePattern(FILE_PATTERN_5);
        changelistOrganizerItem5.setCheckFullPath(CHECK_FULL_PATH_5);
        changelistOrganizerItem5.setConfirmationDialog(CONFIRMATION_DIALOG_5);

        ProjectSettings projectSettings = new ProjectSettings();
        projectSettings.setChangelistOrganizerItems(Arrays.asList(changelistOrganizerItem1, changelistOrganizerItem2, changelistOrganizerItem3, changelistOrganizerItem4, changelistOrganizerItem5));

        return projectSettings;
    }

    private List<VirtualFile> buildVirtualFiles() {
        List<VirtualFile> virtualFiles = new ArrayList<VirtualFile>();
        virtualFiles.add(createVirtualFile(FILE_NAME_1, FILE_PATH_1));
        virtualFiles.add(createVirtualFile(FILE_NAME_2, FILE_PATH_2));
        virtualFiles.add(createVirtualFile(FILE_NAME_3, FILE_PATH_3));
        virtualFiles.add(createVirtualFile(FILE_NAME_4, FILE_PATH_4));
        virtualFiles.add(createVirtualFile(FILE_NAME_5, FILE_PATH_5));

        return virtualFiles;
    }

    private VirtualFile createVirtualFile(final String name, final String path) {
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
            public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
                return null;
            }

            @NotNull
            @Override
            public byte[] contentsToByteArray() throws IOException {
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
            public InputStream getInputStream() throws IOException {
                return null;
            }
        };
    }

}
