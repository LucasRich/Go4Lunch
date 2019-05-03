package com.lucas.go4lunch.Controllers.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucas.go4lunch.Models.ProfileFile.User;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.AlarmReceiver;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Utils.UserHelper;

import java.util.Locale;
import java.util.UUID;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.switch_notification) Switch switchNotification;
    @BindView(R.id.settings_img_profile) ImageView profileImg;
    @BindView(R.id.settings_name_user) TextView nameUser;
    @BindView(R.id.container) RelativeLayout container;
    @BindView(R.id.progressBar) RelativeLayout progressBar;

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private Uri uriImageSelected;

    private static final int RC_CHOOSE_PHOTO = 200;
    private Calendar calendar;
    public final static int radius = SharedPref.read(SharedPref.radius, 300);

    public int valueHour;
    public int valueMin;

    Locale mLocale;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPref.init(this);

        progressBar.setVisibility(View.INVISIBLE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
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
        changeUserNameDialog.setTitle(getString(R.string.change_username));

        //DECLARATION
        EditText userNameText = (EditText)layout.findViewById(R.id.userNameText);

        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {

            //INIT
            User currentUser = documentSnapshot.toObject(User.class);
            userNameText.setText(currentUser.getUsername());
        });

        //BUTTON
        changeUserNameDialog.setPositiveButton(getString(R.string.message_save), (dialog, which) -> {
            UserHelper.updateUsername(userNameText.getText().toString(), this.getCurrentUser().getUid());
            restartMainActivity();
        });
        changeUserNameDialog.setNegativeButton(getString(R.string.message_cancel), (dialog, which) -> { });

        //DISPLAY DIALOG
        changeUserNameDialog.create().show();

    }

    @OnClick(R.id.changeProfilePicture_view)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onCLickChangeProfilePicture(){
        this.choosePictureFromPhone();
    }

    @OnClick(R.id.deleteAccount_view)
    public void onCLickDeleteAccount(){
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_account_title))
                .setMessage(getString(R.string.delete_account_message))
                .setPositiveButton(getString(R.string.message_ok), (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton(getString(R.string.message_cancel), (dialog, which) -> {
                }).show();
    }

    @OnClick(R.id.switch_notification)
    public void onClickNotificationSwitch(){
        if (switchNotification.isChecked()){
            SharedPref.write(SharedPref.getNotificationActived, true);
        } else {
            SharedPref.write(SharedPref.getNotificationActived, false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick(R.id.notification_view)
    public void onClickNotification(){
        //CREATION
        AlertDialog.Builder numberPickerDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.select_hour_notification, (ViewGroup)findViewById(R.id.numberPickerView));
        numberPickerDialog.setView(layout);
        numberPickerDialog.setTitle(getString(R.string.select_hour_notification));

        //DECLARATION
        TimePicker timePicker = (TimePicker) layout.findViewById(R.id.timePickerNotification);

        //INIT INPUT
        timePicker.setHour(SharedPref.read(SharedPref.notificationHour, 12));
        timePicker.setMinute(SharedPref.read(SharedPref.notificationMin, 0));

        //BUTTON
        numberPickerDialog.setPositiveButton(getString(R.string.message_save), (dialog, which) -> {
            SharedPref.write(SharedPref.notificationHour, timePicker.getHour());
            SharedPref.write(SharedPref.notificationMin, timePicker.getMinute());
            startNotificationAtMidday();
        });
        numberPickerDialog.setNegativeButton(getString(R.string.message_cancel), (dialog, which) -> { });

        //DISPLAY DIALOG
        numberPickerDialog.create().show();
    }

    @OnClick(R.id.changeDistanceRadius_view)
    public void onClickChangeDistanceRadius(){
        //CREATION
        AlertDialog.Builder seekBarDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.seek_bar_radius, (ViewGroup)findViewById(R.id.seekBarLayout));
        seekBarDialog.setView(layout);
        seekBarDialog.setTitle(getString(R.string.change_radius));

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
        seekBarDialog.setPositiveButton(getString(R.string.message_save), (dialog, which) -> {
            SharedPref.write(SharedPref.radius, getGoodRadiusDistance(seekBarSetting.getProgress()));
            restartMainActivity();
        });
        seekBarDialog.setNegativeButton(getString(R.string.message_cancel), (dialog, which) -> { });

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
                      .setTitle(getString(R.string.select_language));

        //DECLARATION
        RelativeLayout frFlagLayout = (RelativeLayout)layout.findViewById(R.id.fr_flag_layout);
        RelativeLayout gbFlagLayout = (RelativeLayout)layout.findViewById(R.id.gb_flag_layout);

        //BUTTON VIEW
        frFlagLayout.setOnClickListener(v -> {
            setLocale("fr");
            SharedPref.write(SharedPref.currentLanguage, "fr");
        });

        gbFlagLayout.setOnClickListener(v -> {
            setLocale("en");
            SharedPref.write(SharedPref.currentLanguage, "en");
        });

        //DISPLAY DIALOG
        languageDialog.create().show();
    }

    @OnClick(R.id.updateNote_view)
    public void onClickUpdateNote(){
        uploadPhotoInFirebase(this.getCurrentUser().getUid());
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
    // PERMISSION
    // -------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2 - Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // --------------------
    // HANDLER
    // --------------------

    private void handleResponse(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.profileImg);
                uploadPhotoInFirebase(this.getCurrentUser().getUid());
                waitUploadPicture();
            } else {
                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // -------------------
    // UTILS
    // -------------------

    private void choosePictureFromPhone(){
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void uploadPhotoInFirebase(String userId) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        mImageRef.putFile(this.uriImageSelected)
                .addOnSuccessListener(this, taskSnapshot -> {
                    mImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        UserHelper.updateUrlPicture(uri.toString(), userId);
                        restartMainActivity();
                    });
                })
                .addOnFailureListener(this.onFailureListener());
    }

    private void waitUploadPicture() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        container.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

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

    public void deleteAccount(){
        if (this.getCurrentUser() != null) {

            UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());

            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
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
            Toast.makeText(this, getString(R.string.language_already_selected), Toast.LENGTH_SHORT).show();
        }
    }

    private void startNotificationAtMidday(){
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY,SharedPref.read(SharedPref.notificationHour, 12));
        calendar.set(java.util.Calendar.MINUTE, SharedPref.read(SharedPref.notificationMin, 0));

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void restartMainActivity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
    }

    public static int getGoodRadiusDistance (int radius){ return radius*= 100; }

    // -------------------
    // LIFE CYCLE
    // -------------------


    @Override
    protected void onStart() {
        super.onStart();
        if (SharedPref.read(SharedPref.getNotificationActived, true)){
            switchNotification.setChecked(true);
        }
    }
}
