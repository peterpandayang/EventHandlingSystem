package edu.usfca.cs.sift.server.route;

import edu.usfca.cs.sift.Messages;
import edu.usfca.cs.sift.server.ServerCache;
import edu.usfca.cs.sift.serviceAPI.Service;
import edu.usfca.cs.sift.serviceAPI.email.EmailAPI;
import edu.usfca.cs.sift.serviceAPI.slack.SlackAPI;
import edu.usfca.cs.sift.serviceAPI.ticket.TicketAPI;
import edu.usfca.cs.sift.thread.ServerPutReqThread;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventsRoute extends Route {

    private static Map<String, Service> serviceMap;

    private static void initiateServiceMap(){
        serviceMap = new HashMap<>();
        serviceMap.put("email", new EmailAPI());
        serviceMap.put("slack", new SlackAPI());
        serviceMap.put("ticket", new TicketAPI());
        // could be more...
    }

    /*
    * start the service
    * */
    @Override
    public void start() {
        initiateServiceMap();

        String actionType = requestMsg.getMethodType();
        if(actionType.equals("GET")){

        }
        else if(actionType.equals("POST")){

        }
        else if(actionType.equals("PUT")){
            ServerPutReqThread thread = new ServerPutReqThread(this);
            threadPool.execute(thread);
        }
        else {

        }
    }

    @Override
    public void startGetReqThread() {

    }

    @Override
    public void startPostReqThread() {

    }

    /*
    * handle the put requests
    * */
    @Override
    public void startPutReqThread() {

        if(!requestMsg.getData().isEmpty()){
            String data = requestMsg.getData().toStringUtf8();
            if(requestMsg.getContentType().equals("json")){
                updateServerCacheWithJson(cache, data);
            }
        }
        if(!requestMsg.getConditionActionsList().isEmpty()){
            List<Messages.ConditionActions> list = requestMsg.getConditionActionsList();
            for(Messages.ConditionActions group : list){
                Messages.Condition condition = group.getCondition();
                List<Messages.Action> actions = group.getActionsList();
                List<String> sentInformation = cache.getCondition(condition);
                if(sentInformation.size() != 0){
                    for(Messages.Action action : actions){
                        // actionName:email/slack/ticket
                        // actionPara:subject/channel/priority
                        String actionName = action.getName();
                        String actionPara = action.getPara();
                        String actionContent = action.getContent();
                        if(serviceMap.containsKey(actionName)){
                            Service service = serviceMap.get(actionName);
                            service.dispatchService(actionName, actionPara, actionContent, sentInformation);
                        }
                    }

                }
            }
        }
    }

    /*
    * update the cache information
    * */
    private void updateServerCacheWithJson(ServerCache serverCache, String data){
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i = 0; i <= jsonArray.size() - 1; i++){
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String name = (String) jsonObject.get("name");
            String type = (String) jsonObject.get("type");
            String value = String.valueOf(jsonObject.get("value"));
            serverCache.updateMachineMap(name, type, value);
        }

    }


}
