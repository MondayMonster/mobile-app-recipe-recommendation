package edu.northeastern.numad24su_plateperfect;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;

import edu.northeastern.numad24su_plateperfect.firebase.FirebaseUtil;

public class ChatsActivity extends AppCompatActivity implements IMessageDisplayListener {

    private RecyclerView recyclerview;
    private RecyclerView.Adapter recyclerviewAdapter;
    private DatabaseReference userdatabaseReference;
    private ArrayList<User> usersList;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate();
        Intent intent = getIntent();
        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getString("currentUser");
            FirebaseUtil.setCurrentUser(currentUser);
        } else {
            // Retrieve currentUser from your source if not in savedInstanceState
            currentUser = FirebaseUtil.getCurrentUser();
        }
        //currentUser = "Shashank";
        usersList = new ArrayList<User>();
        userdatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        recyclerview = findViewById(R.id.chatsRecyclerView);
        populateRecyclerView();
        recyclerviewAdapter = new ChatsActivityAdapter(this, usersList, this);

        recyclerview.setAdapter(recyclerviewAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", currentUser);
    }
    private void populateRecyclerView() {
        userdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();  // Clear the list before adding new data
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.d("ChatsActivity", "User: " + userSnapshot.getValue());
                    User user = userSnapshot.getValue(User.class);
                    usersList.add(user);
                }
//                usersList.add(new User("shank"));
//                usersList.add(new User("Test"));
                recyclerviewAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the ListView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatsActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inflate() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chats);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onUserClicked(int pos, String string) {
        // send the username of the user clicked
        // send the current user logged in
        // Based sender and receiver activity should fetch the message history between them
        // recipe ID shared , should have intent which open the recipe id from it.
        User user = usersList.get(pos);
        //navigate to chat activity
        Intent intent = new Intent(this, MessageActivity.class);
        passUserModelAsIntent(intent, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private void passUserModelAsIntent(Intent intent, User user) {
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("sender", user.getUsername());
//        intent.putExtra("phone",user.getPhone());
//        intent.putExtra("userId",model.getUserId());
//        intent.putExtra("fcmToken",model.getFcmToken());
    }
}