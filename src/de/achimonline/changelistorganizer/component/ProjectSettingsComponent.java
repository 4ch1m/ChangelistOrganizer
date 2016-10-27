package de.achimonline.changelistorganizer.component;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
  name = "ChangelistOrganizerProjectSettings",
  storages = { @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
               @Storage(id = "dir",     file = StoragePathMacros.PROJECT_CONFIG_DIR + "/changelistorganizer_project.xml", scheme = StorageScheme.DIRECTORY_BASED)}
)
public class ProjectSettingsComponent implements PersistentStateComponent<ProjectSettings>, ProjectComponent {
    private ProjectSettings projectSettings = new ProjectSettings();

    @Nullable
    @Override
    public ProjectSettings getState() {
        return projectSettings;
    }

    @Override
    public void loadState(ProjectSettings projectSettings) {
        this.projectSettings = projectSettings;
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
        this.projectSettings = null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ChangelistOrganizerProject";
    }
}
