package de.achimonline.changelistorganizer.component;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ChangelistOrganizerProjectSettings", storages = {@Storage("changelistorganizer_project.xml")})
public class ProjectSettingsComponent implements PersistentStateComponent<ProjectSettings>, ProjectComponent {
    private ProjectSettings projectSettings = new ProjectSettings();

    @Nullable
    @Override
    public ProjectSettings getState() {
        return projectSettings;
    }

    @Override
    public void loadState(@NotNull ProjectSettings projectSettings) {
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
