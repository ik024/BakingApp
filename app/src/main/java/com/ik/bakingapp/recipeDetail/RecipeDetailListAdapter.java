package com.ik.bakingapp.recipeDetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.models.RecipeIngredients;
import com.ik.bakingapp.common.models.RecipeSteps;

import butterknife.BindView;
import butterknife.ButterKnife;

class RecipeDetailListAdapter extends  RecyclerView.Adapter<RecipeDetailListAdapter.MyViewHolder>{

    private static final int VIEW_INGREDIENTS = 0;
    private static final int VIEW_STEPS = 1;

    private RecipeIngredients[] ingredients;
    private RecipeSteps[] steps;
    private ItemClickListener listener;

    public RecipeDetailListAdapter(RecipeIngredients[] ingredients, RecipeSteps[] steps, ItemClickListener listener){
        this.ingredients = ingredients;
        this.steps = steps;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe_detail_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (position == 0) {
            holder.tvRecipeDetailHeader.setVisibility(View.GONE);
            holder.mTvRecipeDetailTitle.setText("Recipe Ingredients");
        } else {
            RecipeSteps step = steps[position-1];
            String stepDesc = step.getShortDescription();
            if (position == 1) {
                holder.tvRecipeDetailHeader.setVisibility(View.GONE);
                holder.mTvRecipeDetailTitle.setText(stepDesc);
            }
            else {
                if (position == 2) {
                    holder.tvRecipeDetailHeader.setVisibility(View.VISIBLE);
                }else{
                    holder.tvRecipeDetailHeader.setVisibility(View.GONE);
                }
                holder.mTvRecipeDetailTitle.setText((position-1)+" "+stepDesc);
            }


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_INGREDIENTS;
        } else {
            return VIEW_STEPS;
        }
    }

    @Override
    public int getItemCount() {
        return steps.length + 1;
    }

    void updateList(RecipeIngredients[] ingredients, RecipeSteps[] steps) {

        this.ingredients = ingredients;
        this.steps = steps;

        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_recipe_detail_header)
        TextView tvRecipeDetailHeader;

        @BindView(R.id.tv_recipe_detail_title)
        TextView mTvRecipeDetailTitle;

        MyViewHolder(View itemView, final ItemClickListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
             itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (listener != null)
                         listener.onPositionClicked(getAdapterPosition());
                 }
             });
        }
    }

    interface ItemClickListener{
        void onPositionClicked(int position);
    }

}
