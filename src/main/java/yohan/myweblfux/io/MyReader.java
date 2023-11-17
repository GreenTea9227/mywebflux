package yohan.myweblfux.io;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyReader {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\joseph\\OneDrive\\바탕 화면\\hello.txt");
        try (var fileChannel = FileChannel.open(file.toPath())) {
            var byteBuffer = ByteBuffer.allocateDirect(1024);
            fileChannel.read(byteBuffer);
            byteBuffer.flip();

            var result = StandardCharsets.UTF_8.decode(byteBuffer);
            log.info("result: {}", result);
        }

    }

    private static void logPosition(String str, ByteBuffer byteBuffer) {
        log.info("{} ) position:{}, limit:{}, capacity:{}", str, byteBuffer.position(), byteBuffer.limit(), byteBuffer.capacity());
    }
}
