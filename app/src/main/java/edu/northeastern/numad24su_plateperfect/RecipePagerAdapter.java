package edu.northeastern.numad24su_plateperfect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RecipePagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private String recipeName;
    private String recipeDescription;

    public RecipePagerAdapter(@NonNull FragmentManager fm, int numOfTabs, String recipeName, String recipeDescription) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DetailsFragment.newInstance(recipeName, recipeDescription); // Pass the recipe name and description to DetailsFragment
            case 1:
                return IngredientsFragment.newInstance(recipeName);
            case 2:
                return InstructionsFragment.newInstance(recipeName);
            default:
                return null;
        }
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        // Return the title for each tab
        switch (position) {
            case 0:
                return "Details"; // Tab 1 title
            case 1:
                return "Description"; // Tab 2 title
            case 2:
                return "Instructions"; // Tab 3 title
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return numOfTabs;
    }
}
