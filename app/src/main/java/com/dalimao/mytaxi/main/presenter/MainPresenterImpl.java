package com.dalimao.mytaxi.main.presenter;

import android.os.Handler;
import android.os.Message;

import com.dalimao.mytaxi.main.model.IMainManager;
import com.dalimao.mytaxi.main.view.IMainView;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/4/14 0014.
 */

public class MainPresenterImpl implements IMainPresenter {

    private IMainView mView;
    private IMainManager mMainManager;

    static class MyHandler extends Handler{
        private WeakReference<MainPresenterImpl> refContext;
        public MyHandler(MainPresenterImpl context){
            refContext = new WeakReference<MainPresenterImpl>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainPresenterImpl presenter = refContext.get();
            if (presenter == null){
                return;
            }
            switch (msg.what){
                case IMainManager.LOGIN_SUC:
                    presenter.mView.showLoginSuc();
                    break;
                case IMainManager.TOKEN_INVALID:
                    presenter.mView.showError(IMainManager.TOKEN_INVALID,"");
                    break;
                case IMainManager.SERVER_FAIL:
                    presenter.mView.showError(IMainManager.SERVER_FAIL,"");
                    break;
            }
        }
    }

    public MainPresenterImpl(IMainView view, IMainManager mainManager) {
        mView = view;
        mMainManager = mainManager;
        mMainManager.setHandler(new MyHandler(this));
    }

    @Override
    public void loginByToken() {
        mMainManager.loginByToken();
    }
}
