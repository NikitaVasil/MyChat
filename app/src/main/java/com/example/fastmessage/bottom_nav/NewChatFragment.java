package com.example.fastmessage.bottom_nav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fastmessage.databinding.FragmentChatsBinding;
import com.example.fastmessage.databinding.FragmentNewChatBinding;
import com.example.fastmessage.users.User;
import com.example.fastmessage.users.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class NewChatFragment extends Fragment {

    private FragmentNewChatBinding binding;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("database");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);
        loadUsers();

        return binding.getRoot();
    }

    private void loadUsers() {
        ArrayList<User> users = new ArrayList<User>();

        DatabaseReference usersRef = ref.child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()){
                    if(userSnapshot.getKey().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                        continue;
                    }

                    String userId = userSnapshot.getKey();
                    String username = Objects.requireNonNull(userSnapshot.child("username").getValue()).toString();
                    String profileImage = Objects.requireNonNull(userSnapshot.child("profileImage").getValue()).toString();

                    users.add(new User(userId,username, profileImage));
                }

                binding.usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.usersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                binding.usersRv.setAdapter(new UserAdapter(users));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
