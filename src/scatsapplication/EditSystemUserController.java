/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.sqlUpdateEngagement;
import static scatsapplication.DBManager.sqlUpdateUser;
import static scatsapplication.Engagement.validateEngagement;
import static scatsapplication.SystemUser.validateUser;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class EditSystemUserController implements Initializable {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button saveButton;
    private SystemUser sysuser;
    private Stage thisStage;
    /**
     * Initializes the controller class.
     */
    public EditSystemUserController (SystemUser sysuser) {
        this.sysuser = sysuser;
        thisStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scatsapplication/editSystemUser.fxml"));
            fxmlLoader.setController(this);
            thisStage.setScene(new Scene(fxmlLoader.load()));
            thisStage.setTitle("Update System User");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }        

    /***
    * Name: updateSystemUser()
    * Description: Updates the engagement with changes made to the form
    * @param  - Action Event
    * @return - None
    */  
    @FXML private void updateSystemUser(ActionEvent event) {
        String sysUsername = usernameField.getText();
        String sysPassword = passwordField.getText();

        String errorMessages = validateUser(this.sysuser, sysUsername, sysPassword);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Data Invalid");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }           
        sqlUpdateUser(sysuser.getUserIdProperty().get(), sysUsername, sysPassword);
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }      
    
    
    public void showStage() {
        thisStage.show();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usernameField.setText(sysuser.getUsername());
        passwordField.setText(sysuser.getPassword());
    }    
    
}
