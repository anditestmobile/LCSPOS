package id.co.lcs.pos.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.internal.LinkedTreeMap;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import id.co.lcs.pos.R;
import id.co.lcs.pos.fragment.ConfirmationDialogFragment;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static id.co.lcs.pos.constants.Constants.FRAGMENT_DIALOG;
import static id.co.lcs.pos.constants.Constants.REQUEST_CAMERA_CODE;

public class BarcodeScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private Handler handler = new Handler();
    private Button btnOk;
    private EditText doId;
    private boolean isStarted = false;
    private ImageView btnBack;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_custom_view_finder_scanner);
        init();

        initBarcode();
    }

    private void initBarcode() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BarcodeScannerActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        doId = findViewById(R.id.customer_name);
        btnOk = findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> {

            String doIdTemp = doId.getText().toString();
            if (doIdTemp.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Scan Items / Barcode and Item Code", Toast.LENGTH_SHORT).show();
                return;
            } else {
                searchData(doIdTemp, false);
            }
        });
        ViewGroup contentFrame = findViewById(R.id.content_frame);

        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ConfirmationDialogFragment
                        .newInstance(R.string.camera_permission_confirmation,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA_CODE,
                                R.string.camera_and_storage_permission_not_granted)
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_CODE);
            }
        } else {
            startCamera();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        searchData(rawResult.getText(), true);
    }

    //nat
    private void searchData(String result, boolean refreshCamera) {
        //ted, 0018101963, 0017005223
//        if ((boolean) Helper.getItemParam(Constants.ACCESSED_FROM_SELFCOLLECTION)) {
//            //call API
//            requestForScanDO(result);
//        } else {
//            Utils.backgroundTask(getProgressDialogDefault(), () -> {
//                if (result != null) {
//                    //ambil dari list DO yang ud d pilih berdasrkan customer
//                    for (DoHeader doHd : Helper.allSelectedDoHeader) {
//                        if (result.equals(doHd.getDoNumber().replaceFirst("^0+(?!$)", ""))) {
//                            return doHd;
//                        }
//                    }
//                }
//                return null;
//            }, new CallbackOnResult<DoHeader>() {
//                @Override
//                public void onFinish(DoHeader result) {
//                    if (result != null) {
//                        gotoDeliveryItem(result, refreshCamera);
//                    } else {
//                        Utils.showInformationDialog(id.co.qualitas.podv2.activity.BarcodeScannerActivity.this, "Wrong barcode", "Please try another barcode.", "OK", (dialog, which) -> {
//                            if (refreshCamera) {
//                                requestPermission();
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailed() {
//                    Utils.showInformationDialog(id.co.qualitas.podv2.activity.BarcodeScannerActivity.this, "Wrong barcode", "Please try another barcode.", "OK", (dialog, which) -> {
//                        if (refreshCamera) {
//                            requestPermission();
//                        }
//                    });
//                }
//            });
//        }
    }

    //ted
    private void requestForScanDO(String doNumber) {
        //ci mel baru cek ke db blm ke sap klo misalkan d db blm ada

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), getString(R.string.camera_permission_not_granted), Toast.LENGTH_SHORT).show();
                } else {
                    startCamera();
                }
                break;
        }
    }

    private void startCamera() {
        if (Build.VERSION.SDK_INT < 23) {
            if (!isStarted) {
                getProgressDialogDefault().show();
                handler.postDelayed(() -> {
                    mScannerView.setResultHandler(BarcodeScannerActivity.this);
                    mScannerView.startCamera();
                    getProgressDialogDefault().hide();
                    isStarted = true;
                }, 2000);
            } else {
                mScannerView.stopCamera();
                getProgressDialogDefault().show();
                handler.postDelayed(() -> {
                    mScannerView.setResultHandler(BarcodeScannerActivity.this);
                    mScannerView.startCamera();
                    getProgressDialogDefault().hide();
                    isStarted = true;
                }, 2000);
            }
        } else {
            mScannerView.setResultHandler(BarcodeScannerActivity.this);
            mScannerView.startCamera();
            getProgressDialogDefault().hide();
        }
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "Scanning...";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 20;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;
            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);
        }
    }

    //nat
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
