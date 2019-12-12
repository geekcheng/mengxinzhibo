package com.tongchuang.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.im.ImMessageBean;
import com.tongchuang.phonelive.im.ImMessageUtil;
import com.tongchuang.phonelive.utils.ClickUtil;

import java.util.List;

/**
 * Created by cxf on 2018/11/28.
 */

public class ChatImagePreviewAdapter extends RecyclerView.Adapter<ChatImagePreviewAdapter.Vh> {

    private List<ImMessageBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private ActionListener mActionListener;

    public ChatImagePreviewAdapter(Context context, List<ImMessageBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ClickUtil.canClick()){
                    return;
                }
                if (mActionListener != null) {
                    mActionListener.onImageClick();
                }
            }
        };
    }


    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_im_chat_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
    }

    class Vh extends RecyclerView.ViewHolder {

        ImageView mImageView;

        public Vh(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView;
            mImageView.setOnClickListener(mOnClickListener);
        }

        void setData(ImMessageBean bean) {
            ImMessageUtil.getInstance().displayImageFile(bean, mImageView);
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onImageClick();
    }
}
