package com.webrand.gpstracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.webrand.gpstracker.adapters.RoadsAdapter;
import com.webrand.gpstracker.db.DatabaseOperation;
import com.webrand.gpstracker.models.TrackerInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private RecyclerView recyclerView;
    private TextView textWhenRecyclerEmpty;
    private FloatingActionButton fabForNewTrack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_list_of_track);
        textWhenRecyclerEmpty  =findViewById(R.id.text_when_recycler_is_empty);

        fabForNewTrack = findViewById(R.id.btn_new_track);
        fabForNewTrack.setOnClickListener(view -> {
            if(isServicesOK()){
                init();
            }
        });

        getDataFromDB();


    }

    private void getDataFromDB() {
        ArrayList<TrackerInfo> list = DatabaseOperation.get(this).getListOfTrackers();
        if (list.isEmpty()){
            textWhenRecyclerEmpty.setVisibility(View.VISIBLE);
        }else {textWhenRecyclerEmpty.setVisibility(View.GONE);}
        setRecyclerView(list);
    }

    private void init() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    private boolean isServicesOK () {
            Log.d(TAG, "isServicesOK: checking google services version");

            int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

            if (available == ConnectionResult.SUCCESS) {
                //everything is fine and the user can make map requests
                Log.d(TAG, "isServicesOK: Google Play Services is working");
                return true;
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                //an error occured but we can resolve it
                Log.d(TAG, "isServicesOK: an error occured but we can fix it");
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
                dialog.show();
            } else {
                Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

    private void setRecyclerView(ArrayList<TrackerInfo> roads) {
        RoadsAdapter adapter = new RoadsAdapter(this, roads);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    fabForNewTrack.hide();
                } else{
                    fabForNewTrack.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDB();
    }

}
