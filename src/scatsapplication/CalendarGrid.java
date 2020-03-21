/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.input.MouseEvent;
/**
 *
 * @author becatasu
 */
public class CalendarGrid {
    private Text monthTitle;
    private Text navigationSpacerL;
    private Text navigationSpacerR;
    private YearMonth currentYearMonth;
    private ArrayList<AnchorPaneNode> calendarDayPanes = new ArrayList<>(35);
    private VBox calendarGrid;
    
    public CalendarGrid(YearMonth yearMonth) {
        currentYearMonth = yearMonth;
        GridPane calendar = new GridPane();
        calendar.setPrefSize(600,480);
        calendar.setGridLinesVisible(true);
        
        for (int i=0; i<5; i++) {
            for (int p=0; p<7; p++) {
                AnchorPaneNode pNode = new AnchorPaneNode();
                pNode.setPrefSize(200,200);
                calendar.add(pNode, p, i);
                calendarDayPanes.add(pNode);
            }
        }
        
        Text[] weekDays;
        //ResourceBundle cView = ResourceBundle.getBundle("CalendarView");
        weekDays = new Text[]{
                new Text("Sunday"),
                new Text("Monday"), 
                new Text("Tuesday"),
                new Text("Wednesday"), 
                new Text("Thursday"), 
                new Text("Friday"),
                new Text("Saturday")};

        GridPane days = new GridPane();
        days.setPrefWidth(650);
        int columns = 0;
        for (Text day : weekDays) {
            AnchorPane cPane = new AnchorPane();
            cPane.setPrefSize(200, 10);
            cPane.setBottomAnchor(day, 5.0);
            day.setWrappingWidth(100);
            day.setTextAlignment(TextAlignment.CENTER);
            cPane.getChildren().add(day);
            days.add(cPane, columns++, 0);
        }
        
        monthTitle = new Text();
        monthTitle.setStyle("-fx-font: 24 arial;");
        navigationSpacerL = new Text("                  ");
        navigationSpacerR = new Text("                  ");
        Button cBackButton = new Button("<--");                         //Lambda expressions used to ensure GridView renders fluidly
            cBackButton.setOnAction(event -> CalendarBack());           //Lambda Expression for calendar back functionality
        Button cTodayButton = new Button("Today");
            cTodayButton.setOnAction(event -> CalendarToday());         //Lambda Expression for calendar <today> functionality
        Button cForwardButton = new Button("-->");
            cForwardButton.setOnAction(event -> CalendarForward());     //Lambda Expression for calendar forward functionality
        HBox titleBar = new HBox(monthTitle);
        HBox navigationBar = new HBox(cBackButton, navigationSpacerL, cTodayButton, navigationSpacerR, cForwardButton);
        titleBar.setAlignment(Pos.BASELINE_RIGHT);
        navigationBar.setAlignment(Pos.BASELINE_CENTER);        
        populateCalendar(yearMonth);
        calendarGrid = new VBox(titleBar, days, calendar, navigationBar);
    }
        
    public void populateCalendar(YearMonth yearMonth) {
        LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
        while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY")) {
            calendarDate = calendarDate.minusDays(1);
        }
        
        String localizedMonth = new DateFormatSymbols().getMonths()[yearMonth.getMonthValue()-1];
        String properMonth = localizedMonth.substring(0,1).toUpperCase() + localizedMonth.substring(1);
        monthTitle.setText(" " + properMonth + " " + String.valueOf(yearMonth.getYear()) + " ");
        ObservableList<Engagement> allEngagements = FXCollections.observableArrayList();
        ObservableList<Engagement> apnEngagements = FXCollections.observableArrayList();
        Calendar cal = Calendar.getInstance();   
        LocalDate today = LocalDate.now();
        for (AnchorPaneNode apn : calendarDayPanes) {
            if (apn.getChildren().size() != 0) {
                apn.getChildren().remove(0, apn.getChildren().size());
            }
            
                Text date = new Text(String.valueOf(calendarDate.getDayOfMonth()));
                apn.setDate(calendarDate);
                apn.setTopAnchor(date, 5.0);
                apn.setLeftAnchor(date, 5.0);
                apn.getChildren().add(date);
                
                ObservableList<Engagement> engagementList = EngagementCollection.getEngagementCollection();
                int calendarDateYear = calendarDate.getYear();
                int calendarDateMonth = calendarDate.getMonthValue();
                int calendarDateDay = calendarDate.getDayOfMonth();
                int appointmentCount = 0;
                String engagementDescription = "";
                for (Engagement engagement : engagementList) {
                    Date appointmentDate = engagement.getStartDate();
                    Date appointmentEndDate = engagement.getEndDate();
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.setTime(appointmentDate);
                    int appointmentYear = calendar.get(Calendar.YEAR);
                    int appointmentMonth = calendar.get(Calendar.MONTH) +1;
                    int appointmentDay = calendar.get(Calendar.DAY_OF_MONTH);
                    if (calendarDateYear == appointmentYear && calendarDateMonth == appointmentMonth && calendarDateDay == appointmentDay) {
                        allEngagements.add(engagement);
                        appointmentCount++;
                    }

                }
                if (appointmentCount != 0) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY");
                    DateTimeFormatter fullFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
                    String apnCalendarDate = formatter.format(calendarDate);
                    String fullapnCalendarDate = fullFormat.format(calendarDate);
                    Text appointmentsForDay = new Text(String.valueOf(appointmentCount));
                    Text rectText = new Text("Test");
                    Rectangle rectAppt = new Rectangle(0d,50d,105d,20d);
                    rectAppt.setFill(Color.DARKGREY);

                    rectAppt.setOnMouseClicked(new EventHandler<MouseEvent>() 
                    {
                        @Override
                        public void handle(MouseEvent t) {
                            apnEngagements.clear();
                            for (Engagement eng : allEngagements) {
                                SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
                                String testDate = format.format(eng.getStartDate());
                                if (testDate.equals(apnCalendarDate)) {
                                    apnEngagements.add(eng);
                                }
                            }                            
                            PopupEngagementsController popUp = new PopupEngagementsController(apnEngagements, fullapnCalendarDate, apnCalendarDate);
                            popUp.showStage();

                        }
                    });
                    
                    appointmentsForDay.setFont(Font.font(16));
                    appointmentsForDay.setFill(Color.DARKGREEN);
                    apn.getChildren().addAll(appointmentsForDay, rectAppt);
                    apn.setTopAnchor(appointmentsForDay, 47.0);
                    apn.setLeftAnchor(appointmentsForDay, 110.0);
                    }
                calendarDate = calendarDate.plusDays(1);
                }                
            }
    
    private void CalendarForward() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }
    private void CalendarBack() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }  
    private void CalendarToday() {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        populateCalendar(YearMonth.now());
    }      
    public VBox getView() {
        return calendarGrid;
    }
    public YearMonth getCurrentYearMonth() {
        return currentYearMonth;
    }    

}
    