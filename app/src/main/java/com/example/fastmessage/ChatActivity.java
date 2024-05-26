package com.example.fastmessage;

import android.os.Bundle;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fastmessage.databinding.ActivityChatBinding;
import com.example.fastmessage.message.Message;
import com.example.fastmessage.message.MessagesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("database");
    final DatabaseReference chatsRef = ref.child("chats");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String chatID = getIntent().getStringExtra("chatId");

        loadMessages(chatID);

        binding.sendMessageBtn.setOnClickListener(view -> {
            String message = binding.messageEt.getText().toString();
            if(message.isEmpty()){
                Toast.makeText(this, "Поле сообщения не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String date = simpleDateFormat.format(new Date());

            binding.messageEt.setText(""); // clear EditText
            sendMessage(chatID, message, date);
        });
    }

    private void sendMessage(String chatID,String message, String date) {
        if(chatID == null) return;

        HashMap<String, String> messageInfo = new HashMap<>();
        messageInfo.put("text", message);
        messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        messageInfo.put("date", date);

        chatsRef.child(chatID).child("messages").push().setValue(messageInfo);

    }

    private  void loadMessages(String chatID) {
        if(chatID == null) return;
        chatsRef.child(chatID).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                List<Message> messages = new ArrayList<Message>();
                for(DataSnapshot messageSnapshot : snapshot.getChildren()){
                    String messageId = messageSnapshot.getKey();
                    String ownerId = Objects.requireNonNull(messageSnapshot.child("ownerId").getValue()).toString();
                    String textMessage = Objects.requireNonNull(messageSnapshot.child("text").getValue()).toString();
                    String dateMessage = Objects.requireNonNull(messageSnapshot.child("date").getValue()).toString();

                    messages.add(new Message(messageId, ownerId, textMessage, dateMessage));
                }

                binding.messageRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                binding.messageRv.setAdapter(new MessagesAdapter(messages));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}