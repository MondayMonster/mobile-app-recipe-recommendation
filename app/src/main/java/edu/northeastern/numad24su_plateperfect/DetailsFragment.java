package edu.northeastern.numad24su_plateperfect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {

    private static final String ARG_RECIPE_NAME = "recipe_name";
    private static final String ARG_RECIPE_DESCRIPTION = "recipe_description";
    private String recipeName;
    private String recipeDescription;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String recipeName, String recipeDescription) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_NAME, recipeName);
        args.putString(ARG_RECIPE_DESCRIPTION, recipeDescription);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeName = getArguments().getString(ARG_RECIPE_NAME);
            recipeDescription = getArguments().getString(ARG_RECIPE_DESCRIPTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        TextView detailsTextView = view.findViewById(R.id.details_text);

        // Set details text
        detailsTextView.setText(recipeDescription);

        return view;
    }
}
