package com.compartamos.cecano.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    final String TEXT = "Pago Avon\n" +
            "2019-01-10\t12:42:12\nComercio: Quiqueprueba\nId: 3447" +
            "\nFol Avon: 7897097987076354637213\nAut: 123123\nRef: 09090010107151\n" +
            "Servicio:\t$480.00\nTotal:\t$480.00";
    private int LETTER_SIZE_WIDTH = 1530;
    private int LETTER_SIZE_HIGH = 1980;
    private ImageView iv_screenShot;
    private Button b_topdf;
    PdfDocument document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        onClickViews();
    }

    private void initUi() {
        iv_screenShot = findViewById(R.id.iv_pdf);
        b_topdf = findViewById(R.id.b_topdf);
    }

    private void onClickViews(){
        b_topdf.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.b_topdf:
                final Bitmap bitmap = createBitmap();
                iv_screenShot.setImageBitmap(bitmap);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        savePDF(createPDF(bitmap));
                    }
                }).start();
            break;
        }
    }

    Bitmap createBitmap() {

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

    private PdfDocument createPDF(Bitmap  bitmap) {
        document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument
                .PageInfo
                .Builder(LETTER_SIZE_WIDTH, LETTER_SIZE_HIGH, 1).create();
                //.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create(); // CON TAMAÑO DEL BITMAP
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);
        return document;
    }

    private void savePDF(PdfDocument pdfDocument) {
        String targetPdf = "/sdcard/downloaded_rom/test.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error :C ", Toast.LENGTH_LONG).show();
            Log.e(TAG, "savePDF: " + e.getMessage());
        }
        document.close();
    }
}
