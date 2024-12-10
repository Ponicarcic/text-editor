import java.io.*;
import java.nio.file.*;
import java.util.*;

public class startup {

    private static final String FILE_PATH = "/home/admin1/Downloads/Ponicarcic Victor W-2142 lab-1.docx"; // Change to your file location

    public static void main(String[] args) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                addToWindowsStartup();
                restartWindows();
            } else if (os.contains("nix") || os.contains("nux")) {
                addToLinuxStartup();
                restartLinux();
            } else {
                System.out.println("Unsupported operating system.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addToWindowsStartup() throws IOException {
        String command = String.format(
                "reg add HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v OpenDocumentAtStartup /t REG_SZ /d \"%s\" /f",
                FILE_PATH
        );
        Process process = Runtime.getRuntime().exec(command);
        try {
            process.waitFor();
            System.out.println("Added to Windows startup.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Failed to add to Windows startup.", e);
        }
    }

    private static void addToLinuxStartup() throws IOException {
        String serviceName = "open_document.service";
        String serviceContent = String.format(
                "[Unit]\n" +
                "Description=Open Document at Startup\n\n" +
                "[Service]\n" +
                "ExecStart=/usr/bin/xdg-open %s\n" +
                "Restart=always\n\n" +
                "[Install]\n" +
                "WantedBy=default.target\n",
                FILE_PATH
        );

        Path tempServiceFile = Files.createTempFile(serviceName, ".tmp");
        Files.write(tempServiceFile, serviceContent.getBytes());

        String moveCommand = String.format("sudo mv %s /etc/systemd/system/%s", tempServiceFile.toAbsolutePath(), serviceName);
        String enableCommand = String.format("sudo systemctl enable %s", serviceName);
        String startCommand = String.format("sudo systemctl start %s", serviceName);

        executeCommand(moveCommand);
        executeCommand(enableCommand);
        executeCommand(startCommand);

        System.out.println("Added to Linux startup.");
    }

    private static void restartWindows() throws IOException {
        String command = "shutdown /r /t 0";
        executeCommand(command);
    }

    private static void restartLinux() throws IOException {
        String command = "sudo reboot";
        executeCommand(command);
    }

    private static void executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Command execution interrupted: " + command, e);
        }
    }

}

