package org.jenkinsci.plugins.scmanywhere;

import hudson.scm.EditType;
import hudson.scm.ChangeLogSet;

public class SCMAffectedFile implements ChangeLogSet.AffectedFile {

    private SCMChangeSet changeSet;
    private EditType editType;
    private String oldPath;
    private String path;
    private String fileId;

    public SCMAffectedFile(EditType editType, String oldPath, String path, String fileId) {
        this.editType = editType;
        this.oldPath = oldPath;
        this.path = path;
        this.fileId = fileId;
    }

    public void setChangeSet(SCMChangeSet changeSet) {
        this.changeSet = changeSet;
    }

    public SCMChangeSet getChangeSet() {
        return this.changeSet;
    }

    public EditType getEditType() {
        return this.editType;
    }

    public String getOldPath() {
        return this.oldPath;
    }

    public String getPath() {
        return this.path;
    }

    public String getFileId() {
        return this.fileId;
    }
}
