package com.pika.nocash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private String load;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        requestCameraPermission();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    //Toast.makeText(MainActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                    String[] str = result.getContents().split("@");
                    String amount = str[0];
                    String description = str[1];
                    String acct_No = str[2];
                    String bank = str[3];

                    load = "Confirm you want to pay\n" + "#" + amount + " to " + acct_No + "(" +
                            bank + ")\n" + "for " + description + "?";

                    ShowScanResult();
                }
            });

    private void ShowScanResult() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(load)
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Toast.makeText(getApplicationContext(), "Payment Successful",
                            Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, id) -> {

                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "Payment Cancelled",
                            Toast.LENGTH_SHORT).show();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_make_payment).setOnClickListener(view -> requestCameraPermission());

        findViewById(R.id.btn_generate_payment).setOnClickListener(view -> startActivity(new Intent(this, GeneratePayment.class)));
    }

    private void requestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setCaptureActivity(ScanToPay.class);
        barcodeLauncher.launch(options);
    }
}