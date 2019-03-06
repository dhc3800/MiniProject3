package com.dhc3800.mp3;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity to create an event
 */
public class CreateEvent extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private DatabaseReference userRef;
    private StorageReference imagesRef;
    private FirebaseUser currentUser;
    private EditText title;
    private EditText description;
    private ImageView image;
    private Uri downloadUrl;
    private Button submit;
    private Button date;
    private boolean dateSelected = false;
    private int month = -1;
    private int day = -1;
    private int year = -1;
    private boolean imageSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        title = findViewById(R.id.eventName);
        description = findViewById(R.id.eventDescription);
        image  = findViewById(R.id.eventImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                        ),
                        1
                );
            }
        });
        mRef = FirebaseDatabase.getInstance().getReference("events");
        userRef = FirebaseDatabase.getInstance().getReference("rsvp");
        mAuth = FirebaseAuth.getInstance();
        imagesRef = FirebaseStorage.getInstance().getReference("images");
        date = findViewById(R.id.setDate);
        date.setOnClickListener(this);
//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//                dateSelected = true;
//                DatePickerDialog dialog = new DatePickerDialog(
//                        CreateEvent.this,
//                        R.style.Theme_AppCompat_Light_Dialog_Alert,
//                        this,
//                        year, month, day);
//                dialog.show();
//            }
//        });

        currentUser = mAuth.getCurrentUser();
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()) {
                    Toast.makeText(CreateEvent.this, "Event Name is empty", Toast.LENGTH_LONG).show();
                } else if (description.getText().toString().isEmpty()) {
                    Toast.makeText(CreateEvent.this, "Event Description is empty", Toast.LENGTH_LONG).show();
                } else if (!dateSelected) {
                    Toast.makeText(CreateEvent.this, "Date isn't set properly", Toast.LENGTH_LONG).show();
                } else if (!imageSet) {
                    Toast.makeText(CreateEvent.this, "Image isn't set", Toast.LENGTH_LONG).show();
                } else {
                    upload();
                }
            }
        });


    }

    /**
     * retrieving an image from camera gallery
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(bitmap);
                imageSet = true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.setDate:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        CreateEvent.this,
                        R.style.Theme_AppCompat_Light_Dialog_Alert,
                        this,
                        year, month, day);
                dialog.show();
                break;
        }
    }

    /**
     * uploading the image to firebase storage
     */
    private void upload() {
        final String eventName = title.getText().toString();
        final String eventDescription = description.getText().toString();
        final String userEmail = currentUser.getEmail();
        final String userID =  currentUser.getUid();
        final String eventID = mRef.push().getKey();
        final String rsvpID = userRef.push().getKey();
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = imagesRef.child(eventID).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateEvent.this, "Couldn't upload image", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
        final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                Log.i("TAG", imagesRef.getDownloadUrl().toString());
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> Task) {
                if (Task.isSuccessful()) {
                    downloadUrl = Task.getResult();
                    int rsvp = 1;
                    String lel;
                    if (month < 10) {
                        if (day < 10) {
                            lel = "0" + month +"/0"+day +  "/" + year;
                        } else {
                            lel = "0" + month+"/" +day + "/" + year;
                        }
                    } else {
                        if (day < 10) {
                            lel = month + "/" + "0" + day + "/" + year;
                        } else {
                            lel = month+"/" + day + "/" + year;
                        }
                    }
                    Date current = new Date();
                    Events event = new Events(eventID, userEmail, downloadUrl.toString(), eventName, rsvp, eventDescription, lel, current.getTime());
                    mRef.child(eventID).setValue(event);
                    ArrayList<String> users = new ArrayList<>();
                    users.add(eventID);
                    userRef.child(userID).setValue(users);
                    startActivity(new Intent(CreateEvent.this, EventsPage.class));
                } else {
                    Toast.makeText(CreateEvent.this, "Image couldn't be uploaded", Toast.LENGTH_LONG).show();
                }
            }
        });








    }

    private void setEditingEnabled(boolean enabled) {

    }

    /**
     * uploading the event to firebase
     * @param userID
     * @param email
     * @param title
     * @param description
     * @param imageURL
     */
    private void writeEvent(String userID, String email, String title, String description, String imageURL) {
        String key = mRef.child("events").push().getKey();
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put("email", email);
        eventValues.put("title", title);
        eventValues.put("description", description);
        eventValues.put("imageURL", imageURL);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, eventValues);
        mRef.updateChildren(childUpdates);

    }

    /**
     * checking if a date was selected and assigning the String date the value
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = Integer.toString(month + 1) + "/" + Integer.toString(dayOfMonth) + "/" + Integer.toString(year);
        this.year = year;
        this.month = month + 1;
        this.day = dayOfMonth;
        dateSelected = true;
    }

}
