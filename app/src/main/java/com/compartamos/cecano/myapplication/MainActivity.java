package com.compartamos.cecano.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    final String TEXT = "Pago Avon\n" +
            "2019-01-10\t12:42:12\nComercio: Quiqueprueba\nId: 3447" +
            "\nFol Avon: 7897097987076354637213\nAut: 123123\nRef: 09090010107151\n" +
            "Servicio:\t$480.00\nTotal:\t$480.00";

    private ImageView iv_screenShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        createView();
        iv_screenShot.setImageBitmap(createView());

        creaetePDF();
    }

    private void initUi() {
        iv_screenShot = findViewById(R.id.iv_pdf);

    }

    Bitmap createView() {
        try {
            View screenShot = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pdf_layout, null);
            TextView body = screenShot.findViewById(R.id.tv_pdftexto);
            body.setText(TEXT);

            screenShot.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            screenShot.layout(0, 0, screenShot.getMeasuredWidth(),screenShot.getMeasuredHeight());

            final Bitmap bitmap = Bitmap.createBitmap(screenShot.getMeasuredWidth(),
                    screenShot.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            screenShot.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "createView: " + e.toString());
        }
        return null;
    }

    void creaetePDF() {
        View screenShot = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pdf_layout, null);
        TextView body = screenShot.findViewById(R.id.tv_pdftexto);
        body.setText(TEXT);

        screenShot.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        screenShot.layout(0, 0, screenShot.getMeasuredWidth(),screenShot.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(screenShot.getMeasuredWidth(),
                screenShot.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument
                .PageInfo.Builder(300, 600, 1)
                .create();

        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas1 = page.getCanvas();

        canvas1.drawBitmap(bitmap,0, 0, null);

        pdfDocument.finishPage(page);

        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/downloaded_rom/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path +"test-2.pdf";
        File filePath = new File(targetPdf);
        try {
            pdfDocument.writeTo(new FileOutputStream(filePath));
            Log.e(TAG, "creaetePDF: HECHO" );

        } catch (IOException e) {
            Log.e("main", "error "+ e.toString());;
            Log.e(TAG, "creaetePDF: ERROR\n"+e.getMessage());
        }
        // close the document
        pdfDocument.close();

        File mFile = new File(directory_path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(mFile),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }
}
