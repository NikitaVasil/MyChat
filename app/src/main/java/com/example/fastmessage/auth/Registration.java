package com.example.fastmessage.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fastmessage.MainActivity;
import com.example.fastmessage.R;
import com.example.fastmessage.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("database");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.editLogin.getText().toString().isEmpty() ||
                        binding.editMail.getText().toString().isEmpty() ||
                        binding.editPassword.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.editMail.getText().toString(), binding.editPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DatabaseReference usersRef = ref.child("users");

                                Map<String, String> userInfo = new HashMap<>();
                                userInfo.put("email", binding.editMail.getText().toString());
                                userInfo.put("username", binding.editLogin.getText().toString());

                                usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo);
                                startActivity(new Intent(Registration.this, MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Ошибка в регистрации!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }

}