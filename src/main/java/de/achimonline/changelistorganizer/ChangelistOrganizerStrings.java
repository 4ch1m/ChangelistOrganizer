package de.achimonline.changelistorganizer;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public class ChangelistOrganizerStrings {
    private static Reference<ResourceBundle> ourBundle;

    @NonNls
    private static final String BUNDLE = "de.achimonline.changelistorganizer.changelistorganizer";

    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;

        if (ourBundle != null) {
            bundle = ourBundle.get();
        }

        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<>(bundle);
        }

        return bundle;
    }
}
