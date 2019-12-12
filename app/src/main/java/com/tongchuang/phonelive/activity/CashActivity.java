package com.tongchuang.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.tongchuang.phonelive.Constants;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.adapter.CashAccountAdapter;
import com.tongchuang.phonelive.bean.CashAccountBean;
import com.tongchuang.phonelive.http.HttpCallback;
import com.tongchuang.phonelive.http.HttpConsts;
import com.tongchuang.phonelive.http.HttpUtil;
import com.tongchuang.phonelive.utils.DialogUitl;
import com.tongchuang.phonelive.utils.SpUtil;
import com.tongchuang.phonelive.utils.ToastUtil;
import com.tongchuang.phonelive.utils.WordUtil;
import com.tongchuang.phonelive.views.CashAccountViewHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/10/20.
 *  提现账户
 */

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

    private void loadData() {
        HttpUtil.getCashAccountList(new HttpCallback() {
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
        switch (v.getId()) {
            case R.id.btn_add:
                addAccount();
                break;
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
        if (!bean.getId().equals(mCashAccountId)) {
            Map<String, String> map = new HashMap<>();
            map.put(Constants.CASH_ACCOUNT_ID, bean.getId());
            map.put(Constants.CASH_ACCOUNT, bean.getAccount());
            map.put(Constants.CASH_ACCOUNT_TYPE, String.valueOf(bean.getType()));
            SpUtil.getInstance().setMultiStringValue(map);
        }
        onBackPressed();
    }

    @Override
    public void onItemDelete(final CashAccountBean bean, final int position) {
        DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.cash_delete), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                HttpUtil.deleteCashAccount(bean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (bean.getId().equals(mCashAccountId)) {
                                SpUtil.getInstance().removeValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
                            }
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
        HttpUtil.cancel(HttpConsts.GET_USER_ACCOUNT_LIST);
        HttpUtil.cancel(HttpConsts.ADD_CASH_ACCOUNT);
        HttpUtil.cancel(HttpConsts.DEL_CASH_ACCOUNT);
        super.onDestroy();
    }
}
