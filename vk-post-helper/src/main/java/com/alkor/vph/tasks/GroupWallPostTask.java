package com.alkor.vph.tasks;

import com.alkor.vph.vk.VKConnector;
import com.alkor.vph.vk.entities.*;

import java.io.IOException;

/**
 * Author: akorobitsyn
 * Date: 08.07.13
 * Time: 15:59
 */
public class GroupWallPostTask implements VKBotTask {

    private VKConnector vkConnector;

    private Group group;

    private Post post;

    public GroupWallPostTask(VKConnector vkConnector, Group group, Post post) {
        this.vkConnector = vkConnector;
        this.group = group;
        this.post = post;
    }

    @Override
    public String getTaskId() {
        return "group-" + String.valueOf(group.getGid());
    }

    @Override
    public MethodResult post(Captcha captcha, String token) throws IOException, InterruptedException {
        if (!group.isMember()) {
            vkConnector.joinGroup(group.getGid(), token);
        }

        WallPostResult wallPostResult = vkConnector.wallPost(-group.getGid(), post, captcha, token);
        return wallPostResult.getMethodResult();
    }

    @Override
    public boolean shouldBeSkipped() {
        return group.isClosed() && !group.isMember();
    }

    @Override
    public String toString() {
        return "GroupWallPostTask{" +
                "vkConnector=" + vkConnector +
                ", group=" + group +
                ", post=" + post +
                '}';
    }
}
