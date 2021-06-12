package com.myylook.mall.activity;

import android.view.View;
import android.widget.TextView;

import com.myylook.common.CommonAppConfig;
import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.bean.ConfigBean;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.mall.R;
import com.myylook.mall.http.MallHttpConsts;
import com.myylook.mall.http.MallHttpUtil;

/**
 * 开店保证金
 */
public class ShopApplyBondActivity extends AbsActivity implements View.OnClickListener {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_shop_apply_bond;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_046));
        findViewById(R.id.btn_submit).setOnClickListener(this);
        TextView bond = findViewById(R.id.bond);
        bond.setText(getIntent().getStringExtra(Constants.MALL_APPLY_BOND));
        ConfigBean configBean = CommonAppConfig.getInstance().getConfig();
        if (configBean != null) {
            TextView textView = findViewById(R.id.tip);
            textView.setText(String.format(WordUtil.getString(R.string.mall_050), configBean.getShopSystemName()));
        }

    }

    @Override
    public void onClick(View v) {
        MallHttpUtil.setBond(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.SET_BOND);
        super.onDestroy();
    }
}
