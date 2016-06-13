package org.kdhaliwal.benevity.utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class Config {
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String hostName;
    private String hostIp;
    private String knownHostsFile;
    private String identityFile;
    private int timeout;
    private Map<String, VirtualMachine> vm = new HashMap<String, VirtualMachine>();
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }
    
    public void setKnownHostsFile(String knownHostsFile) {
        this.knownHostsFile = knownHostsFile;
    }
    
    public void setIdentityFile(String identityFile) {
        this.identityFile = identityFile;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public void setVm(Map<String, VirtualMachine> vm) {
        this.vm = vm;
    }
    
    public void addVm(String name, VirtualMachine vm){
        this.getVm().put(name, vm);
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public String getHostIp() {
        return hostIp;
    }
    
    public String getKnownHostsFile() {
        return knownHostsFile;
    }
    
    public String getIdentityFile() {
        return identityFile;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public Map<String, VirtualMachine> getVm() {
        return vm;
    }
    
    public static Config create() throws YamlException, FileNotFoundException{
        Config config = new Config();
        
        YamlReader reader = new YamlReader(new FileReader("config.yml"));
       
        //First Node
        @SuppressWarnings("unchecked")
        Map<String, String> firstNode = (Map<String, String>) reader.read();
        config.setUsername(firstNode.get("username"));
        config.setHostName(firstNode.get("host machine"));
        config.setHostIp(firstNode.get("host ip"));
        config.setKnownHostsFile(firstNode.get("knownHosts file"));
        config.setIdentityFile(firstNode.get("identity file"));
        config.setTimeout(Integer.valueOf(firstNode.get("timeout")));
        
        //Sub Nodes, its ArrayList
        while(true){
            List<Map<String, Object>> read = (List<Map<String, Object>>) reader.read();
            
            if(read == null){
                break;
            }
            Map<String, Object> node = read.get(0);
            
            //Virtual Machine Configuration
            VirtualMachine tempVm = new VirtualMachine();
            tempVm.setName(node.get("virtual machine name").toString());
            tempVm.setIp(node.get("virtual machine ip").toString());
            tempVm.setUserName(node.get("user").toString());
            tempVm.setPassword(node.get("password").toString());
            tempVm.setBranch(node.get("branch").toString());
            tempVm.setType(node.get("type").toString());

                //Log Files Configuration
                /*Map<String, String> logMap = new HashMap<String, String>();
                for(Map<String, String> map: (List<Map<String, String>>)node.get("logs")){
                    logMap.put(map.get("name"), map.get("file"));
                }*/
            
                Map<String, String> logMap = new HashMap<String, String>();
                List<LogFiles> logs = new ArrayList<LogFiles>();
                for(Map<String, String> map: (List<Map<String, String>>)node.get("logs")){
                    LogFiles log = new LogFiles();
                    log.setName(map.get("name"));
                    log.setUrl(map.get("file"));
                    
                    logs.add(log);
                }
            tempVm.setLogFiles(logs);
            
            // Adding Virtual Machines in Configuration
            config.addVm(tempVm.getName(), tempVm);
        }
        return config;
    }
    
}
