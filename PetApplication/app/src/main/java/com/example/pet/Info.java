package com.example.pet;

import static android.content.ContentValues.TAG;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Info extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String happy;
    String relax;
    String anxiety;
    String angry;
    String fear;
    String agg;
    String emotionStr;
    String max_value;

    String ACount_st;
    String Time;
    Integer ACount;
    Integer Total_Acount=0;

    String Act;
    String Rest;

    TextView tv_abnormal;
    TextView tv_min_rest;
    TextView tv_min_act;
    ImageView Emotion_img;

    public static String format_yyMMdd_HH = "yyMMdd_HH";

    private FirebaseAuth firebaseAuth;
    Map<String, Object> Emotion = new HashMap<>();
    Map<String, Object> Abnormal = new HashMap<>();
    Map<String, String> sampleMap = new HashMap<>();
    Map<String, Object> Action = new HashMap<>();
    ArrayList<Integer> num = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        // Home ???????????? pet ?????? ????????????
        Intent intentHome = new Intent(this.getIntent());
        String petNameStr = intentHome.getStringExtra("petName");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userUid = user.getUid();

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat(format_yyMMdd_HH, Locale.getDefault());
        String currentTimeStr = format.format(currentTime);

        // pet name
        TextView tvPetName = (TextView) findViewById(R.id.tv_petName);
        tvPetName.setText(petNameStr);

        // ???????????? ??????
        ImageButton btnInfoBack = (ImageButton) findViewById(R.id.btn_info_back);
        btnInfoBack.setOnClickListener(view -> {
            Intent intentHomeBack = new Intent(getApplicationContext(), Home.class);
            startActivity(intentHomeBack);
            finish();
        });

        // pet info ??????
        ImageButton btnPetInfo = (ImageButton) findViewById(R.id.btn_petInfo);
        Intent intentProfile_info = new Intent(this, Profile_info.class);
        btnPetInfo.setOnClickListener(view -> {
            // pet ?????? ?????? ???????????? ????????????
            intentProfile_info.putExtra("petName", petNameStr);
            startActivity(intentProfile_info);
        });

        // ??????
        LinearLayout layoutEmotion = (LinearLayout) findViewById(R.id.layout_emotion);
        TextView tvEmotion = (TextView) findViewById(R.id.tv_emotion);
        ImageView ivEmotion = (ImageView) findViewById(R.id.iv_emotion);
        tv_abnormal = findViewById(R.id.tv_abnormal_behavior_1day);
        tv_min_rest = findViewById(R.id.tv_m_rest);
        tv_min_act = findViewById(R.id.tv_m_movement);
        Emotion_img = findViewById(R.id.iv_emotion);

        // TODO: emotion
        //---------------------------Emotion ??? ?????? ?????? ?????? ?????? ?????? ?????????------------------------
        DocumentReference docRef = db.collection("Users").document(userUid)
                .collection("Pets").document(petNameStr)
                .collection("Emotions").document(currentTimeStr);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Emotion = document.getData();

                        happy = String.valueOf(Emotion.get("??????"));
                        relax = String.valueOf(Emotion.get("??????"));
                        anxiety =String.valueOf(Emotion.get("??????"));
                        angry = String.valueOf(Emotion.get("??????"));
                        fear = String.valueOf(Emotion.get("??????"));
                        agg = String.valueOf(Emotion.get("?????????"));

                        num.add(Integer.parseInt(happy));
                        num.add(Integer.parseInt(relax));
                        num.add(Integer.parseInt(anxiety));
                        num.add(Integer.parseInt(angry));
                        num.add(Integer.parseInt(fear));
                        num.add(Integer.parseInt(agg));

                        max_value = String.valueOf(Collections.max(num));

                        sampleMap.put("??????",happy);
                        sampleMap.put("??????",relax);
                        sampleMap.put("??????",anxiety );
                        sampleMap.put("??????",angry );
                        sampleMap.put("??????",fear);
                        sampleMap.put("?????????",agg);

                        emotionStr = getSingleKeyFromValue(sampleMap, max_value);
                        tvEmotion.setText(emotionStr);


                        if (emotionStr.equals("??????")){
                            Emotion_img.setImageResource(R.drawable.happy);
                        }
                        else if (emotionStr.equals("??????")){
                            Emotion_img.setImageResource(R.drawable.relax);
                        }
                        else if (emotionStr.equals("??????")){
                            Emotion_img.setImageResource(R.drawable.anxiety);
                        }
                        else if (emotionStr.equals("??????")){
                            Emotion_img.setImageResource(R.drawable.angry);
                        }
                        else if (emotionStr.equals("??????")){
                            Emotion_img.setImageResource(R.drawable.fear);
                        }
                        else if (emotionStr.equals("?????????")){
                            Emotion_img.setImageResource(R.drawable.agg);
                        }


                    }
                    else{
                        Log.d(TAG, "No such document");
                    }
                }
                else{
                    Log.d(TAG, "get failed with", task.getException());
                }
            }
        });

        DocumentReference docRef4 = db.collection("Users").document(userUid)
                .collection("Pets").document(petNameStr)
                .collection("AbnormalBehaviors").document(currentTimeStr);
        docRef4.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document2 = task.getResult();
                    if (document2.exists()){
                        Abnormal=document2.getData();
                        ACount_st = String.valueOf(Abnormal.get("count"));
                        Time = String.valueOf(Abnormal.get("time"));
                        tv_abnormal.setText(ACount_st);

                        //ACount=Integer.parseInt(ACount_st);
                        //Total_Acount=Total_Acount+ACount;
                    }
                }
            }
        });

        DocumentReference docRef5 = db.collection("Users").document(userUid)
                .collection("Pets").document(petNameStr)
                .collection("Actions").document(currentTimeStr);
        docRef5.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document3 = task.getResult();
                    if (document3.exists()){
                        Action = document3.getData();
                        Act = String.valueOf(Action.get("?????? ??????"));
                        Rest = String.valueOf(Action.get("?????? ??????"));
                        Log.d("Act_time", "Act time : " + Act);

                        tv_min_act.setText(String.valueOf(secondToMinute(Act)));
                        tv_min_rest.setText(String.valueOf(secondToMinute(Rest)));
                    }
                }
            }
        });

        ivEmotion.setImageResource(R.drawable.ic_launcher_foreground);

        Intent intentEmotion = new Intent(this, Chart_Emotion.class);
        layoutEmotion.setOnClickListener(view -> {
            // pet ?????? ???????????? ????????????
            intentEmotion.putExtra("petName", petNameStr);
            startActivity(intentEmotion);
        });
        //------------------------------------------------------------------------------------------

        // ?????? ------------------------------------------------------------------------------------
        LinearLayout layoutAction = (LinearLayout) findViewById(R.id.layout_action);
        Intent intentAction = new Intent(this, Chart_Action.class);
        layoutAction.setOnClickListener(view -> {
            intentAction.putExtra("petName", petNameStr);
            startActivity(intentAction);
        });
        //------------------------------------------------------------------------------------------


        // ????????????
        LinearLayout layoutAbnormalBehavior = (LinearLayout) findViewById(R.id.layout_abnormal_behavior);
        TextView tvAbnormalBehavior = (TextView) findViewById(R.id.tv_abnormal_behavior);

        Intent intentAbnormalBehavior = new Intent(this, Chart_AbnormalBehavior.class);
        layoutAbnormalBehavior.setOnClickListener(view -> {
            intentAbnormalBehavior.putExtra("petName",petNameStr);
            startActivity(intentAbnormalBehavior);
        });

        DocumentReference docRef3 = db.collection("Users").document(userUid)
                .collection("Pets").document(petNameStr);
        docRef3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("PETCCTVURL", "DocumentSnapshot data: " + document.getData());
                        String cctvUrlStr = document.get("cctvUrl").toString();

                        // cctv ??????
                        FloatingActionButton fab = findViewById(R.id.fab);
                        fab.setOnClickListener((view -> {
                            // cctv ???????????? ????????????
                            Intent intentCctv = new Intent(getApplicationContext(), Cctv.class);
                            intentCctv.putExtra("cctvUrl", cctvUrlStr);
                            intentCctv.putExtra("petName", petNameStr);
                            startActivity(intentCctv);
                        }));
                    } else {
                        Log.d("PETCCTVURL", "No such document");
                    }
                } else {
                    Log.d("PETCCTVURL", "get failed with ", task.getException());
                }
            }
        });
    }

    // Value?????? ???????????? Key?????? ?????? ??????
    public static <K, V> K getSingleKeyFromValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    protected int secondToMinute(String second) {
        return parseInt(second) / 60;
    }
}



