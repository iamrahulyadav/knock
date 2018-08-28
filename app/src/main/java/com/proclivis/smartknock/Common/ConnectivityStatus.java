package com.proclivis.smartknock.Common;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class ConnectivityStatus extends ContextWrapper{

    public ConnectivityStatus(Context base) {
        super(base);
    }

    //Amit 19.06.18. Removing Parameter Context since it is not required
    public static boolean isConnected(){
        //Amit 19.06.18. Tried amother method to validate connectivity
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
        //Amit 19.06.18. Commented this to try another method of connectivity check
        /*Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int  exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return false;*/
    }
}