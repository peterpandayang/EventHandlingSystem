package edu.usfca.cs.sift.serviceAPI.ticket;

import edu.usfca.cs.sift.serviceAPI.Service;

import java.util.List;

public class TicketAPI extends Service {
    @Override
    public void dispatchService(String name, String para, String content, List<String> sentInformation) {
        for(String infoItem: sentInformation){
            TicketEntity ticket = new TicketEntity(para, content, infoItem);
            // could call the actual api here
            push(ticket);
        }
    }

    private void push(TicketEntity ticket){
        StringBuilder sb = new StringBuilder();
        sb.append(ticket.name).append(' ').append('\'').append(ticket.content).append('\'').append(' ').append(ticket.para).append(' ').append('\'').append(ticket.info).append('\'');
        System.out.println(sb.toString());
    }
}
