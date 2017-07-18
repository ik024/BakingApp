package com.ik.bakingapp.recipeDetail.steps;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;
import com.ik.bakingapp.R;
import com.ik.bakingapp.common.MyApplication;
import com.ik.bakingapp.common.NetworkConnectionListener;
import com.ik.bakingapp.common.models.RecipeSteps;
import com.ik.bakingapp.recipeDetail.SelectedRecipeViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener{

    @BindView(R.id.tv_recipe_step_full_desc)
    TextView tvFullDesc;

    @BindView(R.id.tv_no_video)
    TextView tvNoVideo;

    @BindView(R.id.my_exo_player)
    SimpleExoPlayerView mPlayerView;

    @BindView(R.id.btn_step_next)
    Button btnNextStep;

    @BindView(R.id.btn_step_prev)
    Button btnPrevStep;

    @BindView(R.id.iv_video_thumbnail)
    ImageView ivVideoThumbnail;

    @BindView(R.id.video_progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.rl_player_layout)
    RelativeLayout rlPlayerLayout;

    private RecipeSteps mStep;
    private SelectedRecipeViewModel mSelectedRecipeViewModel;

    private OnFragmentInteractionListener mListener;

    private boolean isOrientationChange;

    public RecipeStepFragment() {
        // Required empty public constructor
    }

    public static RecipeStepFragment newInstance() {
        return new RecipeStepFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this, view);

        SelectedRecipeViewModel.Factory factory = new SelectedRecipeViewModel.Factory();
        mSelectedRecipeViewModel = ViewModelProviders.of(getActivity(), factory).get(SelectedRecipeViewModel.class);

        mStep = mSelectedRecipeViewModel.getSteps();

        String thumbnailUrl = mStep.getThumbnailURL();
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            ivVideoThumbnail.setVisibility(View.VISIBLE);
            Picasso picasso = ((MyApplication)getContext().getApplicationContext()).getPicasso();
            picasso.load(thumbnailUrl).placeholder(R.drawable.place_holder).into(ivVideoThumbnail);
        } else {
            ivVideoThumbnail.setVisibility(View.GONE);
        }

        int mLastStepId = mSelectedRecipeViewModel.getTotalSteps() - 1;

        tvFullDesc.setText(mStep.getDescription());

        /*If introduction, disable the previous button*/
        if (mStep.getId() == 0) {
            btnPrevStep.setEnabled(false);
        }

        /*If last step, disable the next button*/
        if (mLastStepId == mStep.getId()) {
            btnNextStep.setEnabled(false);
        }

        NetworkConnectionListener networkConnectionListener = NetworkConnectionListener.getInstance(getContext());
        networkConnectionListener.observe((LifecycleOwner)getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isOnline) {
                if (!isOnline) {
                    showMessage(getString(R.string.toast_offline));
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
         /*If video url is null or empty show "No Video" text else show the exo player view*/
        if (mStep.getVideoURL() == null || mStep.getVideoURL().isEmpty()) {
            tvNoVideo.setVisibility(View.VISIBLE);
            rlPlayerLayout.setVisibility(View.GONE);
        } else {
            rlPlayerLayout.setVisibility(View.VISIBLE);
            tvNoVideo.setVisibility(View.GONE);
            mSelectedRecipeViewModel.initExoPlayer(getContext(), mPlayerView, mStep.getVideoURL(), this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.i("onPause orientation: "+isOrientationChange);
        if (Util.SDK_INT <= 23) {
            mSelectedRecipeViewModel.releasePlayer(isOrientationChange);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.i("onStop orientation: "+isOrientationChange);
        if (Util.SDK_INT > 23) {
            mSelectedRecipeViewModel.releasePlayer(isOrientationChange);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.btn_step_next)
    public void onNextClicked() {
        int nextPosition = mStep.getId() + 1;

        if (mSelectedRecipeViewModel != null)
            mSelectedRecipeViewModel.setSelectedPosition(nextPosition + 1);

        if (mListener != null)
            mListener.nextStep(nextPosition);
    }

    @OnClick(R.id.btn_step_prev)
    public void onPrevClicked() {
        int prevPosition = mStep.getId() - 1;

        if (mSelectedRecipeViewModel != null)
            mSelectedRecipeViewModel.setSelectedPosition(prevPosition + 1);

        if (mListener != null)
            mListener.prevStep(prevPosition);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isOrientationChange = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void nextStep(int nextStepId);
        void prevStep(int prevStepId);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case ExoPlayer.STATE_IDLE:
                break;
            case ExoPlayer.STATE_BUFFERING:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case ExoPlayer.STATE_READY:
                progressBar.setVisibility(View.GONE);
                ivVideoThumbnail.setVisibility(View.GONE);
                break;
            case ExoPlayer.STATE_ENDED:
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}

