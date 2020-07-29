package de.achimonline.changelistorganizer;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangelistOrganizerItem implements Serializable {

    private boolean enabled;
    private String changeListName;
    private String filePattern;
    private boolean checkFullPath;
    private boolean confirmationDialog;

}
