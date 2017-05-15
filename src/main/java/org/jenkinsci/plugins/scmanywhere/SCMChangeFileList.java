package org.jenkinsci.plugins.scmanywhere;

public class SCMChangeFileList {

    public SCMChangeFileList(String filename, String action, String dateTime, String author) {
        super();
        this.filename = filename;
        this.action = action;
        this.dateTime = dateTime;
        this.author = author;
    }

    private String filename = "";
    private String action = "";
    private String dateTime;
    private String author = "";

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return filename + ":" + author + ":" + action + ":" + dateTime;
    }
}
