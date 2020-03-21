//************************************************************************
// CLASS: MainController
// FILENAME: MainController.java
//
// DESCRIPTION
//  Primary controller class for handling Main.fxml hooks and events
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.time.YearMonth;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import static scatsapplication.CertificationCollection.getCertificationCollection;
import static scatsapplication.ConsultantCollection.getConsultantCollection;
import static scatsapplication.DBManager.deleteEngagement;
import static scatsapplication.DBManager.refreshCertifications;
import static scatsapplication.EngagementCollection.getEngagementCollection;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.refreshAllEngagements;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.sqlDeactivateEngagement;
import static scatsapplication.EngagementCollection.getAllEngagementCollection;
import static scatsapplication.EngagementCollection.getAllEngagementSearchCollection;
import static scatsapplication.EngagementCollection.getEngagementCollection;
import static scatsapplication.EngagementCollection.getEngagementSearchCollection;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class MainController implements Initializable {
    @FXML private MenuBar mainMenuBar;
    @FXML private Button newEngagementButton;
    @FXML private Button viewConsultantsButton;
    @FXML private Button refreshButton;
    @FXML private Label totalCountLabel;
    @FXML private TextField searchBox;
    @FXML private TableView<Engagement> assignmentsTable;
    @FXML private TableColumn<Engagement, String> assignmentsCompanyField;
    @FXML private TableColumn<Engagement, String> assignmentsConsultantField;    
    @FXML private TableColumn<Engagement, String> assignmentsTypeField;
    @FXML private TableColumn<Engagement, String> assignmentsStatusField;
    @FXML private TableColumn<Engagement, String> assignmentsLocationField;
    @FXML private TableColumn<Engagement, String> assignmentsDescriptionField;    
    @FXML private TableColumn<Engagement, String> assignmentsStartDateField;    
    @FXML private TableColumn<Engagement, String> assignmentsEndDateField;
    @FXML private GridPane primaryGrid;
    private CalendarGrid monthlyCalendar;
    private VBox calendarViewBox;
 
    @FXML private void displayAboutAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About ");
        alert.setHeaderText("Security Consultant Assignment/Engagement Tracker ");
        alert.setContentText("SCATS version 1.2 release 0x01 \nWritten by Brent 'becrevex' Chambers\nCygiene Solutions © 2020");
        alert.showAndWait();   
    }

    @FXML private void exitApplication() {
        Platform.exit();
    }      
    
    @FXML public void updateMainView() {
        refreshEngagements();
        refreshConsultants();
        refreshCertifications();
        monthlyCalendar.populateCalendar(monthlyCalendar.getCurrentYearMonth());        
        Platform.runLater(() -> assignmentsTable.setItems(getEngagementCollection()));
    }

    @FXML public void updateMainViewFromSearch(String searchQuery) {
        Platform.runLater(() -> {ObservableList<Engagement> searchResults = getEngagementSearchCollection(searchQuery);
                                assignmentsTable.setItems(searchResults);
        });
        ObservableList<Engagement> searchResults = getEngagementSearchCollection(searchQuery);
        String count = Integer.toString(searchResults.size());
        totalCountLabel.setText(count);        
    }
    
    @FXML public void onEnter(ActionEvent actionEvent) {
        //refreshCertifications();
        updateMainViewFromSearch(searchBox.getText());
    }    
        
    
    /***
    * Name: launchNewEngagement()
    * Description: Launches newEngagement manager stager to add an eng
    * @param  - Action Event
    * @return - None
    */       
    @FXML private void launchNewEngagement(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewEngagement.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("New Engagement");
            stage.setScene(new Scene(root1));  
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    } 
    
    /***
    * Name: launchManageUsers()
    * Description: Launches system user manager 
    * @param  - Action Event
    * @return - None
    */       
    @FXML private void launchManageUsers(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ManageSystemUsers.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Manage System Users");
            stage.setScene(new Scene(root1));  
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }      
    
    
    /***
    * Name: launchManageConsultants()
    * Description: Launches ManageConsultants manager stager
    * @param  - Action Event
    * @return - None
    */       
    @FXML private void launchManageConsultants(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ManageConsultants.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Manage Consultants");
            stage.setScene(new Scene(root1));  
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }      
    
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

    /***
    * Name: launchEngagementManagement()
    * Description: Launches engagement management
    * @param  - Action Event
    * @return - None
    */       
    @FXML private void launchEngagementOptions(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EngagementOptions.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Engagement Management");
            stage.setScene(new Scene(root1));  
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }     
    

    @FXML public void generateConsultantsReport() throws Exception {
        Writer writer = null;
        try {
            File file = new File("c:\\temp\\all_consultants_report.csv");
            writer = new BufferedWriter( new FileWriter(file));
                String reportHeaders = "Consultant Name" + ", " +
                                    "Location" + ", " +
                                    //"Description" + ", " +
                                    "HireDate" + ", " + "\n";
                writer.write(reportHeaders);
            for (Consultant consultant : getConsultantCollection()) {
                String reportText = consultant.getName() + ", " +
                                    consultant.getLocation().replace(",", " ") + ", " + 
                                    consultant.getStartDate().toString() + "\n";
                writer.write(reportText);
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
        Desktop.getDesktop().open(new File("c:\\temp\\all_consultants_report.csv"));
    }
    
    @FXML public void generateActiveEngagementsReport() throws Exception {
        Writer writer = null;
        try {
            File file = new File("active_engagements_report.csv");
            writer = new BufferedWriter( new FileWriter(file));
                String reportHeaders = "Company/Firm" + ", " +
                                    "Consultant" + ", " +                                    
                                    "Location" + ", " +
                                    "Type" + ", " +
                                    "Status" + ", " +
                                    "Description" + ", " +
                                    "Time of Activation" + ", " +
                                    "Fieldwork Started" + ", " +
                                    "Fieldwork Ended" + "\n";
                writer.write(reportHeaders);
            for (Engagement engagement : getEngagementCollection()) {
                String reportText = engagement.getCompany() + ", " +
                                    engagement.getConsultantIdResolution().get() + ", " +
                                    engagement.getLocation().replace(",", " ") + ", " +
                                    engagement.getType() + ", " +
                                    engagement.getStatus() + ", " +
                                    engagement.getDescription().replace(",", " ") + ", " +
                                    engagement.getDateCreated() + ", " +                        
                                    engagement.getStartDate() + ", " +     
                                    engagement.getEndDate() + "\n";
                writer.write(reportText);
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
        Desktop.getDesktop().open(new File("active_engagements_report.csv"));
    }    

    @FXML public void generateAllEngagementsReport() throws Exception {
        refreshAllEngagements();
        Writer writer = null;
        try {
            File file = new File("all_engagements_report.csv");
            writer = new BufferedWriter( new FileWriter(file));
                String reportHeaders = "Active" + ", " +
                                    "Company/Firm" + ", " +
                                    "Consultant" + ", " +                                    
                                    "Location" + ", " +
                                    "Type" + ", " +
                                    "Status" + ", " +
                                    "Description" + ", " +
                                    "Time of Activation" + ", " +
                                    "Fieldwork Started" + ", " +
                                    "Fieldwork Ended" + "\n";
                writer.write(reportHeaders);
            for (Engagement engagement : getAllEngagementCollection()) {
                String reportText = engagement.getActive() + ", " +
                                    engagement.getCompany() + ", " +
                                    engagement.getConsultantIdResolution().get() + ", " +
                                    engagement.getLocation().replace(",", " ") + ", " +
                                    engagement.getType() + ", " +
                                    engagement.getStatus() + ", " +
                                    engagement.getDescription().replace(",", " ") + ", " +
                                    engagement.getDateCreated() + ", " +                        
                                    engagement.getStartDate() + ", " +     
                                    engagement.getEndDate() + "\n";
                writer.write(reportText);
            }
        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
        Desktop.getDesktop().open(new File("all_engagements_report.csv"));
    }     
    
    
    @FXML public void generateCertificationReport() throws Exception {
            Writer writer = null;
            try {
                File file = new File("all_certifications_report.csv");
                writer = new BufferedWriter( new FileWriter(file));
                    String reportHeaders = "Consultant" + ", " +                                    
                                        "Certification Name" + ", " +
                                        "Abbreviation" + ", " +
                                        "Cert Type" + ", " +
                                        "License Number" + ", " +
                                        "Date Obtained" + ", " +
                                        "Expiration" + "\n";
                    writer.write(reportHeaders);
                for (Certification cert : getCertificationCollection()) {
                    String reportText = cert.getConsultantIdResolution().get() + ", " +
                                        cert.getName() + ", " +
                                        cert.getAbbreviation() + ", " +
                                        cert.getType() + ", " +
                                        cert.getLicenseNumber() + ", " +
                                        cert.getStartDate().toString() + ", " +
                                        cert.getExpiration() + "\n";
                    writer.write(reportText);
                }
            } catch (Exception e) {
                    e.printStackTrace();
            } finally {
                writer.flush();
                writer.close();
            }
            Desktop.getDesktop().open(new File("all_certifications_report.csv"));
        }    
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assignmentsCompanyField.setCellValueFactory(cellData -> cellData.getValue().getCompanyProperty());  // Lambda expressions used to ensure values are assigned quickly, upon initialization
        assignmentsConsultantField.setCellValueFactory(cellData -> cellData.getValue().getConsultantIdResolution());
        assignmentsTypeField.setCellValueFactory(cellData -> cellData.getValue().getTypeProperty());
        assignmentsStatusField.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());
        assignmentsLocationField.setCellValueFactory(cellData -> cellData.getValue().getLocationProperty());
        assignmentsStartDateField.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
        assignmentsEndDateField.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());

        monthlyCalendar = new CalendarGrid(YearMonth.now());
        calendarViewBox = monthlyCalendar.getView();
        primaryGrid.add(calendarViewBox, 0, 0);
        monthlyCalendar.populateCalendar(YearMonth.now());
        
        
        assignmentsTable.setRowFactory(new Callback<TableView<Engagement>, TableRow<Engagement>>() {  
            @Override  
            public TableRow<Engagement> call(TableView<Engagement> tableView) {  
                final TableRow<Engagement> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  

                final MenuItem viewEngagementMenuItem = new MenuItem("View Engagement");
                viewEngagementMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("TBD: View appointments for this customer on the calendar");
                        Engagement engagement = assignmentsTable.getSelectionModel().getSelectedItem();
                        UpdateEngagementController viewHandler = new UpdateEngagementController(engagement);
                        viewHandler.showStage();
                    }
                });
                
                final MenuItem archiveEngagement = new MenuItem("Deactivate Engagement");
                archiveEngagement.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Engagement engagement = assignmentsTable.getSelectionModel().getSelectedItem();
                        if (sqlDeactivateEngagement(engagement.getAssignmentId())) {
                            assignmentsTable.getItems().remove(row.getItem());
                        }
                    }
                   
                });

                final MenuItem deleteEngagement = new MenuItem("Delete Engagement");
                deleteEngagement.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Engagement engagement = assignmentsTable.getSelectionModel().getSelectedItem();
                        if (deleteEngagement(engagement)) {
                            assignmentsTable.getItems().remove(row.getItem());
                        }
                    }
                   
                });
                
                contextMenu.getItems().add(viewEngagementMenuItem);
                contextMenu.getItems().add(archiveEngagement);  
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



