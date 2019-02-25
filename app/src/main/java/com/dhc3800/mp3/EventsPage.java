package com.dhc3800.mp3;

import android.content.Intent;
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

public class EventsPage extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        createEvent = findViewById(R.id.floatingActionButton2);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventsPage.this, CreateEvent.class));
            }
        });
        database = FirebaseDatabase.getInstance();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_page, menu);
        final MenuItem Item = menu.findItem(R.id.LogOut);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mAuth.signOut();
        EventsPage.this.startActivity(new Intent(EventsPage.this, MainActivity.class));
        return true;
    }


    private void fetchData() {
        events = database.getReference("events").orderByChild("time");
        events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String imageURL = snapshot.child("imageURL").getValue().toString();
                    String eventName = snapshot.child("eventName").getValue().toString();
                    int numInterested = Integer.valueOf(snapshot.child("numberInterested").getValue().toString());
                    String description = snapshot.child("eventDescription").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    Long timestamp = Long.valueOf(snapshot.child("time").getValue().toString());
                    eventsList.add(new Events(id, email, imageURL, eventName, numInterested, description, date, timestamp));
                }
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Couldn't retrieve events");
            }
        });


    }
}
