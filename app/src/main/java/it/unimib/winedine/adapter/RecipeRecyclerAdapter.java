package it.unimib.winedine.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.unimib.winedine.R;
import it.unimib.winedine.model.Recipe;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder> {
    private static final int layout = R.layout.item_recipe;
    public interface OnItemClickListener {
        void onRecipeItemClick(Recipe recipe);
    }

    private final List<Recipe> recipeList;
    private final OnItemClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageRecipe;
        private final TextView textTitle;

        public ViewHolder(View view) {
            super(view);
            imageRecipe = view.findViewById(R.id.image_recipe);
            textTitle = view.findViewById(R.id.text_recipe_title);
        }

        public void bind(final Recipe recipe, final OnItemClickListener listener) {
            textTitle.setText(recipe.getTitle());

            Glide.with(itemView.getContext())
                    .load(recipe.getImage())
                    .placeholder(R.color.md_theme_error)
                    .into(imageRecipe);

            itemView.setOnClickListener(v -> listener.onRecipeItemClick(recipe));
        }
    }

    public RecipeRecyclerAdapter(List<Recipe> recipeList, OnItemClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(recipeList.get(position), listener);
    }

    public void updateData(List<Recipe> newRecipes) {
        this.recipeList.clear();
        this.recipeList.addAll(newRecipes);
        notifyDataSetChanged();
        Log.d("ADAPTER_DEBUG", "Aggiornamento dati: " + newRecipes.size());
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}
