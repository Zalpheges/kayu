package com.example.kayu;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.util.Pair;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kayu.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    class Search {
        Timestamp timestamp;
        String id;

        Search(Timestamp timestamp, String id) {
            this.timestamp = timestamp;
            this.id = id;
        }
    }

    private AppBarConfiguration appBarConfiguration;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseFirestore mFirestore;

    private ApiFood mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.kayu.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitiateScan();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        if (mUser == null) {
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Map<String, Object> userMap = new HashMap<>();
                        List<Map<String, Object>> history = new ArrayList<>();

                        userMap.put("uid", mUser.getUid());
                        userMap.put("history", history);

                        mFirestore.collection("Users").add(userMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        mApi = new ApiFood(new ApiFood.MyListener() {
            @Override
            public void OnComplete(boolean isSuccessful, FoodDescription foodInfo) {
                if (isSuccessful) {
                    mFirestore.collection("Users").whereEqualTo("uid", mUser.getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> userMap = new HashMap<>();

                                        DocumentSnapshot document = task.getResult().iterator().next();
                                        List<Map<String, Object>> history = (List<Map<String, Object>>) document.get("history");

                                        Map<String, Object> search = new HashMap<>();
                                        search.put("id", foodInfo.id);
                                        search.put("timestamp", Timestamp.now());

                                        history.add(search);

                                        userMap.put("uid", mUser.getUid());
                                        userMap.put("history", history);

                                        mFirestore.collection("Users").document(document.getId()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                }
                else
                    Toast.makeText(MainActivity.this, "not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnManyComplete(boolean isAllSuccessful, List<FoodDescription> listFoodInfo) {
                if (isAllSuccessful)
                    for (int i = 0; i < listFoodInfo.size(); i++)
                        Toast.makeText(MainActivity.this, listFoodInfo.get(i).name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*mFirestore.collection("Users").whereEqualTo("uid", mUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult().iterator().next();
                            List<Map<String, Object>> history = (List<Map<String, Object>>) document.get("history");

                            String[] ids = new String[history.size()];

                            for (int i = 0; i < history.size(); i++)
                                ids[i] = history.get(i).get("id").toString();

                            mApi.callMany(ids);
                        }
                    }
                });*/
    }

    private void InitiateScan()
    {
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("SCAN");
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);
        if(result != null) {
            if(result.getContents() == null)
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
            else {
                mApi.Call(result.getContents());
            }
        }
        else super.onActivityResult(requestCode , resultCode , data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}