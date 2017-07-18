package com.ik.bakingapp.recipeDetail.ingredients;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.recipeDetail.SelectedRecipeViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeIngredientFragment extends Fragment {
    private static final String ARG_SELECTED_RECIPE = "selected_recipe";


    @BindView(R.id.rv_recipe_ingredients)
    RecyclerView mRvIngredientList;

    private SelectedRecipeViewModel mSelectedRecipeViewModel;

    private OnFragmentInteractionListener mListener;

    public RecipeIngredientFragment() {
        // Required empty public constructor
    }

    public static RecipeIngredientFragment newInstance() {
        RecipeIngredientFragment fragment = new RecipeIngredientFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_ingredient, container, false);
        ButterKnife.bind(this, view);

        mRvIngredientList.setLayoutManager(new LinearLayoutManager(getActivity()));

        SelectedRecipeViewModel.Factory factory = new SelectedRecipeViewModel.Factory();
        mSelectedRecipeViewModel = ViewModelProviders.of(getActivity(), factory).get(SelectedRecipeViewModel.class);
        RecipeIngredientsAdapter adapter = new RecipeIngredientsAdapter(mSelectedRecipeViewModel.getIngredients());
        mRvIngredientList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
    }
}
