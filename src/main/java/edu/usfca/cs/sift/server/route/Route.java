package edu.usfca.cs.sift.server.route;

import edu.usfca.cs.sift.Messages;
import edu.usfca.cs.sift.server.ServerCache;

import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class Route {

    protected Socket socket;
    protected ThreadPoolExecutor threadPool;
    protected ServerCache cache;
    protected Messages.RequestMsg requestMsg;

    public abstract void start();

    public abstract void startGetReqThread();

    public abstract void startPostReqThread();

    public abstract void startPutReqThread();

    public void setSocket(Socket socket){
        this.socket = socket;
    }

    public void setThreadPool(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    public void setServerCache(ServerCache cache) {
        this.cache = cache;
    }

    public void setRequestMsg(Messages.RequestMsg requestMsg) {
        this.requestMsg = requestMsg;
    }
}
