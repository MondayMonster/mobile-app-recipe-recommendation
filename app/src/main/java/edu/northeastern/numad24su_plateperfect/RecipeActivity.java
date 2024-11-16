package edu.northeastern.numad24su_plateperfect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad24su_plateperfect.firebase.FirebaseUtil;

public class RecipeActivity extends AppCompatActivity {

    private static final String TAG = "RecipeActivity";
    private rvChildModelClass recipe;
    private boolean isLiked = false;
    private List<String> userNames = new ArrayList<>();
    private String currentUser;
    private DatabaseReference mdatabase;
    private ImageView recipeImage;
    private TextView recipeName;
    private ImageButton likeButton;
    private String fetchedRecipeName;
    private String fetchedDescription;
    private GestureDetector gestureDetector;
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_TIME_DELTA = 300; //milliseconds

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getString("currentUser");
            FirebaseUtil.setCurrentUser(currentUser);
        } else {
            // Retrieve currentUser from your source if not in savedInstanceState
            currentUser = FirebaseUtil.getCurrentUser();
        }
        fetchUserNames();
//        // Initialize the recipe (this should be retrieved from your database or passed via intent)
//        Map<String, String> ingredients = new HashMap<>();
//        ingredients.put("Rice", "2 bowls");
//        ingredients.put("Onion", "2");
//        ingredients.put("Tomatoes", "2");
//        recipe = new Recipe("1", "Pasta", "Delicious homemade pasta", ingredients, "Step by step instructions");


        // Set up UI elements
        recipeImage = findViewById(R.id.recipe_image);
        likeButton = findViewById(R.id.like_button);
        ImageButton shareButton = findViewById(R.id.share_button);

        recipeName = findViewById(R.id.recipe_name);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ImageButton sendButton = findViewById(R.id.send_button);

        // Send to user
        sendButton.setOnClickListener(v -> {
            sendThisRecipe();
        });


        recipeImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    long currentTime = System.currentTimeMillis();
                    long tapTimeInterval = currentTime - lastTapTime;

                    if (tapTimeInterval < DOUBLE_TAP_TIME_DELTA) {
                        Log.d(TAG, "Double tap detected");
                        likeTheRecipe();
                    } else {
                        Log.d(TAG, "Single tap detected");

                    }

                    lastTapTime = currentTime;
                }
                return true;
            }
        });

        //youtube_button
        ImageButton youtube_button = findViewById(R.id.youtube_button);
        youtube_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipe.getVideoUrl()));
                startActivity(intent);
            }
        });

        // Set up like button click listener
        likeButton.setOnClickListener(v -> {
            likeTheRecipe();
        });

        // Set up share button click listener
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipe.shareRecipe(RecipeActivity.this);
            }
        });

    

        // Set up TabLayout and ViewPager
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Ingredients"));
        tabLayout.addTab(tabLayout.newTab().setText("Instructions"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // Initialize Firebase Database reference
        mdatabase = FirebaseDatabase.getInstance().getReference("PlatePerfect");

        // Fetch data from Firebase
        recipe = getIntent().getParcelableExtra("recipe");
        if (recipe == null) {
            fetchThisRecipe();
        } else {
            fetchRecipeData();
            checkInitialLikeStatus();
        }

    }


    private void fetchThisRecipe() {
        Log.d(TAG, "Fetching recipe: " + getIntent().getStringExtra("recipeName"));
        mdatabase.orderByChild("Name").equalTo(getIntent().getStringExtra("recipeName")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(RecipeActivity.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    recipe = snapshot.getValue(rvChildModelClass.class);
                    fetchRecipeData();
                    checkInitialLikeStatus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void likeTheRecipe() {
        isLiked = !isLiked;
        updateLikeStatus(isLiked);
        likeButton.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_unfilled);
    }

    private void fetchRecipeData() {
        String imageLink = recipe.getImage_Link();
        String name = recipe.getName();
        fetchedDescription = recipe.getDescription();
        Log.d(TAG, "Fetched description: " + fetchedDescription);
        Log.d(TAG, "Fetched name: " + name);
        Log.d(TAG, "Fetched image link: " + imageLink);
        if (imageLink != null && name != null) {
            // Update the UI with the fetched data
            Picasso.get().load(imageLink).into(recipeImage);
            recipeName.setText(name);
            fetchedRecipeName = name;

            // Set up TabLayout and ViewPager
            TabLayout tabLayout = findViewById(R.id.tab_layout);
            ViewPager viewPager = findViewById(R.id.view_pager);
            final RecipePagerAdapter adapter = new RecipePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), fetchedRecipeName, fetchedDescription);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            // Check initial like status

        } else {
            Log.e(TAG, "Image link, name, or description is null");
        }
    }

    private void fetchUserNames() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.getKey();
                    userNames.add(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("RecipeActivity", "Error fetching user names", databaseError.toException());
            }
        });
    }

    private void sendThisRecipe() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.send_recipe, null);
        builder.setView(dialogView);

        Spinner usernameSpinner = dialogView.findViewById(R.id.usernameSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(RecipeActivity.this, android.R.layout.simple_spinner_item, userNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usernameSpinner.setAdapter(adapter);

        builder.setView(dialogView);
        builder.setTitle("Whom do you want to send?")
                .setPositiveButton("Send", (dialog, which) -> {
                    String selectedUsername = usernameSpinner.getSelectedItem().toString();
                    if (!selectedUsername.isEmpty()) {
                        sendMessageToUser(selectedUsername);
                    } else {
                        Toast.makeText(this, "Please select a user", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", currentUser);
    }

    private void sendMessageToUser(String selectedUsername) {
        String chatroomId = getChatroomId(currentUser, selectedUsername);
        // Get a reference to the chats node
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chatrooms/" + chatroomId + "/chats");

        // Create a new message object
        ChatMessage message = new ChatMessage(recipe.getName(),
                currentUser, selectedUsername, System.currentTimeMillis());

        // Get a new key for the message
        String messageId = chatsRef.push().getKey();

        // Set the message data
        chatsRef.child(messageId).setValue(message);
        // Get a reference to the latest node for the receiver
        DatabaseReference latestRef = FirebaseDatabase.getInstance().getReference("latest").child(selectedUsername).child(currentUser);

        // Use addListenerForSingleValueEvent to check if the record exists
        latestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The record exists, handle accordingly (e.g., update it or show a message)
                    Log.d("Message", "Record already exists. Update it if necessary.");
                } else {
                    // The record doesn't exist, proceed with setting the message data
                    latestRef.setValue(new ChatMessage("Hello", currentUser, selectedUsername, System.currentTimeMillis()));
                    Log.d("Message", "Record does not exist. Creating a new record.");
                }

                // Set the message data
                latestRef.setValue(message);
                Toast.makeText(RecipeActivity.this, "Message Sent Successfully",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.e("Message", "Error checking if record exists: " + databaseError.getMessage());
            }
        });



    }

    private String getChatroomId(String currentUser, String otherUser) {
        if (currentUser.hashCode() < otherUser.hashCode()) {
            return currentUser + "_" + otherUser;
        } else {
            return otherUser + "_" + currentUser;
        }
    }

    private void updateLikeStatus(boolean liked) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes").child(currentUser).child(recipe.getName());
        likeRef.setValue(liked ? 1 : 0);
    }

    private void checkInitialLikeStatus() {
        Log.d(TAG, "Checking initial like status");
        Log.d(TAG, "Recipe name: " + recipe.getName());
        Log.d(TAG, "Current user: " + currentUser);
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes"+"/"+currentUser);
        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Snapshot: " + snapshot);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                     ) {
                    if(dataSnapshot.getKey().equals(recipe.getName())) {
                        isLiked = true;
                        likeButton.setImageResource(R.drawable.ic_heart_filled);
                        break;
                    }
                    else{
                        isLiked = false;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to check initial like status", error.toException());
            }
        });
    }
}
