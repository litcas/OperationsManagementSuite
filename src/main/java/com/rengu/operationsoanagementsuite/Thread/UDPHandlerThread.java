package com.rengu.operationsoanagementsuite.Thread;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Network.UDPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;

public class UDPHandlerThread implements Runnable {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DatagramPacket datagramPacket;

    public UDPHandlerThread(DatagramPacket datagramPacket) {
        this.datagramPacket = datagramPacket;
    }

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
        String code = new String(datagramPacket.getData(), 0, ServerConfiguration.UDP_CODE_SIZE);
        if (code.equals(UDPMessage.RECEIVEHEARBEAT)) {
            logger.info("心跳报文");
        }
    }
}