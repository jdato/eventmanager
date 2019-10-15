package com.soundcloud.devchallenge.eventmanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This service takes care of the following functionality. Users can be registered and unregistered in order to
 * receive notifications.
 */
public class FollowerService {

    private Map<Integer, List<Integer>> followers;

    public FollowerService() {
        followers = new HashMap<>();
    }

    /**
     * Adds a follower to the service.
     *
     * @param follower     - Id of user that wants to follow
     * @param toBeFollowed - Id of user to be followed
     */
    public void addFollower(Integer follower, Integer toBeFollowed) {
        if (followers.containsKey(toBeFollowed)) {
            followers.get(toBeFollowed).add(follower);
        } else {
            List<Integer> subs = new ArrayList<>();
            subs.add(follower);
            followers.put(toBeFollowed, subs);
        }
    }

    /**
     * Removes a follower from the service.
     *
     * @param follower     - Id of user that wants to unfollow
     * @param toBeFollowed - Id of user to be unfollowed
     */
    public void removeFollower(Integer follower, Integer toBeFollowed) {
        if (followers.containsKey(toBeFollowed)) {
            followers.get(toBeFollowed).remove(follower);
        }
    }

    /**
     * Returns all the followers of a certain user.
     *
     * @param followedUser - Id of user
     * @return List of users that follow the followedUser
     */
    public List<Integer> getFollowers(Integer followedUser) {
        return followers.get(followedUser);
    }
}
