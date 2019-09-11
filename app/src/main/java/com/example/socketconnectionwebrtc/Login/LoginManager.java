package com.example.socketconnectionwebrtc.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.socketconnectionwebrtc.R;
import com.google.firebase.auth.FirebaseAuth;


public class LoginManager extends AppCompatActivity {
    private static final String TAG = "LoginManager";
    TextView preFix, phoneNumber;
    Button login;
    private String gatheredPrefix;
    private String gatheredPhoneNumber;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manager);
        mAuth = FirebaseAuth.getInstance();
        preFix = findViewById(R.id.preFix);
        phoneNumber = findViewById(R.id.phoneNumber);
        login = findViewById(R.id.login);


        gatheredPrefix = preFix.getText().toString();
        gatheredPhoneNumber = phoneNumber.getText().toString();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: ONClick here");
                gatheredPrefix = preFix.getText().toString();
                gatheredPhoneNumber = phoneNumber.getText().toString();

                Intent intent = new Intent(LoginManager.this, VerifyAccont.class);
                intent.putExtra("preFix", gatheredPrefix);
                intent.putExtra("phoneNumber", gatheredPhoneNumber);
                Log.d(TAG, "onClick: Starting Intent");
                startActivity(intent);

            }
        });
    }


}


































/*
    public void signUpUser(String preFix, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(preFix, phoneNumber)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginManager.this, "Success", Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginManager.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

 */
