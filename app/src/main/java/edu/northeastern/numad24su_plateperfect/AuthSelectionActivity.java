package edu.northeastern.numad24su_plateperfect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AuthSelectionActivity extends AppCompatActivity {

    private Button btnSignUp;
    private Button btnLogin;
    private TextView tvWelcome;

    // Messages to display in sequence
    private String[] messages = {
            "Welcome to Plate Perfect",
            "Your Culinary Journey Awaits!",
            "Join Us and Discover New Recipes!"
    };
    private int messageIndex = 0;  // Index for the current message
    private int charIndex = 0;  // Index for the current character in the message
    private long typeDelay = 100;  // Delay in milliseconds between typing each character
    private long pauseDelay = 1000;  // Pause before starting to delete
    private long deleteDelay = 50;  // Delay in milliseconds between deleting each character

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_selection);

        // Find the TextView and Buttons
        tvWelcome = findViewById(R.id.tvWelcome);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(AuthSelectionActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AuthSelectionActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Start the typewriter animation
        startTypewriterAnimation();
    }

    private void startTypewriterAnimation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (charIndex < messages[messageIndex].length()) {
                    // Type the next character
                    tvWelcome.setText(messages[messageIndex].substring(0, charIndex + 1));
                    tvWelcome.setVisibility(View.VISIBLE);
                    charIndex++;
                    handler.postDelayed(this, typeDelay);
                } else {
                    // Pause before starting to delete
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startDeleteAnimation();
                        }
                    }, pauseDelay);
                }
            }
        }, typeDelay);
    }

    private void startDeleteAnimation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (charIndex > 0) {
                    // Delete the last character
                    tvWelcome.setText(messages[messageIndex].substring(0, charIndex - 1));
                    charIndex--;
                    handler.postDelayed(this, deleteDelay);
                } else {
                    // Move to the next message
                    messageIndex++;
                    if (messageIndex < messages.length) {
                        startTypewriterAnimation();
                    }else {
                        // Reset to the first message and repeat the cycle
                        messageIndex = 0;
                        startTypewriterAnimation();
                    }
                }
            }
        }, deleteDelay);
    }
}
