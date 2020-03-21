/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scatsapplication.Certification.validateCertification;
import static scatsapplication.CertificationCollection.getConstulantCertifications;
import static scatsapplication.DBManager.refreshCertifications;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.sqlNewCertification;
import static scatsapplication.DBManager.sqlUpdateCertification;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class UpdateCertificationController implements Initializable {
    @FXML private TextField certificationNameField;
    @FXML private TextField certificationAbbreviationField;
    @FXML private ChoiceBox<String> certificationTypeChoice;
    @FXML private TextField certificationLicenseNumber;
    @FXML private DatePicker startDateField;
    @FXML private ChoiceBox<String> certificationExpiryChoice;
    @FXML private Button saveButton;
    private Certification certification;
    private Stage thisStage;    
    /**
     * Initializes the controller class.
     */
    
    public UpdateCertificationController(Certification certification) {
        this.certification = certification;
        thisStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scatsapplication/UpdateCertification.fxml"));
            fxmlLoader.setController(this);
            thisStage.setScene(new Scene(fxmlLoader.load()));
            thisStage.setTitle("Update Certification");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showStage() {
        thisStage.show();
    }
    
    @FXML public void updateTableView(){
        refreshCertifications();
    }     

    
    /***
    * Name: updateCertification()
    * Description: Updates certification with changes expressed in the form
    * @param  - Action Event
    * @return - None
    */  
    //public static void sqlNewConsultant(String consultantName, String location, String description, LocalDate startDate) {
    @FXML private void updateCertification(ActionEvent event) {
        String cCertName        = certificationNameField.getText();
        String cAbbreviation    = certificationAbbreviationField.getText();
        String cType            = certificationTypeChoice.getValue();
        String cLicense         = certificationLicenseNumber.getText();
        LocalDate startDate     = startDateField.getValue();
        String cExpiration      = certificationExpiryChoice.getValue();        

        System.out.println("UpdateCertificationCTRL:updateCertification: " + certification.getCertificationId());
        String errorMessages = validateCertification(cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Data Invalid");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }          
        sqlUpdateCertification(certification.getCertificationId(), cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration);                                        
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        refreshEngagements();
        refreshConsultants();
        refreshCertifications();
        stage.close();
    }    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("UpdateCertificationCTRL:initialize: " + certification.getCertificationId());
        certificationTypeChoice.setItems(FXCollections.observableArrayList("Architecture", "Development", "Forensics", "Incident Response", "Pentesting", "Cyber Defense", "Management", "Privacy", "GRC"));
        certificationExpiryChoice.setItems(FXCollections.observableArrayList("1 Year", "2 Years", "3 Years", "5 Years", "Never"));
        
        certificationNameField.setText(certification.getName());
        certificationAbbreviationField.setText(certification.getAbbreviation());
        certificationTypeChoice.setValue(certification.getType());
        certificationLicenseNumber.setText(certification.getLicenseNumber());
        Date certificationStartDate = certification.getStartDate();
        Calendar calendarStart = Calendar.getInstance(TimeZone.getDefault());
        calendarStart.setTime(certificationStartDate);
        int cStartYear = calendarStart.get(Calendar.YEAR);
        int cStartMonth  = calendarStart.get(Calendar.MONTH) + 1;
        int cStartDay  = calendarStart.get(Calendar.DAY_OF_MONTH);
        LocalDate certificationLocalDateStart = LocalDate.of(cStartYear, cStartMonth, cStartDay);          
        startDateField.setValue(certificationLocalDateStart);
        certificationExpiryChoice.setValue(certification.getExpiration());

    }    
}
