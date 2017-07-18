package com.ik.bakingapp.common.data;


import android.content.Context;
import android.content.SharedPreferences;

import com.ik.bakingapp.common.dependencies.scopes.ApplicationScope;

import javax.inject.Inject;

@ApplicationScope
public class MySharedPref {

    private static final String SHARED_PREF = "baking_shared_pref";

    private static final String KEY_POSITION = "position";

    private Context context;
    private SharedPreferences sharedPreferences;

    @Inject
    public MySharedPref(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    }

    public void saveLastVisitedRecipePosition(int position){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_POSITION, position);
        editor.commit();
    }

    public int getLastVisitedRecipePosition(){
        return sharedPreferences.getInt(KEY_POSITION, 0);
    }

}
