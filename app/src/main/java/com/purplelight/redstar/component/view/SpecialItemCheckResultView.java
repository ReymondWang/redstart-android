package com.purplelight.redstar.component.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.purplelight.redstar.R;
import com.purplelight.redstar.provider.entity.SpecialItemCheckResult;

/**
 * 专项检查明细项
 * Created by wangyn on 16/5/22.
 */
public class SpecialItemCheckResultView extends LinearLayout {

    private TextView txtCheckItem;
    private TextView txtIsPass;
    private RadioGroup lytRadio;
    private AppCompatRadioButton radCheck;
    private AppCompatRadioButton radNoCheck;
    private LinearLayout mBottomLine;
    private LinearLayout mContent;

    private int mRowHeight;
    private boolean mEditable;
    private boolean mShowBottomLine;

    private SpecialItemCheckResult mResult;

    public SpecialItemCheckResultView(Context context) {
        this(context, null);
    }

    public SpecialItemCheckResultView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_special_check_detail, this);
        txtCheckItem = (TextView)findViewById(R.id.txtCheckItem);
        txtIsPass = (TextView)findViewById(R.id.txtIsPass);
        lytRadio = (RadioGroup)findViewById(R.id.lytRadio);
        radCheck = (AppCompatRadioButton)findViewById(R.id.radCheck);
        radNoCheck = (AppCompatRadioButton)findViewById(R.id.radNoCheck);
        mBottomLine = (LinearLayout)findViewById(R.id.lytBottomLine);
        mContent = (LinearLayout)findViewById(R.id.lytContent);
    }

    public void setResult(SpecialItemCheckResult result){
        mResult = result;
        initView();
    }

    public int getRowHeight() {
        return mRowHeight;
    }

    public void setRowHeight(int rowHeight) {
        mRowHeight = rowHeight;
    }

    public boolean isEditable() {
        return mEditable;
    }

    public void setEditable(boolean editable) {
        mEditable = editable;
    }

    public boolean isShowBottomLine() {
        return mShowBottomLine;
    }

    public void setShowBottomLine(boolean showBottomLine) {
        mShowBottomLine = showBottomLine;
    }

    public boolean isChecked(){
        return radCheck.isChecked() && !radNoCheck.isChecked();
    }

    public String getName(){
        return txtCheckItem.getText().toString();
    }

    private void initView(){
        if (mRowHeight != 0){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
            );
            params.height = mRowHeight;
            mContent.setLayoutParams(params);
        }

        if (mResult != null){
            txtCheckItem.setText(mResult.getName());
            if (mEditable){
                txtIsPass.setVisibility(GONE);
                lytRadio.setVisibility(VISIBLE);
                switch (mResult.getResult()){
                    case 0:
                        radCheck.setChecked(false);
                        radNoCheck.setChecked(true);
                        break;
                    case 1:
                        radCheck.setChecked(true);
                        radNoCheck.setChecked(false);
                        break;
                    default:
                        radCheck.setChecked(false);
                        radNoCheck.setChecked(false);
                        break;
                }
            } else {
                lytRadio.setVisibility(GONE);
                switch (mResult.getResult()){
                    case 0:
                        txtIsPass.setVisibility(VISIBLE);
                        txtIsPass.setTextColor(getContext().getResources().getColor(R.color.colorDanger));
                        txtIsPass.setText("×");
                        break;
                    case 1:
                        txtIsPass.setVisibility(VISIBLE);
                        txtIsPass.setTextColor(getContext().getResources().getColor(R.color.colorSuccess));
                        txtIsPass.setText("√");
                        break;
                    case 2:
                        txtIsPass.setVisibility(GONE);
                        break;
                }
            }

            if (mShowBottomLine){
                mBottomLine.setVisibility(VISIBLE);
            } else {
                mBottomLine.setVisibility(GONE);
            }
        }
    }

}
