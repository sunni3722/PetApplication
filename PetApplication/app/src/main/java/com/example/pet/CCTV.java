package com.example.pet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class CCTV extends AppCompatActivity {

    private static String cctvUrlStr;

    private LibVLC libVlc;
    private MediaPlayer mediaPlayer;
    private VLCVideoLayout videoLayout;

    private FFmpegMediaMetadataRetriever mediaMetadataRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cctv);

        // Info 화면에서 pet 이름 받아오기
        Intent intentInfo = new Intent(this.getIntent());
        cctvUrlStr = intentInfo.getStringExtra("cctvUrl");

        libVlc = new LibVLC(this);
        mediaPlayer = new MediaPlayer(libVlc);
        videoLayout = findViewById(R.id.videoLayout);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mediaPlayer.attachViews(videoLayout, null, false, false);

        Media media = new Media(libVlc, Uri.parse(cctvUrlStr));
        media.setHWDecoderEnabled(true, false);
        media.addOption(":network-caching=600");

        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();

        /*
        while (<condition to break loop>) {
        FFmpegMediaMetadataRetriever mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(<stream URL>);
        Bitmap b = mediaMetadataRetriever.getFrameAtTime(); // current frame
        mediaMetadataRetriever.release();
        //Pause for 5 seconds
        Thread.sleep(5000);
        */

        mediaMetadataRetriever = new FFmpegMediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(cctvUrlStr);
        mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
        Bitmap b = mediaMetadataRetriever.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
        byte [] artwork = mediaMetadataRetriever.getEmbeddedPicture();

        mediaMetadataRetriever.release();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        mediaPlayer.stop();
        mediaPlayer.detachViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        mediaPlayer.release();
        libVlc.release();
    }
}

