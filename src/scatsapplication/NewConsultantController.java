/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import static scatsapplication.Certification.validateCertification;
import static scatsapplication.Consultant.validateConsultant;
import static scatsapplication.DBManager.deleteCertification;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;

import static scatsapplication.DBManager.sqlNewConsultant;
import static scatsapplication.DBManager.sqlNewEngagement;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class NewConsultantController implements Initializable {
    @FXML private TextField consultantNameField;
    @FXML private TextField consultantLocationField;
    @FXML private DatePicker consultantHireDate;
    @FXML private TextArea consultantDescriptionField;
    @FXML private Button saveConsultantButton;
  
    /**
     * Initializes the controller class.
     */
    
    /***
    * Name: saveNewConsultant()
    * Description: Saves new consultant to the database
    * @param  - Action Event
    * @return - None
    */  
    //public static void sqlNewConsultant(String consultantName, String location, String description, LocalDate startDate) {
    @FXML private void saveNewConsultant(ActionEvent event) {
        String eConsultant       = consultantNameField.getText();
        String eLocation         = consultantLocationField.getText();
        LocalDate eStartDate     = consultantHireDate.getValue();
        String eDescription      = consultantDescriptionField.getText();
        //certificationTable;
        //Validate Consultant
        //SQL Call
        String errorMessages = validateConsultant(eConsultant, eLocation, eStartDate);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Data Invalid");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }          
        sqlNewConsultant(eConsultant, eLocation, eDescription, eStartDate);                                        
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        refreshEngagements();
        refreshConsultants();        
        stage.close();
    }    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         
    }    
    
}
