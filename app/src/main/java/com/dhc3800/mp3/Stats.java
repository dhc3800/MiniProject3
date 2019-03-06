package com.dhc3800.mp3;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.lang.Math;

public class Stats extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference time;
    private DatabaseReference views;
    private ArrayList<Double> times;
    private int mView;
    private double sum;
    private double count;
    private GraphView graph;
    private GraphView graph2;
    private LineGraphSeries<DataPoint> timeSeries;
    private BarGraphSeries<DataPoint> viewSeries;


    /**
     * initializes the graphs and sets boundaries
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        graph = findViewById(R.id.graph);
        graph2 = findViewById(R.id.graph2);
        timeSeries = new LineGraphSeries<>(new DataPoint[] {new DataPoint(0,1)});
        viewSeries = new BarGraphSeries<>(new DataPoint[] {new DataPoint(0,1)});
        times = new ArrayList<>();
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(150);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);


        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0.5);
        graph2.getViewport().setMaxX(2.5);


        graph.addSeries(timeSeries);
        graph2.addSeries(viewSeries);
        viewSeries.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        viewSeries.setSpacing(50);

// draw values on top
        viewSeries.setDrawValuesOnTop(true);
        viewSeries.setValuesOnTopColor(Color.RED);

        fetchData();

    }

    /**
     * retrieves data used to display graphs
     */
    public void fetchData() {
        time = mDatabase.getReference("time").child(mAuth.getCurrentUser().getUid());
        time.keepSynced(true);
        time.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                times.clear();
                times.add(0.0);
                double max = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double y = Double.valueOf(snapshot.getValue().toString());
                    times.add(y);
                    if (y > max) {
                        max = y;
                    }
                }
                graph.getViewport().setMaxX(times.size() - 1);
                graph.getViewport().setMaxY(max);
                updateGraph();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        views  = mDatabase.getReference("views");
        views.keepSynced(true);
        views.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mView = Integer.valueOf(dataSnapshot.child(mAuth.getCurrentUser().getUid()).getValue().toString());
                sum = 0.0;
                count = 0.0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    sum += Double.valueOf(snapshot.getValue().toString());
                    count += 1.0;
                }
                updateGraph2();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * updates graph one whenever data is changed
     */
    public void updateGraph() {
        DataPoint[] timess = new DataPoint[times.size()];
        for (int i = 0; i < times.size(); i++) {
            timess[i] = new DataPoint(i, times.get(i));
        }
        timeSeries.resetData(timess);
    }

    /**
     * updates graph two whenever data is changed
     */
    public void updateGraph2() {
        DataPoint[] viewss = new DataPoint[4];
        viewss[0] = new DataPoint(0, 0);
        viewss[1] = new DataPoint(1, mView);
        viewss[2] = new DataPoint(2, sum/count);
        viewss[3] = new DataPoint(3, 0);

        viewSeries.resetData(viewss);
    }
}
