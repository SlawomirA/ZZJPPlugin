package com.example.base64;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;

public class Utils {
    /**
     * Refreshes project pane after creating file
     * @param project
     * @param outputFile
     */
    public static void refreshProjectPane(Project project, File outputFile) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outputFile);
        if (virtualFile != null) {
            VirtualFileManager.getInstance().syncRefresh();
            virtualFile.refresh(false, true);
            project.getBaseDir().refresh(false, true);
        }
    }

    /**
     * Retrieves base64 String from clipboard and returns it.
     * @return base64 string if the clipboard contains string, "" otherwise
     */
    public static String retrieveBase64StringFromClipboard() {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null); // Get the content from the clipboard
        // Check if the clipboard has data
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return ((String) transferable.getTransferData(DataFlavor.stringFlavor))
                        .replace(" ","").replace("\n",""); // Return base64 string
            } catch (Exception e) {
                return "";
            }
        }
        else return "";
    }

    /**
     * Returns path to the current project
     * @param e
     * @return Path to the current project
     */
    public static String retrieveProjectPath(AnActionEvent e){
        return e.getProject() != null ? e.getProject().getBasePath() : System.getProperty("user.dir");
    }
}
