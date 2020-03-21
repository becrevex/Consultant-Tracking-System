/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.refreshCertifications;
import static scatsapplication.DBManager.sqlUpdateConsultant;
import static scatsapplication.DBManager.sqlUpdateConsultantProfilePic;
import static scatsapplication.DBManager.deleteCertification;
import static scatsapplication.CertificationCollection.getConstulantCertifications;
import static scatsapplication.Consultant.validateConsultant;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class ViewConsultantController implements Initializable {
    @FXML private TextField consultantNameField;
    @FXML private TextField consultantLocationField;
    @FXML private DatePicker consultantHireDate;
    @FXML private TextArea consultantDescriptionField;
    @FXML private Button addCertificationButton;
    @FXML private Button saveConsultantButton;
    @FXML private ImageView consultantProfilePicImage;
    @FXML private Button imageLoadButton;

    @FXML private TableView<Certification> certificationTable;
    @FXML private TableColumn<Certification, String> certificationAbbreviationField;
    @FXML private TableColumn<Certification, String> certificationDescriptionField;    
    @FXML private TableColumn<Certification, String> certificationObtainedField;    
    @FXML private TableColumn<Certification, String> certificationExpiresField;
    
    private Consultant consultant;
    private Stage thisStage;
    /**
     * Initializes the controller class.
     */
    
    public ViewConsultantController(Consultant consultant) {
        this.consultant = consultant;
        thisStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scatsapplication/ViewConsultant.fxml"));
            fxmlLoader.setController(this);
            thisStage.setScene(new Scene(fxmlLoader.load()));
            thisStage.setTitle("View Consultant");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showStage() {
        thisStage.show();
    }    
    
    /***
    * Name: newProfileImage()
    * Description: Selects a local file for a profile pic
    * @param  - Action Event
    * @return - None
    */     
    public void newProfileImage(ActionEvent event) throws IOException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Images", "*.*"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("PNG", "*.png")
        );        
        File file = chooser.showOpenDialog(thisStage);
        if (file != null) {
            Image profile = new Image(file.toURI().toString());
            consultantProfilePicImage.setImage(profile);
            String filepath = file.getAbsolutePath();
            sqlUpdateConsultantProfilePic(consultant.getConsultantId(), filepath);
        }

    }

    @FXML public void updateTableView(){
        refreshEngagements();
        refreshConsultants();
        refreshCertifications();
        Platform.runLater(() -> certificationTable.setItems(getConstulantCertifications(consultant)));
    }   
    
    
    @FXML private void launchNewCertification(ActionEvent event) {
        NewCertificationController viewHandler = new NewCertificationController(consultant);
        viewHandler.showStage();  
        updateTableView();
    }

    /***
    * Name: updateConsultant()
    * Description: Saves new consultant to the database
    * @param  - Action Event
    * @return - None
    */  
    //public static void sqlNewConsultant(String consultantName, String location, String description, LocalDate startDate) {
    @FXML private void updateConsultant(ActionEvent event) {
        String eConsultant       = consultantNameField.getText();
        String eLocation         = consultantLocationField.getText();
        LocalDate eStartDate     = consultantHireDate.getValue();
        String eDescription      = consultantDescriptionField.getText();
        String errorMessages = validateConsultant(eConsultant, eLocation, eStartDate);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Data Invalid");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }
        sqlUpdateConsultant(consultant.getConsultantId(), eConsultant, eLocation, eDescription, eStartDate);                                        
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        refreshEngagements();
        refreshConsultants();        
        stage.close();
    }   
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        consultantNameField.setText(consultant.getName());
        consultantLocationField.setText(consultant.getLocation());
        consultantDescriptionField.setText(consultant.getDescription());
        Date consultantStartDate = consultant.getStartDate();
        Calendar calendarStart = Calendar.getInstance(TimeZone.getDefault());
        calendarStart.setTime(consultantStartDate);
        int cStartYear = calendarStart.get(Calendar.YEAR);
        int cStartMonth  = calendarStart.get(Calendar.MONTH) + 1;
        int cStartDay  = calendarStart.get(Calendar.DAY_OF_MONTH);
        LocalDate engagementLocalDateStart = LocalDate.of(cStartYear, cStartMonth, cStartDay);    
        consultantHireDate.setValue(engagementLocalDateStart);        

        // Load Profile Image If Available
        if (consultant.getProfilePic() != null) {
                InputStream is = null;
            try {           
                is = consultant.getProfilePic().getBinaryStream();
                Image image = new Image(is);
                consultantProfilePicImage.setImage(image);
            } catch (SQLException ex) {
                Logger.getLogger(ViewConsultantController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(ViewConsultantController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        certificationAbbreviationField.setCellValueFactory(cellData -> cellData.getValue().getAbbreviationProperty());  // Lambda expressions used to ensure values are assigned quickly, upon initialization
        certificationDescriptionField.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        certificationObtainedField.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
        certificationExpiresField.setCellValueFactory(cellData -> cellData.getValue().getExpirationProperty());
        certificationTable.setRowFactory(new Callback<TableView<Certification>, TableRow<Certification>>() {  
            @Override  
            public TableRow<Certification> call(TableView<Certification> tableView) {  
                final TableRow<Certification> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  

                final MenuItem updateCertification = new MenuItem("Update Certification");
                updateCertification.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Certification certification = certificationTable.getSelectionModel().getSelectedItem();
                        System.out.println("ViewConsultantCTRL:Initialize: " + certification.getCertificationId());
                        UpdateCertificationController viewHandler = new UpdateCertificationController(certification);
                        updateTableView();
                        viewHandler.showStage();

                        }
                    });

                
                final MenuItem deleteCertification = new MenuItem("Delete Certification");
                deleteCertification.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Certification certification = certificationTable.getSelectionModel().getSelectedItem();
                        if (deleteCertification(certification)) {
                            certificationTable.getItems().remove(row.getItem());
                        }
                    }
                });
                contextMenu.getItems().add(updateCertification);
                contextMenu.getItems().add(deleteCertification);  
                row.contextMenuProperty().bind(  
                        Bindings.when(row.emptyProperty())  
                        .then((ContextMenu)null)  
                        .otherwise(contextMenu)  
                );  
                return row;
            }
        });        
        updateTableView();
    }    
    
}
