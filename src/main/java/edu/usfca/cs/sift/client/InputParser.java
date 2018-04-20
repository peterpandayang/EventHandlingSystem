package edu.usfca.cs.sift.client;

import com.google.protobuf.ByteString;
import edu.usfca.cs.sift.Messages;

import java.io.*;
import java.util.*;

public class InputParser {

    private static String FILE_PATH = "files/";
    private static Map<String, String> actionMapping;

    public InputParser(){
        initiateActionMapping();
    }

    /*
    * Initiate the action map
    * */
    private static void initiateActionMapping(){
        actionMapping = new HashMap<>();
        actionMapping.put("slack", "channel");
        actionMapping.put("email", "subject");
        actionMapping.put("ticket", "priority");
    }


    /*
    EX:
    string host = 1;
    string port = 2;
    string redirect = 3;
    string contentType = 4;
    string filename = 5;
    bytes data = 6;
    string type = 7;

    curl -X PUT localhost:5000/events -H "Content-Type: application/json" -d @events.json
    */

    /*
     * This method return the parsed message
     * If there is failure in parse, return null
     * */
    public static Messages.RequestMsg.Builder parseCurl(String input)throws IOException, FileNotFoundException {
        if(input.length() == 0){
            System.out.println("You should enter something");
        }
        else {
            // action could be get, post or put
            String actionType = null;
            List<String> hostPortRedirect = null;
            String contentType = null;
            File file = null;
            if(input.indexOf("GET") != -1){
                actionType = "GET";
            }
            else if(input.indexOf("POST") != -1){
                actionType = "POST";
            }
            else if(input.indexOf("PUT") != -1){
                actionType = "PUT";
                hostPortRedirect = getHostPortRedirect(input.substring(input.indexOf("PUT") + 3).trim());
            }
            // check content type
            if(input.indexOf("Content-Type") != -1){
                contentType = getDataType(input.substring(input.indexOf("Content-Type")));
                if(contentType == null){
                    return null;
                }
            }
            // check data file
            if(input.indexOf("-d") != -1){
                String filename = getFilename(input.substring(input.indexOf("-d") + 2).trim());
                file = new File(FILE_PATH + filename);
                // error for finding file
                if(!file.exists()){
                    return null;
                }
            }
            // if there is action type and the server to connect
            if(actionType != null && hostPortRedirect != null){
                Messages.RequestMsg.Builder builder = Messages.RequestMsg.newBuilder();
                builder.setMethodType(actionType);
                builder.setHost(hostPortRedirect.get(0));
                builder.setPort(hostPortRedirect.get(1));
                if(hostPortRedirect.size() == 3){
                    builder.setRedirect(hostPortRedirect.get(2));
                }
                if(contentType != null){
                    builder.setContentType(contentType);
                }
                if(input.indexOf("-d") != -1){
                    // here should read the json file and add to the request message
                    InputStream inputStream = new FileInputStream(file);
                    // the buffer size is statically configured to 10 MB and could be customized later by user
                    byte[] buffer = new byte[10 * 1024 * 1024];
                    BufferedInputStream bin = new BufferedInputStream(inputStream);
                    int byteread;
                    ByteString byteString = null;
                    while((byteread = bin.read(buffer)) != -1){
                        byteString = ByteString.copyFrom(buffer, 0, byteread);
                    }
                    builder.setData(byteString);
                }
                // return builder
                return builder;
            }
            else {
                System.out.println("Either action type or host or port is incorrect");
            }
        }

        return null;
    }

    /*
    * Construct the list of <host, port, redirect(if any)>
    * */
    private static List<String> getHostPortRedirect(String s){
        List<String> rst = new LinkedList<>();
        int i = 0;
        boolean hasRedirect = false;
        int start = i;
        while(i <= s.length() - 1){
            char c = s.charAt(i);
            if(c == ':'){
                String host = s.substring(start, i);
                rst.add(host);
                i += 1;
                start = i;
            }
            else if(c == '/'){
                String port = s.substring(start, i);
                rst.add(port);
                hasRedirect = true;
                i += 1;
                start = i;
            }
            else if(c == ' '){
                // finish parsing for url
                if(hasRedirect){
                    String redirect = s.substring(start, i);
                    rst.add(redirect);
                }
                else{
                    String port = s.substring(start, i);
                    rst.add(port);
                }
                break;
            }
            else {
                i += 1;
            }
        }
        if(rst.size() == 2 || rst.size() == 3){
            // the parse rst without or with redirect
            return rst;
        }
        return null;
    }

    /*
    * Get the data type for transferring file
    * */
    private static String getDataType(String s){
        if(s.indexOf("application") != -1){
            int i =  s.indexOf("application");
            int start = i + 12;
            i = start;
            while(i <= s.length() - 1 && s.charAt(i) != '\"'){
                i += 1;
            }
            if(i == s.length()){
                return null;
            }
            return s.substring(start, i);
        }
        return null;
    }

    /*
    * Get the filename
    * */
    private static String getFilename(String s){
        int i = 0;
        int start = i;
        if(s.charAt(i) == '@'){
            start = i + 1;
        }
        while(i <= s.length() - 1 && s.charAt(i) != ' '){
            i += 1;
        }
        return s.substring(start, i);
    }

    /*
    * Format should be:
    * cpu>90 slack:channel:critical email:subject:support@ourcompany.com ticket:priority:high
    * */
    /*
     * The method just return all the actions for one condition,
     * If there is failure in parse, return null
     * */
    public static Map<String, List<String>> parseConditionActions(String s){
        Map<String, List<String>> map = new HashMap<>();
        String[] strings = s.trim().split(" ");
        if(strings.length == 0){
            System.out.println("you should enter something");
            return null;
        }
        else if(strings.length == 1){
            System.out.println("you should enter some actions");
            return null;
        }
        else{
            map.put(strings[0].trim(), new LinkedList<>());
            for(int i = 1; i <= strings.length - 1; i++){
                String action = strings[i];
                if(getAction(action) == null){
                    return null;
                }
                map.get(strings[0]).add(action);
            }
            return map;
        }
    }

    /*
    * Just check the validation of each action
    * */
    private static String getAction(String action){
        if(action.indexOf("slack") != -1){
            int start = action.indexOf("slack") + 6;
            int i = start;
            while(i <= action.length() - 1 && action.charAt(i) != ':'){
                i += 1;
            }
            String para = action.substring(start, i);
            if(!actionMapping.get("slack").equals(para)){
                return null;
            }
        }
        else if(action.indexOf("email") != -1){
            int start = action.indexOf("email") + 6;
            int i = start;
            while(i <= action.length() - 1 && action.charAt(i) != ':'){
                i += 1;
            }
            String para = action.substring(start, i);
            if(!actionMapping.get("email").equals(para)){
                return null;
            }
        }
        else if(action.indexOf("ticket") != -1){
            int start = action.indexOf("ticket") + 7;
            int i = start;
            while(i <= action.length() - 1 && action.charAt(i) != ':'){
                i += 1;
            }
            String para = action.substring(start, i);
            if(!actionMapping.get("ticket").equals(para)){
                return null;
            }
        }
        return action;
    }

}
