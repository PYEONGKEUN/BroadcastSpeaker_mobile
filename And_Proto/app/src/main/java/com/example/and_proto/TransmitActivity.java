package com.example.and_proto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TransmitActivity extends AppCompatActivity {

    final String BASE_URI = "http://itbuddy.iptime.org/broadcastspeaker";
    final String TRANSMIT_PATH = "/transmit";
    final String GET_NAME_PATH = "/getgrpmem";

    public static final int SEND_INFORMATION = 0;
    public static final int SEND_STOP = 1;


    EditText EditText_msg;
    Spinner Spinner_place;
    Spinner Spinner_name;

    Context mContext;
//    Handler handler;
//    final Map<String, Object> data = new HashMap<String, Object>();


//    // 화면이 재구성 될때 발생 화면 돌리기등
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        mainThread.interrupt();
//
//    }
//포커스 잃었을때
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (mainThread != null)
//            mainThread.interrupt();
//
//    }
//
//    //안보일때
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (mainThread != null)
//            mainThread.interrupt();
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmit);

        //화면 요소 매핑
        EditText_msg = (EditText) findViewById(R.id.transmit_editText_msg);
        Spinner_place = (Spinner) findViewById(R.id.transmit_spinner_place);
        Spinner_name = (Spinner) findViewById(R.id.transmit_spinner_name);
        //Toast에 전해줄 Context
        mContext = getApplicationContext();

        //transmit_spinner_name 동적 생성 및 리스너 설정
        setSpinner_name();
        //EditText_msg 리스너 설정
        setEditText_msg();



    }
    public void setEditText_msg(){
        EditText_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //정규식으로 숫자로만 이루어졌있는지 확인후 아니라면 ""으로 세팅
                String regExp = "^[0-9]+$";
                String str = EditText_msg.getText().toString();
                if(!str.matches(regExp)){
                    EditText_msg.setText("");
                }
            }
        });
        EditText_msg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //정규식으로 숫자로만 이루어졌있는지 확인후 아니라면 ""으로 세팅
                String regExp = "^[0-9]+$";
                String str = EditText_msg.getText().toString();
                if(!str.matches(regExp)){
                    EditText_msg.setText("");
                }
            }
        });

    }




    //transmit_spinner_name 동적 생성
    public void setSpinner_name(){
        //
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", "skvudrms54");
        String result = null;

        ArrayAdapter<String> nameItem = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item);
        try {
            PostJsonAsyncTask postJsonAsyncTask = new PostJsonAsyncTask(BASE_URI + GET_NAME_PATH, (HashMap<String, Object>) data);
            postJsonAsyncTask.execute();

            result = postJsonAsyncTask.get();
            Log.i(this.getClass().getName(), result);

            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getString("status").equals("complete")){
                //응답 결과로 받는 array를 파싱 하여 spinner에 저장
                String strJsonArray = jsonObject.getString("array");
                Gson gson = new Gson();
                List<Map<String, String>> tempList = null;
                Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
                tempList = gson.fromJson(strJsonArray.toString(),type);
                nameItem.add("직원");
                for(Map<String, String> row : tempList){
                    nameItem.add(row.get("grpMemName"));
                }
            }else{
                nameItem.add("직원");
                nameItem.add("None");
            }
            Spinner_name.setAdapter(nameItem);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Spinner_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = Spinner_name.getItemAtPosition(position).toString();
                //카테고리 이름인 '직원'이 아닐경우에만 입력됨 -> 가장 첫번째 요소도 '직원'
                if(!selectedItem.equals("직원")){
                    EditText_msg.setText(selectedItem);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    public void transmit(View v) throws ExecutionException, InterruptedException, JSONException {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", "skvudrms54");
        data.put("place", Spinner_place.getSelectedItem().toString());

        //정규식으로 숫자로만 이루어졌있는지 확인후
        // 숫자로만 이루어져 있다면 type : number
        // 숫자로만 이루어져 있다면 type : name
        String regExp = "^[0-9]+$";
        String str = EditText_msg.getText().toString();
        if(str.matches(regExp)){
            data.put("type", "number");
            data.put("number", EditText_msg.getText().toString());
        }else{
            data.put("type", "name");
            data.put("name", EditText_msg.getText().toString());
        }

        PostJsonAsyncTask postJsonAsyncTask = new PostJsonAsyncTask(BASE_URI + TRANSMIT_PATH, (HashMap<String, Object>) data);
        postJsonAsyncTask.execute();

        String result = postJsonAsyncTask.get();
        Log.i(this.getClass().getName(), result);
        JSONObject jsonObject = new JSONObject(result);

        if (jsonObject.getString("status").toString().equals("complete")) {
            Toast.makeText(mContext, "성공", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(mContext, "실패", Toast.LENGTH_SHORT);
        }


    }



}

