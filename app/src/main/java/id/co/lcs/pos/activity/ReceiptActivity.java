package id.co.lcs.pos.activity;

/**
 * Created by TED on 18-Jul-20
 */


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.Map;

import id.co.lcs.pos.BuildConfig;
import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.databinding.ActivitySplashBinding;
import id.co.lcs.pos.models.User;
import id.co.lcs.pos.service.SessionManager;
import id.co.lcs.pos.utils.Helper;


public class ReceiptActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String versionName = BuildConfig.VERSION_NAME;
        binding.txtVersion.setText("v" + versionName);
        session = new SessionManager(getApplicationContext());

        if (session.isUrlEmpty()) {
            Map<String, String> urlSession = session.getUrl();
            Helper.setItemParam(Constants.BASE_URL,
                    urlSession.get(Constants.KEY_URL));
        } else {
            Helper.setItemParam(Constants.BASE_URL, Constants.BASE_URL);
        }

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET};

        String rationale = "Allow LCS POS to access these permissions?";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                new Handler().postDelayed(() -> {
                    checkLogin();
                    finish();
                }, 1000);
            }

            private void checkLogin() {
                if (session.isDataIn()) {
                    Map<String, String> dataSession = session.getDataDetails();
                    String mData = dataSession.get(Constants.KEY_DATA);
                    User user = (User) Helper.stringToObject(mData);
                    Helper.setItemParam(Constants.USER_DETAILS, user);
                    Intent i = new Intent(ReceiptActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }else{
                    Intent intent = new Intent(getApplicationContext(),
                            LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                finish();
            }
        });
    }

}
