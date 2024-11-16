package edu.northeastern.numad24su_plateperfect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.ImageButton;

import java.util.Map;

public class Recipe {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String tagLine;
    private Map<String, String> ingredients;
    private String instructions;
    private boolean isLiked;

    // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    public Recipe() {
    }

    public Recipe(String id, String name, String description, String imageUrl, String videoUrl, String tagLine, Map<String, String> ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.tagLine = tagLine;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.isLiked = false; // Initial state
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public Map<String, String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    // Methods for the UI actions
    public void likeRecipe(ImageButton likeButton) {
        // Logic to handle like functionality
        isLiked = !isLiked;
        if (isLiked) {
            likeButton.setColorFilter(Color.RED); // Change heart color to red
        } else {
            likeButton.setColorFilter(Color.GRAY); // Change heart color to gray
        }
    }

    public void shareRecipe(Context context) {
        // Logic to handle share functionality
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, name + "\n" + description + "\n" + "Watch the recipe video: " + getVideoUrl());
        shareIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public String getDetails() {
        // Return the details of the recipe
        return "This is a recipe for " + name + ".";
    }
}
