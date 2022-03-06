package com.example.pet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Home extends AppCompatActivity {

    private Home_ListViewAdapter adapter;
    private ListView listviewPet;

    private Boolean firstPet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view -> {
            Intent intent_cctv = new Intent(getApplicationContext(),Home_CCTV.class);
            startActivity(intent_cctv);
        }));

        // Adapter 생성
        adapter = new Home_ListViewAdapter();

        // ListView 참조 및 Adapter 달기
        listviewPet = findViewById(R.id.listview_pet);
        listviewPet.setAdapter(adapter);

        //firebase 인스턴스 초기화
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        String userUid = user.getUid();

        // pet 유무 확인
        DocumentReference docRefUsers = firebaseFirestore.collection("Users").document(userUid);
        docRefUsers.get().addOnSuccessListener(documentSnapshot -> {
            DB_User userDB = documentSnapshot.toObject(DB_User.class);
            assert userDB != null;

            firstPet = userDB.getNumPets() == 0;

            // pet 리스트 생성
            View viewHome = findViewById(R.id.v_home);

            if (firstPet) {
                isFirstPet(viewHome, listviewPet);
            } else {
                isNotFirstPet(viewHome, listviewPet);
            }
        });

        // 리스트 생성
        if (!firstPet) {
            firebaseFirestore.collection("Users").document(userUid).collection("Pets")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("LISTVIEWPET", document.getId() + " => " + document.getData());
                                    adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launcher_foreground),
                                            document.get("name").toString(), document.get("age").toString() + "살");
                                    // listview 갱신
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Log.d("LISTVIEWPET", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

        // pet 추가 버튼
        ImageButton btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(view -> {
            // pet 정보 추가 화면으로 넘어가기
            Intent intentProfile_add = new Intent(getApplicationContext(),Profile_add.class);
            startActivity(intentProfile_add);
            // listview 갱신
            adapter.notifyDataSetChanged();
        });

        // listviewPet 에 클릭 이벤트 핸들러 정의.
        Intent intentInfo = new Intent(this, Info.class);
        listviewPet.setOnItemClickListener((parent, v, position, id) -> {
            // get item
            Home_ListView_Item item = (Home_ListView_Item) parent.getItemAtPosition(position);

            String petNameStr = item.getName();

            // pet 정보 화면으로 넘어가기
            intentInfo.putExtra("petName", petNameStr);
            startActivity(intentInfo);

            // 클릭 확인하기
            // Toast.makeText(getApplicationContext(), petNameStr, Toast.LENGTH_SHORT).show();
        });

        // 로그아웃 버튼
        Button btnLogOut = findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(view -> {
            // 로그아웃
            FirebaseAuth.getInstance().signOut();
            // Main_Logo 화면으로 넘어가기
            Intent intentMainLogo = new Intent(getApplicationContext(),Main_Logo.class);
            startActivity(intentMainLogo);
        });
    }

    public void isFirstPet(View v, ListView lv) {
        v.setVisibility(View.GONE);
        lv.setVisibility(View.GONE);
    }
    public void isNotFirstPet(View v, ListView lv) {
        v.setVisibility(View.VISIBLE);
        lv.setVisibility(View.VISIBLE);
    }
}
