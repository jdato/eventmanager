package com.soundcloud.devchallenge.eventmanager.service;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FollowerServiceTest {
    @Test
    public void testFollowerService() {
        FollowerService followerService = new FollowerService();
        followerService.addFollower(1, 2);
        followerService.addFollower(1, 3);
        followerService.addFollower(2, 3);

        List<Integer> followers = followerService.getFollowers(3);
        Assert.assertEquals(2, followers.size());
        Assert.assertEquals(1, followers.get(0).intValue());
        Assert.assertEquals(2, followers.get(1).intValue());

        followerService.removeFollower(2, 3);
        Assert.assertEquals(1, followers.size());
        Assert.assertEquals(1, followers.get(0).intValue());
    }
}
