package edu.usfca.cs.sift.serviceAPI.email;

import edu.usfca.cs.sift.serviceAPI.Service;

import java.util.List;

public class EmailAPI extends Service {

    @Override
    public void dispatchService(String name, String para, String content, List<String> sentInformation) {
        for(String infoItem: sentInformation){
            EmailEntity email = new EmailEntity(para, content, infoItem);
            // could call the actual api here
            send(email);
        }
    }

    private void send(EmailEntity email){
        StringBuilder sb = new StringBuilder();
        sb.append(email.name).append(' ').append('\'').append(email.content).append('\'').append(' ').append(email.para).append(' ').append('\'').append(email.info).append('\'');
        System.out.println(sb.toString());
    }

}
