package com.dhc3800.mp3;

import com.google.firebase.database.DataSnapshot;

public class Utils {

    public static String snapshotValue(DataSnapshot snapshot) {
        return snapshot.getValue().toString();
    }

    public static String dateConvert(int year, int month, int day) {
         return Integer.toString(month + 1) + "/" + Integer.toString(day) + "/" + Integer.toString(year);
    }

    public static String dateAdd0(int year, int month, int day) {
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
        return lel;

    }
}
