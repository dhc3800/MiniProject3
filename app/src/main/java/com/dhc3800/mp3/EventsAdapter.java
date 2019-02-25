package com.dhc3800.mp3;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private ArrayList<Events> EventsList;
    private ArrayList<String> EmailList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventImage;
        public TextView hostEmail;
        public TextView date;
        public TextView eventName;
        public TextView numInterested;
        public CardView cardView;
        public ViewHolder(View view) {
            super(view);
            this.eventImage = view.findViewById(R.id.image);
            this.hostEmail = view.findViewById(R.id.hostEmail);
            this.date = view.findViewById(R.id.eventDate);
            this.eventName = view.findViewById(R.id.eventName);
            this.numInterested = view.findViewById(R.id.numInterested);
            this.cardView = view.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EventDetails.class);
                    Events pass = getEvent(getAdapterPosition());
                    intent.putExtra("EventID", pass.getId());
                    v.getContext().startActivity(intent);

                }
            });
        }
    }

    public Events getEvent(int position) {
        return EventsList.get(position);
    }


    public EventsAdapter(ArrayList<Events> EventsList) {
        this.EventsList = EventsList;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Events event = EventsList.get(position);
        holder.hostEmail.setText(event.getEmail());
        holder.eventName.setText(event.getEventName());
        holder.numInterested.setText("# RSVP: "+Integer.toString(event.getNumberInterested()));
        holder.date.setText(event.getDate());

        Glide.with(holder.eventImage.getContext()).load(event.getImageURL()).centerCrop().into(holder.eventImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventDetails.class);
                Events pass = getEvent(position);
                intent.putExtra("EventID", pass.getId());
                v.getContext().startActivity(intent);
            }
        });

        
    }

    @Override
    public int getItemCount() {
        if (EventsList== null) {
            return 0;
        }
        return EventsList.size();
    }
}
