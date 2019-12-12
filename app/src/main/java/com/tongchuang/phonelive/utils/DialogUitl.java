package com.tongchuang.phonelive.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.view.WheelView;
import com.tongchuang.phonelive.R;
import com.tongchuang.phonelive.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by cxf on 2017/8/8.
 */

public class DialogUitl {
    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_NUMBER = 1;
    public static final int INPUT_TYPE_NUMBER_PASSWORD = 2;
    public static final int INPUT_TYPE_TEXT_PASSWORD = 3;

    //第三方登录的时候用显示的dialog
    public static Dialog loginAuthDialog(Context context) {
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_login_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 用于网络请求等耗时操作的LoadingDialog
     */
    public static Dialog loadingDialog(Context context, String text) {
        Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(text)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.text);
            if (titleView != null) {
                titleView.setText(text);
            }
        }
        return dialog;
    }

    /**
     * 用于网络请求等耗时操作的ProgressDialog
     */
    public static ProgressDialog progressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_loading_2);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static Dialog loadingDialog(Context context) {
        return loadingDialog(context, "");
    }

    public static void showSimpleTipDialog(Context context, String content) {
        showSimpleTipDialog(context, null, content);
    }

    public static void showSimpleTipDialog(Context context, String title, String content) {
        final Dialog dialog = new Dialog(context, R.style.dialog2);
        dialog.setContentView(R.layout.dialog_simple_tip);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (!TextUtils.isEmpty(title)) {
            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            titleView.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            TextView contentTextView = (TextView) dialog.findViewById(R.id.content);
            contentTextView.setText(content);
        }
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showSimpleDialog(Context context, String content, SimpleCallback callback) {
        showSimpleDialog(context, content, true, callback);
    }

    public static void showSimpleDialog(Context context, String content, boolean cancelable, SimpleCallback callback) {
        showSimpleDialog(context, null, content, cancelable, callback);
    }

    public static void showSimpleDialog(Context context, String title, String content, boolean cancelable, SimpleCallback callback) {
        new Builder(context)
                .setTitle(title)
                .setContent(content)
                .setCancelable(cancelable)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, String content, String hint, int inputType, int length, int cancelTextColor, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setContent(content)
                .setCancelable(true)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setCancelTextColor(cancelTextColor)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, String hint, int inputType, int length, int cancelTextColor, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(true)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setCancelTextColor(cancelTextColor)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, int inputType, int length, int cancelTextColor, SimpleCallback callback) {
        showSimpleInputDialog(context, title, null, inputType, length, cancelTextColor, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, String hint, int inputType, int length, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(true)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setClickCallback(callback)
                .build()
                .show();
    }

    public static void showSimpleInputDialog(Context context, String title, int inputType, int length, SimpleCallback callback) {
        showSimpleInputDialog(context, title, null, inputType, length, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, int inputType, SimpleCallback callback) {
        showSimpleInputDialog(context, title, inputType, 0, callback);
    }

    public static void showSimpleInputDialog(Context context, String title, SimpleCallback callback) {
        showSimpleInputDialog(context, title, INPUT_TYPE_TEXT, callback);
    }

    public static void showSimpleInputDialogWithoutCancel(Context context, String title, String hint, int inputType, int length, SimpleCallback callback) {
        new Builder(context).setTitle(title)
                .setCancelable(false)
                .setInput(true)
                .setHint(hint)
                .setInputType(inputType)
                .setLength(length)
                .setClickCallback(callback)
                .hideCancel(true)
                .build()
                .show();
    }

    public static void showStringArrayDialog(Context context, Integer[] array, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        for (int i = 0, length = array.length; i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(54)));
            textView.setTextColor(0xff323232);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER);
            textView.setText(array[i]);
            textView.setTag(array[i]);
            textView.setOnClickListener(itemListener);
            container.addView(textView);
            if (i != length - 1) {
                View v = new View(context);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(1)));
                v.setBackgroundColor(0xfff5f5f5);
                container.addView(v);
            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showStringArrayDialog(Context context, SparseArray<String> array, final StringArrayDialogCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_string_array);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);
        View.OnClickListener itemListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                if (callback != null) {
                    callback.onItemClick(textView.getText().toString(), (int) v.getTag());
                }
                dialog.dismiss();
            }
        };
        for (int i = 0, length = array.size(); i < length; i++) {
            TextView textView = new TextView(context);
            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(54)));
            textView.setTextColor(0xff323232);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setGravity(Gravity.CENTER);
            textView.setText(array.valueAt(i));
            textView.setTag(array.keyAt(i));
            textView.setOnClickListener(itemListener);
            container.addView(textView);
            if (i != length - 1) {
                View v = new View(context);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(1)));
                v.setBackgroundColor(0xfff5f5f5);
                container.addView(v);
            }
        }
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showDatePickerDialog(Context context, final DataPickerCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_date_picker);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        final Calendar c = Calendar.getInstance();
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_confirm) {
                    if (callback != null) {
                        if (c.getTime().getTime() > new Date().getTime()) {
                            ToastUtil.show(WordUtil.getString(R.string.edit_profile_right_date));
                        } else {
                            String result = DateFormat.format("yyyy-MM-dd", c).toString();
                            callback.onConfirmClick(result);
                            dialog.dismiss();
                        }
                    }
                } else {
                    dialog.dismiss();
                }
            }
        };
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(listener);
        dialog.show();
    }

    public static void showSexPickerDialog(Context context, int sex, final SexPickerCallback callback) {
        final Dialog dialog = new Dialog(context, R.style.BottomDialogStyle);
        dialog.setContentView(R.layout.dialog_sex_picker);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        final WheelView wheelView = dialog.findViewById(R.id.wheelview);
        wheelView.setCyclic(false);

        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.add("男");
        mOptionsItems.add("女");
        wheelView.setAdapter(new ArrayWheelAdapter(mOptionsItems));
        if (sex == 1) {
            wheelView.setCurrentItem(0);
        } else {
            wheelView.setCurrentItem(1);
        }

        dialog.findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onConfirmClick(wheelView.getCurrentItem() == 0 ? 1 : 2);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static class Builder {

        private Context mContext;
        private String mTitle;
        private String mContent;
        private String mConfrimString;
        private String mCancelString;
        private boolean mCancelable;
        private boolean mBackgroundDimEnabled;//显示区域以外是否使用黑色半透明背景
        private boolean mInput;//是否是输入框的
        private String mHint;
        private int mInputType;
        private int mLength;
        private SimpleCallback mClickCallback;
        private int mCancelTextColor;
        private boolean mHideCancel;//隐藏取消按钮

        public Builder(Context context) {
            mContext = context;
        }

        public Builder hideCancel(boolean hideCancel) {
            mHideCancel = hideCancel;
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setContent(String content) {
            mContent = content;
            return this;
        }

        public Builder setConfrimString(String confrimString) {
            mConfrimString = confrimString;
            return this;
        }

        public Builder setCancelString(String cancelString) {
            mCancelString = cancelString;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setCancelTextColor(int cancelTextColor) {
            mCancelTextColor = cancelTextColor;
            return this;
        }

        public Builder setBackgroundDimEnabled(boolean backgroundDimEnabled) {
            mBackgroundDimEnabled = backgroundDimEnabled;
            return this;
        }

        public Builder setInput(boolean input) {
            mInput = input;
            return this;
        }

        public Builder setHint(String hint) {
            mHint = hint;
            return this;
        }

        public Builder setInputType(int inputType) {
            mInputType = inputType;
            return this;
        }

        public Builder setLength(int length) {
            mLength = length;
            return this;
        }

        public Builder setClickCallback(SimpleCallback clickCallback) {
            mClickCallback = clickCallback;
            return this;
        }

        public Dialog build() {
            final Dialog dialog = new Dialog(mContext, mBackgroundDimEnabled ? R.style.dialog : R.style.dialog2);
            dialog.setContentView(mInput ? R.layout.dialog_input : R.layout.dialog_simple);
            dialog.setCancelable(mCancelable);
            dialog.setCanceledOnTouchOutside(mCancelable);
            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            if (!TextUtils.isEmpty(mTitle)) {
                titleView.setText(mTitle);
            }
            final TextView content = (TextView) dialog.findViewById(R.id.content);
            if (!TextUtils.isEmpty(mHint)) {
                content.setHint(mHint);
            }
            if (!TextUtils.isEmpty(mContent)) {
                content.setText(mContent);
                CharSequence text = content.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
            }
            if (mInputType == INPUT_TYPE_NUMBER) {
                content.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (mInputType == INPUT_TYPE_NUMBER_PASSWORD) {
                content.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            } else if (mInputType == INPUT_TYPE_TEXT_PASSWORD) {
                content.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            if (mLength > 0 && content instanceof EditText) {
                content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLength)});
            }
            TextView btnConfirm = (TextView) dialog.findViewById(R.id.btn_confirm);
            if (!TextUtils.isEmpty(mConfrimString)) {
                btnConfirm.setText(mConfrimString);
            }
            TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel);
            if (mCancelTextColor != 0) {
                btnCancel.setTextColor(mCancelTextColor);
            }
            if (!TextUtils.isEmpty(mCancelString)) {
                btnCancel.setText(mCancelString);
            }
            if (mHideCancel) {
                btnCancel.setVisibility(View.GONE);
                dialog.findViewById(R.id.viewDialogCancelDivider).setVisibility(View.GONE);
            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn_confirm) {
                        if (mClickCallback != null) {
                            if (mInput) {
                                mClickCallback.onConfirmClick(dialog, content.getText().toString());
                            } else {
                                dialog.dismiss();
                                mClickCallback.onConfirmClick(dialog, "");
                            }
                        } else {
                            dialog.dismiss();
                        }
                    } else {
                        dialog.dismiss();
                        if (mClickCallback instanceof SimpleCallback2) {
                            ((SimpleCallback2) mClickCallback).onCancelClick();
                        }
                    }
                }
            };
            btnConfirm.setOnClickListener(listener);
            btnCancel.setOnClickListener(listener);
            return dialog;
        }

    }

    public interface DataPickerCallback {
        void onConfirmClick(String date);
    }

    public interface StringArrayDialogCallback {
        void onItemClick(String text, int tag);
    }

    public interface SimpleCallback {
        void onConfirmClick(Dialog dialog, String content);
    }

    public interface SimpleCallback2 extends SimpleCallback {
        void onCancelClick();
    }

    public interface SexPickerCallback {
        void onConfirmClick(int sex);
    }

}
