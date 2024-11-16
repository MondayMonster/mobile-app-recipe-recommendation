package edu.northeastern.numad24su_plateperfect;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private EditText searchBox;
    private RecyclerView searchResultsRecyclerView;
    private SearchResultAdapter searchResultAdapter;
    private List<rvChildModelClass> recipeList;
    private List<rvChildModelClass> allRecipesList;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBox = findViewById(R.id.search_box);
        searchBox.requestFocus(); // Focus on EditText when activity starts

        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);
        recipeList = new ArrayList<>();
        allRecipesList = new ArrayList<>();

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("PlatePerfect");

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchResultAdapter = new SearchResultAdapter(this, recipeList);
        searchResultsRecyclerView.setAdapter(searchResultAdapter);
        // Load all data initially
        loadAllRecipes();


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipesWithRegex(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllRecipes() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allRecipesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    rvChildModelClass recipe = snapshot.getValue(rvChildModelClass.class);
                    if (recipe != null) {
                        allRecipesList.add(recipe);
                    }
                }
                filterRecipesWithRegex(searchBox.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors.
            }
        });
    }

    private void filterRecipesWithRegex(String query) {
        recipeList.clear();
        if (!query.isEmpty()) {
            // Use regex pattern to filter the recipes
            Pattern pattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
            for (rvChildModelClass recipe : allRecipesList) {
                String recipeName = recipe.getName();
                if (recipeName != null && pattern.matcher(recipeName).find()) {
                    recipeList.add(recipe);
                }
            }
        } else {
            recipeList.addAll(allRecipesList); // Show all recipes if search query is empty
        }
        searchResultAdapter.updateList(recipeList);
    }
}
