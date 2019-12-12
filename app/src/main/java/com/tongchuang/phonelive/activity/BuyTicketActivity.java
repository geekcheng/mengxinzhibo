package com.tongchuang.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.tongchuang.phonelive.AppConfig;
import com.tongchuang.phonelive.Constants;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.TicketBean;
import com.tongchuang.phonelive.glide.ImgLoader;
import com.tongchuang.phonelive.http.HttpCallback;
import com.tongchuang.phonelive.http.HttpConsts;
import com.tongchuang.phonelive.http.HttpUtil;
import com.tongchuang.phonelive.interfaces.CommonCallback;
import com.tongchuang.phonelive.utils.HtmlFromUtils;
import com.tongchuang.phonelive.utils.ToastUtil;
import com.tongchuang.phonelive.utils.WordUtil;
import com.tongchuang.phonelive.views.BuyTicketConfirmViewHolder;

public class BuyTicketActivity extends AbsActivity implements View.OnClickListener {
    public static final String BUY_TICKET_BEAN = "BUY_TICKET_BEAN";
    public static final String BUY_TICKET_TYPE = "BUY_TICKET_TYPE";
    private TicketBean mTicketBean;
    private BuyTicketConfirmViewHolder mBuyTicketConfirmViewHolder;

    private RoundedImageView ivAvatarThumb;
    private TextView tvTitle;
    private TextView tvNiceName;
    private FrameLayout layoutBuyBottom;
    private TextView tvFollow;
    private TextView tvBuy;
    private TextView tvPrice;
    private TextView tvTimeStart;
    private TextView tvTimeEnd;
    private TextView tvContent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_buy_ticket;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.ticket_buy));

        tvContent = findViewById(R.id.tvContent);
        tvTimeEnd = findViewById(R.id.tvTimeEnd);
        tvTimeStart = findViewById(R.id.tvTimeStart);
        tvPrice = findViewById(R.id.tvPrice);
        tvNiceName = findViewById(R.id.tvNiceName);
        tvTitle = findViewById(R.id.tvTitle);
        ivAvatarThumb = findViewById(R.id.ivAvatarThumb);
        layoutBuyBottom = findViewById(R.id.layoutBuyBottom);
        tvFollow = findViewById(R.id.tvFollow);
        tvBuy = findViewById(R.id.tvBuy);
        tvFollow.setOnClickListener(this);
        tvBuy.setOnClickListener(this);

        mTicketBean = (TicketBean) getIntent().getSerializableExtra(BUY_TICKET_BEAN);
        switch (getIntent().getIntExtra(BUY_TICKET_TYPE, 0)) {
            case Constants.TICKET_SELL:
                tvFollow.setText(mTicketBean.getIsAttention() == 1 ? R.string.following : R.string.follow);
                tvBuy.setText(mTicketBean.getIsTicket() == 1 ? R.string.ticket_buy_button2 : R.string.ticket_buy_button);
                break;
            case Constants.TICKET_HISTORY:
                layoutBuyBottom.setVisibility(View.GONE);
                break;
            case Constants.TICKET_CENTER_ING:
            case Constants.TICKET_CENTER_ED:
                layoutBuyBottom.setVisibility(View.GONE);
                findViewById(R.id.layoutNiceName).setVisibility(View.GONE);
                findViewById(R.id.layoutPrice).setVisibility(View.GONE);
                break;
        }
        ImgLoader.display(mTicketBean.getImage(), ivAvatarThumb);
        tvTitle.setText(mTicketBean.getTitle());
        tvNiceName.setText(mTicketBean.getUser_nicename());
        tvPrice.setText(mTicketBean.getPrice() + WordUtil.getString(R.string.ticket_price_unit));
        tvTimeStart.setText(mTicketBean.getStime());
        tvTimeEnd.setText(mTicketBean.getEtime());
//        tvContent.setText(Html.fromHtml(mTicketBean.getContent()));
        HtmlFromUtils.setTextFromHtml(this, tvContent, mTicketBean.getContent());
    }

    public static void forward(Context context, TicketBean ticketBean, int type) {
        Intent intent = new Intent(context, BuyTicketActivity.class);
        intent.putExtra(BUY_TICKET_BEAN, ticketBean);
        intent.putExtra(BUY_TICKET_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.TICKET_BUY);
        HttpUtil.cancel(HttpConsts.SET_ATTENTION);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvFollow:
                if (AppConfig.getInstance().getUid().equals(mTicketBean.getUid())) {
                    ToastUtil.show("无须关注自己");
                } else {
                    if (mTicketBean.getIsAttention() != 1) {
                        HttpUtil.setAttention(Constants.FOLLOW_FROM_TICKET_BUY, mTicketBean.getUid(), new CommonCallback<Integer>() {
                            @Override
                            public void callback(Integer isAttention) {
                                tvFollow.setText(isAttention == 1 ? R.string.following : R.string.follow);
                            }
                        });
                    }
                }
                break;
            case R.id.tvBuy:
                if (AppConfig.getInstance().getUid().equals(mTicketBean.getUid())) {
                    ToastUtil.show("无须购买自己的活动");
                } else {
                    if (mTicketBean.getIsTicket() != 1) {
                        if (mBuyTicketConfirmViewHolder == null) {
                            mBuyTicketConfirmViewHolder = new BuyTicketConfirmViewHolder(mContext, (ViewGroup) findViewById(R.id.root));
                        }
                        mBuyTicketConfirmViewHolder.addToParent();
                        mBuyTicketConfirmViewHolder.showConfirm();
                        mBuyTicketConfirmViewHolder.setBuyTicketConfirmListener(new BuyTicketConfirmViewHolder.BuyTicketConfirmListener() {
                            @Override
                            public void onError() {
                                startActivity(new Intent(mContext, MyCoinActivity.class));
                            }

                            @Override
                            public void onConfirm() {
                                HttpUtil.buyTicket(mTicketBean.getId(), new HttpCallback() {
                                    @Override
                                    public void onSuccess(int code, String msg, String[] info) {
                                        if (code == 0) {
                                            mTicketBean.setIsTicket(1);
                                            tvBuy.setText(R.string.ticket_buy_button2);
                                            ToastUtil.show(msg);
                                        } else if (code == 1003) {
                                            mBuyTicketConfirmViewHolder.addToParent();
                                            mBuyTicketConfirmViewHolder.showError(msg);
                                        } else {
                                            ToastUtil.show(msg);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBuyTicketConfirmViewHolder != null && mBuyTicketConfirmViewHolder.isShowed()) {
            mBuyTicketConfirmViewHolder.removeFromParent();
            return;
        }
        super.onBackPressed();
    }
}
