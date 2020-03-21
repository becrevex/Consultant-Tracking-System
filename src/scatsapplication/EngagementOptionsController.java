/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.util.Callback;
import static scatsapplication.ConsultantCollection.getConsultantCollection;
import static scatsapplication.DBManager.deleteEngagement;
import static scatsapplication.DBManager.refreshAllEngagements;
import static scatsapplication.DBManager.sqlDeactivateEngagement;
import static scatsapplication.DBManager.sqlReactivateEngagement;
import static scatsapplication.EngagementCollection.getEngagementCollection;
import static scatsapplication.EngagementCollection.getAllEngagementCollection;
import static scatsapplication.EngagementCollection.getAllEngagementSearchCollection;
import static scatsapplication.EngagementCollection.getEngagementSearchCollection;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class EngagementOptionsController implements Initializable {
    @FXML private CheckBox toggleFileEncryption;
    @FXML private Button backupBrowseButton;
    @FXML private TextField searchBox;
    @FXML private Button refreshButton;    
    @FXML private Label totalCountLabel;
    @FXML private TableView<Engagement> allEngagementsTable;
    @FXML private TableColumn<Engagement, String> engagementsActiveField;
    @FXML private TableColumn<Engagement, String> engagementsCompanyField;
    @FXML private TableColumn<Engagement, String> engagementsConsultantField;    
    @FXML private TableColumn<Engagement, String> engagementsTypeField;
    @FXML private TableColumn<Engagement, String> engagementsStatusField;
    @FXML private TableColumn<Engagement, String> engagementsStartDate;
    @FXML private TableColumn<Engagement, String> engagementsEndDate;  

    @FXML public void updateMainView() {
        refreshAllEngagements();
        Platform.runLater(() -> allEngagementsTable.setItems(getAllEngagementCollection()));
        String count = Integer.toString(getAllEngagementCollection().size());
        totalCountLabel.setText(count);
    }
    
    @FXML public void reactiveAllEngagements() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Alert");
        alert.setHeaderText("Reactivate all Security Engagements?");
        alert.setContentText("");
        refreshAllEngagements();
         ObservableList<Engagement> allEngagements = getAllEngagementCollection();
        for (Engagement engagement : allEngagements) {
            sqlReactivateEngagement(engagement.getAssignmentId());
        }
    }

    @FXML public void updateMainViewFromSearch(String searchQuery) {
        //refreshConsultants();
        //refreshCertifications();
        Platform.runLater(() -> {ObservableList<Engagement> searchResults = getAllEngagementSearchCollection(searchQuery);
                                allEngagementsTable.setItems(searchResults);
        });
        ObservableList<Engagement> searchResults = getAllEngagementSearchCollection(searchQuery);
        String count = Integer.toString(searchResults.size());
        totalCountLabel.setText(count);        
    }
    
    @FXML public void onEnter(ActionEvent actionEvent) {
        //refreshCertifications();
        updateMainViewFromSearch(searchBox.getText());
    }    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        engagementsActiveField.setCellValueFactory(cellData -> cellData.getValue().getActiveProperty());
        engagementsCompanyField.setCellValueFactory(cellData -> cellData.getValue().getCompanyProperty());  // Lambda expressions used to ensure values are assigned quickly, upon initialization
        engagementsConsultantField.setCellValueFactory(cellData -> cellData.getValue().getConsultantIdResolution());
        engagementsTypeField.setCellValueFactory(cellData -> cellData.getValue().getTypeProperty());
        engagementsStatusField.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());
        engagementsStartDate.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
        engagementsEndDate.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());

        allEngagementsTable.setRowFactory(new Callback<TableView<Engagement>, TableRow<Engagement>>() {  
            @Override  
            public TableRow<Engagement> call(TableView<Engagement> tableView) {  
                final TableRow<Engagement> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  

                final MenuItem viewEngagementMenuItem = new MenuItem("View Details");
                viewEngagementMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("TBD: View appointments for this customer on the calendar");
                        Engagement engagement = allEngagementsTable.getSelectionModel().getSelectedItem();
                        UpdateEngagementController viewHandler = new UpdateEngagementController(engagement);
                        viewHandler.showStage();
                    }
                });

                final MenuItem deactivateEngagement = new MenuItem("Deactivate Engagement");
                deactivateEngagement.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Engagement engagement = allEngagementsTable.getSelectionModel().getSelectedItem();
                        if (sqlDeactivateEngagement(engagement.getAssignmentId())) {
                            updateMainView();
                        }
                    }
                   
                });                
                
                final MenuItem reactivateEngagement = new MenuItem("Reactivate Engagement");
                reactivateEngagement.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Engagement engagement = allEngagementsTable.getSelectionModel().getSelectedItem();
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.initModality(Modality.NONE);
                        alert.setTitle("Alert");
                        alert.setHeaderText("Engagement " + engagement.getCompany() + " (" + engagement.getType()+ ") reactivated.");
                        alert.setContentText("Notify Assigned Consultant?");                        
                        if (sqlReactivateEngagement(engagement.getAssignmentId())) {
                            updateMainView();
                        }
                    }
                   
                });

                final MenuItem openEngagementReportsFolder = new MenuItem("Browse Reports Folder");
                openEngagementReportsFolder.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Engagement engagement = allEngagementsTable.getSelectionModel().getSelectedItem();

                        Path currentRelativePath = Paths.get("Reports");
                        String s = currentRelativePath.toAbsolutePath().toString();
                        File dir = new File(s + "\\" + engagement.getCompany().replace(" ", "_"));
                        System.out.println(dir.getAbsolutePath());                        
                        try {
                            Desktop.getDesktop().open(dir);
                        } catch (IOException ex) {
                            //Logger.getLogger(EngagementOptionsController.class.getName()).log(Level.SEVERE, null, ex);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Alert");
                            alert.setHeaderText("Not Found");
                            alert.setContentText("No reports folder found for this engagement.");
                            alert.showAndWait();                             
                        }
                    }
                   
                });
                
                final MenuItem deleteEngagement = new MenuItem("Delete Engagement");
                deleteEngagement.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Engagement engagement = allEngagementsTable.getSelectionModel().getSelectedItem();
                        if (deleteEngagement(engagement)) {
                            allEngagementsTable.getItems().remove(row.getItem());
                        }
                    }
                   
                });
                
                contextMenu.getItems().add(viewEngagementMenuItem);
                contextMenu.getItems().add(openEngagementReportsFolder);
                contextMenu.getItems().add(deactivateEngagement);
                contextMenu.getItems().add(reactivateEngagement);  
                contextMenu.getItems().add(deleteEngagement);  
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
    