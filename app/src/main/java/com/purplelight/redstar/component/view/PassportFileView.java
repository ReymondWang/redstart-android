package com.purplelight.redstar.component.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.purplelight.redstar.R;
import com.purplelight.redstar.util.PdfHelper;
import com.purplelight.redstar.util.Validation;
import com.purplelight.redstar.web.entity.PassportFile;

import org.vudroid.pdfdroid.codec.PdfContext;
import org.vudroid.pdfdroid.codec.PdfDocument;
import org.vudroid.pdfdroid.codec.PdfPage;

/**
 * 证照文件View
 * Created by wangyn on 16/5/24.
 */
public class PassportFileView extends LinearLayout {

    private TextView txtTitle;
    private ImageView imagePlaceHolder;
    private LinearLayout lytPdfContainer;

    private PassportFile mFile;
    private int mScreenWidth;

    public PassportFileView(Context context) {
        this(context, null);
    }

    public PassportFileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_passport_file_view, this);

        txtTitle = (TextView)findViewById(R.id.txtTitle);
        imagePlaceHolder = (ImageView)findViewById(R.id.imagePlaceHolder);
        lytPdfContainer = (LinearLayout)findViewById(R.id.lytPdfContainer);
    }

    public void setScreenWidth(int width){
        mScreenWidth = width;
    }

    public void setPassportFile(PassportFile file){
        mFile = file;
        initViews();
    }

    private void initViews(){
        if (mFile != null){
            txtTitle.setText(mFile.getFileName());
            LoadFileTask task = new LoadFileTask();
            task.execute(mFile.getFilePath());
        }
    }

    private class LoadFileTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String absoluteFilePath = "";
            if (!PdfHelper.HasThisFile(params[0])){
                if (PdfHelper.LoadFileFromNet(params[0])) {
                    absoluteFilePath = PdfHelper.GetLocalFileName(params[0]);
                }
            } else {
                absoluteFilePath = PdfHelper.GetLocalFileName(params[0]);
            }
            return absoluteFilePath;
        }

        @Override
        protected void onPostExecute(String absoluteFileName) {
            if (!Validation.IsNullOrEmpty(absoluteFileName)){
                PdfContext pdfContext = new PdfContext();
                PdfDocument pdfDocument = pdfContext.openDocument(absoluteFileName);
                int pageCount = pdfDocument.getPageCount();
                if (pageCount > 1){
                    for (int i = 1; i < pageCount; i++){
                        ImageView imageView = new ImageView(getContext());
                        LayoutParams layoutParams = new LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setImageResource(R.drawable.cc_bg_default_image);
                        lytPdfContainer.addView(imageView);
                    }
                }
                for (int i = 0; i < pageCount; i++){
                    ImageView imageView = (ImageView)lytPdfContainer.getChildAt(i);
                    ShowPdfTask task = new ShowPdfTask(imageView, pdfDocument);
                    task.execute(i);
                }
            }
        }
    }

    private class ShowPdfTask extends AsyncTask<Integer, Void, Bitmap>{
        private ImageView mViewer;

        private PdfDocument mDocument;

        public ShowPdfTask(ImageView view, PdfDocument document){
            mViewer = view;
            mDocument = document;
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            PdfPage vuPage = mDocument.getPage(params[0]);
            RectF rf = new RectF();
            rf.bottom = rf.right = (float)1.0;
            int pdfHeight = (int)(vuPage.getHeight() * ((float)mScreenWidth / vuPage.getWidth()) + 0.5f);
            return vuPage.renderBitmap(mScreenWidth, pdfHeight, rf);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null){
                mViewer.setImageBitmap(bitmap);
            }
        }
    }
}
