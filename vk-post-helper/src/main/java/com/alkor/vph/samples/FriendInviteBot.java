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

    private final String vkToken = "d15e7313f15314650e9d0111669e198d2c5ad43eee4a3b667f80e4f6846ee9303b42a6c0c804538dbff8c";

    private final String antigateToken = "e36ae5781fbcd185906c0325d14e5156";

    private final String groupQuery = "Android";

//    private final String greetingTemplate = "Hi, %s! My name is %s. We are in the one group with you '%s', so we've something to say each other, friend me!";

    private final String greetingTemplate = "Привет! Если у тебя есть смартфон на Android, вступай в группу http://vk.com/club41942656 ;)";

    private final long timeOut = 5000;

    private final int groupsCount = 10;

    private final int usersCount = 1000;

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
            Group group = userGroups.get(user.getId());
            if (group != null) {
                tasks.add(new FriendInviteTask(vkConnector, user, String.format(greetingTemplate, user.getFirstName(), me.getFirstName(), group.getName())));
            } else {
                System.out.println(group);
            }
        }
        vkBot = VKBot.createInstance(tasks, vkTokenProvider, "completed-tasks.txt", AntigateCaptchaParser.createInstance(antigateToken));
    }

    public void run() {
        Thread thread = new Thread(vkBot);
        thread.run();
    }

}
