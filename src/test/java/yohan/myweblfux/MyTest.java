package yohan.myweblfux;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;


@Slf4j
public class MyTest {

    @SneakyThrows
    @Test
    void t1() {
        Flux.fromStream(IntStream.range(0, 40).boxed())
                .take(30, true)
                .buffer(3)
                .subscribe(new BaseSubscriber<List<Integer>>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        subscription.request(1L);
                    }

                    @Override
                    protected void hookOnNext(List<Integer> value) {
                        log.info("value: {}", value);
                        request(1);
                    }

                    @Override
                    protected void hookOnComplete() {
                        log.info("complete");
                    }
                });
    }

    @Test
    void t2() {
        Mono.just(1)
                .subscribe(value -> {
                    log.info("value: {}", value);
                });
        Mono.error(new RuntimeException("mono error"))
                .subscribe(value -> {
                    log.info("value: {}", value);
                }, error -> {
                    log.info("error : {}", error.getMessage());
                });
        Flux.empty()
                .subscribe(value -> {
                    log.info("value: {}", value);
                }, null, () -> {
                    log.info("complete");
                });
        Flux.fromStream(IntStream.rangeClosed(1, 10).boxed())
                .subscribe(value -> {
                    log.info("flux value: {}", value);
                    throw new RuntimeException("flux error!!!");
                }, error -> {
                    log.info("flux error : {}", error.getMessage());
                });
    }

    @Test
    void t3() {
        Mono.fromCallable(() -> {
            return 1;
        }).subscribe(value -> {
            log.info("fromCallable : {}", value);
        });
    }

    @Test
    void t4() {
        Flux.fromArray(new Integer[]{1, 2, 3, 4, 5})
                .subscribe(value -> {
                    log.info("value: {}", value);
                });

        Mono.fromFuture(CompletableFuture.supplyAsync(() -> {
            return List.of(1, 2, 3, 4, 5);
        })).subscribe(value ->
                log.info("mono : {}", value));
    }

    @Test
    void t5() {
        Flux.generate(() -> 0, (state, sink) -> {
            sink.next(state);
            if (state == 9) {
                sink.complete();
            }
            return state + 1;
        }).subscribe(value -> {
            log.info("first: {}", value);
        }, null, () -> {
            log.info("complete");
        });
    }

    @Test
    void t6() {
        Flux.create(sink -> {
            var task1 = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < 5; i++) {
                    sink.next(i);
                }
            });

            var task2 = CompletableFuture.runAsync(() -> {
                for (int i = 5; i < 10; i++) {
                    sink.next(i);
                }
            });

            CompletableFuture.allOf(task1, task2);
