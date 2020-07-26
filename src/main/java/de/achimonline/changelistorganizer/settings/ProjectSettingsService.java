package de.achimonline.changelistorganizer.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ChangelistOrganizerProjectSettings", storages = {@Storage("changelistorganizer_project.xml")})
public class ProjectSettingsService implements PersistentStateComponent<ProjectSettings> {

    ProjectSettings projectSettings = new ProjectSettings();

    @Nullable
    @Override
    public ProjectSettings getState() {
        return projectSettings;
    }

    @Override
    public void loadState(@NotNull ProjectSettings projectSettings) {
        this.projectSettings = projectSettings;
    }

}
