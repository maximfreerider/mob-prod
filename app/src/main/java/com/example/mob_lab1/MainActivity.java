package com.example.mob_lab1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    public EditText emailID;
    public EditText password;
    public EditText username;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;
    public int passMinLenght = 8;
    public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.editText2);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailID = findViewById(R.id.editText);
        password = findViewById(R.id.editText3);
        btnSignUp = findViewById(R.id.button);
        tvSignIn = findViewById(R.id.textView);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailID.getText().toString();
                String pwd = password.getText().toString();
                final String userName = username.getText().toString();

                        /*Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExwtra("username", userName);
                        startActivity(intent);
                        finish();*/

                if (email.isEmpty()) {
                    emailID.setError("Please enter your email");
                    emailID.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (userName.isEmpty()) {
                    username.setError("Please enter your username");
                    username.requestFocus();
                } else if (userName.isEmpty() && email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                } else if (password.length() < passMinLenght) {  // Validation of password
                    password.setText("");
                    password.setError("Minimal lengt of password should be 8!");
                } else if (!emailID.getText().toString().trim().matches(emailPattern)) {   // Validation of email
                    emailID.setText("");
                    emailID.setError("Please re-enter your email correctly");
                } else if (!(userName.isEmpty() && email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Sign Up Unsuccesful. Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userName);
                                task.getResult().getUser().updateProfile(builder.build());
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        createNotificationChannel();

    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (NotificationManagerCompat.from(getApplicationContext()).getNotificationChannel("lab_channel") == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String name = "Lab";
                String description = "none";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("lab_channel", name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManagerCompat.from(getApplicationContext())
                        .createNotificationChannel(channel);
            }
        }
    }

}
