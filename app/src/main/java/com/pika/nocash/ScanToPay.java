package com.pika.nocash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.io.IOException;
import java.util.Random;

public class ScanToPay extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private ViewfinderView viewfinderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_to_pay);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = findViewById(R.id.switch_flashlight);

        viewfinderView = findViewById(R.id.zxing_viewfinder_view);

        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        capture.decode();

        changeMaskColor(null);
        changeLaserVisibility(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }

    public void switchFlashlight(View view) {
        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.setMaskColor(color);
    }

    public void changeLaserVisibility(boolean visible) {
        viewfinderView.setLaserVisibility(visible);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
