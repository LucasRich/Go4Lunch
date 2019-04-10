package com.lucas.go4lunch.Controllers.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.SharedPref;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.switch_notification) Switch switchNotification;

    public final static int radius = SharedPref.read(SharedPref.radius, 300);

    Locale mLocale;

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
        toolbar.setTitle(getString(R.string.toolbar_title_setting));
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
        //CREATE ALERT DIALOG
        AlertDialog.Builder changeUserNameDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.edit_text_chage_username, (ViewGroup)findViewById(R.id.changeUsernamLayout));
        changeUserNameDialog.setView(layout);
        changeUserNameDialog.setTitle("Change username");

        //DECLARATION
        EditText userNameText = (EditText)layout.findViewById(R.id.userNameText);

        //INIT
        userNameText.setText("Lucas Ri");

        //BUTTON
        changeUserNameDialog.setPositiveButton("Save", (dialog, which) -> {
            //SAVE NEW USERNAME
        });
        changeUserNameDialog.setNegativeButton("Cancel", (dialog, which) -> { });

        //DISPLAY DIALOG
        changeUserNameDialog.create().show();
    }

    @OnClick(R.id.changeProfilePicture_view)
    public void onCLickChangeProfilePicture(){
        System.out.println("Change profile img");
    }

    @OnClick(R.id.deleteAccount_view)
    public void onCLickDeleteAccount(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure, do you want delete your account ?")
                .setPositiveButton("Ok", (dialog, which) -> {
                    //DELETE ACCOUNT
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                }).show();
    }

    @OnClick(R.id.notification_view)
    public void onClickNotification(){
        if (switchNotification.isChecked()){ switchNotification.setChecked(false); }
        else { switchNotification.setChecked(true); }
    }

    @OnClick(R.id.changeDistanceRadius_view)
    public void onClickChangeDistanceRadius(){
        //CREATION
        AlertDialog.Builder seekBarDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.seek_bar_radius, (ViewGroup)findViewById(R.id.seekBarLayout));
        seekBarDialog.setView(layout);
        seekBarDialog.setTitle("Change radius distance (m)");

        //DECLARATION
        TextView seekBarSettingTxt = (TextView)layout.findViewById(R.id.seekBarSettingTxt);
        SeekBar seekBarSetting = (SeekBar)layout.findViewById(R.id.seekBarSetting);

        //INIT INPUT
        seekBarSetting.setProgress(SharedPref.read(SharedPref.radius, 300)/100);
        seekBarSettingTxt.setText(String.valueOf(getGoodRadiusDistance(seekBarSetting.getProgress())));

        //SEEK BAR
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarSettingTxt.setText(String.valueOf(getGoodRadiusDistance(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        seekBarSetting.setOnSeekBarChangeListener(seekBarChangeListener);

        //BUTTON
        seekBarDialog.setPositiveButton("Save", (dialog, which) -> {
            SharedPref.write(SharedPref.radius, getGoodRadiusDistance(seekBarSetting.getProgress()));
            restartMainActivity();
        });
        seekBarDialog.setNegativeButton("Cancel", (dialog, which) -> { });

        //DISPLAY DIALOG
        seekBarDialog.create().show();
    }

    @OnClick(R.id.language_view)
    public void onClickLanguage() {
        //CREATION
        AlertDialog.Builder languageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.select_language, (ViewGroup)findViewById(R.id.selectLanguageLayout));
        languageDialog.setView(layout)
                      .setTitle("Select language");

        //DECLARATION
        RelativeLayout frFlagLayout = (RelativeLayout)layout.findViewById(R.id.fr_flag_layout);
        RelativeLayout gbFlagLayout = (RelativeLayout)layout.findViewById(R.id.gb_flag_layout);

        //BUTTON VIEW
        frFlagLayout.setOnClickListener(v -> {
            System.out.println("French");
            setLocale("fr");
            SharedPref.write(SharedPref.currentLanguage, "fr");
        });

        gbFlagLayout.setOnClickListener(v -> {
            System.out.println("English");
            setLocale("en");
            SharedPref.write(SharedPref.currentLanguage, "en");
        });

        //DISPLAY DIALOG
        languageDialog.create().show();
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

    // -------------------
    // LEAVE THE SETTINGS
    // -------------------

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        restartMainActivity();
    }

    // -------------------
    // UTILS
    // -------------------

    public void restartMainActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(SharedPref.read(SharedPref.currentLanguage, "en"))) {
            mLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = mLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
        } else {
            Toast.makeText(this, "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }

    public static int getGoodRadiusDistance (int radius){ return radius*= 100; }
}
