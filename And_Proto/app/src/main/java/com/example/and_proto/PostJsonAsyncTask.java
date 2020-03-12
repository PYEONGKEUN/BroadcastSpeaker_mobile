package com.example.and_proto;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class PostJsonAsyncTask extends AsyncTask<Void, Void, String> {

    static final int CONN_TIMEOUT = 2;
    static final int READ_TIMEOUT = 2;

    String _url;
    HashMap<String, Object> _map;

    public PostJsonAsyncTask(String _url, HashMap<String, Object> _map){
        this._url = _url;
        this._map = _map;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Void... params) {
        String result = "error";

        try {
            result = PostJson(_url, _map);
        } catch (JSONException e) {
            e.printStackTrace();
            result = "error";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String params) {
        super.onPostExecute(params);
    }


    public String PostJson(String _url, HashMap<String, Object> _map) throws JSONException {
        // HttpURLConnection 참조 변수.

        HttpURLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;


        /**
         * 1. StringBuffer에 파라미터 연결
         * */

        // 보낼 데이터가 없으면 파라미터를 비운다.
        JSONObject data = null;
        if (_map == null)
            data = null;
            // 보낼 데이터가 있으면 파라미터를 채운다.
        else {
            data = JsonUtil.getJsonStringFromMap(_map);
        }

        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         * */
        try{
            URL url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();

            // [2-1]. urlConn 설정.
            conn.setConnectTimeout(CONN_TIMEOUT * 1000);
            conn.setReadTimeout(READ_TIMEOUT * 1000);
            conn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // [2-2]. parameter 전달 및 데이터 읽어오기.
            os = conn.getOutputStream();
            os.write(data.toString().getBytes()); // 출력 스트림에 출력.
            os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.


            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            String response;

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.


            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData = null;
            int nLength = 0;
            while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();

            response = new String(byteData);

            os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.
            baos.close();
            is.close();
            return response;

        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return null;

    }

}