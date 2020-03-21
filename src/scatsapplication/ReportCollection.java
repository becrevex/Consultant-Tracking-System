//************************************************************************
// CLASS: ReportCollection
// FILENAME: ReportCollection.java
//
// DESCRIPTION
//  Report collection manager to manage all existing reports
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author becatasu
 */
public class ReportCollection {
    private static ObservableList<Report> reportCollection = FXCollections.observableArrayList();
    public static ObservableList<Report> getReportCollection() {
        return reportCollection;
    }

    public static ObservableList<Report> getEngagementReports(Engagement engagement) {
        ObservableList<Report> engagementReports = FXCollections.observableArrayList();
        for (Report report : reportCollection) {
            if (report.getAssignmentId() == engagement.getAssignmentId()) {
                engagementReports.add(report);
            }
        }
        return engagementReports;        
    }    
    
}

