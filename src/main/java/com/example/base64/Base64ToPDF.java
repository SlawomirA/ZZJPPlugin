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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

import static com.example.base64.Utils.refreshProjectPane;
import static com.example.base64.Utils.retrieveBase64StringFromClipboard;
import static com.example.base64.Utils.retrieveProjectPath;

public class Base64ToPDF extends AnAction {
    private void decodeBase64ToPdf(String base64String, String outputPath) throws IOException {
        // Decode Base64 data
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        // Create a ByteArrayInputStream from the decoded bytes
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);

        // Create a PDDocument from the input stream
        PDDocument document = PDDocument.load(inputStream);

        // Close the input stream
        inputStream.close();

        // Save the document to a PDF file
        document.save(outputPath);

        // Close the document
        document.close();
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

                // Decode Base64 data to PDF
                decodeBase64ToPdf(clipboardData, retrieveProjectPath(e) + "/output.pdf");

                // Refresh the project pane
                refreshProjectPane(e.getProject(), new File(retrieveProjectPath(e) + "/output.pdf"));

                title = "Decoding successful";
                result = "Files saved.";

            } catch (IOException ex) {
                ex.printStackTrace();
                title = "Error processing files";
                result = "An error occurred while processing the files:\n"+ex.getMessage();
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
