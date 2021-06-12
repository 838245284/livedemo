package com.myylook.main.activity;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.utils.RouteUtil;
import com.myylook.im.utils.ImMessageUtil;
import com.myylook.im.utils.ImPushUtil;
import com.myylook.main.R;

/**
 * Created by cxf on 2017/10/9.
 * 登录失效的时候以dialog形式弹出的activity
 */
@Route(path = RouteUtil.PATH_LOGIN_INVALID)
public class LoginInvalidActivity extends AbsActivity implements View.OnClickListener {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_invalid;
    }

    @Override
    protected void main() {
        TextView textView = (TextView) findViewById(R.id.content);
        String tip = getIntent().getStringExtra(Constants.TIP);
        textView.setText(tip);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        CommonAppConfig.getInstance().clearLoginInfo();
        //退出极光
        ImMessageUtil.getInstance().logoutImClient();
        ImPushUtil.getInstance().logout();
        LoginActivity.forward();
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
