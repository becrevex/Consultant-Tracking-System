//************************************************************************
// CLASS: DBManager
// FILENAME: DBManager.java
//
// DESCRIPTION
//  SQL Database Connection and Data Management Class
//
// COURSE AND PROJECT INFO
// C868 Software Development Capstone - SCATS System Solution, Fall 2019
// 
// AUTHOR: Brent Chambers, becrevex, bcham49@wgu.edu
//************************************************************************
package scatsapplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Modality;

/**
 *
 * @author becrevex
 * SQL Connection String
 * Server name: database-1.cywwfdawrmgh.us-west-1.rds.amazonaws.com
 * Database name: scats
 * Username: admin
 * Password: 1yJijvntTxuSbH9rHBRu
 */
public class DBManager {
    private static final String db = "scats";
    private static final String url = "jdbc:mysql://database-1.cywwfdawrmgh.us-west-1.rds.amazonaws.com:3306/" + db;
    private static final String connURL = "jdbc:mysql://database-1.cywwfdawrmgh.us-west-1.rds.amazonaws.com:3306/scats?zeroDateTimeBehavior=CONVERT_TO_NULL";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String user = "admin";
    private static final String pass = "1yJijvntTxuSbH9rHBRu";
    private static String activeUser;
    private static int openCount = 0;
    
    
    private static void setActiveUser(String userName){
        activeUser = userName;
    }    

    /*** Authentication Functions ***/
           
