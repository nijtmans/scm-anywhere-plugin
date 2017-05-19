package org.jenkinsci.plugins.scmanywhere;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.digester.Digester;
import org.jenkinsci.plugins.scmanywhere.SCMChangeLogEntry.ModifiedFile;
import org.xml.sax.SAXException;
import com.thoughtworks.xstream.io.StreamException;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogParser;
import hudson.util.AtomicFileWriter;
import hudson.util.Digester2;
import hudson.util.XStream2;

class SCMChangeLogParser extends ChangeLogParser {
    private static String date = "yyyy-MM-dd HH:mm:ss";
    private static String changeSetID;
    private static String author = "";
    private static String comment = "";
    private static String filename;

    @SuppressWarnings("rawtypes")
    @Override
    public SCMChangeSet parse(final AbstractBuild build, final File changelogFile) throws IOException, SAXException {

        Digester digester = new Digester2();
        ArrayList<SCMChangeLogEntry> logEntry = new ArrayList<SCMChangeLogEntry>();
        digester.push(logEntry);
        digester.addObjectCreate("*/changeset", SCMChangeLogEntry.class);
        digester.addSetProperties("*/changeset");
        digester.addSetProperties("*/changeset", "author", "user");
        digester.addBeanPropertySetter("*/changeset/msg");
        digester.addBeanPropertySetter("*/changeset/added");
        digester.addBeanPropertySetter("*/changeset/deleted");
        digester.addBeanPropertySetter("*/changeset/files");
        digester.addBeanPropertySetter("*/changeset/parents");
        digester.addBeanPropertySetter("*/changeset/parents Delete Purge");
        digester.addBeanPropertySetter("*/changeset/parents Delete");
        digester.addSetNext("*/changeset", "add");

        FileInputStream stream = null;
        InputStreamReader reader = null;
        stream = new FileInputStream(changelogFile);
        reader = new InputStreamReader(stream, "8859-1");
        digester.parse(reader);
        reader.close();
        stream.close();

        return new SCMChangeSet(build, logEntry);
    }

    /*
     * Create the change log xml file
     */
    public static List<SCMChangeLogEntry> generateChangeLog(File changelogFile, FilePath workspace,
            final SCMAnyWhereRevisionState previousState, ByteArrayOutputStream byteArray,
            ByteArrayOutputStream DetailbyteArray, TaskListener listener) throws IOException {

        final List<SCMChangeLogEntry> logs = new ArrayList<SCMChangeLogEntry>();
        int avoidContinue = 0;
        listener.getLogger().print(" Inside the generate ChangeLog\n");

        String loggingData = byteArray.toString("8859-1");
        String[] splitLines = loggingData.split("\n");
        if (splitLines.length != 0) {

            List<ModifiedFile> modifiesFiles = null;
            char action = 0;
            String dateTimeDetailFile = "";

            for (String line : splitLines) {
                if (line.length() < 1)
                    continue;
                String[] split = line.split("\\s+");

                if (split.length != 0) {
                    if (split[0].matches("-?\\d+(\\.\\d+)?")) {
                        modifiesFiles = new ArrayList<ModifiedFile>();
                        changeSetID = split[0];
                        // if (changeSetID.equals(previousState.getRevNo())) {
                        // break;
                        // }
                        author = split[1];
                        date = split[2] + " " + split[3];
                        comment = "";
                        for (int i = 4; i < split.length; i++) {
                            comment = comment + " " + split[i];
                        }

                        /* Get modified file list */
                        String loggingFile = DetailbyteArray.toString("8859-1");
                        String[] splitLoggingLines = loggingFile.split("\n");
                        if (splitLoggingLines.length != 0) {
                            for (String lineDetail : splitLoggingLines) {
                                if (lineDetail.length() < 1)
                                    continue;
                                String[] splitDetail = lineDetail.split("\\s+");
                                if (splitDetail.length >= 5) {
                                    int getLen = splitDetail.length;
                                    if (splitDetail[1].matches("Name") && splitDetail[2].matches("User")
                                            && splitDetail[3].matches("Date") && splitDetail[4].matches("Action")) {
                                        continue;
                                    }

                                    String delPurge = splitDetail[getLen - 2] + splitDetail[getLen - 1];
                                    String delParnet = splitDetail[getLen - 2] + " " + splitDetail[getLen - 1];
                                    String parentDelPurge = splitDetail[getLen - 3] + splitDetail[getLen - 2]
                                            + splitDetail[getLen - 1];

                                    if (delParnet.equals("Parent Deleted")) {
                                        filename = splitDetail[getLen - 6];
                                        author = splitDetail[getLen - 5];
                                        dateTimeDetailFile = splitDetail[getLen - 3] + " " + splitDetail[getLen - 4];
                                        action = 'P';
                                    }
                                    if (parentDelPurge.equals("ParentDeleted,Purged")) {
                                        filename = splitDetail[getLen - 7];
                                        author = splitDetail[getLen - 6];
                                        dateTimeDetailFile = splitDetail[getLen - 4] + " " + splitDetail[getLen - 5];
                                        action = 'G';
                                    }
                                    if (delPurge.equals("Deleted,Purged") && !parentDelPurge.equals("ParentDeleted,Purged")) {
                                        filename = splitDetail[getLen - 6];
                                        author = splitDetail[getLen - 5];
                                        dateTimeDetailFile = splitDetail[getLen - 3] + " " + splitDetail[getLen - 4];
                                        action = 'U';
                                    }

                                    if ("Edited".equals(splitDetail[getLen - 1]))
                                        action = 'E';
                                    else if ("Added".equals(splitDetail[getLen - 1]))
                                        action = 'A';
                                    else if ("Created".equals(splitDetail[getLen - 1]))
                                        action = 'C';

                                    if (action != 0) {
                                        filename = splitDetail[getLen - 5];
                                        author = splitDetail[getLen - 4];
                                        dateTimeDetailFile = splitDetail[getLen - 3] + " " + splitDetail[getLen - 2];
                                    }
                                    delPurge = "";
                                    delParnet = "";
                                    parentDelPurge = "";

                                    if (dateTimeDetailFile.equals(date)) {
                                        ModifiedFile file = new ModifiedFile(filename, action);
                                        modifiesFiles.add(file);
                                    }

                                } else
                                    continue;
                            }
                            SCMChangeLogEntry nc = new SCMChangeLogEntry(changeSetID, author, date, comment, modifiesFiles);
                            logs.add(nc);
                        }
                    }
                } else {
                    if (avoidContinue > 5) {
                        avoidContinue++;
                        continue;
                    }

                }
            }
        }
        return logs;
    }

