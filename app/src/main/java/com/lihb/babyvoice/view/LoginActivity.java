/*
 * 文 件 名:  LoginActivity.java
 * 描    述:  <描述>
 * 修 改 人:  liuxinyang
 * 修改时间:  2015年4月1日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.lihb.babyvoice.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.R;
import com.lihb.babyvoice.customview.TitleBar;
import com.lihb.babyvoice.customview.base.BaseFragmentActivity;
import com.lihb.babyvoice.utils.CommonToast;
import com.orhanobut.logger.Logger;

/**
 * Created by lhb on 2017/4/1.
 */
public class LoginActivity extends BaseFragmentActivity {


    private EditText mUserAccountEditText;

    private EditText mUserPasswordEditText;

    private Button mLoginBtn;

    private String mUserAccount;

    private String mPassword;

    private String mLoginAccount = null;

    private ProgressDialog mProgressDialog = null;

    private ImageView mAccountClearInputImg = null;
    private ImageView mPwdClearInputImg = null;

    // 密码可见
    private ImageView mPwdShowImg = null;
    private boolean mIsPwdVisiable = false;
    private TitleBar mTitleBar;
    private TextView mSmsLoginTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        getWindow().setBackgroundDrawable(null);

    }

    private void initViews() {
        mAccountClearInputImg = (ImageView) findViewById(R.id.account_clear_input);
        mAccountClearInputImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserAccountEditText.setText("");
            }
        });
        mPwdClearInputImg = (ImageView) findViewById(R.id.pwd_clear_input);
        mPwdClearInputImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserPasswordEditText.setText("");
            }
        });
        mLoginBtn = (Button) findViewById(R.id.login_btn);
        mLoginBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mLoginAccount = mUserAccountEditText.getText().toString();
                mPassword = mUserPasswordEditText.getText().toString();
                login(mLoginAccount, mPassword);
//				loginWithPassword(mLoginAccount, mPassword);
            }
        });
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setLeftOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, StartupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mPwdShowImg = (ImageView) findViewById(R.id.pwd_show);
        mPwdShowImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsPwdVisiable) {
                    mUserPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwdShowImg.setImageResource(R.mipmap.zy);
                    mIsPwdVisiable = true;
                } else {
                    mUserPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdShowImg.setImageResource(R.mipmap.by);
                    mIsPwdVisiable = false;
                }
                // 光标移到最后
                mUserPasswordEditText.setSelection(mUserPasswordEditText.getText().length());
            }
        });


        mUserPasswordEditText = (EditText) findViewById(R.id.password);
        mUserPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (s.length() > 0) {
                    mPwdClearInputImg.setVisibility(View.VISIBLE);
                } else {
                    mPwdClearInputImg.setVisibility(View.GONE);
                }

                if (s.length() > 5) {
                    mLoginBtn.setBackgroundResource(R.drawable.register_login_pressed_shape);
                } else {
                    mLoginBtn.setBackgroundResource(R.drawable.register_login_normal_shape);
                }

            }
        });
        mUserPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
            }
        });

        mUserAccountEditText = (EditText) findViewById(R.id.account);
        InputFilter[] filters = {new InputFilter.LengthFilter(255)};
        mUserAccountEditText.setFilters(filters);
        mUserAccountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                mUserPasswordEditText.setText("");
                mLoginBtn.setClickable(true);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (s.length() > 0) {
                    mAccountClearInputImg.setVisibility(View.VISIBLE);
                } else {
                    mAccountClearInputImg.setVisibility(View.GONE);
                }

            }
        });

        mSmsLoginTxt = (TextView) findViewById(R.id.sms_login_txt);
        mSmsLoginTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SMSLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void handleLoginSuccess() {
        Logger.i("handleLoginSuccess");
        BabyVoiceApp.getInstance().setFirstLaunch(false);
        // 账号的保存，用于自动登录
//		BabyVoiceApp.getInstance().getUserInfo().getAccountManager()
//				.saveLoginAccount(mLoginAccount);
//		BabyVoiceApp.getInstance().getUserInfo().getAccountManager()
//				.saveAccount(mLoginAccount);
//		BabyVoiceApp.getInstance().getUserInfo().getAccountManager()
//				.savePassword(mPassword);
        // 跳转到主界面
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, StartupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        dismissLoginDialog();

        mProgressDialog = null;
    }


    private void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Login...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void dismissLoginDialog() {
        if (mProgressDialog == null) {
            return;
        }
        mProgressDialog.dismiss();
    }


    private void showDialog(String tips) {
        Toast toast = Toast.makeText(getApplicationContext(),
                tips, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void login(final String userAccount, final String password) {
        if (TextUtils.isEmpty(userAccount) /* || TextUtils.isEmpty(password) */) {

            showDialog("帐号不能为空！");
            return;
        }
        if (TextUtils.equals(mLoginAccount, "admin") && TextUtils.equals(mPassword, "123456")) {
            CommonToast.showShortToast("登录成功");
            Intent intent = new Intent(LoginActivity.this, NewMainActivity.class);
            startActivity(intent);
            finish();
        }else {
            CommonToast.showShortToast("登录失败，账户：admin，密码：123456,请重新登录");
        }

    }


}