package com.lucas.go4lunch.Controllers.Activities;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lucas.go4lunch.R;
import com.lucas.go4lunch.Utils.SharedPref;
import com.lucas.go4lunch.Utils.UserHelper;

import java.util.Arrays;
import java.util.Locale;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.connexion_activity_relative_layout) RelativeLayout relativeLayout;

    private static final int RC_SIGN_IN = 123;

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPref.init(this);

        if (this.isCurrentUserLogged()){
            this.startMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.main_activity_button_login_email)
    public void onClickLoginEmailButton() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

    @OnClick(R.id.main_activity_button_login_facebook)
    public void onClickLoginFacebookButton() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

    @OnClick(R.id.main_activity_button_login_google)
    public void onClickLoginGoogleButton() {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.LoginTheme)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build())) // SUPPORT GOOGLE
                            .setIsSmartLockEnabled(false, true)
                            .setLogo(R.drawable.ic_logo_auth)
                            .build(),
                    RC_SIGN_IN);
    }

    @OnClick(R.id.main_activity_button_login_twitter)
    public void onClickLoginTwitterButton() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build())) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo_auth)
                        .build(),
                RC_SIGN_IN);
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createUserInFirestore(){
        UserHelper.getUsersCollection().document(this.getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    System.out.println(getString(R.string.error_account_already_create));
                } else {
                    String dayRestaurant = "none";

                    if (this.getCurrentUser() != null) {

                        String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : "https://firebasestorage.googleapis.com/v0/b/go4lunch-236014.appspot.com/o/Capture%20d%E2%80%99e%CC%81cran%202019-05-03%20a%CC%80%2015.05.59.png?alt=media&token=f128a5b9-a5e7-4ed9-9884-514ccf83fb83";
                        String username = this.getCurrentUser().getDisplayName();
                        String uid = this.getCurrentUser().getUid();
                        String email = this.getCurrentUser().getEmail();

                        UserHelper.createUser(uid, username, urlPicture, email, dayRestaurant).addOnFailureListener(this.onFailureListener());
                        System.out.println(getString(R.string.user_add_firestore));
                    }
                }

            } else { System.out.println("get failed with " + task.getException()); }
        });
    }


    // --------------------
    // UI
    // --------------------

    private void showSnackBar(RelativeLayout relativeLayout, String message){
        Snackbar.make(relativeLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // --------------------
    // UTILS
    // --------------------

    // 3 - Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS

                this.createUserInFirestore();
                showSnackBar(this.relativeLayout, getString(R.string.connection_succeed));
                this.startMainActivity();

            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.relativeLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.relativeLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.relativeLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void startMainActivity(){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(SharedPref.read(SharedPref.currentLanguage, "en"));
        res.updateConfiguration(conf, dm);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
