package com.example.fastmessage;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fastmessage.auth.Authorization;
import com.example.fastmessage.bottom_nav.ChatsFragment;
import com.example.fastmessage.bottom_nav.NewChatFragment;
import com.example.fastmessage.bottom_nav.ProfileFragment;
import com.example.fastmessage.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Authorization.class));
        }

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new ChatsFragment()).commit();

        binding.bottomNav.setSelectedItemId(R.id.chats);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.chats, new ChatsFragment());
        fragmentMap.put(R.id.new_chat, new NewChatFragment());
        fragmentMap.put(R.id.profile, new ProfileFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();
            return true;
        });


    }
}