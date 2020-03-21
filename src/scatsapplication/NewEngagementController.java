/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static scatsapplication.Consultant.validateConsultant;
import static scatsapplication.Engagement.validateEngagement;

import static scatsapplication.ConsultantCollection.getConsultantCollection;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.sqlNewEngagement;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class NewEngagementController implements Initializable {
    @FXML private ChoiceBox<String> consultantAssignedChoice;
    @FXML private TextField locationField;
    @FXML private TextField companyField;
    @FXML private ChoiceBox<String> engagementTypeChoice;
    @FXML private DatePicker startDateField;
    @FXML private DatePicker endDateField;
    @FXML private TextArea descriptionField;
    @FXML private TableView documentationTable;
    @FXML private Button saveButton;
    
    /***
    * Name: resolveIDFromConsultant()
    * Description: Returns CID based on consultantName lookup
    * @param  - Action Event
    * @return - int:CID
    */      
    private int resolveIDFromConsultant(String consultantName) {
        ObservableList<Consultant> consultants = getConsultantCollection();
        int CID = 0;
        for (Consultant consultant : consultants) {
            if (consultant.getName().equals(consultantName)) {
                CID = consultant.getConsultantId();
            }
        }
        return CID;
    } 

    
    /***
    * Name: getActiveConsultants()
    * Description: Queues up all active consultants
    * @param  - Action Event
    * @return - ObservableList<String> of active consultantNames
    */      
    private ObservableList<String> getActiveConsultants(){
        ObservableList<Consultant> consultants = getConsultantCollection();
        ObservableList<String> consultantNames = FXCollections.observableArrayList();
        for (Consultant consultant : consultants) {
            consultantNames.add(consultant.getName());
        }
        return consultantNames;
    }
    
    /***
    * Name: resolveConsultantFromID()
    * Description: Returns consultantName based on CID lookup
    * @param  - Action Event
    * @return - String:consultantName
    */      
    private String resolveConsultantFromID(int CID) {
        ObservableList<Consultant> consultants = getConsultantCollection();
        String consultantName = "";
        for (Consultant consultant : consultants) {
            if (consultant.getConsultantId() == CID) {
                consultantName = consultant.getName();
            }
        }
        return consultantName;
    }
   
    
    /***
    * Name: saveNewEngagement()
    * Description: Saves new engagement to the database
    * @param  - Action Event
    * @return - None
    */  
    @FXML private void saveNewEngagement(ActionEvent event) {
        String eConsultant       = consultantAssignedChoice.getValue();
        String eLocation         = locationField.getText();
        String eCompany          = companyField.getText();
        String eType             = engagementTypeChoice.getValue();
        LocalDate eStartDate     = startDateField.getValue();
        LocalDate eEndDate       = endDateField.getValue();
        String eDescription      = descriptionField.getText();
        //documentationTable;
        //Validate Engagement
        //SQL Call
       
        String errorMessages = validateEngagement(eLocation, eCompany, eType, eStartDate, eEndDate);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("NOTICE");
            alert.setHeaderText("Required Data Missing");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }         
        sqlNewEngagement(resolveIDFromConsultant(eConsultant), eLocation, eCompany, eType, "New", eDescription, eStartDate, eEndDate);                                        
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
        consultantAssignedChoice.setItems(getActiveConsultants());
        engagementTypeChoice.setItems(FXCollections.observableArrayList("Web App Test", "Network Test", "Adversarial", "Development", "Research", "Training"));
        
        // Set a list of engagement types
    }    
    
}
