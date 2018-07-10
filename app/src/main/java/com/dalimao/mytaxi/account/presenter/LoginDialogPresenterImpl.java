package com.dalimao.mytaxi.account.presenter;

import com.dalimao.mytaxi.MyTaxiApplication;
import com.dalimao.mytaxi.R;
import com.dalimao.mytaxi.account.model.IAccountManager;
import com.dalimao.mytaxi.account.model.response.LoginResponse;
import com.dalimao.mytaxi.account.view.ILoginView;
import com.dalimao.mytaxi.common.databus.RegisterBus;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public class LoginDialogPresenterImpl implements ILoginDialogPresenter {

    private ILoginView mView;
    private IAccountManager mAccountManager;

//    static class MyHandler extends Handler{
//        private WeakReference<LoginDialogPresenterImpl> refContext;
//
//        public MyHandler(LoginDialogPresenterImpl context) {
//            this.refContext = new WeakReference<LoginDialogPresenterImpl>(context);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            LoginDialogPresenterImpl presenter = refContext.get();
//            if (presenter == null){
//                return;
//            }
//            switch (msg.what){
//                case IAccountManager.LOGIN_SUC:
//                    presenter.mView.showLoginSuc();
//                    break;
//                case IAccountManager.PW_ERR:
//                    presenter.mView.showError(IAccountManager.SERVER_FAIL,MyTaxiApplication.getInstance().getString(R.string.password_error));
//                    break;
//                case IAccountManager.SERVER_FAIL:
//                    presenter.mView.showError(IAccountManager.SERVER_FAIL, MyTaxiApplication.getInstance().getString(R.string.error_server));
//                    break;
//            }
//        }
//    }

    public LoginDialogPresenterImpl(ILoginView view, IAccountManager accountManager) {
        mView = view;
        mAccountManager = accountManager;
//        mAccountManager.setHandler(new MyHandler(this));
    }

    @Override
    public void requestLogin(String phone, String password) {
        mAccountManager.login(phone,password);
    }

    @RegisterBus
    public void onLoginResponse(LoginResponse response){
        switch (response.getCode()){
            case IAccountManager.LOGIN_SUC:
                mView.showLoginSuc();
                break;
            case IAccountManager.PW_ERR:
                mView.showError(IAccountManager.SERVER_FAIL, MyTaxiApplication.getInstance().getString(R.string.password_error));
                break;
            case IAccountManager.SERVER_FAIL:
                mView.showError(IAccountManager.SERVER_FAIL, MyTaxiApplication.getInstance().getString(R.string.error_server));
                break;
        }
    }
}
