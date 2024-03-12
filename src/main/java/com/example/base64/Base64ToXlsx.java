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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.example.base64.Utils.refreshProjectPane;
import static com.example.base64.Utils.retrieveBase64StringFromClipboard;
import static com.example.base64.Utils.retrieveProjectPath;

public class Base64ToXlsx extends AnAction {

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

            // Decode Base64 data
            byte[] decodedBytes = Base64.getDecoder().decode(clipboardData);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            inputStream.close();

            // Save the workbook to a file
            String outputPath = retrieveProjectPath(e) + "/output.xlsx";
            FileOutputStream fileOut = new FileOutputStream(outputPath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            // Refresh the project pane
            refreshProjectPane(e.getProject(), new File(outputPath));

            title = "Decoding successful";
            result = "File saved to:" +outputPath;

            } catch (IOException ex) {
                ex.printStackTrace();
                title = "Error saving file";
                result = "An error occurred while saving the file.";
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
