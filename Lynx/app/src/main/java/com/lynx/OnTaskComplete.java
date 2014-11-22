package com.lynx;

/**
 * Created by Vince on 11/22/2014.
 */
public interface OnTaskComplete {
    void OnTaskCompleted(String displayName, boolean fb, boolean linkedin, boolean google, boolean twitter);
}
