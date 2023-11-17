package yohan.myweblfux.reactorpattern;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yohan.myweblfux.reactorpattern.TcpEventHandler;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Slf4j
@RequiredArgsConstructor
public class Acceptor implements EventHandler {
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    @SneakyThrows
    @Override
    public void handle() {
        SocketChannel clientSocket = serverSocketChannel.accept();
        new HttpEventHandler(selector, clientSocket);
//        new TcpEventHandler(selector,clientSocket);
    }
}
