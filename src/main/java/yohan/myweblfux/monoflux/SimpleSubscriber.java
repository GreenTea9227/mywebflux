package yohan.myweblfux.monoflux;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
@RequiredArgsConstructor
public class SimpleSubscriber implements Subscriber {
    private final Integer count;
    private Subscription subscription = null;
    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        log.info("subscribe");
        s.request(count);
        log.info("request : {}",count);
    }

    @SneakyThrows
    @Override
    public void onNext(Object o) {
        log.info("item: {}",o);
        Thread.sleep(1000);
        subscription.request(1);
        log.info("request: {}",1);

    }

    @Override
    public void onError(Throwable t) {
        log.info("error");
    }

    @Override
    public void onComplete() {
        log.info("complete");
    }


}
