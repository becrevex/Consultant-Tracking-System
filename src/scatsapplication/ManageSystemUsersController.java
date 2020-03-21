/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import static scatsapplication.DBManager.deleteEngagement;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.refreshSystemUsers;
import static scatsapplication.DBManager.sqlDeactivateEngagement;
import static scatsapplication.DBManager.sqlUpdateUser;
import static scatsapplication.DBManager.sqlNewSystemUser;
import static scatsapplication.DBManager.sqlDeleteSystemUser;
import static scatsapplication.EngagementCollection.getEngagementCollection;
import static scatsapplication.SystemUser.validateUser;
import static scatsapplication.SystemUserCollection.getSystemUserCollection;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class ManageSystemUsersController implements Initializable {
    @FXML private TextField sysUsernameField;
    @FXML private PasswordField sysPasswordField;
    @FXML private Button saveButton;
    @FXML private TableView<SystemUser> systemUsersTable;
    @FXML private TableColumn<SystemUser, String> sysUsernameTableField;
    @FXML private TableColumn<SystemUser, String> sysPasswordTableField;
    @FXML private TableColumn<SystemUser, String> sysLastUserLoginField;
    @FXML private TableColumn<SystemUser, String> sysCreatedDateField;
    
    @FXML public void updateMainView() {
        refreshSystemUsers();
        Platform.runLater(() -> systemUsersTable.setItems(getSystemUserCollection()));
    }
    
    /***
    * Name: saveNewUser()
    * Description: Updates the engagement with changes made to the form
    * @param  - Action Event
    * @return - None
    */  
    @FXML private void saveNewUser(ActionEvent event) {
        String sysUsername = sysUsernameField.getText();
        String sysPassword = sysPasswordField.getText();

        String errorMessages = validateUser(sysUsername, sysPassword);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Data Invalid");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }           
        sqlNewSystemUser(sysUsername, sysPassword);
        updateMainView();
        sysUsernameField.setText("");
        sysPasswordField.setText("");
        //final Node source = (Node) event.getSource();
        //final Stage stage = (Stage) source.getScene().getWindow();
        //stage.close();
    }     
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sysUsernameTableField.setCellValueFactory(cellData -> cellData.getValue().getUsernameProperty());  // Lambda expressions used to ensure values are assigned quickly, upon initialization
        sysPasswordTableField.setCellValueFactory(cellData -> cellData.getValue().getPasswordProperty());
        sysLastUserLoginField.setCellValueFactory(cellData -> cellData.getValue().getLastLogin());
        sysCreatedDateField.setCellValueFactory(cellData -> cellData.getValue().getCreatedDateProperty());
        updateMainView();
        
        systemUsersTable.setRowFactory(new Callback<TableView<SystemUser>, TableRow<SystemUser>>() {  
            @Override  
            public TableRow<SystemUser> call(TableView<SystemUser> tableView) {  
                final TableRow<SystemUser> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  

                final MenuItem editSystemUserMenuItem = new MenuItem("Edit User");
                editSystemUserMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("Edit system user function");
                        SystemUser systemUser = systemUsersTable.getSelectionModel().getSelectedItem();
                        EditSystemUserController viewHandler = new EditSystemUserController(systemUser);
                        viewHandler.showStage();
                    }
                });
                
                final MenuItem deleteSystemUserMenuItem = new MenuItem("Delete User");
                deleteSystemUserMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("TBD: Delete system user SQL call");
                        SystemUser systemUser = systemUsersTable.getSelectionModel().getSelectedItem();
                        EditSystemUserController viewHandler = new EditSystemUserController(systemUser);
                        if (sqlDeleteSystemUser(systemUser)) {
                            systemUsersTable.getItems().remove(row.getItem());
                        }
                    }
                });
                
                contextMenu.getItems().add(editSystemUserMenuItem);
                contextMenu.getItems().add(deleteSystemUserMenuItem);  
                row.contextMenuProperty().bind(  
                        Bindings.when(row.emptyProperty())  
                        .then((ContextMenu)null)  
                        .otherwise(contextMenu)  
                );  
                return row;
            }
        });        
    }    
    
}
