package com.dalimao.mytaxi.common.databus;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/17 0017.
 */

public class RxBus {
    private static final String TAG = "RxBus";
    private static volatile RxBus instance;
    private Set<Object> mSubscribers;

    private RxBus(){
        mSubscribers = new CopyOnWriteArraySet<>();
    }

    public static RxBus getInstance() {
        synchronized (RxBus.class){
            if (instance == null){
                instance = new RxBus();
            }
            return instance;
        }
    }

    public synchronized void register(Object dataBusSubscriber){
        mSubscribers.add(dataBusSubscriber);
    }

    public synchronized void unRegister(Object dataBusSubscriber){
        mSubscribers.remove(dataBusSubscriber);
    }

    /**
     * 包装处理过程
     */
    public void chainProcess(Function function){
        Log.d(TAG,"function="+function);
        Flowable.just("")
                .subscribeOn(Schedulers.io())
                .map(function)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object data) throws Exception {
                        for (Object subscriber :
                                mSubscribers) {
                            //扫描注解，将数据发送到注册的对象的标记方法
                            if (data!=null){
                                callMethodByAnnotiation(subscriber,data);
                            }
                        }
                    }
                });
    }

    /**
     * 反射获取对象方法列表，判断：
     * 1 是否被注解修饰
     * 2 参数类型是否和data类型一致
     * @param target
     * @param data
     */
    private void callMethodByAnnotiation(Object target, final Object data) {
        Method[] methodArray = target.getClass().getDeclaredMethods();
        for (int i = 0; i < methodArray.length; i++) {
            try {
                if (methodArray[i].isAnnotationPresent(RegisterBus.class)){
                    //被 @RegisterBus 修饰的方法
                    Class paramType = methodArray[i].getParameterTypes()[0];
                    if (data.getClass().getName().equals(paramType.getName())){
                        //参数类型和data一样，调用此方法
                        methodArray[i].invoke(target,new Object[]{data});
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
