package yohan.myweblfux.io;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class MyInputStream {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Socket clientSocket = serverSocket.accept();

        var inputstream = clientSocket.getInputStream();

        try (var bis =  new BufferedInputStream(inputstream)) {
            byte[] buffer = new byte[1024];
            int bytesRead = bis.read(buffer);
            String inputLine = new String(buffer,0,bytesRead);
            log.info("bytes: {}",inputLine);
        }

        clientSocket.close();
        serverSocket.close();

    }
}
