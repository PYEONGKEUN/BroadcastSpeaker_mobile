package com.example.and_proto;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    TextView m_TextViewLog;
    boolean isPlaying = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String uri = "https://itbuddy.iptime.org/broadcastspeaker/download";
        m_TextViewLog = (TextView) findViewById(R.id.textViewLog);
        m_TextViewLog.setMovementMethod(new ScrollingMovementMethod());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {


                    try {
                        //if it's a file, play it.
                        if(!isPlaying){
                            StartMusic(uri);
                        }


                        Log.i(this.getClass().getName(), "sleep 1000millis");
                        m_TextViewLog.append("sleep 1000millis\n");
                        Thread.sleep(1000);

                    } catch (Exception e) {
                    Log.e("MUSIC", e.toString());
                    m_TextViewLog.append(e.toString()+"\n");
                    break;

                    }finally {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }).start();
    }


    public void StartMusic(String url) {
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
                    m_TextViewLog.append("STARTING Streaming\n");
                    mp.start();
                }

            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    Log.i(this.getClass().getName(), "STARTING Streaming End");
                    m_TextViewLog.append("Streaming End \n");
                }
            });

        } catch (Exception e) {
            Log.e("MusicPlayer", e.toString());
        }
    }


}
