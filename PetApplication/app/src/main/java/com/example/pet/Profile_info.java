package com.example.pet;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Profile_info extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String name;
    String breed;
    String cat_dog;
    String disease;
    String gender;
    String neuter;
    String age;

    TextView tvname;
    TextView tvbreed;
    TextView tvcat_dog;
    TextView tvdisease;
    TextView tvgender;
    TextView tvneuter;
    TextView tvage;

    private FirebaseAuth firebaseAuth;
    Map<String, Object> Info = new HashMap<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_info);

        // Info 화면에서 pet 이름 받아오기
        Intent intentInfo = new Intent(this.getIntent());
        String petNameStr = intentInfo.getStringExtra("petName");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userUid = user.getUid();


        DocumentReference docRef = db.collection("Users").document(userUid).collection("Pets").document(petNameStr);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Info = document.getData();

                        name= (String) Info.get("name");
                        tvname = findViewById(R.id.pro_info_name);
                        tvname.setText(name);

                        breed= (String) Info.get("breed");
                        tvbreed = findViewById(R.id.pro_info_breed);
                        tvbreed.setText(breed);

                        cat_dog= (String) Info.get("cat_dog");
                        tvcat_dog = findViewById(R.id.pro_info_c_d);
                        tvcat_dog.setText(cat_dog);

                        disease= (String) Info.get("disease");
                        tvdisease = findViewById(R.id.pro_info_disease);
                        tvdisease.setText(disease);

                        gender= (String) Info.get("gender");
                        tvgender = findViewById(R.id.pro_info_gender);
                        tvgender.setText(gender);

                        neuter= (String) Info.get("neuter");
                        tvneuter = findViewById(R.id.pro_info_neuter);
                        tvneuter.setText(neuter);

                        age= (String) Info.get("age");
                        tvage = findViewById(R.id.pro_info_age);
                        tvage.setText(age);

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





        //취소 버튼
        ImageButton cancel_btn = (ImageButton) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(view -> {
            finish();
        });

    }
}
