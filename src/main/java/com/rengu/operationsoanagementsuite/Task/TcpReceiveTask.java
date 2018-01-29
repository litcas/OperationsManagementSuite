package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Entity.ComponentDetailEntity;
import com.rengu.operationsoanagementsuite.Utils.ApplicationConfiguration;
import com.rengu.operationsoanagementsuite.Utils.JsonUtils;
import com.rengu.operationsoanagementsuite.Utils.ScanResultEntity;
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
public class TcpReceiveTask {

    // 扫描结果报文标示
    private static final String SCAN_RESULT_TAG = "C102";
    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ApplicationConfiguration applicationConfiguration;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public TcpReceiveTask(ApplicationConfiguration applicationConfiguration, StringRedisTemplate stringRedisTemplate) {
        this.applicationConfiguration = applicationConfiguration;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Async
    public void TCPReceiver() throws IOException {
        ServerSocket serverSocket = new ServerSocket(applicationConfiguration.getTcpReceivePort());
        logger.info("启动客户端报文监听线程，监听端口：" + applicationConfiguration.getTcpReceivePort());
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
        if (messageType.equals(SCAN_RESULT_TAG)) {
            String requestId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            String deviceId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            String componentId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            List<ComponentDetailEntity> componentDetailEntityList = new ArrayList<>();
            while (pointer + 256 + 34 <= bytes.length) {
                String filePath = new String(bytes, pointer, 256).trim();
                pointer = pointer + 256;
                String md5 = new String(bytes, pointer, 34).trim();
                pointer = pointer + 34;
                componentDetailEntityList.add(new ComponentDetailEntity(filePath, md5));
            }
            ScanResultEntity scanResultEntity = new ScanResultEntity(requestId, deviceId, componentId, componentDetailEntityList);
            stringRedisTemplate.opsForValue().set(requestId, JsonUtils.getJsonString(scanResultEntity));
        }
    }
}
