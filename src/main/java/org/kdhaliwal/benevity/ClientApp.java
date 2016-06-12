package org.kdhaliwal.benevity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kdhaliwal.benevity.utility.Config;
import org.kdhaliwal.benevity.views.MainView;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class ClientApp {
    public static void main(String[] args) throws FileNotFoundException, YamlException {
        Config config = Config.create();
        //System.out.println(config.getVm().get("kdhaliwal.bend"));
        MainView.run();
    }
    
    public static void iterateAll() throws IOException{
        YamlReader reader = new YamlReader(new FileReader("config.yml"));
        while(true){
            Object read = reader.read();
            if ( read == null ) break;
            
            if(read instanceof List){
                List<Map> contact = (List<Map>) read;
                showList(contact);
                System.out.println(contact);
            }else{
                Map contact = (Map) read;
                //System.out.println(contact);
            }
        }
    }
    public static void showList(List<Map> list){
        for (Map<String, Object> string : list) {
            for(Map.Entry<String, Object> map: string.entrySet()){
                
                //if Value is a List
                if(map.getValue() instanceof List){
                    for (Object value : (List)map.getValue()) {
                        if(value instanceof Map){
                            Map keyMap = (Map) value;
                            System.out.println(keyMap.get("name"));
                        }
                    }
                    continue;
                }
                System.out.println(map.getValue());
            }
        }
    }
}
