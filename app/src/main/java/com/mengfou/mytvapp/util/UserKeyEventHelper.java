package com.mengfou.mytvapp.util;

import com.mengfou.mytvapp.UserKeyEvent;
import java.util.ArrayList;

/**
 * @author 梦否 on 2024/1/14
 * @blog https://mengfou.blog.csdn.net/
 */
public class UserKeyEventHelper {

    private int mSize = 3;
    private final ArrayList<UserKeyEvent> mUserKeyEvents = new ArrayList<>();

    public UserKeyEventHelper(int size) {
        mSize = size;
    }

    public void addKeyEvent(UserKeyEvent userKeyEvent) {
        if(mUserKeyEvents.size() >= mSize) {
          mUserKeyEvents.remove(0);
        }
        mUserKeyEvents.add(userKeyEvent);
    }

    public UserKeyEvent getLastKeyEvent() {
        return mUserKeyEvents.get(mUserKeyEvents.size() - 1);
    }

    public UserKeyEvent getLastDownOrUpKeyEvent() {
        for (int i = mUserKeyEvents.size() - 1; i >= 0 ; i--) {
            if(mUserKeyEvents.get(i) == UserKeyEvent.UP || mUserKeyEvents.get(i) == UserKeyEvent.DOWN) {
                return mUserKeyEvents.get(i);
            }
        }
        return null;
    }
}
