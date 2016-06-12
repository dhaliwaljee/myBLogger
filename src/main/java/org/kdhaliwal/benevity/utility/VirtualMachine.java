package org.kdhaliwal.benevity.utility;

import java.util.List;
import java.util.Map;

public class VirtualMachine {
    private String name;
    private String ip;
    private String userName;
    private String password;
    private String branch;
    private String type;
    private List<LogFiles> logFiles;
    /**
     * @return the name of Virtual Machine
     */
    public String getName() {
        return name;
    }
    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @return the branch
     */
    public String getBranch() {
        return branch;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @return the logFiles
     */
    public List<LogFiles> getLogFiles() {
        return logFiles;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @param branch the branch to set
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @param logFiles the logFiles to set
     */
    public void setLogFiles(List<LogFiles> logFiles) {
        this.logFiles = logFiles;
    }
    
}
