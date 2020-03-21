//************************************************************************
// CLASS: Certification
// FILENAME: Certification.java
//
// DESCRIPTION
//  Certification class to describe the attribute of a certificaiton record
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
import static scatsapplication.ConsultantCollection.getConsultantCollection;
/**
 *
 * @author becatasu
 */
public class Certification {
    private IntegerProperty certificationId;
    private IntegerProperty consultantId;
    private StringProperty name;
    private StringProperty abbreviation;
    private StringProperty type;
    private StringProperty licenseNumber;
    private StringProperty expiration;
    private Date startDate;
    private Date endDate;
    private StringProperty dateString;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty createdBy;   
    public boolean active;
    
    public Certification() {
        certificationId = new SimpleIntegerProperty();
        consultantId    = new SimpleIntegerProperty();
        name            = new SimpleStringProperty();
        abbreviation    = new SimpleStringProperty();
        type            = new SimpleStringProperty();
        licenseNumber   = new SimpleStringProperty();
        expiration      = new SimpleStringProperty();
        startString     = new SimpleStringProperty();
        endString       = new SimpleStringProperty();
        createdBy       = new SimpleStringProperty();
        active          = true;
    }   
    
    public Certification(int certificationId, int consultantId, String name, String abbreviation, 
                        String type, String licenseNumber, String expiration, Date startDate, Date endDate, String createdBy ) { 
        this.certificationId =      new SimpleIntegerProperty(certificationId);
        this.consultantId =         new SimpleIntegerProperty(consultantId);
        this.name =                 new SimpleStringProperty(name);
        this.abbreviation =         new SimpleStringProperty(abbreviation);
        this.type =                 new SimpleStringProperty(type);
        this.licenseNumber =        new SimpleStringProperty(licenseNumber);
        this.expiration =           new SimpleStringProperty(expiration);
        this.startDate =            startDate;
        this.endDate =              endDate;
        SimpleDateFormat format =   new SimpleDateFormat("MM-dd-yyyy");
        this.dateString =           new SimpleStringProperty(format.format(startDate));
        this.createdBy =            new SimpleStringProperty(createdBy);        
    }    
    
      // Setters
    
    public void setExpiration(String expiration) {
        this.expiration.set(expiration);
    }
    
    public void setCertificationId(int certificationId) {
        this.certificationId.set(certificationId);
    }

    public void setConsultantId(int consultantId) {
        this.consultantId.set(consultantId);
    }

    public void setName(String name) {
        this.name.set(name);
    }   
    
    public void setAbbreviation(String abbreviation) {
        this.abbreviation.set(abbreviation);
    }    
    
    public void setType(String type) {
        this.type.set(type);
    }       
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber.set(licenseNumber);
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

    public StringProperty getConsultantIdResolution() {
        ObservableList<Consultant> consultants = getConsultantCollection();
        for (Consultant consultant : consultants) {
            if (this.consultantId.get() == consultant.getConsultantId()) {
                return consultant.getNameProperty();
            }
        }
        return null;
    }    
    
    public String getExpiration() {
        return this.expiration.get();
    }
    
    public int getCertificationId() {
        return this.certificationId.get();
    }   

    public int getConsultantId() {
        return this.consultantId.get();
    }    
    
    public String getName() {
        return this.name.get();
    }
    
    public String getAbbreviation() {
        return this.abbreviation.get();
    }
    
    public String getType() {
        return this.type.get();
    }
    
    public String getLicenseNumber() {
        return this.licenseNumber.get();
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
    
    public StringProperty getExpirationProperty() {
        return this.expiration;
    }

    public IntegerProperty getCertificationIdProperty() {
        return this.certificationId;
    }    
    
    public IntegerProperty getConsultantIdProperty() {
        return this.consultantId;
    }    
    public StringProperty getNameProperty() {
        return this.name;
    }
    
    public StringProperty getAbbreviationProperty() {
        return this.abbreviation;
    }

    public StringProperty getTypeProperty() {
        return this.type;
    }
    
    public StringProperty getLicenseNumberProperty() {
        return this.licenseNumber;
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
    // sqlNewCertification(consultant.getConsultantId(), cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration);
    public static String validateCertification(String certName,
                                               String certAbbreviation,
                                               String certType,
                                               String certLicense,
                                               LocalDate certStartDate,
                                               String certExpiration) {
        String errorMsg = "";
        try {
            if (certName.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide a certification name.\n";
            }
            if (certAbbreviation.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide a certification abbreviation. (e.g. OSCP)\n";
            }            
            if (certType == null) {
                errorMsg = errorMsg + "[!] Please select a certification type.\n";
            }
            if (certLicense.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide a certification license number.\n";
            }
            if (certStartDate == null) {
                errorMsg = errorMsg + "[!] Please provide the date the certification was obtained.\n";
            }
            if (certExpiration == null) {
                errorMsg = errorMsg + "[!] Please provide a certification expiration time frame.\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return errorMsg;
        }
    }        
}
