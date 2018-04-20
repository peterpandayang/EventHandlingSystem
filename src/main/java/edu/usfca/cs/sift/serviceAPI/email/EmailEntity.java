package edu.usfca.cs.sift.serviceAPI.email;

public class EmailEntity {
    String name = "Email";
    String para;
    String content;
    String info;

    public EmailEntity(String para, String content, String info){
        this.para = para;
        this.content = content;
        this.info = info;
    }

}
