package com.example.fastmessage.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fastmessage.MainActivity;
import com.example.fastmessage.R;
import com.example.fastmessage.databinding.ActivityAuthorizationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Authorization extends AppCompatActivity {

    private ActivityAuthorizationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthorizationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.editMail.getText().toString().isEmpty() || binding.editPassword.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Не все поля заполнены!", Toast.LENGTH_SHORT).show();
                } else {
                    login(binding.editMail.getText().toString(), binding.editPassword.getText().toString());
                }
            }
        });

        binding.linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Authorization.this, Registration.class));
            }
        });

    }

    private void login(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Authorization.this, MainActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Неверные учетные данные!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}