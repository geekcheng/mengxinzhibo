package com.tongchuang.phonelive.game.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2018/11/5.
 */

public class GameNzSfAdapter extends RecyclerView.Adapter<GameNzSfAdapter.Vh> {

    private int[][] mArray;
    private LayoutInflater mInflater;
    private String mShengString;
    private String mFuString;

    public GameNzSfAdapter(Context context, int[][] array) {
        mArray = array;
        mInflater = LayoutInflater.from(context);
        mShengString = WordUtil.getString(R.string.game_nz_sheng);
        mFuString = WordUtil.getString(R.string.game_nz_fu);
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.game_item_nz_sf, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mArray[position], position);
    }

    @Override
    public int getItemCount() {
        return mArray.length;
    }

    class Vh extends RecyclerView.ViewHolder {

        RadioButton mRadioButton1;
        RadioButton mRadioButton2;
        RadioButton mRadioButton3;
        View mLine;

        public Vh(View itemView) {
            super(itemView);
            mRadioButton1 = itemView.findViewById(R.id.radio_1);
            mRadioButton2 = itemView.findViewById(R.id.radio_2);
            mRadioButton3 = itemView.findViewById(R.id.radio_3);
            mLine = itemView.findViewById(R.id.line);
        }

        void setData(int[] arr, int position) {
            if (arr[0] == 1) {
                mRadioButton1.setChecked(true);
                mRadioButton1.setText(mShengString);
            } else {
                mRadioButton1.setChecked(false);
                mRadioButton1.setText(mFuString);
            }
            if (arr[1] == 1) {
                mRadioButton2.setChecked(true);
                mRadioButton2.setText(mShengString);
            } else {
                mRadioButton2.setChecked(false);
                mRadioButton2.setText(mFuString);
            }
            if (arr[2] == 1) {
                mRadioButton3.setChecked(true);
                mRadioButton3.setText(mShengString);
            } else {
                mRadioButton3.setChecked(false);
                mRadioButton3.setText(mFuString);
            }
            if (position == mArray.length - 1) {
                if (mLine.getVisibility() == View.VISIBLE) {
                    mLine.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mLine.getVisibility() != View.VISIBLE) {
                    mLine.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
