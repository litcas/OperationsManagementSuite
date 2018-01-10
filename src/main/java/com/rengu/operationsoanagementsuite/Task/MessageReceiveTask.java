package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Entity.ComponentFileEntity;
import com.rengu.operationsoanagementsuite.Entity.DeviceScanResultEntity;
import com.rengu.operationsoanagementsuite.Utils.Tools;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageReceiveTask {

    // 扫描结果报文标示
    private static final String SCAN_RESULT_MESSAGE = "C102";
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 心跳报文接收端口
    private static final int TCP_RECEIVE_PORT = 6005;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Async
    public void messageReceive() throws IOException {
        ServerSocket serverSocket = new ServerSocket(TCP_RECEIVE_PORT);
        logger.info("启动客户端报文监听线程，监听端口：" + TCP_RECEIVE_PORT);
        while (true) {
            Socket socket = serverSocket.accept();
            messageHandler(socket);
        }
    }

    @Async
    public void messageHandler(Socket socket) throws IOException {
        try {
            InputStream inputStream = socket.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, byteArrayOutputStream);
            bytesHandler(byteArrayOutputStream.toByteArray());
        } finally {
            socket.shutdownOutput();
            socket.close();
        }
    }

    private void bytesHandler(byte[] bytes) throws IOException {
        int pointer = 0;
        String messageType = new String(bytes, 0, 4).trim();
        pointer = pointer + 4;
        if (messageType.equals(SCAN_RESULT_MESSAGE)) {
            String requestId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            String deviceId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            String componentId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            DeviceScanResultEntity deviceScanResultEntity = new DeviceScanResultEntity(requestId, deviceId, componentId);
            List<ComponentFileEntity> componentFileEntityList = new ArrayList<>();
            while (pointer + 256 + 34 <= bytes.length) {
                String filePath = new String(bytes, pointer, 256).trim();
                pointer = pointer + 256;
                String md5 = new String(bytes, pointer, 34).trim();
                pointer = pointer + 34;
                componentFileEntityList.add(new ComponentFileEntity(filePath, md5));
            }
            deviceScanResultEntity.setScanResult(componentFileEntityList);
            stringRedisTemplate.opsForValue().set(requestId, Tools.getJsonString(deviceScanResultEntity));
        }
    }
}
