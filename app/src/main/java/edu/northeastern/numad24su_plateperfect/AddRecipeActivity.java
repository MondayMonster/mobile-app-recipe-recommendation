package edu.northeastern.numad24su_plateperfect;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText recipeNameEditText;
    private EditText descriptionEditText;
    private EditText ingredientsEditText;
    private EditText instructionsEditText;
    private Button addRecipeButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate();
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        recipeNameEditText = findViewById(R.id.recipeName);
        descriptionEditText = findViewById(R.id.description);
        ingredientsEditText = findViewById(R.id.ingredients);
        instructionsEditText = findViewById(R.id.instructions);
        addRecipeButton = findViewById(R.id.addRecipeButton);

        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipe();
            }
        });

    }
    private void inflate() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addRecipe() {
        String recipeName = recipeNameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String ingredientsString = ingredientsEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();

        if (recipeName.isEmpty() || description.isEmpty() || ingredientsString.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(AddRecipeActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
        } else {
            String recipeId = databaseReference.push().getKey();

            // Parse ingredients
            Map<String, String> ingredients = parseIngredients(ingredientsString);
            if(ingredients.isEmpty()){
                Toast.makeText(AddRecipeActivity.this, "Looks like you didn't added the ingredients correctly !!", Toast.LENGTH_SHORT).show();
                return;
            }
            //Recipe recipe = new Recipe(recipeId, recipeName, description, ingredients, instructions);
            //test
            Recipe recipe = new Recipe();
            if (recipeId != null) {
                databaseReference.child(recipeId).setValue(recipe);
                Toast.makeText(AddRecipeActivity.this, "Recipe added successfully!", Toast.LENGTH_SHORT).show();
                // Optionally, clear the input fields
                recipeNameEditText.setText("");
                descriptionEditText.setText("");
                ingredientsEditText.setText("");
                instructionsEditText.setText("");
            } else {
                Toast.makeText(AddRecipeActivity.this, "Failed to add recipe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Map<String, String> parseIngredients(String ingredientsString) {
        Map<String, String> ingredients = new HashMap<>();
        String[] pairs = ingredientsString.split("\n");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                ingredients.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return ingredients;
    }
}
