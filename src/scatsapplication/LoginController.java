//************************************************************************
// CLASS: LoginController
// FILENAME: LoginController.java
//
// DESCRIPTION
//  Controller class for handling login.fxml hooks and events
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import static scatsapplication.DBManager.validateUser;
import static scatsapplication.DBManager.validatePass;


/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class LoginController implements Initializable {
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginStatusLabel;
    @FXML private Button submitButton;
    


    @FXML
    private void authenticateUser(ActionEvent event) {
        String password = loginPasswordField.getText();
        String username = loginUsernameField.getText();
        int UserID = validateUser(username);
        boolean validatePass = validatePass(UserID, password);
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please provide username and password.");
            return;
        } else if (UserID == 0){
            loginStatusLabel.setText("Username or password invalid.");
            return;
        } else if (validatePass == false) {
            loginStatusLabel.setText("Username or password invalid."); 
            return;
        } else {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("Main.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }


    @FXML public void onEnter(ActionEvent actionEvent) {
        String password = loginPasswordField.getText();
        String username = loginUsernameField.getText();
        int UserID = validateUser(username);
        boolean validatePass = validatePass(UserID, password);
        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please provide username and password.");
            return;
        } else if (UserID == 0){
            loginStatusLabel.setText("Username or password invalid.");
            return;
        } else if (validatePass == false) {
            loginStatusLabel.setText("Username or password invalid."); 
            return;
        } else {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("Main.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
