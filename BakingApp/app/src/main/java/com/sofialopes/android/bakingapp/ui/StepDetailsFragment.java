package com.sofialopes.android.bakingapp.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.sofialopes.android.bakingapp.R;
import com.sofialopes.android.bakingapp.data.models.Step;
import com.sofialopes.android.bakingapp.interfaces.OnClickTabListener;
import com.sofialopes.android.bakingapp.interfaces.PlayerListener;
import com.sofialopes.android.bakingapp.utils.StepDetailsFragUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.sofialopes.android.bakingapp.utils.ConstantsClass.PLAYER_POSITION_DETAILS_ACTIVITY;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.STEP_DETAILS_BUNDLE_AND_EXTRA;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.STEP_SELECTED_DETAILS_ACTIVITY;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.TAG_LANDSCAPE;
import static com.sofialopes.android.bakingapp.utils.ConstantsClass.TAG_TWO_PANE;


/**
 * Created by Sofia on 4/19/2018.
 */

public class StepDetailsFragment extends Fragment implements
        ExoPlayer.EventListener, OnClickTabListener {

    @BindView(R.id.step_video)
    SimpleExoPlayerView mPlayerView;
    @Nullable
    @BindView(R.id.step_description)
    TextView mDescriptionTV;
    @Nullable
    @BindView(R.id.step_image)
    ImageView mStepImage;

    @Nullable
    @BindView(R.id.nested_scroll)
    NestedScrollView mContainer;

    private SimpleExoPlayer mExoPlayer;

    private PlayerListener mPlayerListenerCallback;
    private long mPlayerPosition = 0;

    private int mCurrentFragmentIndex = -2;
    private int mTabSelected = -1;

    private String mVideoUrl;
    private String mThumbnailUrl;
    private TrackSelector mTrackSelector;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mPlayerListenerCallback = (PlayerListener) context;
        } catch (ClassCastException e) {
            Timber.d("Activity doesn't have listener implemented.");
        }
    }

    public static StepDetailsFragment newInstance(
            Step step,
            int currentPosition,
            long playerPosition) {

        StepDetailsFragment fragment = new StepDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STEP_DETAILS_BUNDLE_AND_EXTRA, step);

        if (currentPosition != -1) {
            bundle.putInt(STEP_SELECTED_DETAILS_ACTIVITY, currentPosition);
        }
        bundle.putLong(PLAYER_POSITION_DETAILS_ACTIVITY, playerPosition);
        fragment.setArguments(bundle);

        return fragment;
    }

    public StepDetailsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_step_details,
                container,
                false);

        ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();

        //Two Pane
        if (getContext().getResources().getBoolean(R.bool.twoPane)) {
            if (savedInstanceState != null
                    && savedInstanceState.containsKey(PLAYER_POSITION_DETAILS_ACTIVITY)) {
                mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION_DETAILS_ACTIVITY);
            }
            setUiAndPlayer(bundle);

            //Single Pane
        } else {
            if (bundle != null && bundle.containsKey(PLAYER_POSITION_DETAILS_ACTIVITY)
                    && bundle.getLong(PLAYER_POSITION_DETAILS_ACTIVITY) > 0) {
                mPlayerPosition = bundle.getLong(PLAYER_POSITION_DETAILS_ACTIVITY);
            }
            //Landscape
            if (getContext().getResources().getBoolean(R.bool.isLandscape)) {
                if (getTag().equals(TAG_LANDSCAPE)) {
                    setUiAndPlayer(bundle);
                    //Setting a tag in the player so espresso can find it
                    mPlayerView.setTag(TAG_LANDSCAPE);
                }
                //Portrait
            } else {
                if (bundle != null && bundle.containsKey(STEP_SELECTED_DETAILS_ACTIVITY)) {
                    mTabSelected = bundle.getInt(STEP_SELECTED_DETAILS_ACTIVITY);
                }
                setUiAndPlayer(bundle);
            }
        }
        return rootView;
    }

    public void setUiAndPlayer(Bundle bundle) {

        if (bundle != null) {
            Step step = null;

            if (bundle.containsKey(STEP_DETAILS_BUNDLE_AND_EXTRA)) {
                step = bundle.getParcelable(STEP_DETAILS_BUNDLE_AND_EXTRA);
                mCurrentFragmentIndex = step.getId();
            }

            String description = step.getDescription();
            if (description.contains("�")) {
                description = description.replace("�", "°");
            }
            if (mDescriptionTV != null) {
                if (!getContext().getResources().getBoolean(R.bool.isLandscape)
                        || getContext().getResources().getBoolean(R.bool.twoPane))
                    mDescriptionTV.setText(description);
            }

            mThumbnailUrl = step.getThumbnailURL();
            mVideoUrl = step.getVideoURL();

            if (!TextUtils.isEmpty(mVideoUrl)) {
                showVideo(true);
                initializePlayer(Uri.parse(mVideoUrl), getContext());

            } else {
                //in landscape, if it doesn't have video it should show the textview anyway
                if (mDescriptionTV != null) {
                    mDescriptionTV.setText(description);
                }
                if (!TextUtils.isEmpty(mThumbnailUrl)) {
                    String type = StepDetailsFragUtils.getMimeType(mThumbnailUrl);
                    if (type.startsWith("video")) {
                        showVideo(true);
                        initializePlayer(Uri.parse(mThumbnailUrl), getContext());
                    } else if (type.startsWith("image")) {
                        showVideo(false);
                        Picasso.with(getContext())
                                .load(mThumbnailUrl)
                                .placeholder(R.drawable.cake)
                                .error(R.drawable.cake)
                                .into(mStepImage);
                    }
                } else {
                    //video url and thumbnail url are empty
                    showVideo(false);
                    mStepImage.setImageResource(R.drawable.cake);
                }
            }
        }
    }

    public int getFragId() {
        return mCurrentFragmentIndex;
    }

    private void initializePlayer(Uri mediaUri, Context context) {
        if (mExoPlayer == null) {
            mTrackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    context,
                    mTrackSelector,
                    loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(context, userAgent),
                    new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            outState.putLong(PLAYER_POSITION_DETAILS_ACTIVITY, mPlayerPosition);
        }
    }

    @Override
    public void tabClicked(int position) {
        mTabSelected = position;
        mPlayerPosition = 0;
        Timber.d("TAB CLICKED: " + position + ", current frag: " + mCurrentFragmentIndex);

        if (mExoPlayer != null) {
            if (mTabSelected == mCurrentFragmentIndex) {
                mExoPlayer.seekTo(mPlayerPosition);
                mExoPlayer.setPlayWhenReady(true);
            } else {
                releasePlayer();
            }
        } else {
            //if it's the current fragment and it has a video, not an image
            if (mTabSelected == mCurrentFragmentIndex && mTrackSelector != null) {
                if (!TextUtils.isEmpty(mVideoUrl)) {
                    initializePlayer(Uri.parse(mVideoUrl), getContext());
                } else {
                    initializePlayer(Uri.parse(mThumbnailUrl), getContext());
                }
                mExoPlayer.seekTo(mPlayerPosition);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    @Override
    public void tabUnclicked(int position) {
        mTabSelected = -1;
        if (mExoPlayer != null && mCurrentFragmentIndex == position)
            mExoPlayer.setPlayWhenReady(false);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("Starting frag: " + mCurrentFragmentIndex + ", " + mTabSelected + ", "
                + mPlayerPosition + ", " + getTag());

        if (mExoPlayer != null) {
            mExoPlayer.seekTo(mPlayerPosition);

            //Landscape and two-pane don't have tab selection:
            if (getTag().equals(TAG_LANDSCAPE) || getTag().equals(TAG_TWO_PANE)) {
                mExoPlayer.setPlayWhenReady(true);

            } else {
                if (mCurrentFragmentIndex == mTabSelected) {
                    mExoPlayer.setPlayWhenReady(true);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();

            Timber.d("Calling pause: " + mCurrentFragmentIndex + ", tab selected: "
                    + mTabSelected + ", player Position: " + mPlayerPosition +", tag: " + getTag());

            if (mPlayerListenerCallback != null) {
                //this check is here so it only activates the listener if it's the correct
                //fragment, in portrait position
                if (!getTag().equals(TAG_LANDSCAPE)){
                    if (mCurrentFragmentIndex == mTabSelected) {
                        mPlayerListenerCallback.getPlayerPosition(mPlayerPosition);
                    }
                    //Landscape fragment
                } else {
                    mPlayerListenerCallback.getPlayerPosition(mPlayerPosition);
                }
            }
            mExoPlayer.setPlayWhenReady(false);
        }

        if(Build.VERSION.SDK_INT <= 23)
            releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();

        if(Build.VERSION.SDK_INT > 23 )
        releasePlayer();
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

        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mExoPlayer.setPlayWhenReady(true);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    private void showVideo(boolean showVideo) {
        if (mPlayerView != null && mStepImage != null)
            if (showVideo) {
                mPlayerView.setVisibility(View.VISIBLE);
                mStepImage.setVisibility(View.GONE);

                //Container present in landscape, single pane
                if (getContext().getResources().getBoolean(R.bool.isLandscape)
                        && !getContext().getResources().getBoolean(R.bool.twoPane))
                    mContainer.setVisibility(View.GONE);

            } else {
                mPlayerView.setVisibility(View.GONE);
                mStepImage.setVisibility(View.VISIBLE);

                //Container present in landscape, single pane
                if (getContext().getResources().getBoolean(R.bool.isLandscape)
                        && !getContext().getResources().getBoolean(R.bool.twoPane))
                    mContainer.setVisibility(View.VISIBLE);
            }
    }
}