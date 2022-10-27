package com.example.logintugasandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final String TAG = "RegisterActivity";

    private EditText mEdtEmail, mEdtPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEdtEmail = findViewById(R.id.et_username);
        mEdtPassword = findViewById(R.id.et_password);


        findViewById(R.id.et_username).setOnClickListener( this);
        findViewById(R.id.et_password).setOnClickListener(this);
        findViewById(R.id.btn_register).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            Log.d(TAG, "onAuthStateChanged: need to be update ui");
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:
                try {
                    createAccount(mEdtEmail.getText().toString(), mEdtPassword.getText().toString());
                }catch (Error err) {
                    Log.d(TAG, "onClick() returned: " + err);
                    Toast.makeText(RegisterActivity.this, "Register Error",Toast.LENGTH_LONG ).show();
                }
                break;
            case R.id.btn_login:
                toLoginActivity();
            default:
                Log.d(TAG, "onClick: not support");
        }
    }

    private void toLoginActivity() {
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(switchActivityIntent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {
                    FirebaseAuthUserCollisionException exception =
                            (FirebaseAuthUserCollisionException)task.getException();
                    Log.d(TAG, "login() returned: " + exception.getErrorCode());
                }

                Log.d(TAG, "onComplete: Success create Account");
                Log.d(TAG, "onComplete() returned: " + task.getResult());
                Toast.makeText(RegisterActivity.this, "Register Success",Toast.LENGTH_LONG ).show();
                toLoginActivity();
            }
        });
    }
}
