package com.example.and_proto;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReqeustActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    TextView TextView_number;
    Spinner Spinner_place;
    boolean isPlaying = false;
    static Thread mainThread;
    static Integer _curNumber;


    // 화면이 재구성 될때 발생 화면 돌리기등
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        mainThread.interrupt();
//
//    }


    @Override
    protected void onPause() {
        super.onPause();

        if(mainThread != null)
            mainThread.interrupt();

    }
    //안보일때
    @Override
    protected void onStop() {
        super.onStop();
        if(mainThread != null)
            mainThread.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reqeust);

        TextView_number = (TextView) findViewById(R.id.textView_number);
        Spinner_place = (Spinner) findViewById(R.id.spinner_place);

        final String BASE_URI = "http://itbuddy.iptime.org/broadcastspeaker";
        final String REQUEST_PATH = "/request";
        final String STREAM_PATH = "/stream";


        final Map<String, Object> data = new HashMap<String, Object>();


        String jsonString = "";
        final String[] streamFilename = new String[1];
        final String[] number = new String[1];

        if(mainThread != null ) {
            mainThread.interrupt();
            mainThread= null;
        }


            mainThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String jsonString = null;
                    JSONObject jsonObject = new JSONObject();
                    try {
                        while (true) {
                            // 오디오 재생중이 아닐때
                            if (!isPlaying) {


                                data.put("id", "skvudrms54");
                                data.put("place", Spinner_place.getSelectedItem().toString());

                                Log.i(this.getClass().getName() + " - request result", "Post:" + data == null ? "null" : data.toString());
                                jsonString = RequestHttpURLConnection.PostJson(BASE_URI + REQUEST_PATH, (HashMap<String, Object>) data);
                                Log.i(this.getClass().getName() + " - request result", jsonString == null ? "null" : jsonString);
                                //TextView_number.append("response : " + jsonString == null ? "null" : jsonString + "\n");
                                data.clear();


                                jsonObject = new JSONObject(jsonString);
                                if (jsonObject.getString("status").equals("complete")) {

                                    streamFilename[0] = jsonObject.getString("fileName");
                                    number[0] = jsonObject.getString("msg");
                                    _curNumber = Integer.parseInt(number[0]);
                                    TextView_number.setText(_curNumber.toString());

                                    //if it's a file, play it.

                                    //TextView_number.append(BASE_URI + STREAM_PATH + streamFilename[0] + "\n");
                                    StartMusic(BASE_URI + STREAM_PATH + streamFilename[0]);

                                }

                            }
                            // 1초 대기및 인터럽트 확인
                            Log.i(this.getClass().getName(), "sleep 1000millis");
                            Thread.sleep(1000);
                        }//while
                    } catch (InterruptedException e) {
                        Log.e(this.getClass().getName(),"mainThread is interrupted");
                        //TextView_number.append(e.toString() + "\n");
                        //break;
                    } catch (Exception e) {
                        Log.e("MUSIC", e.toString());
                        //TextView_number.append(e.toString() + "\n");
                        //break;
                    } finally {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
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

                    playTime[0]++;
                    Log.i(this.getClass().getName(), "STARTING Streaming End");
                    if (playTime[0] < 2) {
                        mp.start();
                    } else if (playTime[0]  >= 2) {
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
