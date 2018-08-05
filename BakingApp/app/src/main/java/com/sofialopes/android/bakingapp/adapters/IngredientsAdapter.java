package com.sofialopes.android.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.data.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sofia on 4/14/2018.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    private Context mContext;
    private List<Ingredient> mIngredientsList = new ArrayList<>();

    public IngredientsAdapter(Context context, ArrayList<Ingredient> ingredientsList) {
        this.mContext = context;
        this.mIngredientsList = ingredientsList;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.ingredients_recycler_item,
                        parent,
                        false);
        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        Ingredient ingredient = mIngredientsList.get(position);
        String ingredientName = ingredient.getIngredient();
        double quantity = ingredient.getQuantity();
        String measure = ingredient.getMeasure();

        holder.name.setText(
                mContext.getResources().getString(R.string.ingredient_name, ingredientName));
        holder.quantity.setText(
                mContext.getResources().getString(R.string.quantity_and_measure, quantity, measure));
    }

    @Override
    public int getItemCount() {
        if (mIngredientsList == null || mIngredientsList.isEmpty()) return 0;

        return mIngredientsList.size();
    }

    public void updateIngredientsList(ArrayList<Ingredient> ingredientsList) {
        mIngredientsList = ingredientsList;
        notifyDataSetChanged();
    }

    public class IngredientsViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView quantity;

        public IngredientsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.ingredient_name);
            quantity = itemView.findViewById(R.id.ingredients_quantity);
        }
    }
}