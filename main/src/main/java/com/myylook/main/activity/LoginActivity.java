package com.myylook.main.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;
import com.myylook.common.CommonAppConfig;
import com.myylook.common.CommonAppContext;
import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.activity.WebViewActivity;
import com.myylook.common.bean.UserBean;
import com.myylook.common.http.CommonHttpConsts;
import com.myylook.common.http.CommonHttpUtil;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.interfaces.CommonCallback;
import com.myylook.common.interfaces.OnItemClickListener;
import com.myylook.common.mob.LoginData;
import com.myylook.common.mob.MobBean;
import com.myylook.common.mob.MobCallback;
import com.myylook.common.mob.MobLoginUtil;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.ToastUtil;
import com.myylook.main.R;
import com.myylook.main.adapter.LoginTypeAdapter;
import com.myylook.main.dialog.LoginForbiddenDialogFragment;
import com.myylook.main.event.RegSuccessEvent;
import com.myylook.main.http.MainHttpConsts;
import com.myylook.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * Created by cxf on 2018/9/17.
 */
@Route(path = RouteUtil.PATH_LOGIN)
public class LoginActivity extends AbsActivity implements OnItemClickListener<MobBean> {

    private View mRoot;
    private ImageView mBg;
    private ObjectAnimator mAnimator;
    private EditText mEditPhone;
    private EditText mEditPwd;
    private View mBtnLogin;
    private RecyclerView mRecyclerView;
    private MobLoginUtil mLoginUtil;
    private boolean mFirstLogin;//????????????????????????
    private String mLoginType = Constants.MOB_PHONE;//????????????

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        mRoot = findViewById(R.id.root);
        mBg = findViewById(R.id.bg);
        mBg.post(new Runnable() {
            @Override
            public void run() {
                if (mBg != null && mRoot != null) {
                    int bgHeight = mBg.getHeight();
                    int rootHeight = mRoot.getHeight();
                    int dy = bgHeight - rootHeight;
                    if (dy > 0) {
                        mAnimator = ObjectAnimator.ofFloat(mBg, "translationY", 0, -dy);
                        mAnimator.setInterpolator(new LinearInterpolator());
                        mAnimator.setDuration(3000);
                        mAnimator.setRepeatCount(-1);
                        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
                        mAnimator.start();
                    }
                }
            }
        });
        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditPwd = (EditText) findViewById(R.id.edit_pwd);
        mBtnLogin = findViewById(R.id.btn_login);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditPwd.getText().toString();
                mBtnLogin.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditPhone.addTextChangedListener(textWatcher);
        mEditPwd.addTextChangedListener(textWatcher);
        String tip = getIntent().getStringExtra(Constants.TIP);
        if (!TextUtils.isEmpty(tip)) {
            DialogUitl.showSimpleTipDialog(mContext, tip);
        }
        EventBus.getDefault().register(this);
        MainHttpUtil.getLoginInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String[] loginTypeArray = JSON.parseObject(obj.getString("login_type"), String[].class);
                    if (loginTypeArray != null && loginTypeArray.length > 0) {
                        List<MobBean> list = MobBean.getLoginTypeList(loginTypeArray);
                        View otherLoginTip = findViewById(R.id.other_login_tip);
                        if (otherLoginTip != null) {
                            otherLoginTip.setVisibility(View.VISIBLE);
                        }
                        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
                        adapter.setOnItemClickListener(LoginActivity.this);
                        mRecyclerView.setAdapter(adapter);
                        mLoginUtil = new MobLoginUtil();
                    }
                    TextView loginTipTextView = findViewById(R.id.login_tip);
                    if (loginTipTextView != null) {
                        JSONObject loginInfo = obj.getJSONObject("login_alert");
                        String loginTip = loginInfo.getString("login_title");
                        if (TextUtils.isEmpty(loginTip)) {
                            return;
                        }
                        SpannableString spannableString = new SpannableString(loginTip);
                        JSONArray msgArray = JSON.parseArray(loginInfo.getString("message"));
                        for (int i = 0, size = msgArray.size(); i < size; i++) {
                            final JSONObject msgItem = msgArray.getJSONObject(i);
                            String title = msgItem.getString("title");
                            int startIndex = loginTip.indexOf(title);
                            if (startIndex >= 0) {
                                ClickableSpan clickableSpan = new ClickableSpan() {

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setColor(0xff3399ee);
                                        ds.setUnderlineText(false);
                                    }

                                    @Override
                                    public void onClick(View widget) {
                                        WebViewActivity.forward(mContext, msgItem.getString("url"), false);
                                    }
                                };
                                int endIndex = startIndex + title.length();
                                spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        loginTipTextView.setText(spannableString);
                        loginTipTextView.setMovementMethod(LinkMovementMethod.getInstance());//????????? ??????????????????
                        loginTipTextView.setHighlightColor(Color.TRANSPARENT); //?????????????????????????????????
                    }
                }
            }
        });
    }


    public static void forward() {
        Intent intent = new Intent(CommonAppContext.sInstance, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CommonAppContext.sInstance.startActivity(intent);
    }

    public void loginClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_login) {
            login();

        } else if (i == R.id.btn_register) {
            register();

        } else if (i == R.id.btn_forget_pwd) {
            forgetPwd();

        }
    }

    //??????
    private void register() {
        startActivity(new Intent(mContext, RegisterActivity.class));
    }

    //????????????
    private void forgetPwd() {
        startActivity(new Intent(mContext, FindPwdActivity.class));
    }


    //?????????????????????
    private void login() {
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(R.string.login_input_phone);
            mEditPhone.requestFocus();
            return;
        }
//        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
//            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
//            mEditPhone.requestFocus();
//            return;
//        }
        String pwd = mEditPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.show(R.string.login_input_pwd);
            mEditPwd.requestFocus();
            return;
        }
        mLoginType = Constants.MOB_PHONE;
        MainHttpUtil.login(phoneNum, pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    //??????????????????????????????????????????
    private void forwardTip() {
//        WebViewActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
    }

    //???????????????
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0) {
            if (info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                String uid = obj.getString("id");
                String token = obj.getString("token");
                mFirstLogin = obj.getIntValue("isreg") == 1;
                CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
                getBaseUserInfo();
                //??????????????????
                MobclickAgent.onProfileSignIn(mLoginType, uid);
            }
        } else if (code == 1002) {
            if (info.length > 0) {
                LoginForbiddenDialogFragment fragment = new LoginForbiddenDialogFragment();
                JSONObject obj = JSON.parseObject(info[0]);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TIP, obj.getString("ban_reason"));
                bundle.putString(Constants.UID, obj.getString("ban_tip"));
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "LoginForbiddenDialogFragment");
            }
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * ??????????????????
     */
    private void getBaseUserInfo() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (mFirstLogin) {
                    RecommendActivity.forward(mContext, mFirstLogin);
                } else {
                    MainActivity.forward(mContext, mFirstLogin);
                }
                finish();
            }
        });
    }

    /**
     * ????????????
     */
    private void loginBuyThird(LoginData data) {
        mLoginType = data.getType();
        MainHttpUtil.loginByThird(data.getOpenID(), data.getNickName(), data.getAvatar(), data.getType(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
        });
    }

    @Override
    public void onItemClick(MobBean bean, int position) {
        if (mLoginUtil == null) {
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    loginBuyThird((LoginData) data);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = null;
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        MainHttpUtil.cancel(MainHttpConsts.GET_LOGIN_INFO);
        if (mLoginUtil != null) {
            mLoginUtil.release();
        }
        super.onDestroy();
    }
}
