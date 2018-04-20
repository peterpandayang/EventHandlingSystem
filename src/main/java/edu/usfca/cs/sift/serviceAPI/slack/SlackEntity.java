package edu.usfca.cs.sift.serviceAPI.slack;

public class SlackEntity {
    String name = "Slack";
    String para;
    String content;
    String info;

    public SlackEntity(String para, String content, String info){
        this.para = para;
        this.content = content;
        this.info = info;
    }
}
