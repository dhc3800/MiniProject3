package com.dhc3800.mp3;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventDetails extends AppCompatActivity implements View.OnClickListener{
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
    private ImageView imageView;
    private TextView description;
    private TextView date;
    private TextView number;
    private TextView name;
    private CheckBox checkBox;
    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String imageURL;
    private String numberAttending;
    private boolean attending;
    private ArrayList<String> eventsAttending = new ArrayList<>();
    DatabaseReference rsvp;
    private boolean check;
    private String id;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBox:
                if (!checkBox.isChecked()) {
                    numberAttending = String.valueOf(Integer.parseInt(numberAttending) - 1);
                    number.setText("# RSVP: " + numberAttending);
                    eventsAttending.remove(id);
                    refEvents.child("numberInterested").setValue(Integer.valueOf(numberAttending));
                    rsvp.setValue(eventsAttending);
                } else {

                    numberAttending = String.valueOf(Integer.parseInt(numberAttending) + 1);
                    number.setText("# RSVP: " + numberAttending);
                    eventsAttending.add(id);
                    refEvents.child("numberInterested").setValue(Integer.valueOf(numberAttending));
                    rsvp.setValue(eventsAttending);
                }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Bundle b = getIntent().getExtras();
        id = b.getString("EventID");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        final DatabaseReference eventsViewed = database.getReference().child("views").child(mAuth.getCurrentUser().getUid());

        /**
         * update how many events the user has viewed
         */
        eventsViewed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    eventsViewed.setValue(Integer.valueOf(dataSnapshot.getValue().toString()) + 1);
                } else {
                    eventsViewed.setValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rsvp = database.getReference().child("rsvp").child(mAuth.getCurrentUser().getUid());
        rsvp.keepSynced(true);

        /**
         * retrieve the rsvp number
         */
        rsvp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    eventsAttending= new ArrayList<>();
                    for (DataSnapshot event: dataSnapshot.getChildren()) {
                        eventsAttending.add(event.getValue().toString());
                    }
                }
                if (eventsAttending != null && eventsAttending.contains(id)) {
                    check = true;
                }
                if (check) {
                    checkBox.setChecked(true);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        checkBox = findViewById(R.id.checkBox);


        imageView = findViewById(R.id.imageView2);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);
        number = findViewById(R.id.number);
        name = findViewById(R.id.name);
        checkBox = findViewById(R.id.checkBox);

        /**
         * changing the rsvp number based on the result of clicking interested
         */
        checkBox.setOnClickListener(this);





//        if (eventsAttending.contains(id)) {
//            check = true;
//        }


        refEvents = database.getReference("events").child(id);
        refEvents.keepSynced(true);
        /**
         * retrieving the event information
         */
        refEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventName = Utils.snapshotValue(dataSnapshot.child("eventName"));
                eventDescription = Utils.snapshotValue(dataSnapshot.child("eventDescription"));
                eventDate = Utils.snapshotValue(dataSnapshot.child("date"));
                imageURL = Utils.snapshotValue(dataSnapshot.child("imageURL"));
                numberAttending = Utils.snapshotValue(dataSnapshot.child("numberInterested"));
                name.setText(eventName);
                date.setText(eventDate);
                number.setText("# RSVP: " + numberAttending);
                description.setText(eventDescription);
                Glide.with(imageView.getContext()).load(imageURL).centerCrop().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Couldn't retrieve event");
            }
        });
        refEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberAttending = Utils.snapshotValue(dataSnapshot.child("numberInterested"));
                number.setText("# RSVP: " +numberAttending);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        System.out.println(eventsAttending);




//        refEvents.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                eventName = dataSnapshot.child("eventName").getValue().toString();
//                eventDescription = dataSnapshot.child("eventDescription").getValue().toString();
//                eventDate = dataSnapshot.child("date").getValue().toString();
//                imageURL = dataSnapshot.child("imageURL").getValue().toString();
//                numberAttending = dataSnapshot.child("numberInterested").getValue().toString();
//                name.setText(eventName);
//                date.setText(eventDate);
//                number.setText(numberAttending);
//                description.setText(eventDescription);
//                Glide.with(imageView.getContext()).load(imageURL).centerCrop().into(imageView);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("TAG", "Couldn't retrieve event");
//            }
//        });



    }
}
