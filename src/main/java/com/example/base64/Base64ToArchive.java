package com.example.base64;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.example.base64.Utils.refreshProjectPane;
import static com.example.base64.Utils.retrieveBase64StringFromClipboard;
import static com.example.base64.Utils.retrieveProjectPath;

public class Base64ToArchive extends AnAction {



    @Override
    public void actionPerformed(AnActionEvent e) {
        String title, result;
        NotificationType notificationType = NotificationType.INFORMATION;

        try {
            //Get base64 string from clipboard
            String clipboardData = retrieveBase64StringFromClipboard();

            if(clipboardData.isEmpty()) {
                //Show hint with error info
                title = "Clipboard does not contain a string.";
                result = "Clipboard does not contain a string.";
                Notifications.Bus.notify(new Notification("Test Balloon Notification", title, result, NotificationType.ERROR));
                return;
            }

            //Decode base64 string
            byte[] decodedBytes = Base64.getDecoder().decode(clipboardData);


            // Save decoded bytes to a file in the project folder
            File outputFile = new File(retrieveProjectPath(e), "decoded_output.zip");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(decodedBytes);
                title = "Decoding successful";
                result = "Archive saved to: " + outputFile.getAbsolutePath();
            } catch (IOException ex) {
                ex.printStackTrace();
                //Show hint with error info
                title = "Error saving archive";
                result = "An error occurred while saving the archive:\n"+ex.getMessage();
                notificationType = NotificationType.ERROR;
            } finally {
                // Refresh the project pane
                refreshProjectPane(e.getProject(), outputFile);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            //Show hint with error info
            title = "Error decoding base64";
            result = "An error occurred while decoding Base64 data:\n"+exception.getMessage();
            notificationType = NotificationType.ERROR;
        }

        Notification notification = new Notification(
                "Test Balloon Notification",
                title,
                result,
                notificationType
        );

        Notifications.Bus.notify(notification);
    }

}
