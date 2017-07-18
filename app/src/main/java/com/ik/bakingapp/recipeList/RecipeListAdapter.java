package com.ik.bakingapp.recipeList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class RecipeListAdapter extends BaseAdapter {

    private List<Recipe> mRecipeList;
    private Picasso picasso;
    private RecipeListAdapter.ItemClickListener mClickListener;

    RecipeListAdapter(List<Recipe> recipeList, Picasso picasso,
                      RecipeListAdapter.ItemClickListener clickListener){
        mRecipeList = recipeList;
        this.picasso = picasso;
        this.mClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return mRecipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_recipe_list, parent, false);

            holder = new MyViewHolder(convertView, mClickListener);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        Recipe recipe = mRecipeList.get(position);

        holder.tvRecipeTitle.setTag(position);
        holder.tvRecipeTitle.setText(recipe.getName());

        if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
            picasso.load(recipe.getImage())
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.no_image)
                    .into(holder.ivRecipeImage);
        }

        return convertView;
    }

    void updateList(List<Recipe> recipeList) {
        mRecipeList = recipeList;
        notifyDataSetChanged();
    }

    static class MyViewHolder{

        @BindView(R.id.iv_recipe_image)
        ImageView ivRecipeImage;

        @BindView(R.id.tv_recipe_title)
        TextView tvRecipeTitle;

        MyViewHolder(View itemView, final RecipeListAdapter.ItemClickListener clickListener) {
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null){
                        clickListener.onItemClicked((Integer) tvRecipeTitle.getTag());
                    }
                }
            });
        }
    }

    interface ItemClickListener{
        void onItemClicked(int position);
    }
}
