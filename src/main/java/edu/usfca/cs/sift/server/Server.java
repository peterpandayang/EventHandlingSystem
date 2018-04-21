package edu.usfca.cs.sift.server;

import edu.usfca.cs.sift.Messages;
import edu.usfca.cs.sift.server.route.EventsRoute;
import edu.usfca.cs.sift.server.route.Route;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private static ServerSocket serverSocket;
    private static final int SERVER_PORT = 5000;
    private static ThreadPoolExecutor threadPool;
    private static Map<String, Route> routeMap;
    private static ServerCache cache;

    /*
    * initiate some fields
    * */
    private static void initiate(){
        initThreadPool();
        initiateRouteMap();
        cache = new ServerCache();
    }

    private static void initThreadPool(){
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    private static void initiateRouteMap(){
        routeMap = new HashMap<>();
        routeMap.put("events", new EventsRoute());
        // there might be some other routes to be added...
    }

    public static void main(String[] args) throws IOException {
        initiate();

        System.out.println("server starts to listening on port 5000...");
        serverSocket = new ServerSocket(SERVER_PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            Messages.MessageWrapper msgWrapper = Messages.MessageWrapper.parseDelimitedFrom(socket.getInputStream());

            if(msgWrapper.hasRequestMsg()){
                Messages.RequestMsg requestMsg = msgWrapper.getRequestMsg();
                if(requestMsg.getRedirect() != null && requestMsg.getRedirect().length() != 0){
                    // has redirect for this request
                    String redirect = requestMsg.getRedirect();
                    if(routeMap.containsKey(redirect)){
                        Route route = routeMap.get(redirect);
                        updateRoute(route, socket, threadPool, cache, requestMsg);
                        route.start();
                    }
                }
                else{
                    // no redirect for this request
                }
            }

        }

    }

    /*
    * update the redirect route
    * */
    private static void updateRoute(Route route, Socket socket, ThreadPoolExecutor threadPool, ServerCache cache, Messages.RequestMsg requestMsg){
        route.setSocket(socket);
        route.setThreadPool(threadPool);
        route.setServerCache(cache);
        route.setRequestMsg(requestMsg);
    }

}
