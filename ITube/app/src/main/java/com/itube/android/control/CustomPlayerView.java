package com.itube.android.control;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;

/**
 * Created by lenovo on 09/12/2018.
 */

public class CustomPlayerView extends PlayerView implements ExoPlayer.EventListener {

    String TAG = "CustomPlayerView";
    Context context;

    public CustomPlayerView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CustomPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public CustomPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    BandwidthMeter bandwidthMeter;
    TrackSelection.Factory videoTrackSelectionFactory;
    TrackSelector trackSelector ;
    SimpleExoPlayer player;
    RtmpDataSourceFactory rtmpDataSourceFactory;
    MediaSource videoSource;

    public void initView(){
        bandwidthMeter = new DefaultBandwidthMeter();

        videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        player.addListener(this);

        //player.setVideoScalingMode();
        //setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        //setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        //setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        //setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        //setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        //setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        setUseController(false);
        setPlayer(player);
    }

    public void initLocal(String path){
        Uri uri = Uri.parse(path);
        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        videoSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);



    }

    public void initRTMP(String url){
        rtmpDataSourceFactory = new RtmpDataSourceFactory();
        videoSource = new ExtractorMediaSource.Factory(rtmpDataSourceFactory)
                .createMediaSource(Uri.parse(url));
    }

    public void initURL(String url){
        hasUrl = true;
        videoSource = new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab"))
                .createMediaSource(Uri.parse(url));
        /*rtmpDataSourceFactory = new RtmpDataSourceFactory();
        videoSource = new ExtractorMediaSource.Factory(rtmpDataSourceFactory)
                .createMediaSource(Uri.parse(url));*/
    }

    boolean isPlaying = false;
    boolean autoHide = false;
    boolean autoSize = false;
    boolean hasUrl = false;

    public void play(){
        //stop();

        if(isPlaying){
            player.setPlayWhenReady(true);
            return;
        }

        if(autoHide){
            setVisibility(VISIBLE);
        }

        player.prepare(videoSource);
        player.setPlayWhenReady(true);

        isPlaying = true;
    }

    public void pause(){
        if(autoHide){
            setVisibility(GONE);
        }
        player.setPlayWhenReady(false);
    }

    public void stop(){
        if(autoHide){
            setVisibility(GONE);
        }
        try{
            player.setPlayWhenReady(false);
            player.release();

            isPlaying = false;
        }
        catch (Exception e){

        }
    }

    public void setAutoHide(boolean autoHide){
        this.autoHide = autoHide;
    }

    public void setAutoSize(boolean autoSize){
        if(autoSize){
            //setLayoutParams(new ViewGroup.LayoutParams(MA));
        }
        this.autoSize = autoSize;
    }

    public boolean getHasVideoSource(){
        return (videoSource != null);
    }

    public boolean getAutoHide(){
        return autoHide;
    }

    public boolean getIsPlaying(){
        return isPlaying;
    }

    public boolean getHasUrl(){
        return hasUrl;
    }

    //region Default Method
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            if(autoHide){
                setVisibility(GONE);
            }
            Log.d(TAG, "Player Ended");
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
    //endregion

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(context,"Exoplayer-local")).
                createMediaSource(uri);
    }


    /*private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }

    }*/

}
