package com.microsoft.xbox.xle.app.adapter;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.telemetry.helpers.UTCReportUser;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.ReportUserScreenViewModel;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class ReportUserScreenAdapter extends AdapterBase {
    private XLEButton cancelButton = ((XLEButton) findViewById(R.id.report_user_cancel));
    /* access modifiers changed from: private */
    public EditText optionalText = ((EditText) findViewById(R.id.report_user_text));
    private Spinner reasonSpinner = ((Spinner) findViewById(R.id.report_user_reason));
    private ArrayAdapter<String> reasonSpinnerAdapter;
    private XLEButton submitButton = ((XLEButton) findViewById(R.id.report_user_submit));
    private CustomTypefaceTextView titleTextView = ((CustomTypefaceTextView) findViewById(R.id.report_user_title));
    /* access modifiers changed from: private */
    public ReportUserScreenViewModel viewModel;

    public ReportUserScreenAdapter(ReportUserScreenViewModel viewModel2) {
        super(viewModel2);
        this.viewModel = viewModel2;
        XLEAssert.assertNotNull(this.titleTextView);
        XLEAssert.assertNotNull(this.reasonSpinner);
        XLEAssert.assertNotNull(this.optionalText);
        XLEAssert.assertNotNull(this.cancelButton);
        XLEAssert.assertNotNull(this.submitButton);
    }

    public void onStart() {
        super.onStart();
        this.reasonSpinnerAdapter = new ArrayAdapter<>(XboxTcuiSdk.getActivity(), R.layout.report_spinner_item, this.viewModel.getReasonTitles());
        this.reasonSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown);
        this.reasonSpinner.setAdapter(this.reasonSpinnerAdapter);
        if (Build.VERSION.SDK_INT >= 16) {
            this.reasonSpinner.setPopupBackgroundDrawable(new ColorDrawable(this.viewModel.getPreferredColor()));
        }
        this.reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ReportUserScreenAdapter.this.viewModel.setReason(position);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ReportUserScreenAdapter.this.viewModel.onBackButtonPressed();
            }
        });
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UTCReportUser.trackReportDialogOK(ReportUserScreenAdapter.this.viewModel.getReason() == null ? "Unknown" : ReportUserScreenAdapter.this.viewModel.getReason().toString());
                ReportUserScreenAdapter.this.viewModel.submitReport(ReportUserScreenAdapter.this.optionalText.getText().toString());
            }
        });
    }

    /* access modifiers changed from: protected */
    public void updateViewOverride() {
        if (this.titleTextView != null) {
            this.titleTextView.setText(this.viewModel.getTitle());
        }
        if (this.submitButton != null) {
            this.submitButton.setEnabled(this.viewModel.validReasonSelected());
        }
    }
}
