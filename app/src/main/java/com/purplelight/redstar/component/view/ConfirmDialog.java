package com.purplelight.redstar.component.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.purplelight.redstar.R;
import com.purplelight.redstar.util.Validation;

/**
 * 确认对话框
 * Created by wangyn on 16/4/27.
 */
public class ConfirmDialog extends Dialog {
    private TextView txtTitle;
    private TextView txtContent;
    private TextView btnConfirm;
    private TextView btnCancel;

    public ConfirmDialog(Context context){
        super(context, R.style.CustomDialog);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        initView(context);
        initEvents();
    }

    private void initView(Context context){
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_common_confirm, null);

        txtTitle = (TextView)mView.findViewById(R.id.txtTitle);
        txtContent = (TextView)mView.findViewById(R.id.txtContent);
        btnConfirm = (TextView)mView.findViewById(R.id.btnConfirm);
        btnCancel = (TextView)mView.findViewById(R.id.btnCancel);

        setContentView(mView);
    }

    private void initEvents(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setTitle(String title){
        txtTitle.setText(title);
    }

    public void setContent(String content){
        if (!Validation.IsNullOrEmpty(content)){
            txtContent.setVisibility(View.VISIBLE);
            txtContent.setText(content);
        }
    }

    public void setConfirmListener(View.OnClickListener listener){
        btnConfirm.setOnClickListener(listener);
    }

    public void setCancelListener(View.OnClickListener listener){
        btnCancel.setOnClickListener(listener);
    }
}
