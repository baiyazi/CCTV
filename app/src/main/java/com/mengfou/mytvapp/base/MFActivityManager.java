package com.mengfou.mytvapp.base;

import android.app.Activity;

import java.util.Stack;

/**
 * @author 梦否 on 2024/1/20
 * @blog https://mengfou.blog.csdn.net/
 */
class MFActivityManager {
    private final Stack<Activity> activities;

    MFActivityManager() {
        activities = new Stack<>();
    }

    void pop() {
        if(activities.empty()) {
            return;
        }
        activities.pop();
    }

    void push(Activity item) {
        activities.push(item);
    }

    int size() {
        return activities.size();
    }
}
