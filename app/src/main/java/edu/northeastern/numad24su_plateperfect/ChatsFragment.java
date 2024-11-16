package edu.northeastern.numad24su_plateperfect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.northeastern.numad24su_plateperfect.firebase.FirebaseUtil;

public class ChatsFragment extends Fragment implements IMessageDisplayListener {

    private RecyclerView recyclerview;
    private RecyclerView.Adapter recyclerviewAdapter;
    private DatabaseReference userdatabaseReference;
    private ArrayList<User> usersList;
    private String currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getString("currentUser");
            FirebaseUtil.setCurrentUser(currentUser);
        } else {
            // Retrieve currentUser from your source if not in savedInstanceState
            currentUser = FirebaseUtil.getCurrentUser();
        }
        usersList = new ArrayList<>();
        userdatabaseReference = FirebaseDatabase.getInstance().getReference("users");

        recyclerview = view.findViewById(R.id.chatsRecyclerView);
        populateRecyclerView();
        recyclerviewAdapter = new ChatsActivityAdapter(getContext(), usersList, this);

        recyclerview.setAdapter(recyclerviewAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
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
                usersList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    usersList.add(user);
                }
                recyclerviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserClicked(int pos, String string) {
        User user = usersList.get(pos);
        Intent intent = new Intent(getContext(), MessageActivity.class);
        passUserModelAsIntent(intent, user);
        startActivity(intent);
    }

    private void passUserModelAsIntent(Intent intent, User user) {
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("sender", user.getUsername());
    }
}
