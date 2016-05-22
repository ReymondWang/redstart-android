package com.purplelight.redstar.component.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private AppCompatCheckBox mCheckbox;
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
        mCheckbox = (AppCompatCheckBox)findViewById(R.id.chkSelect);
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
                mCheckbox.setVisibility(VISIBLE);
                switch (mResult.getResult()){
                    case 0:
                        mCheckbox.setChecked(false);
                        break;
                    case 1:
                        mCheckbox.setChecked(true);
                        break;
                    default:
                        mCheckbox.setChecked(false);
                        break;
                }
            } else {
                mCheckbox.setVisibility(GONE);
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
