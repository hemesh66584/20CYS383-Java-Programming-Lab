import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

abstract class File {
    private String fileName;
    private long fileSize;
    private String fileType;

    public File(String fileName, long fileSize, String fileType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public abstract void displayFileDetails();
}

class Document extends File {
    private String documentType;

    public Document(String fileName, long fileSize, String fileType, String documentType) {
        super(fileName, fileSize, fileType);
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @Override
    public void displayFileDetails() {
        System.out.println("Document: " + getFileName());
        System.out.println("Size: " + getFileSize() + " bytes");
        System.out.println("Type: " + getDocumentType());
    }
}

class Image extends File {
    private String resolution;

    public Image(String fileName, long fileSize, String fileType, String resolution) {
        super(fileName, fileSize, fileType);
        this.resolution = resolution;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public void displayFileDetails() {
        System.out.println("Image: " + getFileName());
        System.out.println("Size: " + getFileSize() + " bytes");
        System.out.println("Resolution: " + getResolution());
    }
}

class Video extends File {
    private String duration;

    public Video(String fileName, long fileSize, String fileType, String duration) {
        super(fileName, fileSize, fileType);
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public void displayFileDetails() {
        System.out.println("Video: " + getFileName());
        System.out.println("Size: " + getFileSize() + " bytes");
        System.out.println("Duration: " + getDuration());
    }
}

interface FileManager {
    void addFile(File file);
    void deleteFile(String fileName);
    void saveToFile();
    void loadFromFile();
    ArrayList<File> getFiles();
}

class FileManagerImpl implements FileManager {
    private ArrayList<File> files;

    public FileManagerImpl() {
        files = new ArrayList<>();
    }

    @Override
    public void addFile(File file) {
        files.add(file);
    }

    @Override
    public void deleteFile(String fileName) {
        File fileToDelete = null;
        for (File file : files) {
            if (file.getFileName().equals(fileName)) {
                fileToDelete = file;
                break;
            }
        }
        if (fileToDelete != null) {
            files.remove(fileToDelete);
            System.out.println("File deleted: " + fileName);
        } else {
            System.out.println("File not found: " + fileName);
        }
    }

    @Override
    public void saveToFile() {
        try (FileOutputStream fos = new FileOutputStream("file_details.txt");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(files);
            System.out.println("File details saved to file_details.txt");
        } catch (IOException e) {
            System.out.println("Error saving file details: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile() {
        try (FileInputStream fis = new FileInputStream("file_details.txt");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            files = (ArrayList<File>) ois.readObject();
            System.out.println("File details loaded from file_details.txt");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading file details: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<File> getFiles() {
        return files;
    }
}

class FileManagementSystemUI {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private FileManager fileManager;

    public FileManagementSystemUI() {
        fileManager = new FileManagerImpl();
        createUI();
    }

    private void createUI() {
        frame = new JFrame("File Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("File Name");
        tableModel.addColumn("File Size");
        tableModel.addColumn("File Type");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JTextField fileNameField = new JTextField(10);
        JTextField fileSizeField = new JTextField(10);
        JComboBox<String> fileTypeComboBox = new JComboBox<>(new String[]{"Text", "Image", "Video"});

        JButton addButton = new JButton("Add File");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = fileNameField.getText();
                long fileSize = Long.parseLong(fileSizeField.getText());
                String fileType = (String) fileTypeComboBox.getSelectedItem();

                if (fileType.equals("Text")) {
                    // Add text file
                    String documentType = ".txt";
                    Document document = new Document(fileName, fileSize, fileType, documentType);
                    fileManager.addFile(document);
                } else if (fileType.equals("Image")) {
                    // Add image file
                    String resolution = JOptionPane.showInputDialog(frame, "Enter the resolution:");
                    Image image = new Image(fileName, fileSize, fileType, resolution);
                    fileManager.addFile(image);
                } else if (fileType.equals("Video")) {
                    // Add video file
                    String duration = JOptionPane.showInputDialog(frame, "Enter the duration:");
                    Video video = new Video(fileName, fileSize, fileType, duration);
                    fileManager.addFile(video);
                }

                updateTable();
            }
        });

        panel.add(new JLabel("File Name:"));
        panel.add(fileNameField);
        panel.add(new JLabel("File Size:"));
        panel.add(fileSizeField);
        panel.add(new JLabel("File Type:"));
        panel.add(fileTypeComboBox);
        panel.add(addButton);

        JButton deleteButton = new JButton("Delete File");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String fileName = (String) table.getValueAt(selectedRow, 0);
                    fileManager.deleteFile(fileName);
                    updateTable();
                }
            }
        });
        panel.add(deleteButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTable();
            }
        });
        panel.add(refreshButton);

        frame.add(panel, BorderLayout.SOUTH);
    }

    public void updateTable() {
        tableModel.setRowCount(0);
        ArrayList<File> files = fileManager.getFiles();
        for (File file : files) {
            Object[] rowData = {file.getFileName(), file.getFileSize(), file.getFileType()};
            tableModel.addRow(rowData);
        }
    }

    public void show() {
        frame.setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        FileManagementSystemUI fileManagementSystemUI = new FileManagementSystemUI();
        fileManagementSystemUI.show();
    }
}
