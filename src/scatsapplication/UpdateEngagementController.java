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
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import static scatsapplication.ConsultantCollection.getConsultantCollection;
import static scatsapplication.DBManager.deleteEngagement;
import static scatsapplication.ReportCollection.getEngagementReports;
import static scatsapplication.DocumentCollection.getEngagementDocuments;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.refreshReports;
import static scatsapplication.DBManager.refreshDocuments;
import static scatsapplication.DBManager.sqlDeactivateEngagement;
import static scatsapplication.DBManager.sqlNewEngagement;
import static scatsapplication.DBManager.sqlUpdateEngagement;
import static scatsapplication.DBManager.deleteDocument;
import static scatsapplication.DBManager.deleteReport;
import static scatsapplication.Engagement.validateEngagement;
import static scatsapplication.EngagementCollection.getEngagementCollection;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class UpdateEngagementController implements Initializable {
    @FXML private ChoiceBox<String> engagementStatusChoice;
    @FXML private ChoiceBox<String> engagementAssignmentChoice;
    @FXML private TextField engagementLocationField;
    @FXML private TextField engagementCompanyField;
    @FXML private ChoiceBox<String> engagementTypeChoice;
    @FXML private DatePicker engagementStartDate;
    @FXML private DatePicker engagementEndDate;
    @FXML private TextArea engagementDescriptionField;
    @FXML private Button addDocumentationButton;
    @FXML private Button saveEngagementButton;
    @FXML private TableView<Document> engagementDocumentsTable;
    @FXML private TableColumn<Document, String> engagementFilenameField;
    @FXML private TableColumn<Document, String> engagementDateField;
    @FXML private TableView<Report> engagementReportsTable;
    @FXML private TableColumn<Report, String> engagementReportsFilenameField;
    @FXML private TableColumn<Report, String> engagementReportsDateField;  

    private Engagement engagement;
    private Stage thisStage;
    
    
    /**
     * Initializes the controller class.
     */
    public UpdateEngagementController(Engagement engagement) {
        this.engagement = engagement;
        System.out.println(engagement.getAssignmentId());
        thisStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scatsapplication/UpdateEngagement.fxml"));
            fxmlLoader.setController(this);
            thisStage.setScene(new Scene(fxmlLoader.load()));
            thisStage.setTitle("View Engagement");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showStage() {
        thisStage.show();
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
    * Name: launchAddDocument()
    * Description: Launches newDocument dialog to associate a document with the engagement
    * @param  - Action Event
    * @return - None
    */       
    @FXML private void launchAddDocument(ActionEvent event) {
        AddDocumentController viewHandler = new AddDocumentController(engagement);
        viewHandler.showStage();  
        updateMainView();
    }
    
    @FXML public void updateMainView() {
        refreshReports();
        refreshDocuments();
        Platform.runLater(() -> engagementDocumentsTable.setItems(getEngagementDocuments(this.engagement)));
        Platform.runLater(() -> engagementReportsTable.setItems(getEngagementReports(this.engagement)));
    }
    
    /***
    * Name: updateEngagement()
    * Description: Updates the engagement with changes made to the form
    * @param  - Action Event
    * @return - None
    */  
    @FXML private void updateEngagement(ActionEvent event) {
        String eStatus = engagementStatusChoice.getValue();
        int eCID = resolveIDFromConsultant(engagementAssignmentChoice.getValue());
        String eLocation = engagementLocationField.getText();
        String eCompany = engagementCompanyField.getText();
        String eType = engagementTypeChoice.getValue();
        LocalDate eStartDate = engagementStartDate.getValue();
        LocalDate eEndDate = engagementEndDate.getValue();
        String eDescription = engagementDescriptionField.getText();
        //engagementDocumentsTable        
        //documentationTable;
        System.out.println("UpdateEngagementController:updateEngagement: " + eStartDate.toString());
        System.out.println("UpdateEngagementController:updateEngagement: " + eEndDate.toString());
        //Validate Engagement
        //SQL Call
        String errorMessages = validateEngagement(eLocation, eCompany, eType, eStartDate, eEndDate);
        if (errorMessages.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Data Invalid");
            alert.setContentText(errorMessages);
            alert.showAndWait();            
            return;
        }           
        sqlUpdateEngagement(engagement.getAssignmentId(), eCID, eLocation, eCompany, eType, eStatus, eDescription, eStartDate, eEndDate);                                        
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        refreshEngagements();
        refreshConsultants();
        stage.close();
    }   
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up existing data fields
    engagementStatusChoice.setItems(FXCollections.observableArrayList("New", "Scoping", "Active", "Reporting", "Complete"));
    engagementTypeChoice.setItems(FXCollections.observableArrayList("Web App Test", "Network Test", "Adversarial", "Development", "Research", "Training"));
    engagementAssignmentChoice.setItems(getActiveConsultants());
        // Populate the fields
    engagementStatusChoice.setValue(engagement.getStatus());
    engagementLocationField.setText(engagement.getLocation());
    engagementCompanyField.setText(engagement.getCompany());
    engagementTypeChoice.setValue(engagement.getType());
    
    // Convert the startdate
    Date engagementStart = engagement.getStartDate();
    Calendar calendarStart = Calendar.getInstance(TimeZone.getDefault());
    calendarStart.setTime(engagementStart);
    int engStartYear = calendarStart.get(Calendar.YEAR);
    int engStartMonth  = calendarStart.get(Calendar.MONTH) + 1;
    int engStartDay  = calendarStart.get(Calendar.DAY_OF_MONTH);
    LocalDate engagementLocalDateStart = LocalDate.of(engStartYear, engStartMonth, engStartDay);    
    engagementStartDate.setValue(engagementLocalDateStart);
    //Convert the endDate
    Date engagementEnd = engagement.getEndDate();
    Calendar calendarEnd = Calendar.getInstance(TimeZone.getDefault());
    calendarEnd.setTime(engagementEnd);
    int engEndYear = calendarEnd.get(Calendar.YEAR);
    int engEndMonth  = calendarEnd.get(Calendar.MONTH) + 1;
    int engEndDay  = calendarEnd.get(Calendar.DAY_OF_MONTH);
    LocalDate engagementLocalDateEnd = LocalDate.of(engEndYear, engEndMonth, engEndDay);   
    engagementEndDate.setValue(engagementLocalDateEnd);
    
    //Populate the Consultant if available
    engagementDescriptionField.setText(engagement.getDescription());
    if (engagement.getConsultantId() != 0) {
        engagementAssignmentChoice.setValue(resolveConsultantFromID(engagement.getConsultantId()));
    }

    engagementFilenameField.setCellValueFactory(cellData -> cellData.getValue().getFileNameProperty());
    engagementDateField.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
    engagementReportsFilenameField.setCellValueFactory(cellData -> cellData.getValue().getFileNameProperty());
    engagementReportsDateField.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());

    // Right Click Menu Functionality for Documents
    engagementDocumentsTable.setRowFactory(new Callback<TableView<Document>, TableRow<Document>>() {  
        @Override  
        public TableRow<Document> call(TableView<Document> tableView) {  
            final TableRow<Document> row = new TableRow<>();  
            final ContextMenu contextMenu = new ContextMenu();  

            final MenuItem openDocument = new MenuItem("Open");
            openDocument.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Document document = engagementDocumentsTable.getSelectionModel().getSelectedItem();
                    Path currentRelativePath = Paths.get("Reports");
                    String s = currentRelativePath.toAbsolutePath().toString();
                    try {
                        Desktop.getDesktop().open(new File(s + 
                                                "\\" + engagement.getCompany().replace(" ", "_") + 
                                                "\\" + document.getFileName()));
                    } catch (IOException ex) {
                        Logger.getLogger(UpdateEngagementController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            final MenuItem deleteDocument = new MenuItem("Delete");
            deleteDocument.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Document document = engagementDocumentsTable.getSelectionModel().getSelectedItem();
                    if (deleteDocument(document)) {
                        engagementDocumentsTable.getItems().remove(row.getItem());
                    }
                }

            });

            contextMenu.getItems().add(openDocument);
            contextMenu.getItems().add(deleteDocument);  
            row.contextMenuProperty().bind(  
                    Bindings.when(row.emptyProperty())  
                    .then((ContextMenu)null)  
                    .otherwise(contextMenu)  
            );  
            return row;
        }
    });

        // Right Click Menu Functionality for Documents
    engagementReportsTable.setRowFactory(new Callback<TableView<Report>, TableRow<Report>>() {  
        @Override  
        public TableRow<Report> call(TableView<Report> tableView) {  
            final TableRow<Report> row = new TableRow<>();  
            final ContextMenu contextMenu = new ContextMenu();  

            final MenuItem openReport = new MenuItem("Open");
            openReport.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Report report = engagementReportsTable.getSelectionModel().getSelectedItem();
                    Path currentRelativePath = Paths.get("Reports");
                    String s = currentRelativePath.toAbsolutePath().toString();
                    try {
                        Desktop.getDesktop().open(new File(s + 
                                                "\\" + engagement.getCompany().replace(" ", "_") + 
                                                "\\" + report.getFileName()));
                    } catch (IOException ex) {
                        Logger.getLogger(UpdateEngagementController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            final MenuItem deleteReport = new MenuItem("Delete");
            deleteReport.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Report report = engagementReportsTable.getSelectionModel().getSelectedItem();
                    if (deleteReport(report)) {
                        engagementReportsTable.getItems().remove(row.getItem());
                    }
                }
            });
            contextMenu.getItems().add(openReport);
            contextMenu.getItems().add(deleteReport);  
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
