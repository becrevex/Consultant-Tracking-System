/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scatsapplication;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author becatasu
 */
public class SystemUser {
    private IntegerProperty userId;
    private StringProperty username;
    private StringProperty password;    
    private StringProperty lastLogin;
    private StringProperty createdDate;
    
    
    public SystemUser() {
        userId = new SimpleIntegerProperty();
        username = new SimpleStringProperty();
        password = new SimpleStringProperty();
        lastLogin = new SimpleStringProperty();
        createdDate = new SimpleStringProperty();
    }
    
    public SystemUser(int userId, String username, String password, String lastLogin) {
        this.userId = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
        this.lastLogin = new SimpleStringProperty();
    }

    public String getUsername(){
        return this.username.get();
    }
    
    public String getPassword() {
        return this.password.get();
    }
    
    public int getUserId() {
        return this.userId.get();
    }
    
    public String getCreatedDate() {
        return this.createdDate.get();
    }
    
    
    public IntegerProperty getUserIdProperty() {
        return userId;
    }

    public StringProperty getUsernameProperty() {
        return username;
    }
    
    public StringProperty getCreatedDateProperty() {
        return createdDate;
    }    

    public StringProperty getPasswordProperty() {
        return password;
    }

    public StringProperty getLastLogin() {
        return lastLogin;
    }

    public void setUserId(IntegerProperty userId) {
        this.userId = userId;
    }

    public void setUsername(StringProperty username) {
        this.username = username;
    }
    
    public void setCreatedDate(StringProperty createdDate) {
        this.createdDate = createdDate;
    }

    public void setPassword(StringProperty password) {
        this.password = password;
    }

    public void setLastLogin(StringProperty lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public void setuserid(int uID) {
        this.userId.set(uID);
    }
    
    public void setusername(String username) {
        this.username.set(username);
    }

    public void setpassword(String password) {
        this.password.set(password);
    }

    public void setlastlogin(String lastlogin) {
        this.lastLogin.set(lastlogin);
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate.set(createdDate);
    }      

    public static String validateUser(String username, String password) {
        String errorMsg = "";
        try {
            if (username.equals("admin")) {
                errorMsg = errorMsg + "[!] You cannot add a new admin account.\n";
            }
            if (username.length() < 5) {
                errorMsg = errorMsg + "[!] Usernames cannot be less than 5 characters.\n";
            }            
            if (password.length() < 8) {
                errorMsg = errorMsg + "[!] Please provide a password with more than 8 characters.\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return errorMsg;
        }
    }       
    
    
 // sqlNewCertification(consultant.getConsultantId(), cCertName, cAbbreviation, cType, cLicense, startDate, cExpiration);
    public static String validateUser(SystemUser sysuser, String username, String password) {
        String errorMsg = "";
        try {
            if ((sysuser.getUserIdProperty().get() == 1) && !username.equals("admin")) {
                errorMsg = errorMsg + "[!] The default admin account name cannot be changed.\n";
            }
            if (username.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide a valid username.\n";
            }            
            if (password.length() == 0) {
                errorMsg = errorMsg + "[!] Please provide a valid password.\n";
            }
            if ((sysuser.getUserIdProperty().get() != 1) && password.length() < 8) {
                errorMsg = errorMsg + "[!] Please provide a password with more than 8 characters.\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return errorMsg;
        }
    }            

    
}
