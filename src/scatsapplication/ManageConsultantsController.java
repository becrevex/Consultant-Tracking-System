/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.deleteConsultant;
import static scatsapplication.ConsultantCollection.getConsultantCollection;
import static scatsapplication.ConsultantCollection.getConsultantSearchCollection;
import static scatsapplication.DBManager.refreshCertifications;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class ManageConsultantsController implements Initializable {
    @FXML private Button newConsultantButton;
    @FXML private TableView<Consultant> consultantsTable;
    @FXML private TableColumn<Consultant, String> consultantNameField;
    @FXML private TableColumn<Consultant, String> consultantLocationField; 
    @FXML private ImageView consultantProfileImg;
    @FXML private TextField searchBox;

    
    @FXML public void updateMainView() {
        refreshConsultants();
        refreshCertifications();
        Platform.runLater(() -> consultantsTable.setItems(getConsultantCollection()));
    }    
    
    
    @FXML public void updateMainViewFromSearch(String searchQuery) {
        //refreshConsultants();
        //refreshCertifications();
        Platform.runLater(() -> {ObservableList<Consultant> searchResults = getConsultantSearchCollection(searchQuery);
                                consultantsTable.setItems(searchResults);
        });
    }
    
    @FXML public void onEnter(ActionEvent actionEvent) {
        //refreshCertifications();
        updateMainViewFromSearch(searchBox.getText());
    }
    
    /**
     * Initializes the controller class.
     */
    
    /***
    * Name: launchNewConsultant()
    * Description: Launches newConsultant manager stager to add a consultant
    * @param  - Action Event
    * @return - None
    */       
    @FXML private void launchNewConsultant(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewConsultant.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("New Consultant");
            stage.setScene(new Scene(root1));  
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        consultantNameField.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());  // Lambda expressions used to ensure values are assigned quickly, upon initialization
        consultantLocationField.setCellValueFactory(cellData -> cellData.getValue().getLocationProperty());
        consultantsTable.setRowFactory(new Callback<TableView<Consultant>, TableRow<Consultant>>() {  
            @Override  
            public TableRow<Consultant> call(TableView<Consultant> tableView) {  
                final TableRow<Consultant> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  
                
                final MenuItem viewConsultant = new MenuItem("View Consultant");
                viewConsultant.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("TBD: View/Edit this consultant");
                        Consultant consultant = consultantsTable.getSelectionModel().getSelectedItem();
                        ViewConsultantController viewHandler = new ViewConsultantController(consultant);
                        viewHandler.showStage();
                    }
                });                
                
                final MenuItem deleteConsultant = new MenuItem("Delete Consultant");
                deleteConsultant.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Consultant consultant = consultantsTable.getSelectionModel().getSelectedItem();
                        if (deleteConsultant(consultant)) {
                            consultantsTable.getItems().remove(row.getItem());
                        }
                    }
                    
                });

                contextMenu.getItems().add(viewConsultant);
                contextMenu.getItems().add(deleteConsultant);  
                row.contextMenuProperty().bind(  
                        Bindings.when(row.emptyProperty())  
                        .then((ContextMenu)null)  
                        .otherwise(contextMenu)  
                );  
                return row;
            }
        });
        
        updateMainView();
    }    
    
}
