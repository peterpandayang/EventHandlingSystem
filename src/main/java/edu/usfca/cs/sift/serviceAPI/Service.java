package edu.usfca.cs.sift.serviceAPI;

import java.util.List;

public abstract class Service {

    public abstract void dispatchService(String name, String para, String content, List<String> sentInformation);

}
