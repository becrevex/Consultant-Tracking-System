//************************************************************************
// CLASS: EngagmentCollection
// FILENAME: EngagementCollection.java
//
// DESCRIPTION
//  Engagement collection class to manage existing engagements 
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static scatsapplication.ConsultantCollection.getConsultantCollection;

/**
 *
 * @author becatasu
 */
public class EngagementCollection {
    private static ObservableList<Engagement> engagementCollection = FXCollections.observableArrayList();
    private static ObservableList<Engagement> allEngagementCollection = FXCollections.observableArrayList();
    private static ObservableList<Engagement> searchResults = FXCollections.observableArrayList();
    
    public static ObservableList<Engagement> getEngagementCollection() {
        return engagementCollection;
    }
    public static ObservableList<Engagement> getAllEngagementCollection() {
        return allEngagementCollection;
    }
    
    public static Engagement getEngagementObjectFromID(int CID) {
        ObservableList<Engagement> engagements = getAllEngagementCollection();
        for (Engagement engagement : engagements) {
            if (engagement.getConsultantId() == CID) {
                return engagement;
            }
        }
        return null;
    }
    
    public static ObservableList<Engagement> getEngagmentsByDate(String datelookup) {
        ObservableList<Engagement> apnEngagements = FXCollections.observableArrayList();
        for (Engagement eng : engagementCollection) {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            String testDate = format.format(eng.getStartDate());
            if (testDate.equals(datelookup)) {
                apnEngagements.add(eng);
            }
        }
        return apnEngagements;
    }
    
    
    public static ObservableList<Engagement> getEngagementSearchCollection(String searchQuery) {
        if (searchQuery.length() == 0) {
            return engagementCollection;
        }
        searchResults.clear();
        for (Engagement e : engagementCollection) {
            if (e.getCompany().contains(searchQuery)){
                searchResults.add(e);
            } else if (e.getLocation().contains(searchQuery)) {
                searchResults.add(e);
            } else if (e.getType().contains(searchQuery)) {
                searchResults.add(e);
            } else if (e.getStatus().contains(searchQuery)){
                searchResults.add(e);
            } else if (e.getDescription().contains(searchQuery)) {
                searchResults.add(e);
            }
        }
        for (Consultant c : getConsultantCollection()) {
            if (c.getName().contains(searchQuery)) {
                searchResults.add(getEngagementObjectFromID(c.getConsultantId()));
            }
                
        }
        return searchResults;
    }    

    public static ObservableList<Engagement> getAllEngagementSearchCollection(String searchQuery) {
        if (searchQuery.length() == 0) {
            return allEngagementCollection;
        }
        searchResults.clear();
        for (Engagement e : allEngagementCollection) {
            if (e.getCompany().contains(searchQuery)){
                searchResults.add(e);
            } else if (e.getLocation().contains(searchQuery)) {
                searchResults.add(e);
            } else if (e.getType().contains(searchQuery)) {
                searchResults.add(e);
            } else if (e.getStatus().contains(searchQuery)){
                searchResults.add(e);
            } else if (e.getDescription().contains(searchQuery)) {
                searchResults.add(e);
            }
        }
        for (Consultant c : getConsultantCollection()) {
            if (c.getName().contains(searchQuery)) {
                searchResults.add(getEngagementObjectFromID(c.getConsultantId()));
            }
                
        }
        return searchResults;
    }
}
