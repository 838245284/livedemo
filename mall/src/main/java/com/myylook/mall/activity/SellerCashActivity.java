package com.myylook.mall.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.utils.CommonIconUtil;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.StringUtil;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.mall.R;
import com.myylook.mall.http.MallHttpConsts;
import com.myylook.mall.http.MallHttpUtil;

/**
 * 卖家提现
 */
public class SellerCashActivity extends AbsActivity implements View.OnClickListener {

    public static void forward(Context context, String balanceVal, String totalVal) {
        Intent intent = new Intent(context, SellerCashActivity.class);
        intent.putExtra(Constants.MALL_CASH_BALANCE, balanceVal);
        intent.putExtra(Constants.MALL_CASH_TOTAL, totalVal);
        ((Activity) context).startActivityForResult(intent, 0);
    }

    private String mAccountID;
    private TextView mTotal;
    private TextView mBalance;
    private EditText mMoney;
    private View mChooseTip;
    private View mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccountName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_cash;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_301));
        mTotal = findViewById(R.id.total);
        mBalance = findViewById(R.id.balance);
        mMoney = findViewById(R.id.money);
        mChooseTip = findViewById(R.id.choose_tip);
        mAccountGroup = findViewById(R.id.account_group);
        mAccountIcon = findViewById(R.id.account_icon);
        mAccountName = findViewById(R.id.account_name);
        findViewById(R.id.btn_choose_account).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        String totalVal = getIntent().getStringExtra(Constants.MALL_CASH_TOTAL);
        String balanceVal = getIntent().getStringExtra(Constants.MALL_CASH_BALANCE);
        mTotal.setText(renderBalanceText(totalVal));
        mBalance.setText(renderBalanceText(balanceVal));
    }

    private CharSequence renderBalanceText(String text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }
        if (!text.contains(".")) {
            text += ".00";
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new AbsoluteSizeSpan(16, true), text.indexOf("."), builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_choose_account) {
            chooseAccount();
        } else if (id == R.id.btn_submit) {
            submit();
        }
    }

    private void submit() {
        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show(R.string.profit_choose_account);
            return;
        }
        String money = mMoney.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            ToastUtil.show(R.string.mall_306);
            return;
        }
        MallHttpUtil.goodsCash(mAccountID, money, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    setResult(RESULT_OK);
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }


    /**
     * 选择账户
     */
    private void chooseAccount() {
        RouteUtil.forwardCashAccount(this, 100, mAccountID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (intent != null) {
                String accountId = intent.getStringExtra(Constants.CASH_ACCOUNT_ID);
                String account = intent.getStringExtra(Constants.CASH_ACCOUNT);
                String typeString = intent.getStringExtra(Constants.CASH_ACCOUNT_TYPE);
                String accountName = intent.getStringExtra(Constants.CASH_ACCOUNT_NAME);
                int type = 0;
                if (!TextUtils.isEmpty(typeString)) {
                    type = Integer.parseInt(typeString);
                }
                if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(account)) {
                    if (mChooseTip.getVisibility() == View.VISIBLE) {
                        mChooseTip.setVisibility(View.INVISIBLE);
                    }
                    if (mAccountGroup.getVisibility() != View.VISIBLE) {
                        mAccountGroup.setVisibility(View.VISIBLE);
                    }
                    mAccountID = accountId;
                    mAccountIcon.setImageResource(CommonIconUtil.getCashTypeIcon(type));
                    if (type == 2) {
                        mAccountName.setText(account);
                    } else {
                        mAccountName.setText(StringUtil.contact(account, "(", accountName, ")"));
                    }
                }
            }
        }
    }


//    /**
//     * 选择提现账户
//     */
//    private void chooseAccount() {
//        RouteUtil.forwardCashAccount(mAccountID);
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        getAccount();
//    }
//
//    private void getAccount() {
//        String[] values = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE,Constants.CASH_ACCOUNT_NAME);
//        if (values != null && values.length == 4) {
//            String accountId = values[0];
//            String account = values[1];
//            String typeString = values[2];
//            String accountName = values[3];
//            int type = 0;
//            if (!TextUtils.isEmpty(typeString)) {
//                type = Integer.parseInt(typeString);
//            }
//            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(account) ) {
//                if (mChooseTip.getVisibility() == View.VISIBLE) {
//                    mChooseTip.setVisibility(View.INVISIBLE);
//                }
//                if (mAccountGroup.getVisibility() != View.VISIBLE) {
//                    mAccountGroup.setVisibility(View.VISIBLE);
//                }
//                mAccountID = accountId;
//                mAccountIcon.setImageResource(CommonIconUtil.getCashTypeIcon(type));
//                if (type == 2) {
//                    mAccountName.setText(account);
//                } else {
//                    mAccountName.setText(StringUtil.contact(account, "(", accountName, ")"));
//                }
//            } else {
//                if (mAccountGroup.getVisibility() == View.VISIBLE) {
//                    mAccountGroup.setVisibility(View.INVISIBLE);
//                }
//                if (mChooseTip.getVisibility() != View.VISIBLE) {
//                    mChooseTip.setVisibility(View.VISIBLE);
//                }
//                mAccountID = null;
//            }
//        } else {
//            if (mAccountGroup.getVisibility() == View.VISIBLE) {
//                mAccountGroup.setVisibility(View.INVISIBLE);
//            }
//            if (mChooseTip.getVisibility() != View.VISIBLE) {
//                mChooseTip.setVisibility(View.VISIBLE);
//            }
//            mAccountID = null;
//        }
//    }

    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GOODS_CASH);
        super.onDestroy();
    }
}
