package com.lynx;

import java.util.List;

/**
 * Created by Vince on 11/22/2014.
 */
public interface OnTaskCompleteRequest {
    void OnTaskCompleted(List<String> usernames, List<String> reqIds);
}
