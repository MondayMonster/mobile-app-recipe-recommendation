package edu.northeastern.numad24su_plateperfect;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import edu.northeastern.numad24su_plateperfect.databinding.ActivityBottomNavBarBinding;
import edu.northeastern.numad24su_plateperfect.firebase.FirebaseUtil;

public class BottomNavBarActivity extends AppCompatActivity {
    ActivityBottomNavBarBinding binding;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Toast.makeText(this, "Notification access granted !!", Toast.LENGTH_SHORT);
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                    Toast.makeText(this, "Denied !!", Toast.LENGTH_SHORT);
                }
            });
    private String currentUser;
    private DatabaseReference latest;
    private ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityBottomNavBarBinding.inflate(getLayoutInflater());
        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getString("currentUser");
            FirebaseUtil.setCurrentUser(currentUser);
        } else {
            // Retrieve currentUser from your source if not in savedInstanceState
            currentUser = FirebaseUtil.getCurrentUser();
        }
        //currentUser ="test2";

        setContentView(binding.getRoot());
        binding.bottomNavigationView.setSelectedItemId(R.id.homeMenu);
        // Check if the activity is being created for the first time or restored after a configuration change
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            subscribeToUpdates();
        }
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            Bundle bundle = new Bundle();
            bundle.putString("currentUser", currentUser);
            if (item.getItemId() == R.id.homeMenu) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.chatsMenu) {
                selectedFragment = new ChatsFragment();
            } else {
                selectedFragment = new ProfileFragment();
            }
            selectedFragment.setArguments(bundle);
            replaceFragment(selectedFragment);
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        askNotificationPermission();

        //get the fcm token
        getTheFCMToken();
        startListeningForNewMessages();

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", currentUser);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (latest != null && listener != null) {
            latest.removeEventListener(listener);
        }
    }

    private void subscribeToUpdates() {
        FirebaseMessaging.getInstance().subscribeToTopic("newRecipes")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }

                       // Toast.makeText(BottomNavBarActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startListeningForNewMessages() {

        latest = FirebaseDatabase.getInstance().getReference("latest").child(currentUser);
        listener =  getListener();
        latest.addChildEventListener(listener);

    }

    @NonNull
    private ChildEventListener getListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // new child added is new message received

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage msg = snapshot.getValue(ChatMessage.class);
                Intent intent = new Intent(BottomNavBarActivity.this, MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("sender", msg.getSender());
                PendingIntent pendingIntent = PendingIntent.getActivity(BottomNavBarActivity.this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                String channelId = "Default";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(BottomNavBarActivity.this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New Message From " + snapshot.getKey())
                        .setContentText("Hey! Check this new Recipe called " + msg.getMessage()).setAutoCancel(true).setContentIntent(pendingIntent);
                ;
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
                manager.notify((int) msg.getTimestamp(), builder.build());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
    }

    private void getTheFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    private static final String TAG = "FCM TOKEN";

                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d(TAG, token);

                        // Retrieve the username from shared preferences or another source
                        String username = currentUser; // Implement this method to get the username

                        if (username != null) {
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("fcmTokens").child(username);
                            userRef.setValue(token)
                                    .addOnCompleteListener(fcmtask -> {
                                        if (fcmtask.isSuccessful()) {
                                            Log.d(TAG, "Token stored successfully.");
                                        } else {
                                            Log.d(TAG, "Failed to store token: " + task.getException().getMessage());
                                        }
                                    });
                        }
                        //Toast.makeText(BottomNavBarActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frgment_layout, fragment);
        fragmentTransaction.commit();
    }
}