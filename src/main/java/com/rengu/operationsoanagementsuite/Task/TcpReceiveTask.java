package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Configuration.ApplicationConfiguration;
import com.rengu.operationsoanagementsuite.Entity.*;
import com.rengu.operationsoanagementsuite.Utils.JsonUtils;
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
    private static final String TASK_RESULT_TAG = "C105";
    private static final String DISK_RESULT_TAG = "C106";
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
            String id = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            String deviceId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            String componentId = new String(bytes, pointer, 36).trim();
            pointer = pointer + 36;
            List<ComponentDetailEntity> originalScanResultList = new ArrayList<>();
            while (pointer + 256 + 34 <= bytes.length) {
                String filePath = new String(bytes, pointer, 256).trim();
                pointer = pointer + 256;
                String md5 = new String(bytes, pointer, 34).trim();
                pointer = pointer + 34;
                originalScanResultList.add(new ComponentDetailEntity(filePath.replace("//", "/"), md5));
            }
            ScanResultEntity scanResultEntity = new ScanResultEntity();
            scanResultEntity.setId(id);
            scanResultEntity.setDeviceId(deviceId);
            scanResultEntity.setComponentId(componentId);
            scanResultEntity.setOriginalScanResultList(originalScanResultList);
            stringRedisTemplate.opsForValue().set(id, JsonUtils.getJsonString(scanResultEntity));
        }
        if (messageType.equals(TASK_RESULT_TAG)) {
            String id = new String(bytes, pointer, 37).trim();
            pointer = pointer + 37;
            List<TaskInfoEntity> taskInfoEntityList = new ArrayList<>();
            while (pointer + 5 + 128 + 8 < bytes.length) {
                String pid = new String(bytes, pointer, 5).trim();
                pointer = pointer + 5;
                String name = new String(bytes, pointer, 128).trim();
                pointer = pointer + 128 + 8;
                taskInfoEntityList.add(new TaskInfoEntity(pid, name));
            }
            // 序列化进程信息对象
            DeviceTaskEntity deviceTaskEntity = new DeviceTaskEntity();
            deviceTaskEntity.setId(id);
            deviceTaskEntity.setTaskInfoEntities(taskInfoEntityList);
            stringRedisTemplate.opsForValue().set(id, JsonUtils.getJsonString(deviceTaskEntity));
        }
        if (messageType.equals(DISK_RESULT_TAG)) {
            String id = new String(bytes, pointer, 37).trim();
            pointer = pointer + 37;
            List<DiskInfoEntity> diskInfoEntityList = new ArrayList<>();
            while (pointer + 32 + 12 + 12 <= bytes.length) {
                String name = new String(bytes, pointer, 32).trim().replace("\\", "/");
                pointer = pointer + 32;
                double size = Double.parseDouble(new String(bytes, pointer, 12).trim());
                pointer = pointer + 12;
                double usedSize = Double.parseDouble(new String(bytes, pointer, 12).trim());
                pointer = pointer + 12;
                diskInfoEntityList.add(new DiskInfoEntity(name, size, usedSize));
            }
            // 序列化进程信息对象
            DeviceDiskEntity deviceDiskEntity = new DeviceDiskEntity();
            deviceDiskEntity.setId(id);
            deviceDiskEntity.setDiskInfoEntities(diskInfoEntityList);
            stringRedisTemplate.opsForValue().set(id, JsonUtils.getJsonString(deviceDiskEntity));
        }
    }
}
