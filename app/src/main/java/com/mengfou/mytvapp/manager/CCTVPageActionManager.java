package com.mengfou.mytvapp.manager;

import android.webkit.ValueCallback;

import com.mengfou.mytvapp.listener.CCTVPageElementObserver;
import com.mengfou.mytvapp.listener.SimpleCallbackListener;
import com.mengfou.mytvapp.log.Log;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
public class CCTVPageActionManager {
    private static final String TAG = CCTVPageActionManager.class.getSimpleName();
    private final WebViewManager webViewManager;

    public CCTVPageActionManager(WebViewManager webViewManager) {
        this.webViewManager = webViewManager;
    }

    /**
     * 暂停或者播放
     */
    public void play() {
        webViewManager.loadUrl("javascript:(function() { " +
                "var videos = document.getElementsByTagName('video');" +
                "for(var i=0;i<videos.length;i++){" +
                "  try{videos[i].play();}catch(e){}" +
                "}" +
                "})()");
    }

    public void pause() {
        webViewManager.loadUrl("javascript:(function(){" +
                "var videos=document.getElementsByTagName('video');" +
                "for(var i=0;i<videos.length;i++){try{videos[i].pause();}catch(e){}}" +
                "})()");
    }

    public void togglePlayback() {
        webViewManager.loadUrl("javascript:(function(){" +
                "var videos=document.getElementsByTagName('video');" +
                "for(var i=0;i<videos.length;i++){" +
                "try{if(videos[i].paused){videos[i].play();}else{videos[i].pause();}}catch(e){}" +
                "}" +
                "})()");
    }

    /**
     * 全屏
     */
    public void doFullScreen(SimpleCallbackListener listener) {
        webViewManager.evaluateJavascript("javascript:(function() {" +
                "function clickEl(el) {" +
                "  if(!el) return false;" +
                "  try {" +
                "    var event = new MouseEvent('click', {bubbles:true, cancelable:true, view:window});" +
                "    el.dispatchEvent(event);" +
                "    if (typeof el.click === 'function') { el.click(); }" +
                "    return true;" +
                "  } catch (e) { return false; }" +
                "}" +
                "function findFullScreenButton() {" +
                "  var selectors = ['[aria-label*=\\\"全屏\\\"]', '[title*=\\\"全屏\\\"]', 'button[aria-label*=\\\"全屏\\\"]', 'button[title*=\\\"全屏\\\"]', '[data-title*=\\\"全屏\\\"]'];" +
                "  for (var i = 0; i < selectors.length; i++) {" +
                "    var node = document.querySelector(selectors[i]);" +
                "    if (node) return node;" +
                "  }" +
                "  var nodes = document.querySelectorAll('button,div,span,i,a');" +
                "  for (var j = 0; j < nodes.length; j++) {" +
                "    var text = (nodes[j].innerText || nodes[j].textContent || '').trim();" +
                "    if (text.indexOf('全屏') !== -1 || text.indexOf('全屏播放') !== -1) {" +
                "      return nodes[j];" +
                "    }" +
                "  }" +
                "  return null;" +
                "}" +
                "function forceVideoViewport() {" +
                "  var media = document.querySelector('video') || document.querySelector('iframe');" +
                "  if (!media) { return 'no-media'; }" +
                "  var style = document.getElementById('mf-cctv-fullscreen-style');" +
                "  if (!style) {" +
                "    style = document.createElement('style');" +
                "    style.id = 'mf-cctv-fullscreen-style';" +
                "    document.head.appendChild(style);" +
                "  }" +
                "  style.textContent = 'html,body{margin:0!important;padding:0!important;width:100%!important;height:100%!important;overflow:hidden!important;background:#000!important;} video,iframe{background:#000!important;}';" +
                "  var node = media;" +
                "  while (node && node !== document.body) {" +
                "    node.style.setProperty('position', 'fixed', 'important');" +
                "    node.style.setProperty('left', '0', 'important');" +
                "    node.style.setProperty('top', '0', 'important');" +
                "    node.style.setProperty('width', '100vw', 'important');" +
                "    node.style.setProperty('height', '100vh', 'important');" +
                "    node.style.setProperty('max-width', 'none', 'important');" +
                "    node.style.setProperty('max-height', 'none', 'important');" +
                "    node.style.setProperty('margin', '0', 'important');" +
                "    node.style.setProperty('padding', '0', 'important');" +
                "    node.style.setProperty('z-index', '2147483646', 'important');" +
                "    node.style.setProperty('background', '#000', 'important');" +
                "    node = node.parentElement;" +
                "  }" +
                "  if (media.tagName && media.tagName.toLowerCase() === 'video') {" +
                "    media.style.setProperty('object-fit', 'contain', 'important');" +
                "    try { media.play(); } catch(e) {}" +
                "  }" +
                "  var children = document.body ? document.body.children : [];" +
                "  for (var k = 0; k < children.length; k++) {" +
                "    if (!children[k].contains(media)) {" +
                "      children[k].style.setProperty('display', 'none', 'important');" +
                "    }" +
                "  }" +
                "  window.scrollTo(0, 0);" +
                "  return media.tagName ? media.tagName.toLowerCase() : 'media';" +
                "}" +
                "var btn = document.getElementById('player_pagefullscreen_no_player') || findFullScreenButton();" +
                "clickEl(btn);" +
                "var result = forceVideoViewport();" +
                "var tries = 0;" +
                "var timer = setInterval(function() {" +
                "  forceVideoViewport();" +
                "  tries++;" +
                "  if (tries >= 8) { clearInterval(timer); }" +
                "}, 500);" +
                "var video = document.querySelector('video');" +
                "if (video) {" +
                "  try { if (video.webkitEnterFullscreen) { video.webkitEnterFullscreen(); return 'webkit'; } } catch(e) {}" +
                "  try { if (video.requestFullscreen) { video.requestFullscreen(); return 'request'; } } catch(e) {}" +
                "  try { if (video.webkitRequestFullscreen) { video.webkitRequestFullscreen(); return 'webkitRequest'; } } catch(e) {}" +
                "}" +
                "return result;" +
                "})()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onReceiveValue: " + value);
                if(listener != null) {
                    listener.onFinish();
                }
            }
        });
    }

    /**
     * 是否有全屏按钮检查
     */
    public void checkPageFullScreenElement(CCTVPageElementObserver observer) {
        webViewManager.evaluateJavascript( "javascript:(function() {" +
                "var hasButton = !!document.getElementById('player_pagefullscreen_no_player');" +
                "if(!hasButton){" +
                "  var selectors = ['[aria-label*=\\\"全屏\\\"]', '[title*=\\\"全屏\\\"]', 'button[aria-label*=\\\"全屏\\\"]', 'button[title*=\\\"全屏\\\"]', '[data-title*=\\\"全屏\\\"]'];" +
                "  for (var i = 0; i < selectors.length; i++) {" +
                "    if (document.querySelector(selectors[i])) { hasButton = true; break; }" +
                "  }" +
                "}" +
                "return hasButton;" +
                "})()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onReceiveValue2: " + value);
                if(observer != null) {
                    observer.hasFullScreenBtn("true".equals(value));
                }
            }
        });
    }
}
