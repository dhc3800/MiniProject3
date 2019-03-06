package com.dhc3800.mp3;

import com.google.firebase.database.DataSnapshot;

public class Utils {

    public static String snapshotValue(DataSnapshot snapshot) {
        return snapshot.getValue().toString();
    }
}
