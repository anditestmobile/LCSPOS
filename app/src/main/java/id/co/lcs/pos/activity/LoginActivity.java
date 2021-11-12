package id.co.lcs.pos.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import id.co.lcs.pos.BuildConfig;
import id.co.lcs.pos.R;
import id.co.lcs.pos.constants.Constants;
import id.co.lcs.pos.databinding.ActivityLoginBinding;
import id.co.lcs.pos.models.WSMessage;
import id.co.lcs.pos.models.User;
import id.co.lcs.pos.models.UserLogin;
import id.co.lcs.pos.service.SessionManager;
import id.co.lcs.pos.utils.Helper;

/**
 * Created by TED on 17-Jul-20
 */
public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    public int PARAM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String versionName = BuildConfig.VERSION_NAME;
        binding.txtVersion.setText("v" + versionName);

        binding.btnLogin.setOnClickListener(view -> {
            if (binding.edtUsername.getText() != null && binding.edtUsername.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.login_msg_empty_username), Toast.LENGTH_SHORT).show();
            } else if (binding.edtPassword.getText() != null && binding.edtPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.login_msg_empty_password), Toast.LENGTH_SHORT).show();
            } else {
                PARAM = 0;
                new RequestUrl().execute();
                getProgressDialog().show();
            }
        });


    }

    private class RequestUrl extends AsyncTask<Void, Void, WSMessage> {

        @Override
        protected WSMessage doInBackground(Void... voids) {
            try {
                if (PARAM == 0) {
                    String URL_LOGIN = Constants.API_PREFIX + Constants.API_LOGIN;

                    final String url = Helper.getItemParam(Constants.BASE_URL).toString().concat(URL_LOGIN);
                    UserLogin userLogin = new UserLogin();
                    userLogin.setPassword(binding.edtPassword.getText().toString());
                    userLogin.setUserName(binding.edtUsername.getText().toString());
                    return (WSMessage) Helper.postWebservice(url, userLogin, WSMessage.class);
                } else {
                    return null;
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Helper.setItemParam(Constants.INTERNAL_SERVER_ERROR, ex.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(WSMessage logins) {
            if (PARAM == 0) {
                getProgressDialog().dismiss();
                if (logins != null && logins.getType().equals("S")) {
                    User user = new User();
                    user.setUsername(binding.edtUsername.getText().toString());
                    Helper.setItemParam(Constants.USER_DETAILS, user);
                    new SessionManager(getApplicationContext()).createDataSession(Helper.objectToString(user));
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                } else {
                    if (Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR) != null) {
                        Toast.makeText(getApplicationContext(), Helper.getItemParam(Constants.INTERNAL_SERVER_ERROR).toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), logins.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
//        finish();
        moveTaskToBack(true);
    }
}
