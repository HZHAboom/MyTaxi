package com.dalimao.mytaxi.account.presenter;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.account.view.ICreatePasswordDialogView;
import com.dalimao.mytaxi.common.databus.RegisterBus;
import com.dalimao.mytaxi.common.http.biz.BaseBizResponse;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public class CreatePasswordDialogPresenterImpl implements ICreatePasswordDialogPresenter {

    private ICreatePasswordDialogView mView;
    private IAccountManager mAccountManager;

//    static class MyHandler extends Handler{
//        private WeakReference<CreatePasswordDialogPresenterImpl> mReference;
//
//        public MyHandler(CreatePasswordDialogPresenterImpl context) {
//            mReference = new WeakReference<CreatePasswordDialogPresenterImpl>(context);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            CreatePasswordDialogPresenterImpl presenter = mReference.get();
//            if (presenter == null){
//                return;
//            }
//            switch (msg.what){
//                case IAccountManager.REGISTER_SUC:
//                    presenter.mView.showRegisterSuc();
//                    break;
//                case IAccountManager.LOGIN_SUC:
//                    presenter.mView.showLoginSuc();
//                    break;
//                case IAccountManager.SERVER_FAIL:
//                    presenter.mView.showError(IAccountManager.SERVER_FAIL, MyTaxiApplication.getInstance().getString(R.string.error_server));
//                    break;
//            }
//        }
//    }

    public CreatePasswordDialogPresenterImpl(ICreatePasswordDialogView view, IAccountManager accountManager) {
        mView = view;
        mAccountManager = accountManager;
//        mAccountManager.setHandler(new MyHandler(this));
    }

    @Override
    public void requestRegister(String phone, String pw) {
        mAccountManager.register(phone,pw);
    }

    @Override
    public void requestLogin(String phone, String pw) {
        mAccountManager.login(phone,pw);
    }

    @RegisterBus
    public void onRegisterResponse(BaseBizResponse response){
        switch (response.getCode()){
            case IAccountManager.REGISTER_SUC:
                    mView.showRegisterSuc();
                    break;
            case IAccountManager.SERVER_FAIL:
                mView.showError(IAccountManager.SERVER_FAIL, MyTaxiApplication.getInstance().getString(R.string.error_server));
                break;
        }
    }

    @RegisterBus
    public void onLoginResponse(LoginResponse response){
        switch (response.getCode()){
            case IAccountManager.LOGIN_SUC:
                mView.showLoginSuc();
                break;
            case IAccountManager.SERVER_FAIL:
                mView.showError(IAccountManager.SERVER_FAIL, MyTaxiApplication.getInstance().getString(R.string.error_server));
                break;
        }
    }
}
