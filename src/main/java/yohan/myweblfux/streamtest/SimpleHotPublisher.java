package yohan.myweblfux.streamtest;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

@Slf4j
public class SimpleHotPublisher implements Flow.Publisher<Integer> {

    private final ExecutorService publisherExecutor = Executors.newSingleThreadExecutor();
    private final Future<Void> task;
    private List<Integer> numbers = new ArrayList<>();
    private List<SimpleHotSubscription> subscriptions = new ArrayList<>();

    public SimpleHotPublisher() {
        numbers.add(1);
        task = publisherExecutor.submit(()-> {
            for (int i = 0;!Thread.interrupted(); i++) {
                numbers.add(i);
                log.info("numbers: {}",numbers);
                subscriptions.forEach(SimpleHotSubscription::wakeup);
                Thread.sleep(100);
            }

            return null;
        });
    }

    public void shutdonw() {
        this.task.cancel(true);
        publisherExecutor.shutdown();
    }

    @Override
    public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
        var subscription = new SimpleHotSubscription(subscriber);
        subscriptions.add(subscription);
        subscriber.onSubscribe(subscription);
    }

    private class SimpleHotSubscription implements Flow.Subscription {

        private int offset;
        private int requiredOffset;
        private final Flow.Subscriber subscriber;
        private final ExecutorService subscriptionsExecutorService = Executors.newSingleThreadExecutor();

        public SimpleHotSubscription(Flow.Subscriber<? super Integer> subscriber) {
            int lastElementIndex= numbers.size() - 1;
            offset = lastElementIndex;
            requiredOffset = lastElementIndex;
            this.subscriber = subscriber;
        }

        @Override
        public void request(long n) {
            requiredOffset += n;

            onNextWhilePossible();
        }

        private void onNextWhilePossible() {
            subscriptionsExecutorService.submit(() -> {
                while (offset < requiredOffset && offset < numbers.size()) {
                    var item = numbers.get(offset);
                    subscriber.onNext(item);
                    offset++;
                }
            });
        }

        public void wakeup() {
            onNextWhilePossible();
        }

        @Override
        public void cancel() {
            this.subscriber.onComplete();
            if (subscriptions.contains(this))
                subscriptions.remove(this);
            subscriptionsExecutorService.shutdown();
        }


    }
}
