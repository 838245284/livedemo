package com.myylook.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.myylook.common.Constants;
import com.myylook.common.activity.AbsActivity;
import com.myylook.common.http.HttpCallback;
import com.myylook.common.utils.DialogUitl;
import com.myylook.common.utils.RouteUtil;
import com.myylook.common.utils.ToastUtil;
import com.myylook.common.utils.WordUtil;
import com.myylook.main.R;
import com.myylook.main.adapter.CashAccountAdapter;
import com.myylook.main.bean.CashAccountBean;
import com.myylook.main.http.MainHttpConsts;
import com.myylook.main.http.MainHttpUtil;
import com.myylook.main.views.CashAccountViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/20.
 * 提现账户
 */
@Route(path = RouteUtil.PATH_CASH_ACCOUNT)
public class CashActivity extends AbsActivity implements View.OnClickListener, CashAccountAdapter.ActionListener {

    private CashAccountViewHolder mCashAccountViewHolder;
    private View mNoAccount;
    private RecyclerView mRecyclerView;
    private CashAccountAdapter mAdapter;
    private String mCashAccountId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cash;
    }

    @Override
    protected void main() {
        Intent intent = getIntent();
        mCashAccountId = intent.getStringExtra(Constants.CASH_ACCOUNT_ID);
        if (mCashAccountId == null) {
            mCashAccountId = "";
        }
        findViewById(R.id.btn_add).setOnClickListener(this);
        mNoAccount = findViewById(R.id.no_account);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CashAccountAdapter(mContext, mCashAccountId);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        loadData();
    }

    public void loadData() {
        MainHttpUtil.getCashAccountList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<CashAccountBean> list = JSON.parseArray(Arrays.toString(info), CashAccountBean.class);
                    if (list.size() > 0) {
                        if (mNoAccount.getVisibility() == View.VISIBLE) {
                            mNoAccount.setVisibility(View.INVISIBLE);
                        }
                        mAdapter.setList(list);
                    } else {
                        if (mNoAccount.getVisibility() != View.VISIBLE) {
                            mNoAccount.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_add) {
            addAccount();

        }
    }

    private void addAccount() {
        if (mCashAccountViewHolder == null) {
            mCashAccountViewHolder = new CashAccountViewHolder(mContext, (ViewGroup) findViewById(R.id.root));
        }
        mCashAccountViewHolder.addToParent();
    }

    @Override
    public void onBackPressed() {
        if (mCashAccountViewHolder != null && mCashAccountViewHolder.isShowed()) {
            mCashAccountViewHolder.removeFromParent();
            return;
        }
        super.onBackPressed();
    }

    public void insertAccount(CashAccountBean cashAccountBean) {
        if (mAdapter != null) {
            if (mNoAccount.getVisibility() == View.VISIBLE) {
                mNoAccount.setVisibility(View.INVISIBLE);
            }
            mAdapter.insertItem(cashAccountBean);
        }
    }

    @Override
    public void onItemClick(CashAccountBean bean, int position) {
//        if (!bean.getId().equals(mCashAccountId)) {
//            Map<String, String> map = new HashMap<>();
//            map.put(Constants.CASH_ACCOUNT_ID, bean.getId());
//            map.put(Constants.CASH_ACCOUNT, bean.getAccount());
//            map.put(Constants.CASH_ACCOUNT_TYPE, String.valueOf(bean.getType()));
//            map.put(Constants.CASH_ACCOUNT_NAME, bean.getUserName());
//            SpUtil.getInstance().setMultiStringValue(map);
//        }
//        onBackPressed();

        Intent intent = new Intent();
        intent.putExtra(Constants.CASH_ACCOUNT_ID, bean.getId());
        intent.putExtra(Constants.CASH_ACCOUNT, bean.getAccount());
        intent.putExtra(Constants.CASH_ACCOUNT_TYPE, String.valueOf(bean.getType()));
        intent.putExtra(Constants.CASH_ACCOUNT_NAME, bean.getUserName());
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void onItemDelete(final CashAccountBean bean, final int position) {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.cash_delete), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                MainHttpUtil.deleteCashAccount(bean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
//                            if (bean.getId().equals(mCashAccountId)) {
//                                SpUtil.getInstance().removeValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE, Constants.CASH_ACCOUNT_NAME);
//                            }
                            if (mAdapter != null) {
                                mAdapter.removeItem(position);
                                if (mAdapter.getItemCount() == 0) {
                                    if (mNoAccount.getVisibility() != View.VISIBLE) {
                                        mNoAccount.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_USER_ACCOUNT_LIST);
        MainHttpUtil.cancel(MainHttpConsts.ADD_CASH_ACCOUNT);
        MainHttpUtil.cancel(MainHttpConsts.DEL_CASH_ACCOUNT);
        super.onDestroy();
    }
}