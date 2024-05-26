package com.example.fastmessage.bottom_nav;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fastmessage.MainActivity;
import com.example.fastmessage.auth.Authorization;
import com.example.fastmessage.databinding.FragmentNewChatBinding;
import com.example.fastmessage.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference("database");
    private FragmentProfileBinding binding;

    private Uri filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);


        loadUserInfo();

        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.logautBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), Authorization.class));
            }
        });

        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!=null){
                        filePath = result.getData().getData();

                        try{
                            Bitmap bitmap = MediaStore.Images.Media
                                    .getBitmap(
                                            requireContext().getContentResolver(),
                                            filePath
                                    );
                            binding.profileImageView.setImageBitmap(bitmap);
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        uploadImage();
                    }
                }
            }
    );

    private void loadUserInfo() {
        DatabaseReference usersRef = ref.child("users");
        usersRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String profileImage = Objects.requireNonNull(snapshot.child("profileImage").getValue()).toString();

                binding.username.setText(username);

                if(!profileImage.isEmpty()) {
                    Glide.with(getContext()).load(profileImage).into(binding.profileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage() {
        if(filePath != null) {
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            FirebaseStorage.getInstance().getReference().child("image/"+uid)
                    .putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Фотография загружена", Toast.LENGTH_SHORT).show();

                            FirebaseStorage.getInstance().getReference().child("image/"+uid).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            DatabaseReference usersRef = ref.child("users");
                                            usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child("profileImage").setValue(uri.toString());
                                        }
                                    });
                        }
                    });
        }
    }
}
