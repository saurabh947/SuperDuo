package it.jaschke.alexandria.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;

import java.util.List;

import it.jaschke.alexandria.constants.Constants;

public class BarcodeScannerActivity extends Activity implements OnScanListener {

    private BarcodePicker mBarcodePicker;
    private Intent mReturnIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Keep the screen on while the scanner is running.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mReturnIntent = new Intent();
        initializeAndStartBarcodeScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBarcodePicker.stopScanning();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBarcodePicker.startScanning();
    }

    @Override
    public void onBackPressed() {
        mBarcodePicker.stopScanning();
        setResult(Activity.RESULT_CANCELED, mReturnIntent);
        finish();
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        ScanditLicense.setAppKey(Constants.API_KEY);
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ScanSettings settings = ScanSettings.create();
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_EAN13, true);

        mBarcodePicker = new BarcodePicker(this, settings);
        mBarcodePicker.setOnScanListener(this);

        setContentView(mBarcodePicker);
    }

    @Override
    public void didScan(ScanSession session) {
        List<Barcode> barcodes = session.getNewlyRecognizedCodes();
        String isbn = barcodes.get(0).getData();

        if (TextUtils.isEmpty(isbn)) {
            setResult(Constants.RESULT_FAIL, mReturnIntent);
        } else {
            mReturnIntent.putExtra("result", isbn);
            setResult(Activity.RESULT_OK, mReturnIntent);
        }
        finish();
    }
}
