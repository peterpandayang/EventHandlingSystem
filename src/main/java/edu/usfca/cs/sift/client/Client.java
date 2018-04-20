package edu.usfca.cs.sift.client;

import edu.usfca.cs.sift.Messages;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client {

    private static InputParser parser = new InputParser();

    /*
    * Main method for client
    * */
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        String line = "";

        while (true) {
            System.out.println(
                    "please enter your command in the format of cURL and here is an example(case sensitive!) " +
                            "curl -X PUT localhost:5000/events -H \"Content-Type: application/json\" -d @events.json"
            );
            line = scanner.nextLine();
            if (line.equals("EOF")) {
                break;
            }
            Messages.RequestMsg.Builder builder = parser.parseCurl(line);
            if(builder != null){
                while(true){
                    System.out.println("Do you want to enter more condition + actions? Type Yes to proceed and enter No not to proceed");
                    line = scanner.nextLine();

                    if(line.equals("Yes")){
                        // proceed to get the condition + actions
                        System.out.println("please enter your conditions + actions as following(separated by space): " +
                                "cpu>90 slack:channel:critical email:subject:support@ourcompany.com ticket:priority:high");
                        line = scanner.nextLine();
                        Map<String, List<String>> conditionActions = parser.parseConditionActions(line);
                        if(conditionActions != null){
                            // add the map to the builder
                            addMapToBuilder(builder, conditionActions);
                        }
                    }
                    else if(line.equals("No")){
                        break;
                    }

                }
                Messages.RequestMsg requestMsg = builder.build();
                // here should create socket and send the msg to the server
                Socket socket = new Socket(requestMsg.getHost(), Integer.parseInt(requestMsg.getPort()));
                Messages.MessageWrapper finishWrapper =
                        Messages.MessageWrapper.newBuilder()
                                .setRequestMsg(requestMsg).build();
                finishWrapper.writeDelimitedTo(socket.getOutputStream());
                socket.close();

            }
            else {
                System.out.println("something went wrong, please reenter");
            }

        }

    }

    /*
    * This method build the nested data structure like ConditionAction{Condition, [Action1, Action2...]}
    * And add it to MessageBuilder
    * */
    private static void addMapToBuilder(Messages.RequestMsg.Builder builder, Map<String, List<String>> map){
        // every time there is only one <condition + actions>
        for(String condition : map.keySet()){
            List<String> actions = map.get(condition);
            Messages.ConditionActions.Builder conditionActionsBuilder = Messages.ConditionActions.newBuilder();

            // build condition
            Messages.Condition.Builder conditionBuilder = Messages.Condition.newBuilder();
            String[] strs1 = condition.split(">");
            conditionBuilder.setName(strs1[0]);
            conditionBuilder.setThreshold(Integer.parseInt(strs1[1]));
            conditionActionsBuilder.setCondition(conditionBuilder);

            for(String action : actions){
                // build actions
                Messages.Action.Builder actionBuilder = Messages.Action.newBuilder();
                String[] strs2 = action.split(":");
                actionBuilder.setName(strs2[0]);
                actionBuilder.setPara(strs2[1]);
                actionBuilder.setContent(strs2[2]);
                conditionActionsBuilder.addActions(actionBuilder);
            }
            builder.addConditionActions(conditionActionsBuilder);
        }
    }

}
