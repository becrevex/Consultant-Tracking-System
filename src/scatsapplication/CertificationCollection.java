//************************************************************************
// CLASS: CertificationCollection
// FILENAME: CertificationCollection.java
//
// DESCRIPTION
//  Certification collection manager to manage all existing certifications
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
public class CertificationCollection {
    private static ObservableList<Certification> certificationCollection = FXCollections.observableArrayList();
    public static ObservableList<Certification> getCertificationCollection() {
        return certificationCollection;
    }
    
    public static ObservableList<Certification> getConstulantCertifications(Consultant consultant) {
        ObservableList<Certification> consultantCertifications = FXCollections.observableArrayList();
        for (Certification cert : certificationCollection) {
            if (cert.getConsultantId() == consultant.getConsultantId()) {
                consultantCertifications.add(cert);
            }
        }
        return consultantCertifications;        
    }
}