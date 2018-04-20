package edu.usfca.cs.sift.server;

import edu.usfca.cs.sift.Messages;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerCache{

    // status map
    private ConcurrentHashMap<String, Map<String, String>> machineMap;

    public ServerCache(){
        machineMap = new ConcurrentHashMap<>();
    }

    /*
    * update the information for each machine and its corresponding status
    * EX: host1 -> cpu -> 98
    * */
    public void updateMachineMap(String name, String type, String value){
        if(!machineMap.containsKey(name)){
            machineMap.put(name, new ConcurrentHashMap<>());
        }
        machineMap.get(name).put(type, value);
    }

    /*
    * get the list of information that should be sent
    * */
    public List<String> getCondition(Messages.Condition condition){
        List<String> rst = new LinkedList<>();
        String typeName = condition.getName();
        int threshold = condition.getThreshold();
        for(String machine : machineMap.keySet()){
            for(String type : machineMap.get(machine).keySet()){
                Map<String, String> statusMap = machineMap.get(machine);
                int val = Integer.parseInt(statusMap.get(type));
                if(type.equals(typeName) && val > threshold){
                    StringBuilder sb = new StringBuilder();
                    sb.append(type).append(" usage at ").append(val);
                    if(type.equals("disk") || type.equals("cpu")){
                        sb.append("%");
                    }
                    sb.append(" for ").append(machine);
                    rst.add(sb.toString());
                }
            }
        }
        return rst;
    }

}
