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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.example.base64.Utils.refreshProjectPane;
import static com.example.base64.Utils.retrieveBase64StringFromClipboard;
import static com.example.base64.Utils.retrieveProjectPath;

public class Base64ToZip extends AnAction {

    private void decodeBase64ToZip(String base64String, String outputPath) throws IOException {
        // Decode Base64 data
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        // Create a ByteArrayInputStream from the decoded bytes
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);

        // Create a ZipInputStream from the input stream
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            // Create the output directory if it doesn't exist
            File outputDirectory = new File(outputPath);
            outputDirectory.mkdirs();

            // Extract each entry in the ZIP file
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryPath = outputDirectory.getAbsolutePath() + File.separator + entry.getName();
                File entryFile = new File(entryPath);

                // Create parent directories if they don't exist
                entryFile.getParentFile().mkdirs();

                try (FileOutputStream outputStream = new FileOutputStream(entryFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

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

            // Decode Base64 data to ZIP
            decodeBase64ToZip(clipboardData, retrieveProjectPath(e) + "/output.zip");

            // Refresh the project pane
            refreshProjectPane(e.getProject(), new File(retrieveProjectPath(e) + "/output.zip"));

            title = "Decoding successful";
            result = "ZIP files saved. Path: "+retrieveBase64StringFromClipboard()+"/output.zip";

            } catch (IOException ex) {
                ex.printStackTrace();
                title = "Error processing ZIP files";
                result = "An error occurred while processing the ZIP files.";
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
