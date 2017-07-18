package com.ik.bakingapp.recipeDetail.ingredients;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.models.RecipeIngredients;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.MyViewHolder> {


    private RecipeIngredients[] ingredients;

    public RecipeIngredientsAdapter(RecipeIngredients[] ingredients){
        this.ingredients = ingredients;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_ingredients, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            holder.llIngredientsLabels.setVisibility(View.VISIBLE);
            holder.llIngredientsValues.setVisibility(View.GONE);
        } else {
            holder.llIngredientsLabels.setVisibility(View.GONE);
            holder.llIngredientsValues.setVisibility(View.VISIBLE);
            RecipeIngredients ingredient = ingredients[position-1];
            holder.tvQuantityValue.setText(String.valueOf(ingredient.getQuantity()));
            holder.tvMeasureValue.setText(String.valueOf(ingredient.getMeasure()));
            holder.tvIngredientsValue.setText(String.valueOf(ingredient.getIngredient()));
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.length + 1;
    }

    public void updateList(RecipeIngredients[] ingredients){
        this.ingredients = ingredients;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.ll_labels)
        LinearLayout llIngredientsLabels;

        @BindView(R.id.ll_values)
        LinearLayout llIngredientsValues;

        @BindView(R.id.tv_quantity_value)
        TextView tvQuantityValue;

        @BindView(R.id.tv_ingredients_value)
        TextView tvIngredientsValue;

        @BindView(R.id.tv_measure_value)
        TextView tvMeasureValue;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
