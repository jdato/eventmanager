package com.soundcloud.devchallenge.eventmanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowerService {

    private Map<Integer, List<Integer>> followers;

    public FollowerService() {
        followers = new HashMap<>();
    }

    public void addFollower(Integer follower, Integer toBeFollowed) {
        if (followers.containsKey(toBeFollowed)) {
            followers.get(toBeFollowed).add(follower);
        } else {
            List<Integer> subs = new ArrayList<>();
            subs.add(follower);
            followers.put(toBeFollowed, subs);
        }
    }

    public void removeFollower(Integer follower, Integer toBeFollowed) {
        if (followers.containsKey(toBeFollowed)) {
            followers.get(toBeFollowed).remove(follower);
        }
    }

    public List<Integer> getFollowers(Integer followedUser) {
        return followers.get(followedUser);
    }
}
