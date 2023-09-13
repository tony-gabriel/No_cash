package com.pika.nocash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Objects;

public class GeneratePayment extends AppCompatActivity {

    String amount, description, acct_no, bank;
    EditText amt, desc, acct, bnk;
    ImageView scan;
    RelativeLayout relativeLayout;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_payment);

        amt = findViewById(R.id.et_amount);
        desc = findViewById(R.id.et_description);
        acct = findViewById(R.id.et_acct_no);
        bnk = findViewById(R.id.et_bank);
        scan = findViewById(R.id.img_code);
        relativeLayout = findViewById(R.id.rv_layout);
        constraintLayout = findViewById(R.id.constraint_layout);

        findViewById(R.id.btn_generate).setOnClickListener(view -> Generate());
    }

    private void Generate() {

        amount = amt.getText().toString().trim();
        description = desc.getText().toString().trim();
        acct_no = acct.getText().toString().trim();
        bank = bnk.getText().toString();

        String load = amount + "@" + description + "@" + acct_no + "@" + bank;

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int width = point.x;
        int height = point.y;
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        MultiFormatWriter mWriter = new MultiFormatWriter();

        try {
            //BitMatrix class to encode entered text and set Width & Height
            BitMatrix mMatrix = mWriter.encode(load, BarcodeFormat.QR_CODE, 400, 400);

            BarcodeEncoder mEncoder = new BarcodeEncoder();

            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            scan.setImageBitmap(mBitmap);//Setting generated QR code to imageView
            relativeLayout.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.GONE);

            // to hide the keyboard
            InputMethodManager keyManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            keyManager.hideSoftInputFromWindow(bnk.getApplicationWindowToken(), 0);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void CloseCodeView(View view) {
        relativeLayout.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);
    }
}