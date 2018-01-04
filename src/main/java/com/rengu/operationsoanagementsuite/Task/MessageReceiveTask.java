package com.rengu.operationsoanagementsuite.Task;

import com.rengu.operationsoanagementsuite.Configuration.ServerConfiguration;
import com.rengu.operationsoanagementsuite.Utils.UDPUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class MessageReceiveTask {
    @Async
    public void messageReceive() throws IOException {
        ServerSocket serverSocket = new ServerSocket(ServerConfiguration.TCP_RECEIVE_PORT);
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
            byte[] bytes = byteArrayOutputStream.toByteArray();
            scanResultHandler(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            socket.shutdownOutput();
            socket.close();
        } finally {
            socket.shutdownOutput();
            socket.close();
        }
    }

    private void scanResultHandler(byte[] bytes) {
        int pointer = 0;
        String messageType = new String(bytes, 0, 4);
        pointer = pointer + 4;
        if (messageType.equals(UDPUtils.RECEIVE_SCAN_RESULT)) {
            String requestId = new String(bytes, pointer, 36);
            pointer = pointer + 36;
            String deviceId = new String(bytes, pointer, 36);
            pointer = pointer + 36;
            String componentId = new String(bytes, pointer, 36);
            pointer = pointer + 36;
            while (pointer + 256 + 34 <= bytes.length) {
                String filePath = new String(bytes, pointer, 256);
                pointer = pointer + 256;
                String md5 = new String(bytes, pointer, 34);
                pointer = pointer + 34;
            }
        }
    }
}
