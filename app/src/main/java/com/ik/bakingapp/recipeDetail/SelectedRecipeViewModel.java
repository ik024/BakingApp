package com.ik.bakingapp.recipeDetail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ik.bakingapp.common.models.Recipe;
import com.ik.bakingapp.common.models.RecipeIngredients;
import com.ik.bakingapp.common.models.RecipeSteps;

import timber.log.Timber;

public class SelectedRecipeViewModel extends ViewModel {
    private MutableLiveData<Recipe> mSelectedRecipe = new MutableLiveData<>();
    private int mSelectedPosition, mTotalSteps = 0;
    private SimpleExoPlayer mExoPlayer;
    private RecipeSteps mSteps;
    private RecipeIngredients[] mIngredients;

    private long playbackPosition;
    private int currentWindow;


    private SelectedRecipeViewModel(){
    }

    void setSelectedRecipe(Recipe selectedRecipe) {
        mTotalSteps = selectedRecipe.getSteps().length;
        mSelectedRecipe.setValue(selectedRecipe);
    }
    public LiveData<Recipe> getSelectedRecipeLiveData() {
        return mSelectedRecipe;
    }

    void setIngredients(RecipeIngredients[] ingredients){
        mIngredients = ingredients;
    }

    public RecipeIngredients[] getIngredients(){
        return mIngredients;
    }

    void setSteps(RecipeSteps steps) {
        mSteps = steps;
    }

    public RecipeSteps getSteps() {
        return mSteps;
    }

    int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }

    public int getTotalSteps(){
        return mTotalSteps;
    }


    public void initExoPlayer(Context context, SimpleExoPlayerView playerView, String videoUrl,
                              ExoPlayer.EventListener listener) {

        if (mExoPlayer == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(
                    new AdaptiveTrackSelection.Factory(bandwidthMeter));

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            mExoPlayer.addListener(listener);
            playerView.setPlayer(mExoPlayer);
        }
        Timber.i("currentWindow: "+currentWindow);
        Timber.i("playbackPosition: "+playbackPosition);
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.seekTo(currentWindow, playbackPosition);
        Uri uri = Uri.parse(videoUrl);
        MediaSource mediaSource = buildMediaSource(context, uri);
        mExoPlayer.prepare(mediaSource, true, false);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
    }

    private MediaSource buildMediaSource(Context context, Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "ExoPlayer"));
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(uri, dataSourceFactory,
                extractorsFactory, null, null);
    }


    public void releasePlayer(boolean storeState) {

        if (mExoPlayer != null) {
            if (storeState) {
                playbackPosition = mExoPlayer.getCurrentPosition();
                currentWindow = mExoPlayer.getCurrentWindowIndex();
            } else {
                playbackPosition = 0;
                currentWindow = 0;
            }
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        private static SelectedRecipeViewModel selectedRecipeViewModel;

        public Factory(){
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (selectedRecipeViewModel == null) {
                selectedRecipeViewModel = new SelectedRecipeViewModel();
            }
            return (T) selectedRecipeViewModel;
        }
    }

}
