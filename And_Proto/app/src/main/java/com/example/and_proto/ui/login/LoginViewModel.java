package com.example.and_proto.ui.login;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.and_proto.PostJsonAsyncTask;
import com.example.and_proto.RequestHttpURLConnection;
import com.example.and_proto.data.LoginRepository;
import com.example.and_proto.data.Result;
import com.example.and_proto.data.model.LoggedInUser;
import com.example.and_proto.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LoginViewModel extends ViewModel {

    final String BASE_URI = "http://itbuddy.iptime.org/broadcastspeaker";
    final String LOGIN_PATH = "/login.json";

    static Thread mainThread;

    final String[] jsonString = {""};
    final JSONObject[] jsonObject = {new JSONObject()};
    final Map<String, Object> data = new HashMap<String, Object>();

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public Boolean login(String username, String password, Context context) throws ExecutionException, InterruptedException, JSONException {
        // can be launched in a separate asynchronous job
        //이전 값은 삭제하고 값을추가
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("id",username);
        data.put("pw",password);

        PostJsonAsyncTask postJsonAsyncTask = new PostJsonAsyncTask(BASE_URI+LOGIN_PATH, data);
        postJsonAsyncTask.execute();

        String result = postJsonAsyncTask.get();
        // result가 Null일때 처리
        // 서버가 꺼져있을때 널을 리턴
        if(result == null) return false;
        Log.i(this.getClass().getName(), result);
        JSONObject jsonObject = new JSONObject(result);

        if(jsonObject.getString("status").toString().equals("complete")){
            return true;
        }else{
            return false;
        }


//        Result<LoggedInUser> result = loginRepository.login(username, password,context);
//
//        if (result instanceof Result.Success) {
//            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
//            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
//        } else {
//            loginResult.setValue(new LoginResult(R.string.login_failed));
//        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 4;
    }
}