    /**
     * Generate a change log file containing the differences between one build and the next and save the result as XML in a
     * specified file.
     *
     * @param previousState The previous state of the repository
     * @param changelogFile The file in which we will store the set of differences between the two states
     * @param workspace The FilePath of the workspace to use when computing differences. This path might be on a slave machine.
     * @param byteArray The project logging details
     * @param DetailbyteArray The project logging details with files
     * @throws IOException is thrown if we have problems writing to the changelogFile
     * @throws InterruptedException is thrown if we are interrupted while waiting on the git commands to run in a forked process.
     */
    public static void saveChangeLog(final SCMAnyWhereRevisionState previousState, final File changelogFile,
            final FilePath workspace, ByteArrayOutputStream byteArray, ByteArrayOutputStream DetailbyteArray,
            TaskListener listener) throws IOException, InterruptedException {

        listener.getLogger().print(" Inside the save change log\n");
        List<SCMChangeLogEntry> logs = generateChangeLog(changelogFile, workspace, previousState, byteArray,
                DetailbyteArray, listener);

        final XStream2 xs = new XStream2();
        final AtomicFileWriter w = new AtomicFileWriter(changelogFile);
        try {
            w.write("<?xml version='1.0' encoding='UTF-8'?>\n");
            w.write("<Changelog>");
            for (SCMChangeLogEntry log : logs) {
                w.write("\n\t<Changeset version=\"" + log.getChangeSetId() + "\">");
                w.write("\n\t\t<Date>" + log.getDateTime() + "</Date>");
                w.write("\n\t\t<User>" + log.getUser() + "</User>");
                w.write("\n\t\t<Comment>" + log.getComments() + "</Comment>");
                w.write("\n\t\t\t<ModifiedFiles>");
                for (SCMChangeLogEntry.ModifiedFile modifiedFile : log.getAffectedFiles()) {
                    w.write("\n\t\t\t\t<File action=\"" + modifiedFile.getAction() + "\">" + modifiedFile.getPath() + "</File>");
                }
                w.write("\n\t\t\t</ModifiedFiles>");
                w.write("\n\t</Changeset>");
            }
            w.write("</Changelog>");

            xs.toXML(w);
            w.commit();
        } catch (final StreamException e) {
            throw new IOException(e);
        } finally {
            w.close();
        }
    }
}
