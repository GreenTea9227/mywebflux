package yohan.myweblfux.io;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class MyOutputStream {
    public static void main(String[] args) throws IOException {
        var file = new File("C:\\Users\\joseph\\OneDrive\\바탕 화면\\hello.txt");
        try (var bis = new BufferedOutputStream(new FileOutputStream(file))) {
            bis.write("Hello world this is yohan".getBytes());
            bis.flush();
        };

    }
}
