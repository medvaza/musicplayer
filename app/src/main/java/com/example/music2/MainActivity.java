package com.example.music2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private final String DATA_STREAM = "http://ep128.hostingradio.ru:8030/ep128";
    private final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/music.mp3";
    private String nameAudio = "";

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Toast toast;
    private TextView textOut;
    private Switch switchLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textOut = findViewById(R.id.textOut);
        switchLoop = findViewById(R.id.switchLoop);


        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


        switchLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(mediaPlayer != null)
                    mediaPlayer.setLooping(isChecked);
            }
        });
    }
    public void onClickSource(View view) {
        releaseMediaPlayer();
        try {
            switch (view.getId()) {
                case R.id.btnStream:
                    toast = Toast.makeText(this, "запущен поток аудио", Toast.LENGTH_SHORT);
                    toast.show();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(DATA_STREAM);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.prepareAsync();
                    nameAudio = "РАДИО";
                    break;
                case R.id.btnRAW:
                    toast = Toast.makeText(this, "запущено аудио с памяти телефона", Toast.LENGTH_SHORT);
                    toast.show();
                    mediaPlayer = MediaPlayer.create(this, R.raw.smelchak);
                    mediaPlayer.start();
                    nameAudio = "Король и шут - Смельчак и ветер";
                    break;
                case R.id.btnSD:
                    toast = Toast.makeText(this, "запущено аудио с SD карты", Toast.LENGTH_SHORT);
                    toast.show();
                    mediaPlayer = MediaPlayer.create(this, R.raw.fred);
                    mediaPlayer.start();
                    nameAudio = "Король и шут - Фред";
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            toast = Toast.makeText(this, "Источник информации не найден", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (mediaPlayer == null) return;
        mediaPlayer.setLooping(switchLoop.isChecked());
        mediaPlayer.setOnCompletionListener(this);
    }

    public void onClick(View view) {
        if (mediaPlayer == null) return;
        try {
            switch (view.getId()) {
                case R.id.btnResume:
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                    break;
                case R.id.btnPause:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break;
                case R.id.btnStop:
                    mediaPlayer.stop();
                    break;
                case R.id.btnForward:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                    break;
                case R.id.btnBack:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000); // переход к определённой позиции трека
                    break;
            }

            textOut.setText(nameAudio + "\n(проигрывание " + mediaPlayer.isPlaying() + ", время " + mediaPlayer.getCurrentPosition() + ",\nповтор " + mediaPlayer.isLooping() + ", громкость " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + ")");
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        toast = Toast.makeText(this, "старт медиа-плейера",Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        toast = Toast.makeText(this, "отключение медиа-плейера",Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Toast.makeText(this, "Нажата кнопка громче", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Toast.makeText(this, "Нажата кнопка тише", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(this, "Нажата кнопка назад", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}

