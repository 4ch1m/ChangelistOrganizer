package de.achimonline.changelistorganizer;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

public class ChangelistOrganizerIcons {

    public static Icon get(String iconName) {
        return IconLoader.getIcon("/icons/" + iconName);
    }

}
