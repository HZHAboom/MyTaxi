package com.dalimao.mytaxi.common.databus;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.functions.Function;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class RxBusTest {

    private static final String TAG = "RxBusTest";
    Presenter mPresenter;

    @Before
    public void setUp() throws Exception{
        mPresenter = new Presenter(new Manager());
        RxBus.getInstance().register(mPresenter);
    }

    @After
    public void tearDown(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RxBus.getInstance().unRegister(mPresenter);
    }

    @Test
    public void testGetUser(){
        mPresenter.getUser();
    }

    @Test
    public void testGetOrder(){
        mPresenter.getOrder();
    }

    class Presenter{
        private Manager manager;

        public Presenter(Manager manager) {
            this.manager = manager;
        }

        public void getUser(){
            manager.getUser();
        }

        public void getOrder(){
            manager.getOrder();
        }

        @RegisterBus
        public void onUser(User user){
            Log.d(TAG,"receive User in thread:" + Thread.currentThread().getName());
        }

        @RegisterBus
        public void onOrder(Order order){
            Log.d(TAG,"receive data :" + Thread.currentThread().getName());
        }

//        /**
//         * 实现DataBusSubscriber接口，接收数据
//         * @param data
//         */
//        @Override
//        public void onEvent(Object data) {
//            if (data instanceof User){
//                Log.d(TAG,"receive User in thread:" + Thread.currentThread().getName());
//            }else if (data instanceof Order){
//                Log.d(TAG,"receive Order :" + Thread.currentThread().getName());
//            }else {
//                Log.d(TAG,"receive data :" + Thread.currentThread().getName());
//            }
//        }
    }

    class Manager{
        public void getUser(){
            RxBus.getInstance().changeProcess(new Function() {
                @Override
                public Object apply(Object o) throws Exception {
                    Log.d(RxBusTest.TAG,"chainProcess getUser start in thread:" + Thread.currentThread().getName());
                    User user = new User();
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    return user;
                }
            });
        }
        public void getOrder(){
            RxBus.getInstance().changeProcess(new Function() {
                @Override
                public Object apply(Object o) throws Exception {
                    Log.d(TAG,"chainProcess getOrder start in thread:" + Thread.currentThread().getName());
                    Order order = new Order();
                    return order;
                }
            });
        }
    }

    class User{

    }

    class Order{

    }
}
