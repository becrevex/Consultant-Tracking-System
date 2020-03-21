//************************************************************************
// CLASS: DocumentCollection
// FILENAME: DocumentCollection.java
//
// DESCRIPTION
//  Document collection manager to manage all existing documents
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
public class DocumentCollection {
    private static ObservableList<Document> documentCollection = FXCollections.observableArrayList();
    public static ObservableList<Document> getDocumentCollection() {
        return documentCollection;
    }

    public static ObservableList<Document> getEngagementDocuments(Engagement engagement) {
        ObservableList<Document> engagementDocuments = FXCollections.observableArrayList();
        for (Document doc : documentCollection) {
            if (doc.getAssignmentId() == engagement.getAssignmentId()) {
                engagementDocuments.add(doc);
            }
        }
        return engagementDocuments;        
    }       
    
}
