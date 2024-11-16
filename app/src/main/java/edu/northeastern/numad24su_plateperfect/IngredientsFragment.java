package edu.northeastern.numad24su_plateperfect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IngredientsFragment extends Fragment {

    private static final String ARG_RECIPE_NAME = "recipe_name";
    private String recipeName;
    private DatabaseReference databaseReference;
    private LinearLayout ingredientsLayout;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    public static IngredientsFragment newInstance(String recipeName) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_NAME, recipeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeName = getArguments().getString(ARG_RECIPE_NAME);
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("recipe_ingredients");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ingredientsLayout = view.findViewById(R.id.ingredients_layout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch and display ingredients after the view has been created
        fetchIngredients();
    }

    private void fetchIngredients() {
        databaseReference.orderByChild("recipe").equalTo(recipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String qty = String.valueOf(snapshot.child("qty").getValue(Object.class));
                    String unit = snapshot.child("unit").getValue(String.class);

                    if (name != null && qty != null && unit != null) {
                        addIngredientToLayout(name, qty, unit);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void addIngredientToLayout(String name, String qty, String unit) {
        // Ensure that the fragment is attached before trying to inflate views
        if (isAdded() && getView() != null) {
            View ingredientView = getLayoutInflater().inflate(R.layout.item_ingredient, ingredientsLayout, false);
            TextView ingredientName = ingredientView.findViewById(R.id.ingredient_name);
            TextView ingredientQty = ingredientView.findViewById(R.id.ingredient_qty);
            TextView ingredientUnit = ingredientView.findViewById(R.id.ingredient_unit);

            ingredientName.setText(name);
            ingredientQty.setText(qty);
            ingredientUnit.setText(unit);

            ingredientsLayout.addView(ingredientView);
        }
    }
}
