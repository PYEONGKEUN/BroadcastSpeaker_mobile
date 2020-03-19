package  com.example.and_proto.ui.login;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.and_proto.MainActivity;
import com.example.and_proto.PreferenceManager;
import com.example.and_proto.R;
import com.example.and_proto.ui.login.LoginViewModel;
import com.example.and_proto.ui.login.LoginViewModelFactory;
import com.example.and_proto.vo.LoginVo;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;




    public LoginActivity() throws IOException {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final CheckBox checkBoxautoLogin = findViewById(R.id.checkBox_auto_login);

        final Context mContext = this.getApplicationContext();

        // 자동 로그인 구현부
        //autoLogin = true
        //id = "~"
        //pw = "~"
        if(PreferenceManager.getString(this.getApplicationContext(), "autoLogin").equals("")){
            PreferenceManager.setString(this.getApplicationContext(), "autoLogin", "false");
        }

        if(PreferenceManager.getString(this.getApplicationContext(), "autoLogin").equals("true")){
            // asnyctask로 구현한 웹통신 개체로
            try{
             Boolean result = loginViewModel.login(PreferenceManager.getString(this.getApplicationContext(), "id"),
                    PreferenceManager.getString(this.getApplicationContext(), "pw"),
                    getApplicationContext());
            loadingProgressBar.setVisibility(View.INVISIBLE);
            if(result){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
            }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        checkBoxautoLogin.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxautoLogin.isChecked()) {
                    // TODO : CheckBox is checked.
                    PreferenceManager.setString(mContext, "autoLogin", "true");
                    //아이디 비번 저장
                    PreferenceManager.setString(mContext, "id", usernameEditText.getText().toString());
                    PreferenceManager.setString(mContext, "pw", passwordEditText.getText().toString());
                } else {
                    // TODO : CheckBox is unchecked.
                    PreferenceManager.setString(mContext, "autoLogin", "false");
                }
            }
        });





        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loadingProgressBar.setVisibility(View.VISIBLE);


                    try {
                        // asnyctask로 구현한 웹통신 개체로
                        Boolean result =loginViewModel.login(usernameEditText.getText().toString(),passwordEditText.getText().toString(),getApplicationContext());
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                        if(result){
                            if (checkBoxautoLogin.isChecked()) {
                                // TODO : CheckBox is checked.
                                PreferenceManager.setString(mContext, "autoLogin", "true");
                                //아이디 비번 저장
                                PreferenceManager.setString(mContext, "id", usernameEditText.getText().toString());
                                PreferenceManager.setString(mContext, "pw", passwordEditText.getText().toString());
                            } else {
                                // TODO : CheckBox is unchecked.
                                PreferenceManager.setString(mContext, "autoLogin", "false");
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                Boolean result;
                try {
                    result =loginViewModel.login(usernameEditText.getText().toString(),passwordEditText.getText().toString(),getApplicationContext());
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    if(result){
                        if (checkBoxautoLogin.isChecked()) {
                            // TODO : CheckBox is checked.
                            PreferenceManager.setString(mContext, "autoLogin", "true");
                            //아이디 비번 저장
                            PreferenceManager.setString(mContext, "id", usernameEditText.getText().toString());
                            PreferenceManager.setString(mContext, "pw", passwordEditText.getText().toString());
                        } else {
                            // TODO : CheckBox is unchecked.
                            PreferenceManager.setString(mContext, "autoLogin", "false");
                        }
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        });



    }



    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }



}
