package com.soundcloud.devchallenge.eventmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowerService {

    Logger LOG = LoggerFactory.getLogger(FollowerService.class);

    // List of followed users with followers
    private Map<Integer, List<Integer>> followers;

    public FollowerService() {
        followers = new HashMap<>();
    }

    public void addFollower(Integer follower, Integer toBeFollowed) {
        if (followers.containsKey(toBeFollowed)) {
            followers.get(toBeFollowed).add(follower);
            //LOG.debug("Added {} to {}", follower, toBeFollowed);
        } else {
            List subs = new ArrayList<>();
            subs.add(follower);
            followers.put(toBeFollowed, subs);
            //LOG.debug("Added {} to {}", follower, toBeFollowed);
        }
    }

    public void removeFollower(Integer follower, Integer toBeFollowed) {
        if (followers.containsKey(toBeFollowed)) {
            followers.get(toBeFollowed).remove(follower);
            //LOG.debug("Rmovd {} fr {}", follower, toBeFollowed);
        }
    }

    public List<Integer> getFollowers(Integer followedUser) {
        return followers.get(followedUser);
    }
}
