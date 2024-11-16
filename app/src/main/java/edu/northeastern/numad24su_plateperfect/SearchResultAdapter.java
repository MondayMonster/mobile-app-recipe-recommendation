package edu.northeastern.numad24su_plateperfect;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private Context context;
    private List<rvChildModelClass> recipeList;

    public SearchResultAdapter(Context context, List<rvChildModelClass> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        rvChildModelClass recipe = recipeList.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.tagline.setText(recipe.getTagline());
        Picasso.get().load(recipe.getImage_Link()).into(holder.recipeImage); // Use getImageUrl() here

        holder.itemView.setOnClickListener(v -> {
            // Open the recipe details activity
            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra("recipe", recipe);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void updateList(List<rvChildModelClass> newList) {
        recipeList = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, tagline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            tagline = itemView.findViewById(R.id.tagline);
        }
    }
}