    /***
    * Name: validateUser()
    * Description: Validates the user exists in the database and returns the ID
    * @param  - String username
    * @return - int 
    ***/     
    public static int validateUser(String username) {
        int UID = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                ResultSet getID = st.executeQuery("select userId FROM user WHERE userName = '" + username + "'");
                System.out.println(getID);
                if (getID.next()) {
                    UID = getID.getInt("userID");
                    setActiveUser(username);
                }
                getID.close();
            } catch (SQLException e) {
                e.printStackTrace();
                }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            }
        return UID;
    }

    /***
    * Name: sqlUpdateUserTimestamp()
    * Description: updates the lastLogin timestamp of a user
    * @param  - String username
    * @return - int 
    */
    public static void sqlUpdateUserTimestamp(int UID) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String sql = "UPDATE scats.`user` SET lastLogin = '" + timestamp +
                        "', lastUpdate = CURRENT_TIMESTAMP" +
                        ", lastUpdatedBy = 'admin' WHERE userId = " + UID;
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }
    
    /***
    * Name: validatePass()
    * Description: Authenticates the user based on the userId and provided password
    * @param  - int UID, String password
    * @return - boolean -- returns true if authentication is successful
    */     
    public static boolean validatePass(int UID, String password) {
    String pw = null;
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ResultSet getPass = st.executeQuery("select password FROM user WHERE userID = '" + UID + "'");
            System.out.println(getPass);
            if (getPass.next()) {
                pw = getPass.getString("password");
            } else {
                return false;
            }
            if (pw.equals(password)) {
                sqlUpdateUserTimestamp(UID);        // update loginTimestamp on successful authentication
                return true;
                
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
        return false;
        }
    }
    
    /**
    * Name: sqlNewSystemUser()
    * Description: saves a new system user record to the database
    * @param  - int assignmentId, String fileName, String type, String status, String filePath, LocalDate publishDate
    * @return - int 
    **/
    public static void sqlNewSystemUser(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                java.sql.Date createDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
                ResultSet IDResult = st.executeQuery("SELECT userId FROM user ORDER BY userId");
                int ID;
                if (IDResult.last()) { 
                    ID = IDResult.getInt(1) + 1;
                    IDResult.close();
                } else { 
                    IDResult.close();
                    ID = 1;
                }        
                // Perform Insert
                String sql = "INSERT INTO scats.`user` (userId, username, password, active, createdDate, createdBy, lastUpdate, lastUpdatedBy) VALUES (" + 
                            ID + ", '" + 
                            username + "', '" + 
                            password + "', " +
                            "1, '" + 
                            createDate +
                            "', 'admin', CURRENT_TIMESTAMP, 'admin')";
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }    

    /***
    * Name: sqlUpdateUser()
    * Description: updates an existing user record to the database
    * @param  - int userId, String username, String password
    * @return - None
    */
    public static void sqlUpdateUser(int userId, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                String sql = "UPDATE scats.`user` SET username = '" + username +
                        "', password = '" + password +
                        "', lastUpdate = '" + timestamp +
                        "', lastUpdatedBy = 'admin' WHERE userId = " + userId;
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }

    /***
    * Name: refreshSystemUser()
    * Description: retrieves all stored consultants
    * @param  - String username
    * @return - int 
    */
    public static void refreshSystemUsers() {
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ObservableList<SystemUser> sysusers = SystemUserCollection.getSystemUserCollection();
            sysusers.clear();
            ResultSet getResults = st.executeQuery("SELECT userId FROM scats.`user` WHERE active = 1");
            ArrayList<Integer> userArray = new ArrayList<>();
            while (getResults.next()) {
                userArray.add(getResults.getInt(1));
            }
            for (int ID:userArray) {
                SystemUser sys = new SystemUser();
                ResultSet getUserResults = st.executeQuery("SELECT userId, username, password, lastLogin, createdDate FROM scats.`user` WHERE userId = " + ID);
                getUserResults.next();
                int uID = getUserResults.getInt(1);
                String uUsername = getUserResults.getString(2);
                String uPassword = getUserResults.getString(3);
                String uLastlogin = getUserResults.getString(4);
                String ucreatedDate = getUserResults.getString(5);
                sys.setuserid(uID);
                sys.setusername(uUsername);
                sys.setpassword(uPassword); 
                sys.setlastlogin(uLastlogin);
                sys.setCreatedDate(ucreatedDate);
                sysusers.add(sys);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
    
    /***
    * Name: deleteSystemUser()
    * Description: deletes provided SystemUser from the database
    * @param  - SystemUser systemUser
    * @return - boolean true if successful 
    */    
    public static boolean sqlDeleteSystemUser(SystemUser systemUser) {
        int userId = systemUser.getUserId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete User " + systemUser.getUsername() + "?");
        alert.setContentText("Please confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                    String sql = "DELETE FROM user WHERE userId = " + userId;
                    st.executeUpdate(sql);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } 
        return false;
    }    
    
    
    /** User Management Functions **/
    
    /***
    * Name: lookupUID()
    * Description: Returns Consultant name from database from provided consultantID
    * @param  - int UID, String password
    * @return - String -- returns Consultant Name
    */ 
    public static String lookupUID(int CID) {
    String consultantName = "";
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ResultSet getName = st.executeQuery("select consultantName FROM scats.`consultant` WHERE consultantId = " + CID);
            System.out.println(getName);
            if (getName.next()) {
                consultantName = getName.getString("consultantName");
            }
            getName.close();
        } catch (SQLException e) {
            e.printStackTrace();
            }
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
        }
    return consultantName;
}
    

    
    /** Consultant Management Functions **/
   
    /***
    * Name: sqlNewConsultant()
    * Description: saves a new consultant record to the database
    * @param  - String username
    * @return - int 
    */
    public static void sqlNewConsultant(String consultantName, String location, String description, LocalDate startDate) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                ResultSet CIDResult = st.executeQuery("SELECT consultantId FROM consultant ORDER BY consultantId");
                int consultantId;
                if (CIDResult.last()) { 
                    consultantId = CIDResult.getInt(1) + 1;
                    CIDResult.close();
                } else { 
                    CIDResult.close();
                    consultantId = 1;
                }        
                // Perform Insert
                Date start_date = Date.valueOf(startDate.plusDays(1));
                String sql = "INSERT INTO scats.`consultant` (consultantId, consultantName, active, location, description, createdDate, createdBy, lastUpdate, lastUpdatedBy) VALUES ('" + 
                                    consultantId + "', '" + consultantName + "', " + "'1', '" + location + "', '" + description + "', '" + 
                                    start_date  + "', 'admin', CURRENT_TIMESTAMP, 'admin')";
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }

    /***
    * Name: sqlUpdateConsultant()
    * Description: updates a new consultant record to the database
    * @param  - consultantName, location, description, startDate
    * @return - None 
    */
    public static void sqlUpdateConsultant(int consultantId, String consultantName, String location, String description, LocalDate startDate) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                Date start_date = Date.valueOf(startDate.plusDays(1));
                String sql = "UPDATE scats.`consultant` SET consultantName = '" + consultantName +
                        "', location = '" + location +
                        "', description = '" + description +
                        "', createdDate = '" + start_date + 
                        "', lastUpdate = CURRENT_TIMESTAMP" +
                        ", lastUpdatedBy = 'admin' WHERE consultantId = " + consultantId;
                System.out.println(sql);
                
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }

    public static void sqlUpdateConsultantProfilePic(int consultantId, String profilePic) {
        //System.out.println(profilePic.substring(6));
        String picPath = profilePic;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                String sql = "UPDATE scats.`consultant` SET profilepic = ? WHERE consultantId = ?";
                PreparedStatement statement = conn.prepareStatement(sql);
                InputStream inputStream = new FileInputStream(new File(picPath));
                statement.setBlob(1, inputStream);
                statement.setInt(2, consultantId);
                System.out.println(sql);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }    
    
    /***
    * Name: refreshConsultants()
    * Description: retrieves all stored consultants
    * @param  - String username
    * @return - int 
    */
    public static void refreshConsultants() {
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ObservableList<Consultant> consultants = ConsultantCollection.getConsultantCollection();
            consultants.clear();
            ResultSet getResults = st.executeQuery("SELECT consultantId FROM scats.`consultant` WHERE active = 1");
            ArrayList<Integer> consultantArray = new ArrayList<>();
            while (getResults.next()) {
                consultantArray.add(getResults.getInt(1));
            }
            for (int CID:consultantArray) {
                Consultant consultant = new Consultant();
                ResultSet getConsultantResults = st.executeQuery("SELECT consultantId, consultantName, location, description, createdDate, profilepic FROM scats.`consultant` WHERE consultantId = " + CID);
                getConsultantResults.next();
                int cID = getConsultantResults.getInt(1);
                String cName = getConsultantResults.getString(2);
                String cLocation = getConsultantResults.getString(3);
                String cDescription = getConsultantResults.getString(4);
                Date cCreatedDate = getConsultantResults.getDate(5);
                Blob cProfilePic = getConsultantResults.getBlob(6);
                consultant.setConsultantId(cID);
                consultant.setConsultantName(cName);
                consultant.setLocation(cLocation);
                consultant.setDescription(cDescription);
                consultant.setStartDate(cCreatedDate);
                consultant.setProfilePic(cProfilePic);
                consultants.add(consultant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*** Name: deleteConsultant()
    * Description: deletes selected engagement
    * @param  - Engagement engagement
    * @return - None 
    */    
    public static boolean deleteConsultant(Consultant consultant) {
        int consultantId = consultant.getConsultantId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Consultant");
        alert.setHeaderText("Delete Consultant " + consultant.getName()+"?");
        alert.setContentText("Please confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                    String sql = "DELETE FROM consultant WHERE consultantId = " + consultantId;
                    st.executeUpdate(sql);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } 
        return false;
    }

    
    
    /*** Engagement Management Functions ***/
    
    /***
    * Name: sqlNewEngagement()
    * Description: saves a new Engagement record to the database
    * @param  - String username
    * @return - int 
    */
    public static void sqlNewEngagement(int consultantId, String location, String company, String type,
                                        String status, String description, LocalDate startDate, LocalDate endDate) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                ResultSet AIDResult = st.executeQuery("SELECT assignmentId FROM assignment ORDER BY assignmentId");
                int assignmentId;
                if (AIDResult.last()) { 
                    assignmentId = AIDResult.getInt(1) + 1;
                    AIDResult.close();
                } else { 
                    AIDResult.close();
                    assignmentId = 1;
                }        
                // Perform Insert
                Date start_date = Date.valueOf(startDate.plusDays(1));
                Date end_date = Date.valueOf(endDate.plusDays(1));
                String sql = "INSERT INTO scats.`assignment` (assignmentId, consultantId, active, company, location, type, status, description, startDate, endDate, createdDate, createdBy, lastUpdate, lastUpdatedBy) VALUES ('" + 
                                    assignmentId + "', '" + consultantId + "', " + "'1', '" + company + "', '" + location + "', '" + 
                                    type  + "', '" + status + "', '" + description + "', '" + start_date + "', '" + end_date + "', '" + timestamp + "', 'admin', CURRENT_TIMESTAMP, 'admin')";
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }

    /***
    * Name: sqlUpdateEngagement()
    * Description: updates a new Engagement record to the database
    * @param  - String username
    * @return - int 
    */
    public static void sqlUpdateEngagement(int engagementId, int consultantId, String location, String company, String type,
                                        String status, String description, LocalDate startDate, LocalDate endDate) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                Date start_date = Date.valueOf(startDate.plusDays(1));
                Date end_date = Date.valueOf(endDate.plusDays(1));                
                String sql = "UPDATE scats.`assignment` SET consultantId = " + consultantId +
                        ", location = '" + location +
                        "', company = '" + company +
                        "', type = '" + type +
                        "', status = '" + status +
                        "', description = '" + description +
                        "', startDate = '" + start_date + 
                        "', endDate = '" + end_date +
                        "', lastUpdate = CURRENT_TIMESTAMP" +
                        ", lastUpdatedBy = 'admin' WHERE assignmentId = " + engagementId;
                System.out.println("DBManager:sqlUpdateEngagement: " + startDate.toString());
                System.out.println("DBManager:sqlUpdateEngagement: " + endDate.toString());

                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }

    /***
    * Name: refreshEngagements()
    * Description: retrieves all active engagements and assignments
    * @param  - String username
    * @return - int 
    */
    public static void refreshEngagements() {
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ObservableList<Engagement> engagements = EngagementCollection.getEngagementCollection();
            engagements.clear();
            ResultSet getResults = st.executeQuery("SELECT assignmentId FROM scats.`assignment` WHERE active = 1");
            ArrayList<Integer> engagementArray = new ArrayList<>();
            while (getResults.next()) {
                engagementArray.add(getResults.getInt(1));
            }
            for (int EID:engagementArray) {
                Engagement engagement = new Engagement();
                ResultSet getEngagementResults = st.executeQuery("SELECT company, consultantId, location, type, status, description, startDate, endDate, createdDate FROM scats.`assignment` WHERE assignmentId = " + EID);
                getEngagementResults.next();
                String eCompany = getEngagementResults.getString(1);
                int eConsultant = getEngagementResults.getInt(2);
                String eLocation = getEngagementResults.getString(3);
                String eType = getEngagementResults.getString(4);
                String eStatus = getEngagementResults.getString(5);
                String eDescription = getEngagementResults.getString(6);
                Date eStartDate = getEngagementResults.getDate(7);
                Date eEndDate = getEngagementResults.getDate(8);
                String eDateCreated = getEngagementResults.getString(9);
                
                engagement.setAssignmentId(EID);
                engagement.setCompany(eCompany);
                engagement.setConsultantId(eConsultant);
                engagement.setLocation(eLocation);
                engagement.setType(eType);
                engagement.setStatus(eStatus);
                engagement.setDescription(eDescription);
                engagement.setStartDate(eStartDate);
                engagement.setEndDate(eEndDate);
                engagement.setDateCreated(eDateCreated);

                engagements.add(engagement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /***
    * Name: refreshAllEngagements()
    * Description: retrieves ALL stored engagements and assignments
    * @param  - String username
    * @return - int 
    */
    public static void refreshAllEngagements() {
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ObservableList<Engagement> engagements = EngagementCollection.getAllEngagementCollection();
            engagements.clear();
            ResultSet getResults = st.executeQuery("SELECT assignmentId FROM scats.`assignment`");
            ArrayList<Integer> engagementArray = new ArrayList<>();
            while (getResults.next()) {
                engagementArray.add(getResults.getInt(1));
            }
            for (int EID:engagementArray) {
                Engagement engagement = new Engagement();
                ResultSet getEngagementResults = st.executeQuery("SELECT company, consultantId, location, type, status, description, startDate, endDate, createdDate, active FROM scats.`assignment` WHERE assignmentId = " + EID);
                getEngagementResults.next();
                String eCompany = getEngagementResults.getString(1);
                int eConsultant = getEngagementResults.getInt(2);
                String eLocation = getEngagementResults.getString(3);
                String eType = getEngagementResults.getString(4);
                String eStatus = getEngagementResults.getString(5);
                String eDescription = getEngagementResults.getString(6);
                Date eStartDate = getEngagementResults.getDate(7);
                Date eEndDate = getEngagementResults.getDate(8);
                String eDateCreated = getEngagementResults.getString(9);
                int eActive = getEngagementResults.getInt(10);
               
                engagement.setAssignmentId(EID);
                engagement.setCompany(eCompany);
                engagement.setConsultantId(eConsultant);
                engagement.setLocation(eLocation);
                engagement.setType(eType);
                engagement.setStatus(eStatus);
                engagement.setDescription(eDescription);
                engagement.setStartDate(eStartDate);
                engagement.setEndDate(eEndDate);
                engagement.setDateCreated(eDateCreated);
                if (eActive == 1) {
                    engagement.setActive();
                } else {
                    engagement.setInactiveActive();
                }

                engagements.add(engagement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
    
    /***
    * Name: deleteEngagement()
    * Description: deletes selected engagement
    * @param  - Engagement engagement
    * @return - None 
    */    
    public static boolean deleteEngagement(Engagement engagement) {
        int assignmentId = engagement.getAssignmentId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Engagement");
        alert.setHeaderText("Delete Engagement " + engagement.getCompany() + " (" + engagement.getType()+ ")?");
        alert.setContentText("Please confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                    String sql = "DELETE FROM assignment WHERE assignmentId = " + assignmentId;
                    st.executeUpdate(sql);
                    return true;
                } catch (SQLException e) {
                    //e.printStackTrace();
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.initModality(Modality.NONE);
                    alert2.setTitle("Cannot Delete");
                    alert2.setHeaderText("Engagement " + engagement.getCompany() + " (" + engagement.getType()+ ")" + "\ncontains existing documents and/or reports. \nPlease `archive` to remove from application view.");
                    alert2.showAndWait();
                    return false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } 
        return false;
    }

    /***
    * Name: sqlDeactivateEngagement()
    * Description: deactivates selected engagement
    * @param  - int engagementId
    * @return - None 
    */      
    public static boolean sqlDeactivateEngagement(int engagementId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("NOTICE");
        alert.setHeaderText("Archive Engagement?");
        alert.setContentText("Please confirm");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                String sql = "UPDATE scats.`assignment` SET active = 0" +
                        ", lastUpdate = CURRENT_TIMESTAMP" +
                        ", lastUpdatedBy = 'admin' WHERE assignmentId = " + engagementId;
                System.out.println(sql);
                st.executeUpdate(sql);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  
            return false;
        }
    }

    /***
    * Name: sqlDeactivateEngagement()
    * Description: deactivates selected engagement
    * @param  - int engagementId
    * @return - None 
    */      
    public static boolean sqlReactivateEngagement(int engagementId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                String sql = "UPDATE scats.`assignment` SET active = 1" +
                        ", lastUpdate = CURRENT_TIMESTAMP" +
                        ", lastUpdatedBy = 'admin' WHERE assignmentId = " + engagementId;
                System.out.println(sql);
                st.executeUpdate(sql);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  
            return false;
        }
    }    
    
    
    
    /*** Certificate Management Functions ***/    

    /***
    * Name: sqlNewCertificate()
    * Description: saves a new certificate record to the database
    * @param  - consultant.getConsultantId(), cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration
    * @return - None 
    */
    public static void sqlNewCertification(int consultantId, String cCertName, String cAbbreviation, String cType,
                                        String cLicense, LocalDate startDate, String cExpiration) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                ResultSet CIDResult = st.executeQuery("SELECT certificationId FROM certification ORDER BY certificationId");
                int certificationId;
                if (CIDResult.last()) { 
                    certificationId = CIDResult.getInt(1) + 1;
                    CIDResult.close();
                } else { 
                    CIDResult.close();
                    certificationId = 1;
                }        
                // Perform Insert
                Date start_date = Date.valueOf(startDate.plusDays(1));
                String sql = "INSERT INTO scats.`certification` (certificationId, consultantId, certificationName, abbreviation, type, license, startdate, expiration, createdDate, createdBy, lastUpdate, lastUpdatedBy) VALUES (" + 
                        certificationId + ", " + 
                        consultantId + ", '" + 
                        cCertName + "', '" + 
                        cAbbreviation + "', '" + 
                        cType + "', '" + 
                        cLicense + "', '" + 
                        start_date + "', '" + 
                        cExpiration + "', CURRENT_DATE, 'admin', CURRENT_TIMESTAMP, 'admin')";
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }    

    /***
    * Name: refreshCertification()
    * Description: retrieves all stored certifications
    * @param  - None
    * @return - None
    */
    public static void refreshCertifications() {
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ObservableList<Certification> certifications = CertificationCollection.getCertificationCollection();
            certifications.clear();
            ResultSet getResults = st.executeQuery("SELECT certificationId FROM scats.`certification` WHERE active = 1");
            ArrayList<Integer> certificationArray = new ArrayList<>();
            while (getResults.next()) {
                certificationArray.add(getResults.getInt(1));
            }
            for (int certID:certificationArray) {
                Certification certification = new Certification();
                ResultSet getCertResults = st.executeQuery("SELECT certificationId, consultantId, certificationName, abbreviation, type, license, startdate, expiration FROM scats.`certification` WHERE certificationId = " + certID);
                getCertResults.next();
                int certificationId = getCertResults.getInt(1);
                int consultantId = getCertResults.getInt(2);
                String certName = getCertResults.getString(3);
                String certAbbreviation = getCertResults.getString(4);
                String certType = getCertResults.getString(5);
                String certLicense = getCertResults.getString(6);
                Date certStartDate = getCertResults.getDate(7);
                String certExpiration = getCertResults.getString(8);
                
                certification.setCertificationId(certificationId);
                certification.setConsultantId(consultantId);
                certification.setName(certName);
                certification.setAbbreviation(certAbbreviation);
                certification.setType(certType);
                certification.setLicenseNumber(certLicense);
                certification.setStartDate(certStartDate);
                certification.setExpiration(certExpiration);

                certifications.add(certification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
    * Name: sqlUpdateCertification()
    * Description: updates a new Engagement record to the database
    * @param  - String username
    * @return - int 
    */
    public static void sqlUpdateCertification(int certificationId, String cCertName, String cAbbreviation, String cType,
                                        String cLicense, LocalDate startDate, String eExpiration) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                Date start_date = Date.valueOf(startDate.plusDays(1));
                String sql = "UPDATE scats.`certification` SET certificationName = '" + cCertName +
                        "', abbreviation = '" + cAbbreviation +
                        "', type = '" + cType +
                        "', license = '" + cLicense +
                        "', startDate = '" + start_date + 
                        "', expiration = '" + eExpiration +
                        "', lastUpdate = CURRENT_TIMESTAMP" +
                        ", lastUpdatedBy = 'admin' WHERE certificationId = " + certificationId;
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }
    
    /***
    * Name: deleteCertification()
    * Description: deletes selected certification
    * @param  - String username
    * @return - int 
    */    
    public static boolean deleteCertification(Certification certification) {
        int certificationId = certification.getCertificationId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Certification");
        alert.setHeaderText("Delete Certification " + certification.getAbbreviation() + " (" + certification.getLicenseNumber() + ")?");
        alert.setContentText("Please confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                    String sql = "DELETE FROM certification WHERE certificationId = " + certificationId;
                    st.executeUpdate(sql);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } 
        return false;
    }
    
    
    
    /*** Document Management Functions ***/

    /**
    * Name: sqlNewDocument()
    * Description: saves a new Engagement record to the database
    * @param  - String username
    * @return - int 
    **/
    public static void sqlNewDocument(int assignmentId, String fileName, String type, String status, String checksum, LocalDate publishDate) {
                                         
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                //Retrieve appointmentId increment
                ResultSet DIDResult = st.executeQuery("SELECT documentId FROM document ORDER BY documentId");
                int documentId;
                if (DIDResult.last()) { 
                    documentId = DIDResult.getInt(1) + 1;
                    DIDResult.close();
                } else { 
                    DIDResult.close();
                    documentId = 1;
                }        
                // Perform Insert
                String sql = "INSERT INTO scats.`document` (documentId, assignmentId, filename, type, status, checksum, publishDate, createdDate, createdBy, lastUpdate, lastUpdatedBy) VALUES (" + 
                            documentId + ", " + 
                            assignmentId + ", '" + 
                            fileName + "', '" + 
                            type + "', '" + 
                            status + "', '" + 
                            checksum + "', '" + 
                            publishDate.plusDays(1) + "', " + 
                            "CURRENT_DATE, 'admin', CURRENT_TIMESTAMP, 'admin')";
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }

    /***
    * Name: refreshDocuments()
    * Description: retrieves all stored document details
    * @param  - None
    * @return - None
    */
    public static void refreshDocuments() {
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ObservableList<Document> documents = DocumentCollection.getDocumentCollection();
            documents.clear();
            ResultSet getResults = st.executeQuery("SELECT documentId FROM scats.`document` WHERE active = 1");
            ArrayList<Integer> documentArray = new ArrayList<>();
            while (getResults.next()) {
                documentArray.add(getResults.getInt(1));
            }
            for (int DID:documentArray) {
                Document document = new Document();
                ResultSet getDocResults = st.executeQuery("SELECT documentId, assignmentId, filename, type, status, checksum, publishDate FROM scats.`document` WHERE documentId = " + DID);
                getDocResults.next();
                int documentId = getDocResults.getInt(1);
                int assignmentId = getDocResults.getInt(2);
                String fileName = getDocResults.getString(3);
                String type = getDocResults.getString(4);
                String status = getDocResults.getString(5);
                String checksum = getDocResults.getString(6);
                Date publishDate = getDocResults.getDate(7);
                
                document.setDocumentId(documentId);
                document.setAssignmentId(assignmentId);
                document.setFileName(fileName);
                document.setType(type);
                document.setStatus(status);
                document.setChecksum(checksum);
                document.setStartDate(publishDate);
                documents.add(document);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /***
    * Name: deleteDocument()
    * Description: deletes specified Document record
    * @param  - Document document
    * @return - boolean 
    */    
    public static boolean deleteDocument(Document document) {
        int documentId = document.getDocumentId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Document");
        alert.setHeaderText("Delete Certification " + document.getFileName() + " (" + document.getType() + ")?");
        alert.setContentText("Please confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                    String sql = "DELETE FROM document WHERE documentId = " + documentId;
                    st.executeUpdate(sql);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } 
        return false;
    }    
    
     /*** Report Management Functions ***/
    
    /**
    * Name: sqlNewReport()
    * Description: saves a new report record to the database
    * @param  - int assignmentId, String fileName, String type, String status, String filePath, LocalDate publishDate
    * @return - int 
    **/
    public static void sqlNewReport(int assignmentId, String fileName, String type, String status, String checksum, LocalDate publishDate) {
                                         
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                ResultSet RIDResult = st.executeQuery("SELECT reportId FROM report ORDER BY reportId");
                int reportId;
                if (RIDResult.last()) { 
                    reportId = RIDResult.getInt(1) + 1;
                    RIDResult.close();
                } else { 
                    RIDResult.close();
                    reportId = 1;
                }        
                Date publish_date = Date.valueOf(publishDate.plusDays(1));
                String sql = "INSERT INTO scats.`report` (reportId, assignmentId, filename, type, status, checksum, publishDate, createdDate, createdBy, lastUpdate, lastUpdatedBy) VALUES (" + 
                            reportId + ", " + 
                            assignmentId + ", '" + 
                            fileName + "', '" + 
                            type + "', '" + 
                            status + "', '" + 
                            checksum + "', '" + 
                            publish_date + "', " + 
                            "CURRENT_DATE, 'admin', CURRENT_TIMESTAMP, 'admin')";
                System.out.println(sql);
                st.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();                
        }
    }

    /***
    * Name: refreshReports()
    * Description: retrieves all stored report records
    * @param  - None
    * @return - None
    */
    public static void refreshReports() {
        try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
            ObservableList<Report> reports = ReportCollection.getReportCollection();
            reports.clear();
            ResultSet getResults = st.executeQuery("SELECT reportId FROM scats.`report` WHERE active = 1");
            ArrayList<Integer> reportArray = new ArrayList<>();
            while (getResults.next()) {
                reportArray.add(getResults.getInt(1));
            }
            for (int RID:reportArray) {
                Report report = new Report();
                ResultSet getReportResults = st.executeQuery("SELECT reportId, assignmentId, filename, type, status, checksum, publishDate FROM scats.`report` WHERE reportId = " + RID);
                getReportResults.next();
                int documentId = getReportResults.getInt(1);
                int assignmentId = getReportResults.getInt(2);
                String fileName = getReportResults.getString(3);
                String type = getReportResults.getString(4);
                String status = getReportResults.getString(5);
                String checksum = getReportResults.getString(6);
                Date publishDate = getReportResults.getDate(7);
                
                report.setReportId(documentId);
                report.setAssignmentId(assignmentId);
                report.setFileName(fileName);
                report.setType(type);
                report.setStatus(status);
                report.setChecksum(checksum);
                report.setStartDate(publishDate);
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    /***
    * Name: deleteReport()
    * Description: deletes specified Report record
    * @param  - Report report
    * @return - boolean 
    */    
    public static boolean deleteReport(Report report) {
        int reportId = report.getReportId();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle("Delete Report");
        alert.setHeaderText("Delete Report " + report.getFileName() + "?");
        alert.setContentText("Please confirm");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(url, user, pass); Statement st = conn.createStatement()) {
                    String sql = "DELETE FROM report WHERE reportId = " + reportId;
                    st.executeUpdate(sql);
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } 
        return false;
    }
    
}

    
    


