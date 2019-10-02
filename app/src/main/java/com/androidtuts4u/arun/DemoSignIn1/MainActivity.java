package com.androidtuts4u.arun.DemoSignIn1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SignInButton btnSignIn;
    private Button btnSignout,btnDisconnect;
    private TextView tv_name;
    private ImageView profilPic;
    private GoogleSignInClient googleClient;
    private  static final int RC_SIGN_IN = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignout = findViewById(R.id.btn_signout);
        btnDisconnect = findViewById(R.id.btn_disconnect);
        tv_name = findViewById(R.id.tv_name);
        profilPic = findViewById(R.id.img_profile_pic);
        btnSignIn.setOnClickListener(this);
        btnSignout.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleClient = GoogleSignIn.getClient(this,gso);
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account != null){
            String str = " display name : "+account.getDisplayName() +"\n"+
                    " email        : " +account.getEmail()+"\n"
                    +" name         : " + account.getGivenName();
            Glide.with(this).load(account.getPhotoUrl()).into(profilPic);
            tv_name.setText(str);
            btnSignout.setVisibility(View.VISIBLE);
            profilPic.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnDisconnect.setVisibility(View.VISIBLE);
        }else{
            btnSignout.setVisibility(View.GONE);
            btnDisconnect.setVisibility(View.GONE);
            profilPic.setVisibility(View.GONE);
            btnSignIn.setVisibility(View.VISIBLE);
            tv_name.setText("");
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btn_signout:
                signout();
                break;
            case R.id.btn_disconnect:
                revokeAccess();
                break;
        }

    }

    private void revokeAccess() {
        googleClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);

            }
        });
    }

    private void signout() {
        googleClient.signOut().addOnCompleteListener(this,new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            e.printStackTrace();
            Log.w("mainActivity","failed : "+e.getStatusCode());
            updateUI(null);
        }

    }
}
