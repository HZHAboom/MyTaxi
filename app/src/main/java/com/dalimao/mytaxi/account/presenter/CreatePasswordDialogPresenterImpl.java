package com.dalimao.mytaxi.account.presenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.view.ICreatePasswordDialogView;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public class CreatePasswordDialogPresenterImpl implements ICreatePasswordDialogPresenter {

    private ICreatePasswordDialogView mView;
    private IAccountManager mAccountManager;

    static class MyHandler extends Handler{
        private WeakReference<CreatePasswordDialogPresenterImpl> mReference;

        public MyHandler(CreatePasswordDialogPresenterImpl context) {
            mReference = new WeakReference<CreatePasswordDialogPresenterImpl>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CreatePasswordDialogPresenterImpl presenter = mReference.get();
            if (presenter == null){
                return;
            }
            switch (msg.what){
                case IAccountManager.REGISTER_SUC:
                    presenter.mView.showRegisterSuc();
                    break;
                case IAccountManager.LOGIN_SUC:
                    presenter.mView.showLoginSuc();
                    break;
                case IAccountManager.SERVER_FAIL:
                    presenter.mView.showError(IAccountManager.SERVER_FAIL, MyTaxiApplication.getInstance().getString(R.string.error_server));
                    break;
            }
        }
    }

    public CreatePasswordDialogPresenterImpl(ICreatePasswordDialogView view, IAccountManager accountManager) {
        mView = view;
        mAccountManager = accountManager;
        mAccountManager.setHandler(new MyHandler(this));
    }

    @Override
    public void requestRegister(String phone, String pw) {
        mAccountManager.register(phone,pw);
    }

    @Override
    public void requestLogin(String phone, String pw) {
        mAccountManager.login(phone,pw);
    }
}
