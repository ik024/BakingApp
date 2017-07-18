package com.ik.bakingapp.recipeList;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ik.bakingapp.R;
import com.ik.bakingapp.common.MyApplication;
import com.ik.bakingapp.common.RecipeApiService;
import com.ik.bakingapp.common.data.DatabaseIntentService;
import com.ik.bakingapp.common.data.MySharedPref;
import com.ik.bakingapp.common.dependencies.components.ContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerContextComponent;
import com.ik.bakingapp.common.dependencies.components.DaggerSharedPrefComponent;
import com.ik.bakingapp.common.dependencies.components.SharedPrefComponent;
import com.ik.bakingapp.common.dependencies.modules.ContextModule;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.widget.RecipeWidgetProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListFragment extends Fragment implements
        LifecycleRegistryOwner,
        RecipeListAdapter.ItemClickListener {

    private OnFragmentInteractionListener mListener;
    private LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @BindView(R.id.gv_recipe_list)
    GridView mGvRecipeList;
    @BindView(R.id.pb_loading_list)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_network_response)
    TextView mTvNetworkResponse;

    private RecipeViewModel mModel;
    private RecipeListAdapter mAdapter;
    private MySharedPref mSharedPreferences;

    public RecipeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextComponent contextComponent = DaggerContextComponent.builder()
                .contextModule(new ContextModule(getContext())).build();
        SharedPrefComponent component = DaggerSharedPrefComponent.builder()
                .contextComponent(contextComponent)
                .build();
        mSharedPreferences = component.getSharedPref();

        RecipeApiService apiService = ((MyApplication) getActivity().getApplication()).getApiService();
        RecipeViewModel.Factory factory = new RecipeViewModel.Factory(getActivity().getApplication(), apiService);
        mModel = ViewModelProviders.of(this, factory).get(RecipeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mModel.observeRecipeListResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String response) {
                if (response != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mGvRecipeList.setVisibility(View.GONE);
                    mTvNetworkResponse.setText(response);
                }
            }
        });
        mModel.fetchRecipeList().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                if (recipeList != null) {

                    DatabaseIntentService.startActionBulkInsert(getContext(),
                            (ArrayList<Recipe>) recipeList);

                    mProgressBar.setVisibility(View.GONE);
                    mGvRecipeList.setVisibility(View.VISIBLE);
                    if (mAdapter == null) {
                        Picasso picasso = ((MyApplication)getActivity().getApplicationContext()).getPicasso();
                        mAdapter = new RecipeListAdapter(recipeList, picasso, RecipeListFragment.this);
                        mGvRecipeList.setAdapter(mAdapter);

                    } else {
                        mAdapter.updateList(recipeList);
                    }
                } else {
                    //Loading...
                    mProgressBar.setVisibility(View.VISIBLE);
                    mGvRecipeList.setVisibility(View.GONE);
                }
            }
        });

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
        return mRegistry;
    }

    @Override
    public void onItemClicked(int position) {
        /*Updating the widget*/
        mSharedPreferences.saveLastVisitedRecipePosition(position);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(
                getContext(), RecipeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gv_widget);


        if (mListener != null && mModel != null)
            mListener.onRecipeSelected(mModel.getRecipeAtPosition(position));
    }

    interface OnFragmentInteractionListener {
        void onRecipeSelected(Recipe selectedRecipe);
    }
}
