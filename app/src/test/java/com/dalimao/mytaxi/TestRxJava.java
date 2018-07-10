package com.dalimao.mytaxi;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public class TestRxJava {
    @Before
    public void setUp(){
        Thread.currentThread().setName("currentThread");
    }
    @Test
    public void testSubScribe(){
        //观察者/订阅者
        Subscriber<String> subscriber =
                new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext in thread:" +
                                Thread.currentThread().getName());
                        System.out.println(s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("onError in thread:" +
                                Thread.currentThread().getName());
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onCompleted in thread:" +
                                Thread.currentThread().getName());
                    }
                };

        //被观察者
        Flowable flowable = Flowable.create(
                new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        System.out.println("call in thread:" + Thread.currentThread().getName());
//                        emitter.onError(new Exception("error"));
                        emitter.onNext("hello world");
                        emitter.onComplete();
                    }
                }
        , BackpressureStrategy.BUFFER);
        flowable.subscribe(subscriber);
    }

    @Test
    public void testScheduler(){
        //观察者/订阅者
        Subscriber<String> subscriber =
                new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext in thread:" +
                                Thread.currentThread().getName());
                        System.out.println(s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("onError in thread:" +
                                Thread.currentThread().getName());
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onCompleted in thread:" +
                                Thread.currentThread().getName());
                    }
                };

        //被观察者
        Flowable flowable = Flowable.create(
                new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                        System.out.println("call in thread:" + Thread.currentThread().getName());
//                        emitter.onError(new Exception("error"));
                        emitter.onNext("hello world");
                        emitter.onComplete();
                    }
                }
                , BackpressureStrategy.BUFFER);
        flowable.subscribeOn(Schedulers.io())   //指定生产事件在当前线程中进行
                .observeOn(Schedulers.newThread())  //指定消费事件在新线程中进行
                .subscribe(subscriber);
    }

    @Test
    public void testMap(){
        String name = "hzh";
        Flowable.just(name)
                .subscribeOn(Schedulers.newThread())    //指定下一个生成节点在新线程中处理
                .map(new Function<String, User>() {
                    @Override
                    public User apply(String s) throws Exception {
                        User user = new User();
                        user.setName(s);
                        System.out.println("process User call in thread:" +
                                Thread.currentThread().getName());
                        return user;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .map(new Function<User, Object>() {
                    @Override
                    public Object apply(User user) throws Exception {
                        //  如果需要，这里还可以对User进行加工
                        System.out.println("process User call in thread:" +
                                Thread.currentThread().getName());
                        return user;
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        System.out.println("receive User call in thread:" + Thread.currentThread().getName());
                    }
                });
    }
    class User {
        String name;

        public User() {
        }

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
