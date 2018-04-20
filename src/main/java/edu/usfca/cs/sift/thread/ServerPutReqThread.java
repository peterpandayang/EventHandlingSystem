package edu.usfca.cs.sift.thread;

import edu.usfca.cs.sift.server.route.Route;

import java.io.IOException;

public class ServerPutReqThread extends Thread {
    private Route route;

    public ServerPutReqThread(Route route) {
        this.route = route;
    }

    @Override
    public void run() {
        route.startPutReqThread();
    }
}