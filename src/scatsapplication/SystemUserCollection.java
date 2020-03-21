//************************************************************************
// CLASS: SystemUserCollection
// FILENAME: UserCollection.java
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
public class SystemUserCollection {
    private static ObservableList<SystemUser> systemUserCollection = FXCollections.observableArrayList();
    public static ObservableList<SystemUser> getSystemUserCollection() {
        return systemUserCollection;
    }
   
}