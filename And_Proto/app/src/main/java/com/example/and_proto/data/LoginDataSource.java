package com.example.and_proto.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.and_proto.MainActivity;
import com.example.and_proto.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    String uri = "https://itbuddy.iptime.org/broadcastspeaker/login.and.action";
    Context context;

    public LoginDataSource( Context context){
        context = this.context;
    }
    public LoginDataSource( ){

    }
    public Result<LoggedInUser> login(final String username, final String password) {

        try {
            // TODO: handle loggedInUser authentication

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject response = new JSONObject(request(username, password));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            LoggedInUser fakeUser = new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);


        } catch (Exception e) {
            Log.e(this.getClass().getName(),e.toString());
            return new Result.Error(new IOException("Error logging in", e));

        }
    }

    public void logout() {
        // TODO: revoke authentication


    }






    private String request(String username, String password) { //key&value로 전송하고 json으로 받기.



        Map<String,Object> params = new LinkedHashMap<>(); // 파라미터 세팅
        params.put("id", username);
        params.put("pw", password);

        StringBuilder postData = new StringBuilder();

        try {
            for(Map.Entry<String,Object> param : params.entrySet()) {
                if(postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }



            URL Url = new URL(uri);
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpsURLConnection con = (HttpsURLConnection) Url.openConnection();
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            con.setDefaultUseCaches(false);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
            setCookieHeader();

            //사용자가 로그인해서 세션 쿠키를 서버로부터 발급받은적 있다면 그 다음 요청 헤더 부터는 그 세션 쿠키를 포함해서 전송해야 함.
            con.getOutputStream().write(postDataBytes); // POST 호출


            Log.d("LOG", uri + "로 HTTP 요청 전송");
            if (con.getResponseCode() != HttpsURLConnection.HTTP_OK) { //이때 요청이 보내짐.
                Log.d("LOG", "HTTP_OK를 받지 못했습니다.");
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String line;
            String page = "";
            while ((line = reader.readLine()) != null) {
                page += line;
            }
            getCookieHeader();
            return page;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    private void getCookieHeader() throws IOException {//Set-Cookie에 배열로 돼있는 쿠키들을 스트링 한줄로 변환
        URL url = new URL(uri);
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
        //cookies -> [JSESSIONID=D3F829CE262BC65853F851F6549C7F3E; Path=/smartudy; HttpOnly] -> []가 쿠키1개임.
        //Path -> 쿠키가 유효한 경로 ,/smartudy의 하위 경로에 위의 쿠키를 사용 가능.
        if (cookies != null) {
            for (String cookie : cookies) {
                String sessionid = cookie.split(";\\s*")[0];
                //JSESSIONID=FB42C80FC3428ABBEF185C24DBBF6C40를 얻음.
                //세션아이디가 포함된 쿠키를 얻었음.
                setSessionIdInSharedPref(sessionid);

            }
        }

    }

    private void setSessionIdInSharedPref(String sessionid){
        SharedPreferences pref = context.getSharedPreferences("sessionCookie",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        if(pref.getString("sessionid",null) == null){ //처음 로그인하여 세션아이디를 받은 경우
            Log.d("LOG","처음 로그인하여 세션 아이디를 pref에 넣었습니다."+sessionid);
        }else if(!pref.getString("sessionid",null).equals(sessionid)){ //서버의 세션 아이디 만료 후 갱신된 아이디가 수신된경우
            Log.d("LOG","기존의 세션 아이디"+pref.getString("sessionid",null)+"가 만료 되어서 "
                    +"서버의 세션 아이디 "+sessionid+" 로 교체 되었습니다.");
        }
        edit.putString("sessionid",sessionid);
        edit.apply(); //비동기 처리
    }

    private void setCookieHeader() throws IOException {
        URL url = new URL(uri);
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

        SharedPreferences pref = context.getSharedPreferences("sessionCookie",Context.MODE_PRIVATE);
        String sessionid = pref.getString("sessionid",null);
        if(sessionid!=null) {
            Log.d("LOG","세션 아이디"+sessionid+"가 요청 헤더에 포함 되었습니다.");
            conn.setRequestProperty("Cookie", sessionid);
        }
    }


}
