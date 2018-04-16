package com.dalimao.mytaxi.main.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.view.PhoneInputDialog;
import com.dalimao.mytaxi.common.http.IHttpClient;
import com.dalimao.mytaxi.common.http.impl.OkHttpClientImpl;
import com.dalimao.mytaxi.common.storage.SharedPreferencesDao;
import com.dalimao.mytaxi.common.util.ToastUtils;
import com.dalimao.mytaxi.main.model.IMainManager;
import com.dalimao.mytaxi.main.model.MainManagerImpl;
import com.dalimao.mytaxi.main.presenter.IMainPresenter;
import com.dalimao.mytaxi.main.presenter.MainPresenterImpl;

/**
 * 1.检查本地记录（登录态检查）
 * 2.若用户没登录则登录
 * 3.登录之前先校验手机号码
 * 4.token有效使用  token 自动登录
 * todo: 地图初始化
 */
public class MainActivity extends AppCompatActivity implements IMainView{

    private static final String TAG = "MainActivity";
    private static final int READ_PHONE_STATE_REQUEST_CODE = 100;
    private IMainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},READ_PHONE_STATE_REQUEST_CODE);
        }
        IHttpClient httpClient = new OkHttpClientImpl();
        SharedPreferencesDao dao = new SharedPreferencesDao(MyTaxiApplication.getInstance(),SharedPreferencesDao.FILE_ACCOUNT);
        IMainManager mainManager = new MainManagerImpl(httpClient,dao);
        mPresenter = new MainPresenterImpl(this,mainManager);
        mPresenter.loginByToken();
    }


    /**
     * 显示手机输入框
     */
    private void showPhoneInputDialog() {
        PhoneInputDialog dialog = new PhoneInputDialog(this);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (requestCode == READ_PHONE_STATE_REQUEST_CODE){

            }
        }else {
            ToastUtils.show(this,"注册账号需要改权限");
        }
    }

    @Override
    public void showLoginSuc() {
        ToastUtils.show(MainActivity.this,getString(R.string.login_suc));
    }

    @Override
    public void showError(int code, String msg) {
        switch (code){
            case IMainManager.SERVER_FAIL:
                ToastUtils.show(MainActivity.this,getString(R.string.error_server));
                showPhoneInputDialog();
                break;
            case IMainManager.TOKEN_INVALID:
                ToastUtils.show(MainActivity.this,getString(R.string.token_invalid));
                showPhoneInputDialog();
                break;
        }
    }
}
