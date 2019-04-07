package com.lucas.go4lunch.Controllers.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Build;
import android.os.Bundle;

import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.SharedPref;

public class SettingsActivity extends AppCompatActivity {

    public final static int radius = SharedPref.read(SharedPref.radius, 300);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        SharedPref.init(this);


        this.configureToolbar();
    }

    // -------------------
    // CONFIGURATION
    // -------------------

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Setting");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setElevation(0);
    }

    // -------------------
    // ACTION
    // -------------------

    @OnClick(R.id.changeUsernam_view)
    public void onCLickChangeUsername(){
        System.out.println("Change username");
    }

    @OnClick(R.id.changeProfilePicture_view)
    public void onCLickChangeProfilePicture(){
        System.out.println("Change profile img");
    }

    @OnClick(R.id.deleteAccount_view)
    public void onCLickDeleteAccount(){
        System.out.println("Delete account");
    }

    @OnClick(R.id.notification_view)
    public void onClickNotification(){
        System.out.println("Notification");
    }

    @OnClick(R.id.changeDistanceRadius_view)
    public void onClickChangeDistanceRadius(){
        System.out.println("Change distance radius");
    }

    @OnClick(R.id.updateNote_view)
    public void onClickUpdateNote(){
        System.out.println("Update note");
    }

    @OnClick(R.id.support_view)
    public void onCLickSupport(){
        System.out.println("support");
    }

    @OnClick(R.id.about_view)
    public void onClickAbout(){
        System.out.println("About");
    }
}
