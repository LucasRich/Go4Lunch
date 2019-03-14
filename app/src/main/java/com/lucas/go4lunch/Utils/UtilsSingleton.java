package com.lucas.go4lunch.Utils;

import android.widget.CheckBox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilsSingleton {

    private static UtilsSingleton instance;

    private UtilsSingleton() { }

    public static UtilsSingleton getInstance() {
        if (instance == null) {
            instance = new UtilsSingleton();
        }
        return instance;
    }

}
