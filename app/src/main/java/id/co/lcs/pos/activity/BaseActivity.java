package id.co.lcs.pos.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.utils.Helper;
import id.co.lcs.pos.R;
import id.co.lcs.pos.utils.Utils;

public class BaseActivity extends AppCompatActivity {
    protected BottomSheetDialog bottomSheetDialog;
    protected ProgressDialog progress, progressDefault;
//    public User user;

    protected void init() {
//        user = (User) Helper.getItemParam(Constants.USER_DETAILS);
        initBase();
    }



    protected void setToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    public void setButton(Button btn, String text, int id) {
        btn.setBackgroundDrawable(ContextCompat
                .getDrawable(getApplicationContext(),
                        id));
        btn.setText(text);

    }

    protected void initBase() {
        progress = Utils.createProgressDialog(this);
        progressDefault = Utils.createProgressDialog(this);
    }

    public ProgressDialog getProgressDialog(){
        return progress;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        if(progressDefault!=null && progressDefault.isShowing()){
            progressDefault.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void getImageGlide(Context context, String imageUrl, ImageView image) {
        if (!imageUrl.equals("false")) {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_no_image)
                            .error(R.drawable.ic_no_image))
                    .asBitmap()
                    .load(Base64.decode(imageUrl, Base64.DEFAULT))
                    .into(image);
        } else {
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_no_image)
                            .error(R.drawable.ic_no_image))
                    .asBitmap()
                    .load(imageUrl)
                    .into(image);
        }
    }

    public void sentCart(String qty){
        Intent broadcast = new Intent();
        broadcast.setAction("CART");
        broadcast.putExtra("message", qty);
        getApplicationContext().sendBroadcast(broadcast);
    }





    public int getImage(String imageName) {

        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", this.getPackageName());

        return drawableResourceId;
    }

    protected ProgressDialog getProgressDialogDefault(){
        return progressDefault;
    }

}
