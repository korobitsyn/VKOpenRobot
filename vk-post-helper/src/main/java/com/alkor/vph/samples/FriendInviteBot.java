package com.alkor.vph.samples;

import com.alkor.vph.tasks.FriendInviteTask;
import com.alkor.vph.VKBot;
import com.alkor.vph.tasks.VKBotTask;
import com.alkor.vph.captcha.AntigateCaptchaParser;
import com.alkor.vph.vk.VKConnector;
import com.alkor.vph.vk.VKConnectorImpl;
import com.alkor.vph.vk.VKTokenProvider;
import com.alkor.vph.vk.VKTokenProviderImpl;
import com.alkor.vph.vk.entities.Group;
import com.alkor.vph.vk.entities.User;

import java.io.IOException;
import java.util.*;

/**
 * Author: akorobitsyn
 * Date: 29.07.13
 * Time: 21:03
 */
public class FriendInviteBot {

    private final String vkToken = "aac84d2ada6df8e122cdebfdf788ebd0f6ae8bb047395a3fd90c0891b9c8a12a0026707c131856f60fa29";

    private final String antigateToken = "e36ae5781fbcd185906c0325d14e5156";

    private final String groupQuery = "GitHub";

    private final String greetingTemplate = "Hi, %s! My name is %s. We are in the one group with you '%s', so we've something to say each other, friend me!";

    private final long timeOut = 5000;

    private final int groupsCount = 10;

    private final int usersCount = 10;

    private final VKBot vkBot;

    public static void main(String[] params) throws IOException, InterruptedException {
        FriendInviteBot friendInviteBot = new FriendInviteBot();
        friendInviteBot.run();
    }

    public FriendInviteBot() throws IOException, InterruptedException {
        VKConnector vkConnector = VKConnectorImpl.createInstance();
        VKTokenProvider vkTokenProvider = VKTokenProviderImpl.createInstance(vkToken);

        User me = vkConnector.getUsers(null, vkTokenProvider.getToken()).get(0);

        List<Group> groups = vkConnector.searchGroups(groupQuery, groupsCount, vkTokenProvider.getToken());
        Set<Long> userIds = new HashSet<>();
        List<VKBotTask> tasks = new ArrayList<>();
        Map<Long, Group> userGroups = new HashMap<>();
        for (Group group : groups) {
            List<Long> groupMembers = vkConnector.getGroupMembers(group.getGid(), usersCount, vkTokenProvider.getToken());
            for (Long userId : groupMembers) {
                userGroups.put(userId, group);
            }
            userIds.addAll(groupMembers);
            Thread.sleep(timeOut);
        }
        List<User> users = vkConnector.getUsers(userIds, vkTokenProvider.getToken());
        for (User user : users) {
            tasks.add(new FriendInviteTask(vkConnector, user, String.format(greetingTemplate, user.getFirstName(), me.getFirstName(), userGroups.get(user.getId()).getName())));
        }
        vkBot = VKBot.createInstance(tasks, vkTokenProvider, AntigateCaptchaParser.createInstance(antigateToken));
    }

    public void run() {
        Thread thread = new Thread(vkBot);
        thread.run();
    }

}
