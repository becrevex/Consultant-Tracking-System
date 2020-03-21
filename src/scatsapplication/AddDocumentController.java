/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static scatsapplication.DBManager.sqlNewReport;
import static scatsapplication.DBManager.sqlNewDocument;
import static scatsapplication.Report.validateReport;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class AddDocumentController implements Initializable {
    @FXML private Button addDocumentButton;
    @FXML private ChoiceBox<String> documentTypeChoice;
    @FXML private ChoiceBox<String> documentStatusChoice;
    @FXML private DatePicker publicationDate;
    @FXML private Button saveDocumentButton;
    @FXML private Label selectedFileLabel;
    private Stage thisStage;
    private Engagement engagement;
    private File currentFile;
    /**
     * Initializes the controller class.
     */

    public AddDocumentController(Engagement engagement) {
        this.engagement = engagement;
        System.out.println(engagement.getAssignmentId());
        thisStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scatsapplication/AddDocument.fxml"));
            fxmlLoader.setController(this);
            thisStage.setScene(new Scene(fxmlLoader.load()));
            thisStage.setTitle("Add New Document");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showStage() {
        thisStage.show();
    }     
    
    
    /***
    * Name: addNewDocument()
    * Description: Selects a local file for a profile pic
    * @param  - Action Event
    * @return - None
    */     
    public void addNewDocument(ActionEvent event) {
        FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Word Document", "*.docx"),
                new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"),
                new FileChooser.ExtensionFilter("Word 97-2003 Document", "*.doc"),
                new FileChooser.ExtensionFilter("Excel 97-2003 Workbook", "*.xls"),
                new FileChooser.ExtensionFilter("Text Document", "*.txt"));        
        this.currentFile = chooser.showOpenDialog(thisStage);
        if (this.currentFile != null) {
            String filePath = this.currentFile.toURI().toString();
            String fileName = this.currentFile.getName();
            selectedFileLabel.setText(fileName);
        }
    }    


    /***
    * Name: copyFileUsingStream()
    * Description: Copies one file source to another
    * @param  - Action Event
    * @return - None
    */      
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }    

    private String convertLocalDatetoFileSuffix(LocalDate ld){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = ld.format(formatter);
        return dateString;
    }
    

    private File createCompanyDirectoryAndCopy(String fileName) {
        Path currentRelativePath = Paths.get("Reports");
        String s = currentRelativePath.toAbsolutePath().toString();
        File dir = new File(s + "\\" + engagement.getCompany().replace(" ", "_"));
        
        File engagementDir = new File(dir.getAbsolutePath());
        if (!engagementDir.exists()) {
            engagementDir.mkdir();
        } 
        File newDocument = new File(dir.getAbsolutePath()+"\\"+fileName);
        try {
            copyFileUsingStream(this.currentFile, newDocument);
            return newDocument;
        } catch (Exception e) {
            e.printStackTrace();    
        }
        return null;
    }

    /***
    * Name: getFileCheckSum()
    * Description: Saves document attributes to database, and associates it with an engagement
    * @param  - Action Event
    * @return - None
    */     
    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0; 

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
       return sb.toString();
    }    
    
    /***
    * Name: getFileExtension()
    * Description: returns a filename extension using string handling
    * @param  - String filename
    * @return - String extension
    */ 
    public Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
    
    
    /***
    * Name: saveNewDocument()
    * Description: Saves document attributes to database, and associates it with an engagement
    * @param  - Action Event
    * @return - None
    */  
    @FXML private void saveNewDocument(ActionEvent event) throws IOException {
        String dType = documentTypeChoice.getValue();
        String dStatus = documentStatusChoice.getValue();
        LocalDate dDate = publicationDate.getValue();
        java.sql.Date createDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        String errorMessages = validateReport(this.currentFile.getPath(),
                                              dType,
                                              dStatus,
                                              dDate);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("NOTICE");
            alert.setHeaderText("Missing or Inaccurate Data Fields");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }        
        // Setup the destination file
        Optional<String> fileExtension = getFileExtension(this.currentFile.getName());
        String fExt = "."+fileExtension.get();
        
        String engagement_type = engagement.getType().replace(" ", "_");
        String dateString = convertLocalDatetoFileSuffix(dDate);
        String fileName = engagement.getCompany().replace(" ", "_")+"_"+ engagement_type + "_"+dType+"_"+dStatus+"_"+dateString + fExt;

                
        File newDocument = createCompanyDirectoryAndCopy(fileName);
        MessageDigest shaDigest = null;
        //Create checksum
        try {
            shaDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  
        }
        String checksum = getFileChecksum(shaDigest, newDocument);

        if (dType.equals("Report")) {
            sqlNewReport(engagement.getAssignmentId(), fileName, dType, dStatus, checksum, dDate);
        } else {
            sqlNewDocument(engagement.getAssignmentId(), fileName, dType, dStatus, checksum, dDate);
        }
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        //refreshEngagements();
        //refreshConsultants();        
        stage.close();
    } 
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        documentTypeChoice.setItems(FXCollections.observableArrayList("Report", "ROE", "SOW", "RAW"));
        documentStatusChoice.setItems(FXCollections.observableArrayList("Draft", "Final"));
    }    
    
}
