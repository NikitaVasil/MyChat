package com.example.fastmessage.bottom_nav;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fastmessage.chats.Chat;
import com.example.fastmessage.chats.ChatsAdapter;
import com.example.fastmessage.databinding.FragmentChatsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("database");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        loadChats();

        return binding.getRoot();
    }

    private void loadChats() {

        ArrayList<Chat> chats = new ArrayList<>();
        String uID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatsStr = Objects.requireNonNull(snapshot.child("users").child(uID).child("chats").getValue().toString());
                String[] chatsID = chatsStr.split(",");

                for(String chatID : chatsID){
                    DataSnapshot chatSnapshot = snapshot.child("chats").child(chatID);
                    String userId1 = Objects.requireNonNull(chatSnapshot.child("userId1").getValue()).toString();
                    String userId2 = Objects.requireNonNull(chatSnapshot.child("userId2").getValue()).toString();

                    String chatUserId = (uID.equals(userId1))? userId2 : userId1;
                    String chatName = Objects.requireNonNull(snapshot.child("users").child(chatUserId).child("username").getValue()).toString();

                    Chat chat = new Chat(chatID, chatName, userId1, userId2);
                    chats.add(chat);
                }

                binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.chatsRv.setAdapter(new ChatsAdapter(chats));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Не удалось получить доступ к чатам пользователей", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
