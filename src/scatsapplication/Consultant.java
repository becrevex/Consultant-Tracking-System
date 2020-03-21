//************************************************************************
// CLASS: Consultant
// FILENAME: Consultant.java
//
// DESCRIPTION
//  Consultant class to describe each consultant record object
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;
import java.awt.image.BufferedImage;
import java.sql.Blob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 *
 * @author becatasu
 */
public class Consultant {
    private IntegerProperty consultantId;
    private StringProperty consultantName;
    public boolean active;
    private StringProperty location;
    private StringProperty description;
    private Date startDate;
    private Date endDate;
    private StringProperty dateString;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty createdBy;
    private Blob profilepic;


    public Consultant() {
        consultantId    = new SimpleIntegerProperty();
        consultantName  = new SimpleStringProperty();
        active          = true;
        location        = new SimpleStringProperty();
        description     = new SimpleStringProperty();
        startString     = new SimpleStringProperty();
        endString       = new SimpleStringProperty();
        createdBy       = new SimpleStringProperty();
    }    
    
    
    public Consultant(int consultantId, String consultantName, String location, String description, 
                        Date startDate, Date endDate, String createdBy ) { 
        this.consultantId =         new SimpleIntegerProperty(consultantId);
        this.consultantName =       new SimpleStringProperty(consultantName);
        this.location =             new SimpleStringProperty(location);
        this.description =          new SimpleStringProperty(description);
        this.startDate =            startDate;
        this.endDate =              endDate;
        SimpleDateFormat format =   new SimpleDateFormat("MM-dd-yyyy");
        this.dateString =           new SimpleStringProperty(format.format(startDate));
        this.createdBy =            new SimpleStringProperty(createdBy);      
    }
    
      // Setters
    public void setProfilePic(Blob profilePic) {
        this.profilepic = profilePic;
    }
    
    public void setConsultantId(int consultantId) {
        this.consultantId.set(consultantId);
    }
    
    public void setConsultantName(String consultantName) {
        this.consultantName.set(consultantName);
    }    

    public void setLocation(String location) {
        this.location.set(location);
    }   
    
    public void setDescription(String description) {
        this.description.set(description);
    }    
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        this.startString.set(startDate.toString());
        SimpleDateFormat format =   new SimpleDateFormat("MM-dd-yyyy");
        this.dateString =           new SimpleStringProperty(format.format(startDate));        
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        this.endString.set(endDate.toString());
    }    

    public void setCreatedBy (String createdBy) {
        this.createdBy.set(createdBy);
    }
    
  //Getters
    
    public Blob getProfilePic() {
        return this.profilepic;
    }
    
    public int getConsultantId() {
        return this.consultantId.get();
    }    
    
    public String getName() {
        return this.consultantName.get();
    }    
    
    public String getLocation() {
        return this.location.get();
    }
    
    public String getDescription() {
        return this.description.get();
    }    
    
    public Date getStartDate() {
        return this.startDate;
    }     

    public Date getEndDate() {
        return this.endDate;
    }   
    
    public String getDateString() {
        return this.dateString.get();
    } 
    
//Property Getters
   
    public IntegerProperty getConsultantIdProperty() {
        return this.consultantId;
    }    

    public StringProperty getNameProperty() {
        return this.consultantName;
    }

    public StringProperty getLocationProperty() {
        return this.location;
    }
    
    public StringProperty getDescriptionProperty() {
        return this.description;
    }    
    
    public StringProperty getStartDateProperty() {
        return this.startString;
    }     

    public StringProperty getEndDateProperty() {
        return this.endString;
    }   
    
    public StringProperty getDateStringProperty() {
        return this.dateString;
    } 

    // sqlNewCertification(consultant.getConsultantId(), cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration);
    public static String validateConsultant(String consultantName,
                                               String consultantLocation,
                                               LocalDate consultantHireDate) {
        String errorMsg = "";
        try {
            if (consultantName.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide a name or handle for the consultant.\n";
            }
            if (consultantLocation.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide the primary location of the consultant.\n";
            }            
            if (consultantHireDate == null) {
                errorMsg = errorMsg + "[!] Please provide the date the consultant was onboarded.\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return errorMsg;
        }
    }     

}
