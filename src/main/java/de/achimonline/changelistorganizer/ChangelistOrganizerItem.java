package de.achimonline.changelistorganizer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangelistOrganizerItem implements Serializable {
    private boolean enabled;
    private String changeListName;
    private String filePattern;
    private boolean checkFullPath;
    private boolean confirmationDialog;
}