//                    .thenRun(sink::complete);
        }).subscribe(value -> {
            log.info("value: {}", value);
        }, null, () -> {
            log.info("complete");
        });
    }

    @Test
    void t7() {
        var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(() -> {
                Flux.create(sink -> {
                    for (int i = 0; i < 5; i++) {
                        log.info("next : {}", i);
                        sink.next(i);
                    }
                }).subscribe((value) -> {
                    log.info("value: {}", value);
                });
            });
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (Exception e) {

        } finally {

            executor.shutdown();
        }

    }

    @SneakyThrows
    @Test
    void t8() {
        for (int i = 0; i < 100; i++) {
            final int idx = i;
            Flux.create(sink -> {
                        log.info("next : {}", idx);
                        sink.next(idx);
                    })
                    .subscribeOn(Schedulers.newSingle("single"))
                    .subscribe(value -> {
                        log.info("value: " + value);

                    });
        }


        Thread.sleep(1000);
    }

    @SneakyThrows
    @Test
    void t9() {


        Flux.create(sink -> {
                    for (int i = 0; i < 5; i++) {
                        log.info("next : {}", i);
                        sink.next(i);
                    }
                })
                .publishOn(Schedulers.newSingle("single"))
                .doOnNext(item -> {
                    log.info("doOnNext: {}", item);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(item -> {
                    log.info("doOnNext2: {}", item);
                })
                .subscribeOn(Schedulers.newSingle("my-main"))
                .subscribe(value -> {
                    log.info("value: " + value);
                });


        Thread.sleep(1000);
    }

    @Test
    void t10() {
        Flux.error(new ArithmeticException("ArithmeticException!!!"))
                .doOnError(e -> log.info("doOnError: {}", e.getMessage()))
                .onErrorMap(e -> new MyException("MyException!!!"))
                .subscribe(null, e -> {
                    log.info("error:" + e.getMessage());
                });

        Mono.error(new ArithmeticException("ArithmeticException!!!"))
                .onErrorReturn(0)
                .subscribe((v) -> {
                    log.info("return : {}", v);
                });

        Mono.error(new ArithmeticException("ArithmeticException!!!"))
                .onErrorComplete()
                .subscribe(null, null, () -> log.info("complete"));
    }

    @SneakyThrows
    @Test
    void t11() {
        Flux.create(sink -> {
                    for (int i = 0; i < 5; i++) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        sink.next(i);
                    }
                    sink.complete();
                })
                .delayElements(Duration.ofMillis(500))
                .doOnNext(value -> {
                    log.info("doOnNext: " + value);
                })
                .subscribeOn(Schedulers.single())
                .subscribe();
        Thread.sleep(3000);
    }

    @SneakyThrows
    @Test
    void t12() {
        Flux<Integer> flux1 = Flux.range(1, 3)
                .doOnSubscribe(value -> {
                    log.info("doOnSubscribe1");
                })
                .delayElements(Duration.ofMillis(100));
        Flux<Integer> flux2 = Flux.range(10, 3)
                .doOnSubscribe(value -> {
                    log.info("doOnSubscribe2");
                })
                .delayElements(Duration.ofMillis(100));

        Flux.concat(flux1, flux2)
                .doOnNext(value -> log.info("doOnNext: " + value))
                .subscribe();
        Thread.sleep(1000);
    }

    @SneakyThrows
    @Test
    void t13() {
        Flux<Integer> flux1 = Flux.range(1, 3)
                .doOnSubscribe(value -> {
                    log.info("doOnSubscribe1");
                })
                .delayElements(Duration.ofMillis(100));
        Flux<Integer> flux2 = Flux.range(10, 3)
                .doOnSubscribe(value -> {
                    log.info("doOnSubscribe2");
                })
                .delayElements(Duration.ofMillis(100));

        Flux.merge(flux1, flux2)
                .doOnNext(value -> log.info("doOnNext: " + value))
                .subscribe();
        Thread.sleep(1000);
    }

    @SneakyThrows
    @Test
    void t14() {
        Flux<Integer> flux1 = Flux.range(1, 3)
                .doOnSubscribe(value -> {
                    log.info("doOnSubscribe1");
                })
                .delayElements(Duration.ofMillis(100));
        Flux<Integer> flux2 = Flux.range(10, 3)
                .doOnSubscribe(value -> {
                    log.info("doOnSubscribe2");
                })
                .delayElements(Duration.ofMillis(100));

        Flux.mergeSequential(flux1, flux2)
                .doOnNext(value -> log.info("doOnNext: " + value))
                .subscribe();
        Thread.sleep(1000);
    }

    @Test
    void t15() {
        Flux.range(1, 5)
                .map(v -> v * 2)
                .doOnNext(v -> {
                    log.info("doOnNext: {}", v);
                }).subscribe();

        Flux.range(1, 10)
                .mapNotNull(v -> {
                    if (v % 2 == 0)
                        return v;
                    return null;
                })
                .buffer(3)
                .doOnNext(v -> {
                    log.info("doOnNext: {}", v);
                })
                .subscribe();

    }

    @Test
    void t16() {
        Flux.range(1, 20)
                .doOnRequest(v -> {
                    log.info("doOnRequest: " + v);
                }).subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnNext(Integer value) {
                        request(3);
                    }
                });
    }

    @Test
    void t17() {
        Mono<List<Integer>> listMono = Flux.range(1, 15)
                .collectList()
                .doOnNext(v -> log.info("doOnNext: " + v));

        listMono.subscribe();
        listMono.subscribe();
        listMono.subscribe();

    }


    static class MyException extends RuntimeException {
        public MyException() {
            super();
        }

        public MyException(String message) {
            super(message);
        }

        public MyException(Throwable cause) {
            super(cause);
        }
    }

    @SneakyThrows
    @Test
    void t20() {
        ThreadLocal<Object> threadLocal = new ThreadLocal<>();
        threadLocal.set("yohan");
        Flux.create((sink) -> {
                    log.info("threadLocal : {}", threadLocal.get());
                    sink.next(1);
                }).publishOn(Schedulers.single())
                .map((value) -> {
                    log.info("threadLocal : {}", threadLocal.get());
                    return value;
                }).publishOn(Schedulers.boundedElastic())
                .map((value) -> {
                    log.info("threadLocal : {}", threadLocal.get());
                    return value;
                }).publishOn(Schedulers.parallel())
                .subscribe();

        Thread.sleep(1000);


    }

    @Test
    void t21() {
        Flux.just(1)
                .contextWrite(c -> c.put("name", "yohan1"))
                .contextWrite(c -> c.put("name", "yohan2"))
                .contextWrite(c -> c.put("name", "yohan3"))
                .subscribe(null, null, null, Context.of("name", "yohan"));
    }

    @SneakyThrows
    @Test
    void t22() {
        Flux.generate(() -> 0, (state, sink) -> {
                    sink.next(state + 1);
                    log.info("state: {}", state);
                    return state + 1;
                }).delayElements(Duration.ofMillis(1000))
                .doOnComplete(() -> log.info("doOnComplete"))
//                .doOnRequest((v) -> log.info("doOnRequest: {}",v))
                .doOnNext((v) -> log.info("doOnNext: {}", v))
                .subscribe();
        Thread.sleep(20000);
    }

    @Test
    public void t23() {
        Flux.range(0, 15)
                .flatMap(v -> {
                    // 각 값에 대한 비동기 처리를 시뮬레이션하기 위해 Mono.delay를 사용합니다.
                    // 이는 각 숫자를 제곱한 후, 비동기적으로 결과를 반환합니다.

                    return Mono.delay(Duration.ofMillis(1000))
                            .map(delay -> v * v);
                })
                .doOnNext(v -> {
                    System.out.println("doOnNext");
                })
                .subscribe(
                        value -> System.out.println("Processed value: " + value),
                        error -> System.err.println("Error: " + error),
                        () -> System.out.println("Completed!")
                );

        // 비동기 처리를 기다리기 위해 잠시 대기합니다.
        try {
            Thread.sleep(5000); // 5초 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void t24() {
        Flux.generate(
                        () -> new long[]{0, 1}, // 상태 (첫 번째 숫자, 두 번째 숫자)
                        (state, sink) -> {
                            long nextValue = state[0];
                            sink.next(nextValue); // 다음 피보나치 숫자 방출
                            state = new long[]{state[1], state[0] + state[1]}; // 다음 상태 계산
                            return state; // 상태 업데이트
                        }
                ).take(10) // 처음 10개의 피보나치 숫자만 방출
                .subscribe(System.out::println,null,() -> System.out.println("complete")); // 결과 출력
    }


}
