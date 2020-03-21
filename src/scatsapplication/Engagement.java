//************************************************************************
// CLASS: Engagement
// FILENAME: Engagement.java
//
// DESCRIPTION
//  Engagement class to describe attributes of each engagement object
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import static scatsapplication.DBManager.refreshConsultants;
import static scatsapplication.ConsultantCollection.getConsultantCollection;

/**
 *
 * @author becatasu
 */
public class Engagement {
    private IntegerProperty assignmentId;
    private IntegerProperty consultantId;
    private StringProperty company;
    private StringProperty location;
    private StringProperty type;
    private StringProperty status;
    private StringProperty description;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private Date startDate;
    private Date endDate;
    private StringProperty dateString;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty createdBy;
    private StringProperty consultantName;
    private StringProperty dateCreated;
    private StringProperty active;
    
    public Engagement() {
        assignmentId    = new SimpleIntegerProperty();
        consultantId    = new SimpleIntegerProperty();
        company         = new SimpleStringProperty();
        location        = new SimpleStringProperty();
        type            = new SimpleStringProperty();
        status          = new SimpleStringProperty();
        description     = new SimpleStringProperty();
        startString     = new SimpleStringProperty();
        endString       = new SimpleStringProperty();
        createdBy       = new SimpleStringProperty();
        dateCreated     = new SimpleStringProperty();
        active          = new SimpleStringProperty();
        
                
    }
    
    //Constructor
    public Engagement(int assignmentId, int consultantId, String company,
                      String location, String type, String status, String description,
                      Date startDate, Date endDate, String createdBy) {
        this.assignmentId =         new SimpleIntegerProperty(assignmentId);
        this.consultantId =         new SimpleIntegerProperty(consultantId);
        this.company =              new SimpleStringProperty(company);
        this.location =             new SimpleStringProperty(location);
        this.type =                 new SimpleStringProperty(type);
        this.status =               new SimpleStringProperty(status);
        this.description =          new SimpleStringProperty(description);
        this.startDate =            startDate;
        this.endDate =              endDate;
        SimpleDateFormat format =   new SimpleDateFormat("MM-dd-yyyy");
        this.dateString =           new SimpleStringProperty(format.format(startDate));
        this.createdBy =            new SimpleStringProperty(createdBy);
    }

    // Setters
    
    public void setActive() {
        this.active.set("True");
    }
    
    public void setInactiveActive() {
        this.active.set("False");
    }    
    
    public void setDateCreated(String createdDate) {
        this.dateCreated.set(createdDate);
    }
    
    public void setAssignmentId(int assignmentId) {
        this.assignmentId.set(assignmentId);
    }

    public void setConsultantId(int consultantId) {
        this.consultantId.set(consultantId);
    }
    
    public void setCompany(String company) {
        this.company.set(company);
    }

    public void setLocation(String location) {
        this.location.set(location);
    }   
    
    public void setType(String type) {
        this.type.set(type);
    }    
    
    public void setStatus(String status) {
        this.status.set(status);
    }   
    
    public void setDescription(String description) {
        this.description.set(description);
    }    
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        this.startString.set(startDate.toString());
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        this.endString.set(endDate.toString());
    }    

    public void setCreatedBy (String createdBy) {
        this.createdBy.set(createdBy);
    }
    
    //Getters
    
    public String getActive() {
        return this.active.get();
    }
    
    public String getDateCreated() {
        return this.dateCreated.get();
    }
    
    public int getAssignmentId() {
        return this.assignmentId.get();
    }
    
    public int getConsultantId() {
        return this.consultantId.get();
    }    
    
    public String getCompany() {
        return this.company.get();
    }

    public String getLocation() {
        return this.location.get();
    }
    
    public String getType() {
        return this.type.get();
    }

    public String getStatus() {
        return this.status.get();
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
    
    public StringProperty getActiveProperty() {
        return this.active;
    }
    
    public StringProperty getDateCreatedProperty() {
        return this.dateCreated;
    }
    
    public IntegerProperty getAssignmentIdProperty() {
        return this.assignmentId;
    }
    
    public IntegerProperty getConsultantIdProperty() {
        return this.consultantId;
    } 
    
    public StringProperty getConsultantIdResolution() {
        ObservableList<Consultant> consultants = getConsultantCollection();
        for (Consultant consultant : consultants) {
            if (this.consultantId.get() == consultant.getConsultantId()) {
                return consultant.getNameProperty();
            }
        }
        return null;
    }
    
    public StringProperty getCompanyProperty() {
        return this.company;
    }

    public StringProperty getLocationProperty() {
        return this.location;
    }
    
    public StringProperty getTypeProperty() {
        return this.type;
    }

    public StringProperty getStatusProperty() {
        return this.status;
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

    // Validation TBD
    public static String validateEngagement(String engagementLocation,
                                               String engagementCompany,
                                               String engagementType,
                                               LocalDate startDate,
                                               LocalDate endDate) {
        String errorMsg = "";
        try {
            if (engagementLocation.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide the location of the engagement.\n";
            }
            if (engagementCompany.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide the company or firm associated with this engagement.\n";
            }            
            if (engagementType == null) {
                errorMsg = errorMsg + "[!] Please select an appropriate engagement type.\n";
            }
            if (startDate == null) {
                errorMsg = errorMsg + "[!] Please provide the estimated start date for engagement fieldwork.\n";
            }
            if (endDate == null) {
                errorMsg = errorMsg + "[!] Please provide the estimated end date for engagement fieldwork.\n";
            }
            if (startDate.isAfter(endDate)){
                errorMsg = errorMsg + "[!] Please ensure engagement fieldwork dates are appropriate.\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return errorMsg;
        }
    } 
    
}
    
    