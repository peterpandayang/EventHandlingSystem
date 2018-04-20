package edu.usfca.cs.sift.serviceAPI.ticket;

public class TicketEntity {
    String name = "Ticket";
    String para;
    String content;
    String info;

    public TicketEntity(String para, String content, String info){
        this.para = para;
        this.content = content;
        this.info = info;
    }
}
