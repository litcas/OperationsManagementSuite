package com.rengu.operationsoanagementsuite.Thread;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Utils.DeviceRealInfoEntity;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import com.rengu.operationsoanagementsuite.Utils.UDPMessage;
import com.rengu.operationsoanagementsuite.Utils.UDPSTools;
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
        byte[] data = new byte[datagramPacket.getLength() - ServerConfiguration.UDP_CODE_SIZE];
        System.arraycopy(datagramPacket.getData(), ServerConfiguration.UDP_CODE_SIZE, data, 0, datagramPacket.getLength() - ServerConfiguration.UDP_CODE_SIZE);
        if (code.equals(UDPMessage.RECEIVEHEARBEAT)) {
            String ip = (data[0] & 0xff) + "." + (data[1] & 0xff) + "." + (data[2] & 0xff) + "." + (data[3] & 0xff);
            String platform = Tools.getPlatformName((data[4] & 0xff));
            DeviceRealInfoEntity deviceRealInfoEntity = new DeviceRealInfoEntity(ip, platform, 3);
            int index = UDPSTools.onlineDevices.indexOf(deviceRealInfoEntity);
            if (index == -1) {
                // 不存在直接添加到数组
                UDPSTools.onlineDevices.add(deviceRealInfoEntity);
            } else {
                // 已存在更新计数器
                UDPSTools.onlineDevices.get(index).setCount(3);
            }
            System.out.println("当前设备数量-udp：" + UDPSTools.onlineDevices.size() + "台");
        }
    }
}
