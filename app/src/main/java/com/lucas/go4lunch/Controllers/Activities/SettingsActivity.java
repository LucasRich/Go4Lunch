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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Utils.UserHelper;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.switch_notification) Switch switchNotification;
    @BindView(R.id.settings_img_profile) ImageView profileImg;
    @BindView(R.id.settings_name_user) TextView nameUser;

    public final static int radius = SharedPref.read(SharedPref.radius, 300);

    Locale mLocale;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPref.init(this);

        this.initUser();
        this.displayUserInfo();
        this.configureToolbar();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_settings;
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

    public void initUser(){

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

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {

            //INIT
            User currentUser = documentSnapshot.toObject(User.class);
            userNameText.setText(currentUser.getUsername());
        });

        //BUTTON
        changeUserNameDialog.setPositiveButton("Save", (dialog, which) -> {
            UserHelper.updateUsername(userNameText.getText().toString(), this.getCurrentUser().getUid());
            restartMainActivity();
        });
        changeUserNameDialog.setNegativeButton("Cancel", (dialog, which) -> { });

        //DISPLAY DIALOG
        changeUserNameDialog.create().show();

    }

    @OnClick(R.id.changeProfilePicture_view)
    public void onCLickChangeProfilePicture(){
        //System.out.println(user.getUsername());
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

    private void displayUserInfo(){

        if (this.getCurrentUser() != null){

            UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {

                User currentUser = documentSnapshot.toObject(User.class);

                String username = TextUtils.isEmpty(currentUser.getUsername()) ? getString(R.string.info_no_username_found) : currentUser.getUsername();
                nameUser.setText(username);

                Glide.with(this)
                        .load(currentUser.getUrlPicture())
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImg);
            });
        }
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

    public void restartMainActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }

    public static int getGoodRadiusDistance (int radius){ return radius*= 100; }
}
