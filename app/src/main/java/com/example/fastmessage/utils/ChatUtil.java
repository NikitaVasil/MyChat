package com.example.fastmessage.utils;

import com.example.fastmessage.users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ChatUtil {

    static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static final DatabaseReference ref = database.getReference("database");
    static final DatabaseReference chatsRef = ref.child("chats");
    static final DatabaseReference userRef = ref.child("users");

    public static void createChat(User user){

        String uID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        HashMap<String, String> chatInfo = new HashMap<>();
        chatInfo.put("userId1", uID);
        chatInfo.put("userId2", user.userId);

        String chatID = generateChatID(uID, user.userId);
        chatsRef.child(chatID).setValue(chatInfo);

        addChatIdToUser(uID, chatID);
        addChatIdToUser(user.userId, chatID);
    }

    private static String generateChatID(String userId1, String userId2){
        String sumUserId1UserId2 = userId1 + userId2;
        char[] charArray = sumUserId1UserId2.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }

    private static void addChatIdToUser(String userID, String chatID ){
        userRef.child(userID).child("chats").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String chats = Objects.requireNonNull(task.getResult().getValue()).toString();
                String chatsUpdate = addIdToStr(chats, chatID);

                userRef.child(userID).child("chats").setValue(chatsUpdate);
            }
        });
    }

    private static String addIdToStr (String str, String chatID){
        str += (str.isEmpty()) ? chatID : (","+chatID);
        return str;
    }
}
