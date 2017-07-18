package com.ik.bakingapp.recipeDetail;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.common.models.RecipeIngredients;
import com.ik.bakingapp.common.models.RecipeSteps;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeDetailListFragment extends Fragment implements
        LifecycleRegistryOwner,
        RecipeDetailListAdapter.ItemClickListener {

    private final String LIST_STATE_KEY = "list_state";
    private LifecycleRegistry mLifeCycleRegistry = new LifecycleRegistry(this);

    @BindView(R.id.rv_recipe_details)
    RecyclerView mRvRecipeDetailList;

    private RecipeDetailListAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private SelectedRecipeViewModel mModel;
    private Parcelable mListState;
    private LinearLayoutManager mLinearLayoutManager;

    public RecipeDetailListFragment() {
        // Required empty public constructor
    }

    public static RecipeDetailListFragment newInstance() {
        RecipeDetailListFragment fragment = new RecipeDetailListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(LIST_STATE_KEY)) {
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this, view);

        SelectedRecipeViewModel.Factory factory = new SelectedRecipeViewModel.Factory();
        mModel = ViewModelProviders.of(getActivity(), factory).get(SelectedRecipeViewModel.class);
        Timber.d("mSelectedRecipeViewModel hashcode: "+mModel.hashCode());

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRvRecipeDetailList.setLayoutManager(mLinearLayoutManager);

        mModel.getSelectedRecipeLiveData().observe((RecipeDetailListActivity)getActivity(), new Observer<Recipe>() {
            @Override
            public void onChanged(@Nullable Recipe recipe) {
                if (recipe != null) {
                    if (mListener != null) {
                        mListener.setActionBarTitle(recipe.getName());
                    }
                    RecipeIngredients[] ingredients = recipe.getIngredients();
                    RecipeSteps[] steps = recipe.getSteps();
                    if (mAdapter == null) {
                        mAdapter = new RecipeDetailListAdapter(ingredients, steps, RecipeDetailListFragment.this);
                        mRvRecipeDetailList.setAdapter(mAdapter);
                    } else {
                        mAdapter.updateList(ingredients, steps);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListState != null)
            mLinearLayoutManager.onRestoreInstanceState(mListState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list state
        mListState = mRvRecipeDetailList.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);
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

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifeCycleRegistry;
    }

    @Override
    public void onPositionClicked(int position) {
        mModel.setSelectedPosition(position);
        if (mListener != null)
            mListener.onItemClicked(position);
    }

    interface OnFragmentInteractionListener {
        void setActionBarTitle(String title);

        void onItemClicked(int position);
    }
}
