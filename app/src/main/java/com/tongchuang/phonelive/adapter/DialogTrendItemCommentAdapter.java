package com.tongchuang.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.bean.CommentBean;
import com.tongchuang.phonelive.custom.LinkTouchMovementMethod;
import com.tongchuang.phonelive.custom.MySpannableTextView;
import com.tongchuang.phonelive.glide.ImgLoader;
import com.tongchuang.phonelive.interfaces.OnTrendCommentItemClickListener;
import com.tongchuang.phonelive.utils.TextRender;

public class DialogTrendItemCommentAdapter extends RefreshAdapter<CommentBean> {
    private View.OnClickListener mOnClickListener;
    private OnTrendCommentItemClickListener onTrendCommentItemClickListener;

    public DialogTrendItemCommentAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.layoutComment: {
                        Object tag = v.getTag();
                        if (tag != null) {
                            int position = (int) tag;
                            if (onTrendCommentItemClickListener != null) {
                                onTrendCommentItemClickListener.onItemClick(mList.get(position));
                            }
                        }
                    }
                    break;
                    case R.id.tvAvatarClick:
                    case R.id.tvUserName: {
                        Object tag = v.getTag();
                        if (tag != null) {
                            int position = (int) tag;
                            if (onTrendCommentItemClickListener != null) {
                                onTrendCommentItemClickListener.onUserName(mList.get(position));
                            }
                        }
                    }
                    break;
                }
            }
        };
    }

    public void setOnTrendCommentItemClickListener(OnTrendCommentItemClickListener onTrendCommentItemClickListener) {
        this.onTrendCommentItemClickListener = onTrendCommentItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_dialog_trend_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position), position);
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView tvAvatarClick;
        ImageView ivAvatar;
        TextView tvUserName;
        TextView tvTime;
        RelativeLayout layoutComment;
        MySpannableTextView tvComment;

        public Vh(View itemView) {
            super(itemView);
            tvAvatarClick = itemView.findViewById(R.id.tvAvatarClick);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTime = itemView.findViewById(R.id.tvTime);
            layoutComment = itemView.findViewById(R.id.layoutComment);
            tvComment = itemView.findViewById(R.id.tvComment);
            // 设置此方法后，点击事件才能生效
            LinkTouchMovementMethod linkTouchMovementMethod = new LinkTouchMovementMethod();
            tvComment.setLinkTouchMovementMethod(linkTouchMovementMethod);
            tvComment.setMovementMethod(linkTouchMovementMethod);
            tvAvatarClick.setOnClickListener(mOnClickListener);
            tvUserName.setOnClickListener(mOnClickListener);
            layoutComment.setOnClickListener(mOnClickListener);
        }

        void setData(CommentBean bean, int position) {
            tvAvatarClick.setTag(position);
            tvUserName.setTag(position);
            layoutComment.setTag(position);
            tvComment.setTag(bean);

            ImgLoader.display(bean.getUserInfo().getAvatar(), ivAvatar);
            tvUserName.setText(bean.getUserInfo().getUser_nicename());
            tvTime.setText(bean.getDatetime());

            if (bean.getParentid().equals("0")) {
                tvComment.setText(TextRender.renderVideoCommentForDialog("", bean.getContent(), onTrendCommentItemClickListener));
            } else {
                tvComment.setText(TextRender.renderVideoCommentForDialog(bean.getToUserInfo().getUser_nicename(), bean.getContent(), onTrendCommentItemClickListener));
            }
        }
    }

}