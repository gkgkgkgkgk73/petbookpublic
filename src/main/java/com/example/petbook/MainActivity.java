package com.example.petbook;

import static android.view.View.GONE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.SignInButton;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static Context mainActivityContext;
    SharedPreferences sp;
    private DatabaseReference mDatabase;
//
//    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
//            new FirebaseAuthUIActivityResultContract(),
//            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
//                @Override
//                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
//                    onSignInResult(result);
//                }
//            }
//    );

    // Google Sign In API와 호출할 구글 로그인 클라이언트
    GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";
    View signIn;
//    View kakaoLogin;
    FirebaseAuth mAuth;

//    boolean isKakaoUser = false;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    ReadAndWriteSnippets readAndWriteSnippets = new ReadAndWriteSnippets(database.getReference());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityContext = this;
//        KakaoSdk.init(this, "61bc25e1e353efd6cccdcfd47fa145f4");

        signIn = findViewById(R.id.sign_in_button);
//        kakaoLogin = findViewById(R.id.kakao_sign_in_button);
//        kakaoLogin.setOnClickListener(this::onClick);
        signIn.setOnClickListener(this::onClick);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(//TODO)
                .requestEmail()
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
//            case R.id.kakao_sign_in_button:
//
//                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
//
//                    UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this, kakaoCallback);
//                } else {
//                    UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this, kakaoCallback);
//                }
//
//                break;
            case R.id.sign_in_button:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);
                break;
        }
    }

//    public void createSignInIntent() {
//        // [START auth_fui_create_intent]
//        // Choose authentication providers
//        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.GoogleBuilder().build());
//
//        // Create and launch sign-in intent
//        Intent signInIntent = AuthUI.getInstance()
//                .createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .build();
////        signInLauncher.launch(signInIntent);
//        // [END auth_fui_create_intent]
//    }

    //TODO user 정보 받아서 쓰기
    private void uploadUserDB(String userID, String name, String email,Uri photo){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        DatabaseReference usersRef = ref.child("users");
        usersRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                com.example.petbook.User user = snapshot.getValue(com.example.petbook.User.class);
                if(user!=null) {
                    Log.d("Login","user exists! not upload");
                }
                else {
                    readAndWriteSnippets.writeNewUserWithTaskListeners(userID, name, email, photo);
                    Log.d("Login", "upload userDB!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void delete() {
        // [START auth_fui_delete]

        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_delete]
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
        }
        // [END check_current_user]
    }

    public void getProviderData() {
        // [START get_provider_data]
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
            }
        }
        // [END get_provider_data]
    }

    public void updateProfile() {
        // [START update_profile]
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .setPhotoUri(userPhotoUrl)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
        // [END update_profile]
    }

    public void updateEmail() {
        // [START update_email]
        FirebaseUser user = mAuth.getCurrentUser();

        user.updateEmail(user.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });
        // [END update_email]
    }

    public void updatePassword() {
        // [START update_password]
        FirebaseUser user = mAuth.getCurrentUser();
        //TODO 여기 입력창에 받는것 설정해야함
        String newPassword = "SOME-SECURE-PASSWORD";

        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
        // [END update_password]
    }


    public void sendPasswordReset() {
        // [START send_password_reset]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //TODO 비밀번호 리셋할 때 이메일 받는 것 만들어야함
        String emailAddress = "user@example.com";

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
        // [END send_password_reset]
    }

    public void deleteUser() {
        // [START delete_user]
        FirebaseUser user = mAuth.getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
        // [END delete_user]
    }


    public void linkAndMerge(AuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // [START auth_link_and_merge]
        FirebaseUser prevUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser currentUser = task.getResult().getUser();
                        // Merge prevUser and currentUser accounts and data
                        // ...
                    }
                });
        // [END auth_link_and_merge]
    }


    String userName;
    Uri userPhotoUrl;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                userName = account.getDisplayName();
                userPhotoUrl = account.getPhotoUrl();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mAuth.updateCurrentUser(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]




    private void updateUI(@Nullable FirebaseUser user) {

        if(user!=null) {
            String userID = user.getUid();
            //전역변수와 SP에 로그인 여부와 userID 저장하기
            saveLogin();
            saveUserID(userID);
            saveUserName(userName);

            Log.d("userName:",userName+"/"+user.getEmail());
            //TODO 여기 변경함(경민)
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            DatabaseReference usersRef = ref.child("users");
            usersRef.child(userID).child("alarmTime").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String alarmTime = snapshot.getValue(String.class);
                    if (alarmTime != null) {
                        Log.d("Login", "user exists");
                        uploadUserDB(userID, userName, user.getEmail(), userPhotoUrl);
                        Intent intent = new Intent(getApplicationContext(), MakeTabActivity.class);
                        usersRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                com.example.petbook.User userInfo = snapshot.getValue(com.example.petbook.User.class);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("petName", userInfo.petName);
                                editor.putString("petSpecies", userInfo.petSpecies);
                                editor.putString("petBD", userInfo.petBD);
                                editor.putString("petGender", userInfo.petGender);
                                editor.putString("alarmTime", userInfo.alarmTime);
                                editor.commit();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        intent.putExtra("userID", userID);
                        startActivity(intent);
                    } else {
                        Log.d("Login", userID + " not exists");
                        uploadUserDB(userID,userName, user.getEmail(), userPhotoUrl);
                        Intent intent = new Intent(getApplicationContext(), SelectAnimalActivity.class);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{

        }
    }

    // Login 정보를 SharedPreferences에 저장하기
    public void saveLogin(){
        sp = getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putBoolean("isLogin", true);
        editor.commit();
    }


    // UserID와 name을 sharedPreferences에 저장하기
    public void saveUserID(String s){
        sp = getSharedPreferences("sp", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userID", s);
        editor.commit();
    }

    public void saveUserName(String s){
        sp = getSharedPreferences("sp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userName", s);
        editor.commit();
    }
