package com.mengfou.mytvapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mengfou.mytvapp.adapter.ChannelAdapter;
import com.mengfou.mytvapp.adapter.LocalVideoAdapter;
import com.mengfou.mytvapp.beans.LocalVideoBean;
import com.mengfou.mytvapp.listener.CCTVPageElementObserver;
import com.mengfou.mytvapp.log.Log;
import com.mengfou.mytvapp.manager.LocalVideoRepository;
import com.mengfou.mytvapp.manager.TotalTvShowManager;
import com.mengfou.mytvapp.util.ActivityUtil;
import com.mengfou.mytvapp.util.BitmapUtil;
import com.mengfou.mytvapp.util.DialogUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends FragmentActivity implements CCTVPageElementObserver {
    private static final int VIDEO_PERMISSION_REQUEST = 1001;
    private static final String TAG = MainActivity.class.getName();

    private enum SourceMode { LIVE, LOCAL }

    private TotalTvShowManager totalTvShowManager;
    private LocalVideoRepository localVideoRepository;
    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor();

    private WebView webView;
    private PlayerView localPlayerView;
    private ExoPlayer localPlayer;
    private TextView pageloadInfo;
    private ImageView pageloadInfoImage;
    private TextView bottomInfoText;
    private TextView touchSwitchBtn;
    private TextView liveTab;
    private TextView localTab;
    private TextView listTitle;
    private TextView rescanButton;
    private TextView localEmptyTitle;
    private TextView localEmptyDetail;
    private TextView playingTitle;
    private LinearLayout textInfo;
    private LinearLayout sidebar;
    private View playerPanel;
    private View localEmptyState;
    private RecyclerView channelList;
    private RecyclerView localVideoList;

    private ChannelAdapter channelAdapter;
    private LocalVideoAdapter localVideoAdapter;
    private SourceMode sourceMode = SourceMode.LIVE;
    private boolean playbackMode;
    private boolean localScanCompleted;
    private int focusedChannelIndex = RecyclerView.NO_POSITION;
    private int focusedLocalVideoIndex = RecyclerView.NO_POSITION;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setFullScreen(this);
        setContentView(R.layout.activity_main);

        bindViews();
        totalTvShowManager = new TotalTvShowManager(webView);
        localVideoRepository = new LocalVideoRepository(this);
        initLocalPlayer();
        initWebView();
        initLists();
        initActions();
        initPlayerGestures();

        totalTvShowManager.clearCache();
        showSource(SourceMode.LIVE);
        enterPlaybackMode();
        totalTvShowManager.loadPage();
    }

    private void bindViews() {
        webView = findViewById(R.id.load_page);
        localPlayerView = findViewById(R.id.local_player);
        pageloadInfo = findViewById(R.id.pageload_info);
        pageloadInfoImage = findViewById(R.id.pageload_info_img);
        textInfo = findViewById(R.id.text_info);
        sidebar = findViewById(R.id.sidebar);
        playerPanel = findViewById(R.id.player_panel);
        channelList = findViewById(R.id.channel_list);
        localVideoList = findViewById(R.id.local_video_list);
        localEmptyState = findViewById(R.id.local_empty_state);
        localEmptyTitle = findViewById(R.id.local_empty_title);
        localEmptyDetail = findViewById(R.id.local_empty_detail);
        bottomInfoText = findViewById(R.id.bottom_info_text);
        touchSwitchBtn = findViewById(R.id.touch_switch_btn);
        liveTab = findViewById(R.id.live_tab);
        localTab = findViewById(R.id.local_tab);
        listTitle = findViewById(R.id.list_title);
        rescanButton = findViewById(R.id.rescan_button);
        playingTitle = findViewById(R.id.playing_title);
    }

    private void initActions() {
        bottomInfoText.setOnClickListener(view -> {
            if (sourceMode == SourceMode.LIVE) {
                totalTvShowManager.loadPage();
            } else {
                scanLocalVideos();
            }
        });
        touchSwitchBtn.setOnClickListener(view -> exitPlaybackMode());
        liveTab.setOnClickListener(view -> showSource(SourceMode.LIVE));
        localTab.setOnClickListener(view -> showSource(SourceMode.LOCAL));
        rescanButton.setOnClickListener(view -> requestVideoPermissionAndScan());
    }

    private void initPlayerGestures() {
        GestureDetector detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                if (playbackMode) {
                    exitPlaybackMode();
                } else {
                    enterPlaybackMode();
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event) {
                togglePlayback();
                return true;
            }
        });
        View.OnTouchListener listener = (view, event) -> detector.onTouchEvent(event);
        playerPanel.setOnTouchListener(listener);
        webView.setOnTouchListener(listener);
        localPlayerView.setOnTouchListener(listener);
    }

    private void initLocalPlayer() {
        localPlayer = new ExoPlayer.Builder(this).build();
        localPlayerView.setPlayer(localPlayer);
        localPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                showPlayerMessage("无法播放此视频", error.getErrorCodeName());
            }
        });
    }

    private void initLists() {
        channelAdapter = new ChannelAdapter();
        channelAdapter.submitList(totalTvShowManager.getCCTVShowInfoBeans());
        channelAdapter.setSelectedIndex(totalTvShowManager.getCurrentShowIndex());
        focusedChannelIndex = totalTvShowManager.getCurrentShowIndex();
        channelAdapter.setChannelListener(new ChannelAdapter.ChannelListener() {
            @Override
            public void onFocused(int position) {
                focusedChannelIndex = position;
                channelAdapter.setSelectedIndex(position);
            }

            @Override
            public void onClicked(int position) {
                selectChannel(position, true);
            }
        });
        configureList(channelList, channelAdapter);

        localVideoAdapter = new LocalVideoAdapter();
        localVideoAdapter.setVideoListener(new LocalVideoAdapter.VideoListener() {
            @Override
            public void onFocused(int position) {
                focusedLocalVideoIndex = position;
                localVideoAdapter.setSelectedIndex(position);
            }

            @Override
            public void onClicked(int position) {
                selectLocalVideo(position);
            }
        });
        configureList(localVideoList, localVideoAdapter);
    }

    private void configureList(RecyclerView list, RecyclerView.Adapter<?> adapter) {
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        list.setItemAnimator(null);
        list.setHasFixedSize(true);
        list.setNestedScrollingEnabled(false);
    }

    private void initWebView() {
        totalTvShowManager.getWebViewManager().setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!totalTvShowManager.isCurrentPageUrl(url)) {
                    return;
                }
                totalTvShowManager.setCurrentShowIsLoadFinish(true);
                if (sourceMode == SourceMode.LIVE) {
                    setWebViewVisibility(true);
                    pageloadInfo.setText("已加载 " + totalTvShowManager.getCCTVShowInfoBean().getName());
                    totalTvShowManager.checkPageFullScreenElement(MainActivity.this);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request == null || !request.isForMainFrame()
                        || !totalTvShowManager.isCurrentPageUrl(request.getUrl().toString())) {
                    return;
                }
                totalTvShowManager.currentShowLoadError();
                if (sourceMode == SourceMode.LIVE) {
                    showPlayerMessage("直播加载失败", "请选择其他频道或重新加载");
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!totalTvShowManager.isCurrentPageUrl(url)) {
                    return;
                }
                totalTvShowManager.setCurrentShowIsLoadFinish(false);
                if (sourceMode == SourceMode.LIVE) {
                    setWebViewVisibility(false);
                    Bitmap bitmap = BitmapUtil.getBitmap(totalTvShowManager.getCCTVShowInfoBean().getImg());
                    pageloadInfoImage.setImageBitmap(bitmap);
                    pageloadInfo.setText("加载中... " + totalTvShowManager.getCCTVShowInfoBean().getName());
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        totalTvShowManager.getWebViewManager().setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (sourceMode != SourceMode.LIVE
                        || !totalTvShowManager.isCurrentPageUrl(view.getUrl())
                        || totalTvShowManager.isCurrentShowIsLoadFinish()) {
                    return;
                }
                pageloadInfo.setText("加载中... " + progress + "%");
                Log.e(TAG, "加载进度: " + progress);
            }
        });
    }

    private void showSource(SourceMode mode) {
        sourceMode = mode;
        boolean live = mode == SourceMode.LIVE;
        liveTab.setSelected(live);
        localTab.setSelected(!live);
        channelList.setVisibility(live ? View.VISIBLE : View.GONE);
        localVideoList.setVisibility(!live && localVideoAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        localEmptyState.setVisibility(!live && localVideoAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        rescanButton.setVisibility(live ? View.GONE : View.VISIBLE);
        listTitle.setText(live ? "频道" : localListTitle());

        if (live) {
            localPlayer.pause();
            localPlayerView.setVisibility(View.GONE);
            textInfo.setVisibility(totalTvShowManager.isCurrentPageLoaded() ? View.GONE : View.VISIBLE);
            totalTvShowManager.getWebViewManager().setVisibility(totalTvShowManager.isCurrentPageLoaded());
            totalTvShowManager.playWebVideo();
        } else {
            totalTvShowManager.pauseWebVideo();
            totalTvShowManager.getWebViewManager().setVisibility(false);
            if (localVideoAdapter.getSelectedIndex() >= 0) {
                localPlayerView.setVisibility(View.VISIBLE);
                textInfo.setVisibility(View.GONE);
            } else {
                showPlayerMessage("选择一个本地视频", "");
            }
            if (!localScanCompleted) {
                requestVideoPermissionAndScan();
            }
        }
        updatePlayingTitle();
        if (!playbackMode) {
            focusCurrentList();
        }
    }

    private String localListTitle() {
        return localScanCompleted ? "本地视频  " + localVideoAdapter.getItemCount() : "本地视频";
    }

    private void selectChannel(int position, boolean forceReload) {
        if (position < 0 || position >= channelAdapter.getItemCount()) {
            return;
        }
        boolean changed = position != totalTvShowManager.getCurrentShowIndex();
        totalTvShowManager.setCurrentShowIndex(position);
        focusedChannelIndex = position;
        channelAdapter.setSelectedIndex(position);
        showSource(SourceMode.LIVE);
        enterPlaybackMode();
        if (changed || forceReload) {
            totalTvShowManager.loadPage();
        }
    }

    private void selectLocalVideo(int position) {
        LocalVideoBean video = localVideoAdapter.getItem(position);
        if (video == null) {
            return;
        }
        focusedLocalVideoIndex = position;
        localVideoAdapter.setSelectedIndex(position);
        showSource(SourceMode.LOCAL);
        localPlayer.setMediaItem(MediaItem.fromUri(video.getUri()));
        localPlayer.prepare();
        localPlayer.play();
        localPlayerView.setVisibility(View.VISIBLE);
        textInfo.setVisibility(View.GONE);
        enterPlaybackMode();
        updatePlayingTitle();
    }

    private void moveCurrentItem(int delta) {
        if (sourceMode == SourceMode.LIVE) {
            int count = channelAdapter.getItemCount();
            if (count > 0) {
                int next = (totalTvShowManager.getCurrentShowIndex() + delta + count) % count;
                selectChannel(next, true);
            }
        } else {
            int count = localVideoAdapter.getItemCount();
            if (count > 0) {
                int current = localVideoAdapter.getSelectedIndex();
                int next = (Math.max(current, 0) + delta + count) % count;
                selectLocalVideo(next);
            }
        }
    }

    private void togglePlayback() {
        if (sourceMode == SourceMode.LOCAL) {
            if (localVideoAdapter.getSelectedIndex() < 0) {
                return;
            }
            if (localPlayer.isPlaying()) {
                localPlayer.pause();
                Toast.makeText(this, "已暂停", Toast.LENGTH_SHORT).show();
            } else {
                localPlayer.play();
                Toast.makeText(this, "继续播放", Toast.LENGTH_SHORT).show();
            }
        } else {
            totalTvShowManager.toggleWebVideoPlayback();
            Toast.makeText(this, "已切换播放状态", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestVideoPermissionAndScan() {
        String permission = videoReadPermission();
        if (permission == null || ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            scanLocalVideos();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{permission}, VIDEO_PERMISSION_REQUEST);
        }
    }

    private String videoReadPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            return Manifest.permission.READ_MEDIA_VIDEO;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        return null;
    }

    private void scanLocalVideos() {
        localScanCompleted = false;
        localVideoList.setVisibility(View.GONE);
        localEmptyState.setVisibility(View.VISIBLE);
        localEmptyTitle.setText("正在扫描本地视频");
        localEmptyDetail.setText("");
        listTitle.setText("本地视频");
        scanExecutor.execute(() -> {
            List<LocalVideoBean> videos = localVideoRepository.scan();
            runOnUiThread(() -> applyLocalVideos(videos));
        });
    }

    private void applyLocalVideos(List<LocalVideoBean> videos) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        localScanCompleted = true;
        localVideoAdapter.submitList(videos);
        if (sourceMode == SourceMode.LOCAL) {
            listTitle.setText(localListTitle());
        }
        boolean empty = videos.isEmpty();
        localVideoList.setVisibility(sourceMode == SourceMode.LOCAL && !empty ? View.VISIBLE : View.GONE);
        localEmptyState.setVisibility(sourceMode == SourceMode.LOCAL && empty ? View.VISIBLE : View.GONE);
        if (empty) {
            localEmptyTitle.setText("没有找到视频");
            localEmptyDetail.setText("请确认设备中已有视频文件");
        } else if (sourceMode == SourceMode.LOCAL && !playbackMode) {
            focusedLocalVideoIndex = 0;
            localVideoAdapter.setSelectedIndex(0);
            focusListPosition(localVideoList, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != VIDEO_PERMISSION_REQUEST) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanLocalVideos();
        } else {
            localScanCompleted = true;
            localVideoList.setVisibility(View.GONE);
            localEmptyState.setVisibility(View.VISIBLE);
            localEmptyTitle.setText("需要视频访问权限");
            localEmptyDetail.setText("授权后才能读取设备中的视频");
        }
    }

    @Override
    public void hasFullScreenBtn(boolean value) {
        if (sourceMode != SourceMode.LIVE || !totalTvShowManager.isCurrentPageLoaded()) {
            return;
        }
        setWebViewVisibility(true);
        totalTvShowManager.playWebVideo();
        totalTvShowManager.doFullScreen(null);
        if (!value) {
            pageloadInfo.setText("已打开 " + totalTvShowManager.getCCTVShowInfoBean().getName());
        }
    }

    @Override
    public void onBackPressed() {
        if (playbackMode) {
            exitPlaybackMode();
            return;
        }
        DialogUtil.exitApp(this, "确认退出？", () -> totalTvShowManager.getWebViewManager().pause());
    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
            togglePlayback();
            return true;
        }
        if (playbackMode) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                moveCurrentItem(-1);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                moveCurrentItem(1);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                togglePlayback();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            View focused = getCurrentFocus();
            if (isDescendantOf(focused, channelList)) {
                selectChannel(getFocusedChannelPosition(), true);
                return true;
            }
            if (isDescendantOf(focused, localVideoList)) {
                selectLocalVideo(getFocusedLocalVideoPosition());
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean isDescendantOf(View view, View parent) {
        View current = view;
        while (current != null) {
            if (current == parent) {
                return true;
            }
            if (!(current.getParent() instanceof View)) {
                return false;
            }
            current = (View) current.getParent();
        }
        return false;
    }

    private void enterPlaybackMode() {
        playbackMode = true;
        sidebar.setVisibility(View.GONE);
        touchSwitchBtn.setVisibility(View.VISIBLE);
        updatePlayingTitle();
    }

    private void exitPlaybackMode() {
        playbackMode = false;
        sidebar.setVisibility(View.VISIBLE);
        touchSwitchBtn.setVisibility(View.GONE);
        playingTitle.setVisibility(View.GONE);
        focusCurrentList();
    }

    private void focusCurrentList() {
        if (sourceMode == SourceMode.LIVE) {
            int position = Math.max(totalTvShowManager.getCurrentShowIndex(), 0);
            channelAdapter.setSelectedIndex(position);
            focusListPosition(channelList, position);
        } else if (localVideoAdapter.getItemCount() > 0) {
            int position = localVideoAdapter.getSelectedIndex();
            if (position < 0) {
                position = Math.max(focusedLocalVideoIndex, 0);
            }
            localVideoAdapter.setSelectedIndex(position);
            focusListPosition(localVideoList, position);
        } else {
            localTab.requestFocus();
        }
    }

    private void focusListPosition(RecyclerView list, int position) {
        list.post(() -> {
            list.scrollToPosition(position);
            RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                holder.itemView.requestFocus();
            } else {
                list.requestFocus();
            }
        });
    }

    private int getFocusedChannelPosition() {
        return getFocusedPosition(channelList, focusedChannelIndex, totalTvShowManager.getCurrentShowIndex());
    }

    private int getFocusedLocalVideoPosition() {
        return getFocusedPosition(localVideoList, focusedLocalVideoIndex, localVideoAdapter.getSelectedIndex());
    }

    private int getFocusedPosition(RecyclerView list, int focusedIndex, int fallback) {
        View focusedChild = list.getFocusedChild();
        if (focusedChild != null) {
            int position = list.getChildAdapterPosition(focusedChild);
            if (position != RecyclerView.NO_POSITION) {
                return position;
            }
        }
        return focusedIndex != RecyclerView.NO_POSITION ? focusedIndex : fallback;
    }

    private void updatePlayingTitle() {
        if (!playbackMode) {
            playingTitle.setVisibility(View.GONE);
            return;
        }
        if (sourceMode == SourceMode.LIVE) {
            playingTitle.setText("直播  ·  " + totalTvShowManager.getCCTVShowInfoBean().getName());
            playingTitle.setVisibility(View.VISIBLE);
        } else {
            LocalVideoBean video = localVideoAdapter.getItem(localVideoAdapter.getSelectedIndex());
            playingTitle.setText(video == null ? "本地视频" : "本地  ·  " + video.getName());
            playingTitle.setVisibility(video == null ? View.GONE : View.VISIBLE);
        }
    }

    private void setWebViewVisibility(boolean visible) {
        boolean showWeb = visible && sourceMode == SourceMode.LIVE;
        totalTvShowManager.getWebViewManager().setVisibility(showWeb);
        if (sourceMode == SourceMode.LIVE) {
            textInfo.setVisibility(showWeb ? View.GONE : View.VISIBLE);
        }
    }

    private void showPlayerMessage(String title, String detail) {
        localPlayerView.setVisibility(View.GONE);
        textInfo.setVisibility(View.VISIBLE);
        pageloadInfoImage.setImageResource(R.drawable.local_video_placeholder);
        pageloadInfo.setText(detail == null || detail.isEmpty() ? title : title + "\n" + detail);
    }

    @Override
    protected void onPause() {
        if (localPlayer != null) {
            localPlayer.pause();
        }
        if (totalTvShowManager != null) {
            totalTvShowManager.pauseWebVideo();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        scanExecutor.shutdownNow();
        if (localPlayer != null) {
            localPlayerView.setPlayer(null);
            localPlayer.release();
        }
        totalTvShowManager.clearCache();
        totalTvShowManager.destroy();
        super.onDestroy();
    }
}
