package com.sofialopes.android.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.data.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sofia on 4/14/2018.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private Context mContext;
    private List<Recipe> mRecipesList = new ArrayList<>();
    private MainAdapterOnClickHandler mClickHandler;
    private boolean mFromWidget;

    public interface MainAdapterOnClickHandler {
        void onRecipeClick(boolean fromWidget, Recipe recipe);
    }

    public MainAdapter(Context context,
                       List<Recipe> mRecipesList,
                       MainAdapterOnClickHandler clickHandler,
                       boolean fromWidget) {
        this.mContext = context;
        this.mRecipesList = mRecipesList;
        this.mClickHandler = clickHandler;
        this.mFromWidget = fromWidget;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(mFromWidget){
           view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_recycler_item_from_widget, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_recycler_item_no_widget, parent, false);
        }

        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        Recipe recipe = mRecipesList.get(position);
        String name = recipe.getName();
        int servings = recipe.getServings();

        holder.recipeName.setText(name);
        holder.numberOfServings.setText(mContext.getString(R.string.nr_of_servings, servings));
        holder.recipeImage.setContentDescription(name);

        String imageUri = recipe.getImage();
        if (TextUtils.isEmpty(imageUri)) {
            holder.recipeImage.setImageResource(R.drawable.cake);
        } else {
            Picasso.with(mContext)
                    .load(imageUri)
                    .placeholder(R.drawable.cake)
                    .error(R.drawable.cake)
                    .into(holder.recipeImage);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecipesList == null || mRecipesList.isEmpty()) return 0;

        return mRecipesList.size();
    }

    public void updateRecipesList(List<Recipe> recipes) {
        mRecipesList = recipes;
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView recipeImage;
        TextView recipeName;
        TextView numberOfServings;
        TextView button;

        public MainViewHolder(View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            numberOfServings = itemView.findViewById(R.id.number_of_servings);

            if(mFromWidget){
                button = itemView.findViewById(R.id.button);
                button.setOnClickListener(this);
            }else{
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onRecipeClick(mFromWidget, mRecipesList.get(getAdapterPosition()));
        }
    }
}
