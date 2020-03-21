//************************************************************************
// CLASS: ConsultantCollection
// FILENAME: ConsultantCollection.java
//
// DESCRIPTION
//  Consultant collection manager to manage all existing consultants
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static scatsapplication.DBManager.refreshCertifications;
import static scatsapplication.CertificationCollection.getConstulantCertifications;
import static scatsapplication.CertificationCollection.getConstulantCertifications;
import static scatsapplication.CertificationCollection.getCertificationCollection;

/**
 *
 * @author becatasu
 */
public class ConsultantCollection {
    private static ObservableList<Consultant> consultantCollection = FXCollections.observableArrayList();
    private static ObservableList<Consultant> searchResults = FXCollections.observableArrayList();
    public static ObservableList<Consultant> getConsultantCollection() {
        return consultantCollection;
    }

    public static Consultant getConsultantObjectFromID(int CID) {
        ObservableList<Consultant> consultants = getConsultantCollection();
        for (Consultant consultant : consultants) {
            if (consultant.getConsultantId() == CID) {
                return consultant;
            }
        }
        return null;
    }    
    
    
    public static ObservableList<Consultant> getConsultantSearchCollection(String searchQuery) {
        if (searchQuery.length() == 0) {
            return consultantCollection;
        }
        searchResults.clear();
        for (Consultant c : consultantCollection) {
            if (c.getName().contains(searchQuery)){
                searchResults.add(c);
            } else if (c.getLocation().contains(searchQuery)) {
                searchResults.add(c);
            } else if (c.getDescription().contains(searchQuery)) {
                searchResults.add(c);
            } 
        }
        for (Certification cert : getCertificationCollection()) {
            if (cert.getAbbreviation().contains(searchQuery)) {
                searchResults.add(getConsultantObjectFromID(cert.getConsultantId()));
            } else if (cert.getName().contains(searchQuery)) {
                searchResults.add(getConsultantObjectFromID(cert.getConsultantId()));
            } else if (cert.getType().contains(searchQuery)) {
                searchResults.add(getConsultantObjectFromID(cert.getConsultantId()));
            } else if (cert.getLicenseNumber().contains(searchQuery)) {
                searchResults.add(getConsultantObjectFromID(cert.getConsultantId()));
            }
                
        }
        return searchResults;
    }
}
        
        
            
