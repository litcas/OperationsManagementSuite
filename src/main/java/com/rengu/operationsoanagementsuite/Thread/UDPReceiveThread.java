package com.rengu.operationsoanagementsuite.Thread;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Utils.UDPSTools;

import java.io.IOException;

public class UDPReceiveThread implements Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            UDPSTools.receiveMessage(ServerConfiguration.UDP_RECEIVE_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
