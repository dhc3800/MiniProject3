package com.dhc3800.mp3;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class EventsPage extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FloatingActionButton createEvent;
    private FirebaseDatabase database;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Events> eventsList;
    private EventsAdapter eventsAdapter;
    private String userID;
    private DatabaseReference refEvents;
    private Query events;
    private FirebaseAnalytics mFirebaseAnalytics;
    private long startTime;
    private long endTime;
    private FloatingActionButton stats;
    private boolean xd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_page);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance();
        Date enter = new Date();
        startTime = enter.getTime();


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.floatingActionButton2).setOnClickListener(this);
        findViewById(R.id.floatingActionButton).setOnClickListener(this);

        userID = mAuth.getCurrentUser().getUid();
        eventsList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        eventsAdapter = new EventsAdapter(eventsList);
        recyclerView.setAdapter(eventsAdapter);
        fetchData();
    }

    @Override
    public void onDestroy() {

        if (xd) {
            updateTime();
        }
        super.onDestroy();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_page, menu);
        final MenuItem Item = menu.findItem(R.id.LogOut);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        updateTime();
        mAuth.signOut();
        Intent intent = new Intent(EventsPage.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        EventsPage.this.startActivity(intent);
        return true;
    }


    private void fetchData() {
        events = database.getReference("events").orderByChild("time");
        events.keepSynced(true);
        events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String imageURL = snapshot.child("imageURL").getValue().toString();
                    String eventName = snapshot.child("eventName").getValue().toString();
                    int numInterested = Integer.valueOf(snapshot.child("numberInterested").getValue().toString());
                    String description = snapshot.child("eventDescription").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    Long timestamp = Long.valueOf(snapshot.child("time").getValue().toString());
                    eventsList.add(0, new Events(id, email, imageURL, eventName, numInterested, description, date, timestamp));
                }
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Couldn't retrieve events");
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton2:
                startActivity(new Intent(EventsPage.this, CreateEvent.class));

            case R.id.floatingActionButton:
                startActivity(new Intent(EventsPage.this, Stats.class));

        }
    }
    public void updateTime() {
        final DatabaseReference time = database.getReference("time").child(mAuth.getCurrentUser().getUid());
        Date end = new Date();
        endTime = end.getTime();
        xd = false;
        time.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Double> time2 = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    time2.add(Double.valueOf(snapshot.getValue().toString()));
                }
                Long l = new Long(endTime - startTime);
                double d = l.doubleValue() / 1000;
                time2.add(d);
                time.setValue(time2);

//                if (dataSnapshot.getValue() != null) {
//                    Long l = new Long(endTime - startTime);
//                    double d = l.doubleValue() / 1000;
//                    time.setValue((Double.valueOf(dataSnapshot.getValue().toString()) + d));
//                } else {
//                    Long l = new Long(endTime - startTime);
//                    double d = l.doubleValue() / 1000;
//                    time.setValue((d));
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

