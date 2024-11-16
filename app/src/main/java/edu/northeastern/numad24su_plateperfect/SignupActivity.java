package edu.northeastern.numad24su_plateperfect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.numad24su_plateperfect.firebase.FirebaseUtil;

public class SignupActivity extends AppCompatActivity {

    private EditText firstName, lastName, username;
    private Button btnSignUp;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        username = findViewById(R.id.editTextUsername);
        btnSignUp = findViewById(R.id.btnSignUp);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnSignUp.setOnClickListener(v -> {
            String first = firstName.getText().toString().trim();
            String last = lastName.getText().toString().trim();
            String user = username.getText().toString().trim();

            if (!first.isEmpty() && !last.isEmpty() && !user.isEmpty()) {
                checkUsernameExists(user, first, last);
            } else {
                Toast.makeText(SignupActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUsernameExists(String username, String firstName, String lastName) {
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Username already exists
                    Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Username does not exist, proceed with user registration
                    addUserToDatabase(username, firstName, lastName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                Toast.makeText(SignupActivity.this, "Error checking username", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserToDatabase(String username, String firstName, String lastName) {
        if (username != null) {
            User newUser = new User(firstName, lastName, username);
            databaseReference.child(username).setValue(newUser)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            // Navigate to next activity
                            Intent intent = new Intent(SignupActivity.this, BottomNavBarActivity.class);
                            intent.putExtra("currentUser", username);
                            FirebaseUtil.setCurrentUser(username);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignupActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
