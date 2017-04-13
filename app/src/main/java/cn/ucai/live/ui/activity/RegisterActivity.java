package cn.ucai.live.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.OnCompleteListener;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.MD5;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.ResultUtils;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.email)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nick)
    EditText nick;
    @BindView(R.id.password2)
    EditText password2;

    String mUserName,mNick, mPassword;
    ProgressDialog pd;
    IUserModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mModel = new UserModel();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ckeckInput()) return;
                pdialog();
                registerWeChat();
            }
        });
    }

    private void pdialog() {
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("正在注册...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private boolean ckeckInput() {
        mUserName = username.getText().toString().trim();
        mNick = nick.getText().toString().trim();
        mPassword = password.getText().toString().trim();
        String mPassword2 = password2.getText().toString().trim();
        if (TextUtils.isEmpty(mUserName)) {
            username.requestFocus();
            username.setError(getString(R.string.user_name_connot_be_empty));
            return false;
        }
        if (!mUserName.matches("[a-zA-Z]\\w{5,15}")) {
            username.requestFocus();
            username.setError(getString(R.string.illegal_user_name));
            return false;
        }
        if (TextUtils.isEmpty(mNick)) {
            nick.requestFocus();
            nick.setError(getString(R.string.nick_name_connot_be_empty));
            return false;
        }
        if (TextUtils.isEmpty(mPassword)) {
            password.requestFocus();
            password.setError(getString(R.string.password_connot_be_empty));
            return false;
        }
        if (TextUtils.isEmpty(mPassword2)) {
            password2.requestFocus();
            password2.setError(getString(R.string.confirm_password_connot_be_empty));
            return false;
        }
        if (!mPassword.equals(mPassword2)) {
            password2.requestFocus();
            password2.setError(getString(R.string.two_input_password));
            return false;
        }
        return true;
    }
    private void registerWeChat() {
        Log.e("register", "registerWeChat()");
        mModel.register(RegisterActivity.this, mUserName, mNick, MD5.getMessageDigest(mPassword),
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (result != null) {
                            Result json = ResultUtils.getResultFromJson(result, String.class);
                            if (json != null) {
                                if (json.isRetMsg()) {
                                    Log.e("register", "成功？");
                                    registerEm();
                                } else if (json.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                                    Log.e("register", "用户已存在");
                                    CommonUtils.showShortToast(R.string.User_already_exists);
                                } else if (json.getRetCode() == I.MSG_REGISTER_FAIL) {
                                    Log.e("register", "注册失败");
                                    CommonUtils.showShortToast(R.string.Registration_failed);
                                }
                            }
                        }
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast(R.string.Registration_failed);
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                }
        );
    }

    private void registerEm() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(mUserName, MD5.getMessageDigest(mPassword));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("注册成功");
                            PreferenceManager.getInstance().setCurrentUserName(mUserName);
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    unRegisterWeChat();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLongToast("注册失败：" + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void unRegisterWeChat() {
        mModel.unRegister(RegisterActivity.this, mUserName, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    Log.e("register", "unRegisterWeChat");
                }
            }

            @Override
            public void onError(String error) {
                Log.e("register", "unRegisterWeChat-error:" + error);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pd != null) {
            pd.dismiss();
        }
    }
}
