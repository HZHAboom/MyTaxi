package com.dalimao.mytaxi.common.storage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2018/4/13 0013.
 */

public class SharedPreferencesDao {
    private static final String TAG = "SharedPreferencesDao";
    public static final String FILE_ACCOUNT = "FILE_ACCOUNT";
    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private SharedPreferences mSharedPreferences;

    public SharedPreferencesDao(Application application,String fileName) {
        mSharedPreferences =
                application.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 保存 K-V
     */
    public void save(String key,String value){
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(key,value);
        edit.commit();
    }

    /**
     * 读取 K-V
     */
    public String get(String key){
        return mSharedPreferences.getString(key,null);
    }

    /**
     * 保存对象
     */
    public void save(String key,Object obj){
        String value = new Gson().toJson(obj);
        save(key,value);
    }

    /**
     * 读取对象
     */
    public Object get(String key,Class cls){
        String value = get(key);
        try {
            if (value!=null){
                Object o = new Gson().fromJson(value,cls);
                return o;
            }
        } catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

        return null;
    }


}
