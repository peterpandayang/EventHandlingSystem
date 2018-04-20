package edu.usfca.cs.sift.serviceAPI.slack;

import edu.usfca.cs.sift.serviceAPI.Service;

import java.util.List;

public class SlackAPI extends Service {
    @Override
    public void dispatchService(String name, String para, String content, List<String> sentInformation) {
        for(String infoItem: sentInformation){
            SlackEntity slack = new SlackEntity(para, content, infoItem);
            // could call the actual api here
            push(slack);
        }
    }

    private void push(SlackEntity slack){
        StringBuilder sb = new StringBuilder();
        sb.append(slack.name).append(' ').append('\'').append(slack.content).append('\'').append(' ').append(slack.para).append(' ').append('\'').append(slack.info).append('\'');
        System.out.println(sb.toString());
    }
}
