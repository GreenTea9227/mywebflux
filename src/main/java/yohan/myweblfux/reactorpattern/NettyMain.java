package yohan.myweblfux.reactorpattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyMain {
    public static void main(String[] args) {
        log.info("start main");
//        List<EventLoop> eventLoops = List.of(new EventLoop(8080), new EventLoop(8081));
//        eventLoops.forEach(EventLoop::run);
        Reactor reactor = new Reactor(8080);
        reactor.run();
        log.info("end main");
    }
}
