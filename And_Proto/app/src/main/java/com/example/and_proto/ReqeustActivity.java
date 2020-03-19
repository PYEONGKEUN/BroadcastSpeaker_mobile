package com.example.and_proto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReqeustActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    TextView TextView_msg;
    Spinner Spinner_place;

    static boolean isPlaying = false;
    static Thread mainThread;
    String msg;


    final int SET_TextView_msg = 0;




    //다시 복귀할때
    @Override
    protected void onResume(){
        Log.i("INFO","onResume");
        super.onResume();
        broadcast();
    }


    // 화면이 재구성 될때 발생 화면 돌리기등
    @Override
    protected void onDestroy() {
        Log.i("INFO","onDestroy");
        super.onDestroy();
        if (mainThread != null)
            mainThread.interrupt();

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i("INFO","onPause");
        if (mainThread != null)
            mainThread.interrupt();
        //isPlaying = false;

    }

    //안보일때
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("INFO","onStop");
        if (mainThread != null)
            mainThread.interrupt();
        //isPlaying = false;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // 화면을 landscape(가로) 화면으로 고정하고 싶은 경우
        setContentView(R.layout.activity_reqeust);


        TextView_msg = (TextView) findViewById(R.id.textView_number);
        Spinner_place = (Spinner) findViewById(R.id.spinner_place);




        broadcast();


    }

    public void broadcast() {



        if(mainThread != null){
            if (!mainThread.isInterrupted()) {
                mainThread.interrupt();
                mainThread = null;
                //isPlaying = false;
            }
        }


        final Map<String, Object> data = new HashMap<String, Object>();
        String jsonString = "";
        final String[] streamFilename = new String[1];
        final String[] number = new String[1];


        mainThread = new Thread(new Runnable() {
            Handler handlerTextView_msg = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    String str = msg.obj.toString();
                    TextView_msg.setText(str);
                }
            };

            @Override
            public void run() {


                String jsonString = null;
                JSONObject jsonObject = new JSONObject();
                while (true) {
                    try {
                        // 1초 대기
                            Log.i(this.getClass().getName(), "sleep 1000millis");
                            Thread.sleep(1000);

                        // 오디오 재생중이 아닐때
                        Log.i("isPlaying value ", isPlaying + "");
                        if (!isPlaying) {

                            Log.i("after isPlaying", "-----------------");
                            data.put("id", "skvudrms54");
                            data.put("place", Spinner_place.getSelectedItem().toString());

                            Log.i(this.getClass().getName() + " - request value", "Post:" + data == null ? "null" : data.toString());
                            jsonString = RequestHttpURLConnection.PostJson(Consts.BASE_URI + Consts.REQUEST_PATH, (HashMap<String, Object>) data);
                            Log.i(this.getClass().getName() + " - request result", jsonString == null ? "null" : jsonString);
                            //TextView_number.append("response : " + jsonString == null ? "null" : jsonString + "\n");
                            data.clear();


                            jsonObject = new JSONObject(jsonString);
                            if (jsonObject.getString("status").equals("complete")) {

                                streamFilename[0] = jsonObject.getString("fileName");
                                number[0] = jsonObject.getString("msg");
                                msg = number[0];
                                String regEx = "^[0-9]+$";
                                Log.i("Before RegEx", msg.matches(regEx) + "");
                                // 숫자만으로 구성된 문자열인지 확인후에 TextBOx의 번호를 바꿈
                                if (msg.matches(regEx)) {

                                    Message message = handlerTextView_msg.obtainMessage();
                                    message.what = SET_TextView_msg;
                                    String information = new String(msg);
                                    message.obj = information;
                                    handlerTextView_msg.sendMessage(message);
                                    Log.i("After setText", TextView_msg.getClass().getName());
                                }
                                //if it's a file, play it.
                                Log.i("Before StartMusic", Consts.BASE_URI + Consts.STREAM_PATH + streamFilename[0]);
                                //TextView_number.append(BASE_URI + STREAM_PATH + streamFilename[0] + "\n");
                                StartMusic(Consts.BASE_URI + Consts.STREAM_PATH + streamFilename[0]);

                            }

                        }


                    } catch (InterruptedException e) {
                        Log.e("Interupted", "mainThread is interrupted");
                        break;
                        //TextView_number.append(e.toString() + "\n");

                    } catch (Exception e) {

                        Log.e("Error", "--------");
                        for (StackTraceElement element : e.getStackTrace()) {
                            Log.e("", element.toString());
                        }
                        Log.e("Error", "--------");
                        //TextView_number.append(e.toString() + "\n");
                        //break;
                    }

                }//while
            }
        });
        mainThread.start();

    }

    public void StartMusic(String url) {
        final int[] playTime = {0};
        try {

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPlaying = true;
                    Log.i(this.getClass().getName(), "STARTING Streaming");
                    //m_TextViewLog.append("STARTING Streaming\n");
                    mp.start();

                }

            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i("mediaplayer", playTime[0] + "");
                    playTime[0]++;
                    Log.i(this.getClass().getName(), "STARTING Streaming End");
                    if (playTime[0] < 2) {
                        mp.start();
                    } else if (playTime[0] >= 2) {
                        Log.i("mediaplayer", "after isPlaying = false");
                        isPlaying = false;
                    }
                    //m_TextViewLog.append("Streaming End \n");

                }
            });


        } catch (Exception e) {
            Log.e("MusicPlayer", e.toString());
        }
    }
}
