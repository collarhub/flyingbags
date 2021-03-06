package net.flyingbags.flyingapps.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import net.flyingbags.flyingapps.R;
import net.flyingbags.flyingapps.presenter.LoginPresenter;
import net.flyingbags.flyingapps.service.LoginService;

/**
 * Created by User on 2017-10-07.
 * 두번째 activity 로그인 화면
 */

public class LoginActivity extends AppCompatActivity implements LoginPresenter.view {
    private ConstraintLayout constraintLayout;  // background layout(배경 touch시 editText focus out시키는 용도)
    private LoginService loginService;          // presenter
    private EditText editTextEmail;
    private EditText editTextPasswd;            // passwd 입력 editText(완료 누르면 sign in 버튼 누른효과)
    private Button buttonSignIn;
    private ProgressDialog progressDialog;
    private Button buttonSignUp;
    private boolean validResultCheck = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginService = new LoginService(this);

        editTextEmail = (EditText) findViewById(R.id.edit_email);
        editTextPasswd = (EditText) findViewById(R.id.edit_passwd);
        // 배경 클릭 이벤트
        constraintLayout = (ConstraintLayout) findViewById(R.id.login_layout);
        constraintLayout.setFocusable(true);
        constraintLayout.setFocusableInTouchMode(true);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextfocusout();
                return false;
            }
        });

        // passwd 입력시 완료 버튼 이벤트
        editTextPasswd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(editTextEmail.getText().toString().equals("") || editTextPasswd.getText().toString().equals("")) {
                        onSignInFailed();
                    }
                    else {
                        loginService.signIn(editTextEmail.getText().toString(), editTextPasswd.getText().toString());
                        runProgress();
                    }
                    return true;
                }
                return false;
            }
        });

        // 로그인 버튼 클릭 이벤트
        buttonSignIn = (Button) findViewById(R.id.button_signin);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextEmail.getText().toString().equals("") || editTextPasswd.getText().toString().equals("")) {
                    onSignInFailed();
                }
                else {
                    loginService.signIn(editTextEmail.getText().toString(), editTextPasswd.getText().toString());
                    runProgress();
                }
            }
        });

        buttonSignUp = (Button) findViewById(R.id.another_signup_button);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSignUpCallBack();
            }
        });
    }

    void buttonSignUpCallBack(){
        startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        this.finish();
    }

    @Override
    public void onSignInFailed() {
        ((LinearLayout) findViewById(R.id.login_linear_forget)).setVisibility(View.VISIBLE);
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onSignInSuccess() {
        if(validResultCheck) {
            this.startActivity(new Intent(this, MainActivity.class));
            validResultCheck = false;
        }
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        this.finish();
    }

    @Override
    public void onCreateUserFailed(String errorMessage) {

    }

    @Override
    public void onCreateUserSuccess() {

    }

    @Override
    public void onAuthAdminSuccess() {
        if(validResultCheck) {
            this.startActivity(new Intent(this, AdminInvoiceListActivity.class));
            validResultCheck = false;
        }
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        this.finish();
    }

    private void editTextfocusout() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editTextEmail.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(editTextPasswd.getWindowToken(), 0);
        editTextEmail.clearFocus();
        editTextPasswd.clearFocus();
    }

    private void runProgress() {
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progressbar_spin);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Please check your network environment.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        progressDialog.dismiss();
                                    } catch (Exception e) {
                                    }
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            }
        }, 20000);
    }
}