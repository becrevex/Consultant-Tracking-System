//************************************************************************
// CLASS: Report
// FILENAME: Report.java
//
// DESCRIPTION
//  Report class to describe the attributes of a report record
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;
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
public class Report {
    private IntegerProperty reportId;
    private IntegerProperty assignmentId;
    private StringProperty type;
    private StringProperty fileName;
    private StringProperty checksum;
    private StringProperty status;
    private Date startDate;
    private Date endDate;
    private StringProperty dateString;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty createdBy;   
    public boolean active;
    
    public Report() {
        reportId        = new SimpleIntegerProperty();
        assignmentId    = new SimpleIntegerProperty();
        type            = new SimpleStringProperty();
        fileName        = new SimpleStringProperty();
        checksum        = new SimpleStringProperty();
        status          = new SimpleStringProperty();
        startString     = new SimpleStringProperty();
        endString       = new SimpleStringProperty();
        createdBy       = new SimpleStringProperty();
        active          = true;
    }   
    
    public Report(int reportId, int assignmentId, String type, String fileName, 
                        String filePath, String status, Date startDate, Date endDate, String createdBy ) { 
        this.reportId =           new SimpleIntegerProperty(reportId);
        this.assignmentId =         new SimpleIntegerProperty(assignmentId);
        this.type =                 new SimpleStringProperty(type);
        this.fileName =             new SimpleStringProperty(fileName);
        this.checksum =             new SimpleStringProperty(filePath);
        this.status =               new SimpleStringProperty(status);
        this.startDate =            startDate;
        this.endDate =              endDate;
        SimpleDateFormat format =   new SimpleDateFormat("MM-dd-yyyy");
        this.dateString =           new SimpleStringProperty(format.format(startDate));
        this.createdBy =            new SimpleStringProperty(createdBy);        
    }    
    
      // Setters
    public void setReportId(int reportId) {
        this.reportId.set(reportId);
    }
    public void setAssignmentId(int assignmentId) {
        this.assignmentId.set(assignmentId);
    }
    public void setType(String type) {
        this.type.set(type);
    }   
    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }    
    public void setChecksum(String checksum) {
        this.checksum.set(checksum);
    }       
    public void setStatus(String status) {
        this.status.set(status);
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
    public int getReportId() {
        return this.reportId.get();
    }   

    public int getAssignmentId() {
        return this.assignmentId.get();
    }    
    
    public String getType() {
        return this.type.get();
    }
    
    public String getFileName() {
        return this.fileName.get();
    }
    
    public String getChecksum() {
        return this.checksum.get();
    }
    
    public String getStatus() {
        return this.status.get();
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

    public IntegerProperty getReportIdProperty() {
        return this.reportId;
    }    
    
    public IntegerProperty getAssignmentIdProperty() {
        return this.assignmentId;
    }    
    public StringProperty getTypeProperty() {
        return this.type;
    }
    
    public StringProperty getFileNameProperty() {
        return this.fileName;
    }

    public StringProperty getChecksumProperty() {
        return this.checksum;
    }
    
    public StringProperty getStatusProperty() {
        return this.status;
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
    public static String validateReport(String selectedFileName,
                                               String documentType,
                                               String documentStatus,
                                               LocalDate publicationDate) {
        String errorMsg = "";
        try {
            if (selectedFileName.length() == 0) {
                errorMsg = errorMsg + "[!] Please select a document file.\n";
            }
            if (documentType.length() == 0) {
                errorMsg = errorMsg + "[!] Please select a document type.\n";
            }            
            if (documentStatus.length() == 0) {
                errorMsg = errorMsg + "[!] Please select a document status.\n";
            }
            if (publicationDate == null) {
                errorMsg = errorMsg + "[!] Please provide the document date of publication.\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return errorMsg;
        }
    }           
}
