package com.example.socketconnectionwebrtc.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socketconnectionwebrtc.BootStrap.MainActivity;
import com.example.socketconnectionwebrtc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyAccont extends AppCompatActivity {
    Button verifyBut;
    TextView verifyTextView;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String phoneNumber, preFix;
    private PhoneAuthProvider.ForceResendingToken mResponds;
    private static final String TAG = "VerifyAccount";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_accont);

        mAuth = FirebaseAuth.getInstance();
        //FindView
        verifyBut = findViewById(R.id.verifyButton);
        verifyTextView = findViewById(R.id.verifyTextView);

        //GetExtra
        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        System.out.println(phoneNumber);
        preFix = getIntent().getExtras().getString("preFix");


        //Requesting phoneCode
        getVerificationCode(phoneNumber);


        verifyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = verifyTextView.getText().toString();
                verifyVerificationCode(code);
            }
        });
    }

    private void getVerificationCode(String number) {
        Log.d(TAG, "getVerificationCode: Entered getVerificationCode");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                120,
                TimeUnit.SECONDS,
                VerifyAccont.this,
                mCallbacks);

        Log.d(TAG, "getVerificationCode: GOING TO CALLBACK");
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: Verfication");
            //
            String code = phoneAuthCredential.getSmsCode();

            //Start The Verification
            verifyVerificationCode(code);

        }


        @Override
        public void onVerificationFailed(FirebaseException e) {

            Log.d(TAG, "onVerificationFailed: Failed To verify", e);

            Toast.makeText(VerifyAccont.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {

                Log.d(TAG, "onVerificationFailed: Invalid Catch");

            } else if (e instanceof FirebaseTooManyRequestsException) {

                Log.d(TAG, "onVerificationFailed: To many Request at once");
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {

            Log.d(TAG, "OnCodeSent: " + verificationId);

            mVerificationId = verificationId;

            mResponds = token;
        }
    };

    private void verifyVerificationCode(String code) {

        Log.d(TAG, "verifyVerificationCode: Do i ARrive here?");

        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);


            Log.d(TAG, "verifyVerificationCode: Success");

        } catch (Exception e) {

            Log.d(TAG, "verifyVerificationCode: Catched");
            System.out.println(e);
            Toast.makeText(VerifyAccont.this, "Failed " + e, Toast.LENGTH_LONG).show();

        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Intent intent = new Intent(VerifyAccont.this, MainActivity.class);

                            startActivity(intent);

                        } else {


                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
