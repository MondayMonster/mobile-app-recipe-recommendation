package edu.northeastern.numad24su_plateperfect;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import edu.northeastern.numad24su_plateperfect.firebase.FirebaseUtil;

public class MessageActivity extends AppCompatActivity {
    // Declare the launcher at the top of your Activity/Fragment:

    private String currentUser;
    private String otherUser;
    private String chatroomId;
    private RecyclerView recyclerView;
    private MessageActivityAdapter messageActivityAdapter;
    private ArrayList<ChatMessage> chatMessageList;
    private TextView senderTextView;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_message);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        senderTextView =  findViewById(R.id.senderTV);


        Intent intent = getIntent();
        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getString("currentUser");
            FirebaseUtil.setCurrentUser(currentUser);
        } else {
            // Retrieve currentUser from your source if not in savedInstanceState
            currentUser = FirebaseUtil.getCurrentUser();
        }
        //get UserModel
        otherUser = getUserModelFromIntent(intent);
       // otherUser = "Shashank";
        chatroomId = getChatroomId(currentUser, otherUser);
        senderTextView.setText(otherUser);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener((v)->{
            getOnBackPressedDispatcher().onBackPressed();
        });
        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", currentUser);
    }

    private void setupChatRecyclerView() {
        //setup a recipe messages list shared by other users as a recycler view
        //attached to recylerview adapter
        chatMessageList =  new ArrayList<ChatMessage>();
        messageActivityAdapter = new MessageActivityAdapter(this, chatMessageList, currentUser);
        recyclerView = findViewById(R.id.messagesRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(messageActivityAdapter);

        // Listen for new chat messages
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chatrooms/"+chatroomId + "/chats");
        chatRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chatMessageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    chatMessageList.add(message);
                }
                // Reverse the list to display messages in descending order
                Collections.reverse(chatMessageList);
                messageActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private void getOrCreateChatroomModel() {
        DatabaseReference chatroomRef = FirebaseDatabase.getInstance().getReference("chatrooms/" + chatroomId);
        chatroomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Create new chatroom
                    ChatRoom chatRoom = new ChatRoom(currentUser, otherUser);
                    chatroomRef.setValue(chatRoom);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private String getChatroomId(String currentUser, String otherUser) {
        if(currentUser.hashCode()<otherUser.hashCode()){
            return currentUser+"_"+otherUser;
        }else{
            return otherUser+"_"+currentUser;
        }
    }

    private String getUserModelFromIntent(Intent intent) {
        return intent.getStringExtra("sender");
    }
}