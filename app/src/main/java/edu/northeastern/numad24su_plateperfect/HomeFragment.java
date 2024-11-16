package edu.northeastern.numad24su_plateperfect;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad24su_plateperfect.firebase.FirebaseUtil;

public class HomeFragment extends Fragment {

    RecyclerView rvParentRecyclerView;
    ArrayList<rvParentModelClass> rvParentModelClassArrayList;
    ArrayList<rvChildModelClass> rvTrendingRecipeArrayList;
    ArrayList<rvChildModelClass> rvQuickMealsArrayList;
    ArrayList<rvChildModelClass> rvNewRecipeArrayList;
    ArrayList<rvChildModelClass> rvDinnerArrayList;
    aParentAdapterClass parentAdapterClass;
    private DatabaseReference databaseReferenceImages;
    private List<rvChildModelClass> imageDataList = new ArrayList<>();

    Map<String, ArrayList<rvChildModelClass>> categoryDictionary;
    private static final int RANDOM_RECIPE_COUNT = 10;
    private String currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Retrieve arguments if any
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        View searchBox = view.findViewById(R.id.search_box);
        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getString("currentUser");
            FirebaseUtil.setCurrentUser(currentUser);
        } else {
            // Retrieve currentUser from your source if not in savedInstanceState
            currentUser = FirebaseUtil.getCurrentUser();
        }
        searchBox.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
        return view;
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", currentUser);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReferenceImages = FirebaseDatabase.getInstance().getReference("PlatePerfect");

        rvParentRecyclerView = view.findViewById(R.id.rv_parent);
        rvParentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        rvParentModelClassArrayList = new ArrayList<>();
        categoryDictionary = new HashMap<>();

        fillTheRecyclerView();

        parentAdapterClass = new aParentAdapterClass(rvParentModelClassArrayList, getContext());
        rvParentRecyclerView.setAdapter(parentAdapterClass);

        // Fetch liked recipes for the current user
        // Replace "currentUsername" with the actual username of the logged-in user
        fetchLikedRecipes(currentUser);
    }

    private void fillTheRecyclerView() {
        rvDinnerArrayList = new ArrayList<>();
        rvNewRecipeArrayList = new ArrayList<>();
        rvTrendingRecipeArrayList = new ArrayList<>();
        rvQuickMealsArrayList = new ArrayList<>();

        fetchImagesData(rvNewRecipeArrayList, "New Recipe");
        fetchImagesData(rvDinnerArrayList, "Dinner Ready");
        fetchImagesData(rvTrendingRecipeArrayList, "Trending Recipe");
        fetchImagesData(rvQuickMealsArrayList, "Quick Meals");

        rvParentModelClassArrayList.add(new rvParentModelClass("Trending Recipe", rvTrendingRecipeArrayList));
        rvParentModelClassArrayList.add(new rvParentModelClass("New Recipe", rvNewRecipeArrayList));
        rvParentModelClassArrayList.add(new rvParentModelClass("Dinner Ready", rvDinnerArrayList));
        rvParentModelClassArrayList.add(new rvParentModelClass("Quick Meals", rvQuickMealsArrayList));
    }

    private void fetchImagesData(ArrayList<rvChildModelClass> fillCategoryList, String categoryKey) {
        databaseReferenceImages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    rvChildModelClass imageData = snapshot.getValue(rvChildModelClass.class);
                    imageDataList.add(imageData);
                }

                List<rvChildModelClass> randomRecipes = getRandomRecipes(imageDataList, RANDOM_RECIPE_COUNT);
                fillCategoryList.clear();
                fillCategoryList.addAll(randomRecipes);

                parentAdapterClass.notifyDataSetChanged();

                for (rvChildModelClass imageData : fillCategoryList) {
                    Log.d("ImagesFragment", "Category: " + categoryKey + " Name: " + imageData.getName() + ", Image Link: " + imageData.getImage_Link());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ImagesFragment", "Failed to read data", databaseError.toException());
            }
        });
    }

    private List<rvChildModelClass> getRandomRecipes(List<rvChildModelClass> sourceList, int count) {
        List<rvChildModelClass> shuffledList = new ArrayList<>(sourceList);
        Collections.shuffle(shuffledList);
        return shuffledList.subList(0, Math.min(count, shuffledList.size()));
    }

    private void fetchLikedRecipes(String username) {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes").child(username);
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashSet<String> likedRecipeNames = new HashSet<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String recipeName = snapshot.getKey();
                    boolean like = snapshot.getValue(Integer.class) == 1;
                    if (recipeName != null && like) {
                        likedRecipeNames.add(recipeName);
                    }
                }
                fetchLikedRecipesDetails(likedRecipeNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeFragment", "Error fetching liked recipes", databaseError.toException());
            }
        });
    }

    private void fetchLikedRecipesDetails(HashSet<String> likedRecipeNames) {
        ArrayList<rvChildModelClass> likedRecipes = new ArrayList<>();
        DatabaseReference recipesRef = FirebaseDatabase.getInstance().getReference("PlatePerfect");
        recipesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    rvChildModelClass recipe = snapshot.getValue(rvChildModelClass.class);
                    if (recipe != null && likedRecipeNames.contains(recipe.getName())) {
                        likedRecipes.add(recipe);
                    }
                }
                updateLikesCategory(likedRecipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomeFragment", "Error fetching liked recipe details", databaseError.toException());
            }
        });
    }

    private void updateLikesCategory(ArrayList<rvChildModelClass> likedRecipes) {
        rvParentModelClass likesCategory = null;
        for (rvParentModelClass category : rvParentModelClassArrayList) {
            if (category.getFoodCategory().equals("Favourites")) {
                likesCategory = category;
                break;
            }
        }

        if (likesCategory == null) {
            likesCategory = new rvParentModelClass("Favourites", new ArrayList<>());
            rvParentModelClassArrayList.add(0, likesCategory);
        }

        likesCategory.getRvChildModelClassList().clear();
        likesCategory.getRvChildModelClassList().addAll(likedRecipes);

        parentAdapterClass.notifyDataSetChanged();
    }
}