package yohan.myweblfux.pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("start main");
        Reactor reactor = new Reactor(8080);
        reactor.run();
        log.info("end main");
    }
}
