/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.net.URL;
import java.text.ParseException;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import static scatsapplication.DBManager.refreshEngagements;
import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.EngagementCollection.getEngagmentsByDate;

/**
 * FXML Controller class
 *
 * @author becatasu
 */
public class PopupEngagementsController implements Initializable {
    @FXML private Button refreshButton;
    @FXML private TableView<Engagement> engagementsTable;
    @FXML private TableColumn<Engagement, String> engagementConsultantField;
    @FXML private TableColumn<Engagement, String> engagementCompanyField;    
    @FXML private TableColumn<Engagement, String> engagementType;
    @FXML private TableColumn<Engagement, String> engagementEndDate;
    private ObservableList<Engagement> popupEngagements;
    private String calendarDate;
    private Stage thisStage; 

    /**
     * Initializes the controller class.
     */
    
    public PopupEngagementsController (ObservableList<Engagement> apnEngagements, String titleString, String apnCalendarDate) {
        this.popupEngagements = apnEngagements;
        this.calendarDate = apnCalendarDate;
        thisStage = new Stage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scatsapplication/popupEngagements.fxml"));
            fxmlLoader.setController(this);
            thisStage.setScene(new Scene(fxmlLoader.load()));
            thisStage.setTitle("Engagements starting: " + titleString);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }        
    

    @FXML public void updateView(){
        refreshEngagements();
        Platform.runLater(() -> engagementsTable.setItems(this.popupEngagements));
    }

    @FXML public void updateRefreshedView(){
        refreshEngagements();
        Platform.runLater(() -> engagementsTable.setItems(getEngagmentsByDate(this.calendarDate)));
    }    
    
    public void showStage() {
        thisStage.show();
    }     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateView();
        engagementConsultantField.setCellValueFactory(cellData -> cellData.getValue().getConsultantIdResolution());  // Lambda expressions used to ensure values are assigned quickly, upon initialization
        engagementCompanyField.setCellValueFactory(cellData -> cellData.getValue().getCompanyProperty());
        engagementType.setCellValueFactory(cellData -> cellData.getValue().getTypeProperty());
        engagementEndDate.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());
        
        engagementsTable.setRowFactory(new Callback<TableView<Engagement>, TableRow<Engagement>>() {  
                    @Override  
                    public TableRow<Engagement> call(TableView<Engagement> tableView) {  
                        final TableRow<Engagement> row = new TableRow<>();  
                        final ContextMenu contextMenu = new ContextMenu();  

                        final MenuItem viewEngagementMenuItem = new MenuItem("View Engagement");
                        viewEngagementMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Engagement engagement = engagementsTable.getSelectionModel().getSelectedItem();
                                UpdateEngagementController viewHandler = new UpdateEngagementController(engagement);
                                viewHandler.showStage();
                                System.out.println("After the stage show!");
                            }
                        });

                        contextMenu.getItems().add(viewEngagementMenuItem);
                        row.contextMenuProperty().bind(  
                                Bindings.when(row.emptyProperty())  
                                .then((ContextMenu)null)  
                                .otherwise(contextMenu)  
                        );  
                        return row;
                    }
                });
            updateView();
            }    
    }
