package com.purplelight.redstar.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.purplelight.redstar.R;
import com.purplelight.redstar.provider.entity.EstimateReport;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 评估报告概况
 */
public class EstimateReportFragment extends Fragment {

    private EstimateReport mReport;

    @InjectView(R.id.txtProject) TextView txtProject;
    @InjectView(R.id.txtArea) TextView txtArea;
    @InjectView(R.id.txtInCharger) TextView txtInCharger;
    @InjectView(R.id.txtReportDate) TextView txtReportDate;
    @InjectView(R.id.txtReporter) TextView txtReporter;
    @InjectView(R.id.txtConstraction) TextView txtConstraction;
    @InjectView(R.id.txtSupervision) TextView txtSupervision;
    @InjectView(R.id.txtRemark) TextView txtRemark;
    @InjectView(R.id.txtTotalScore) TextView txtTotalScore;
    @InjectView(R.id.txtMeasureScore) TextView txtMeasureScore;
    @InjectView(R.id.txtDeductingScore) TextView txtDeductingScore;
    @InjectView(R.id.txtManageScore) TextView txtManageScore;
    @InjectView(R.id.txtSafeScore) TextView txtSafeScore;

    public static EstimateReportFragment newInstance(EstimateReport report) {
        EstimateReportFragment fragment = new EstimateReportFragment();
        Bundle args = new Bundle();
        args.putParcelable("report", report);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReport = getArguments().getParcelable("report");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estimate_report, container, false);
        ButterKnife.inject(this, view);

        initViews();
        return view;
    }

    private void initViews(){
        txtProject.setText(mReport.getProjectName());
        txtArea.setText(mReport.getAreaName());
        txtInCharger.setText(mReport.getInChargePerson());
        txtReportDate.setText(mReport.getReportDate());
        txtReporter.setText(mReport.getReporter());
        txtConstraction.setText(mReport.getConstractionName());
        txtSupervision.setText(mReport.getSupervisionName());
        txtRemark.setText(mReport.getRemark());
        txtTotalScore.setText(String.valueOf(mReport.getGradeZHDF()));
        txtMeasureScore.setText(String.valueOf(mReport.getGradeSCSL()));
        txtDeductingScore.setText(String.valueOf(mReport.getGradeZLKF()));
        txtManageScore.setText(String.valueOf(mReport.getGradeGLXW()));
        txtSafeScore.setText(String.valueOf(mReport.getGradeAQWM()));
    }
}
