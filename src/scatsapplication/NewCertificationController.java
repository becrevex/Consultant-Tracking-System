/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import static scatsapplication.Certification.validateCertification;
import static scatsapplication.DBManager.deleteCertification;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.sqlNewConsultant;
import static scatsapplication.DBManager.sqlNewCertification;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class NewCertificationController implements Initializable {
    @FXML private TextField certificationNameField;
    @FXML private TextField certificationAbbreviationField;
    @FXML private ChoiceBox<String> certificationTypeChoice;
    @FXML private TextField certificationLicenseNumber;
    @FXML private DatePicker startDateField;
    @FXML private ChoiceBox<String> certificationExpiryChoice;
    @FXML private Button saveButton;
    

    private Consultant consultant;
    private Stage thisStage;    

    public NewCertificationController(Consultant consultant) {
        this.consultant = consultant;
        System.out.println(consultant.getConsultantId());
        thisStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scatsapplication/NewCertification.fxml"));
            fxmlLoader.setController(this);
            thisStage.setScene(new Scene(fxmlLoader.load()));
            thisStage.setTitle("New Certification");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }    
    
    public void showStage() {
        thisStage.show();
    }    
    
    
    /***
    * Name: saveNewCertification()
    * Description: Saves new certification to the database
    * @param  - Action Event
    * @return - None
    */  
    //public static void sqlNewConsultant(String consultantName, String location, String description, LocalDate startDate) {
    @FXML private void saveNewCertification(ActionEvent event) {
        String cCertName        = certificationNameField.getText();
        String cAbbreviation    = certificationAbbreviationField.getText();
        String cType            = certificationTypeChoice.getValue();
        String cLicense         = certificationLicenseNumber.getText();
        LocalDate startDate     = startDateField.getValue();
        String cExpiration      = certificationExpiryChoice.getValue();        
        //certificationTable;
        //Validate Consultant
        //SQL Call
       
        String errorMessages = validateCertification(cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Data Invalid");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }        
        sqlNewCertification(consultant.getConsultantId(), cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration);                                        
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        refreshEngagements();
        refreshConsultants();        
        stage.close();
    }    
        
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        certificationTypeChoice.setItems(FXCollections.observableArrayList("Architecture", "Development", "Forensics", "Incident Response", "Pentesting", "Cyber Defense", "Management", "Privacy", "GRC"));
        certificationExpiryChoice.setItems(FXCollections.observableArrayList("1 Year", "2 Years", "3 Years", "5 Years", "Never"));
       
        
    }    
    
}
