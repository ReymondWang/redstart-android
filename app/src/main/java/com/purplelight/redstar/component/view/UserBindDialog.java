package com.purplelight.redstar.component.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.purplelight.redstar.R;

/**
 * 用户绑定的对话框
 * Created by wangyn on 16/5/16.
 */
public class UserBindDialog extends Dialog {

    private EditText mLoginId;
    private EditText mPassword;
    private TextView mConfirm;
    private TextView mCancel;

    private OnBindConfirmListener mListener;

    public UserBindDialog(Context context){
        super(context, R.style.CustomDialog);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        initView(context);
        initEvents();
    }

    public void setOnBindConfirmListener(OnBindConfirmListener listener){
        mListener = listener;
        if (mListener != null){
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.BindConfirmListener(mLoginId.getText().toString(), mPassword.getText().toString());
                }
            });
        }
    }

    private void initView(Context context){
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_bind_system, null);
        mLoginId = (EditText)mView.findViewById(R.id.loginId);
        mPassword = (EditText)mView.findViewById(R.id.password);
        mConfirm = (TextView)mView.findViewById(R.id.btnConfirm);
        mCancel = (TextView)mView.findViewById(R.id.btnCancel);

        setContentView(mView);
    }

    private void initEvents(){
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnBindConfirmListener{
        void BindConfirmListener(String loginId, String password);
    }
}
