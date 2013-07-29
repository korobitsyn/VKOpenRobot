package com.alkor.vph.bots;

import com.alkor.vph.vk.TokenProvider;
import com.alkor.vph.vk.VKConnector;
import com.alkor.vph.vk.entities.Group;
import com.alkor.vph.vk.entities.User;

import java.io.IOException;
import java.util.*;

/**
 * Author: akorobitsyn
 * Date: 09.07.13
 * Time: 17:58
 */
public class FriendsInviteBot implements Runnable {

//    private static final String greetingTemplate = "Здравствуйте, %s! Меня зовут %s." +
//            "\nЯ рекрутер и занимаюсь поисками квалифицированных специалистов в различных областях. Меня заинтересовал Ваш профиль ВК." +
//            "\nБуду рада быть с Вами на связи и информировать Вас об актуальных вакансиях, которые могут Вас заинтересовать." +
//            "\nС Уважением, %s";

    private static final String greetingTemplate = "Привет, %s! Меня зовут %s. Мы с тобой состоим в одной группе '%s', так что нам есть о чем пообщаться, добавляйся в друзья!";


    private final VKConnector vkConnector = new VKConnector();

//    private final TokenProvider tokenProvider = new VacancyTokenProvider();

    private final TokenProvider tokenProvider = new FriendsInviteTokenProvider();

    private final List<BotTask> tasks = new ArrayList<>();

    private WallPoster wallPoster;

    private Map<Long, Group> userGroups = new HashMap<>();

    private User me;

    public FriendsInviteBot(String groupQuery, int groupsCount, int usersCount) throws IOException, InterruptedException {
        me = vkConnector.getUsers(null, tokenProvider.getToken()).get(0);
        List<Group> groups = vkConnector.searchGroups(groupQuery, groupsCount, tokenProvider.getToken());
        Set<Long> userIds = new HashSet<>();
        for (Group group : groups) {
            List<Long> groupMembers = vkConnector.getGroupMembers(group.getGid(), usersCount, tokenProvider.getToken());
            for (Long userId : groupMembers) {
                userGroups.put(userId, group);
            }
            userIds.addAll(groupMembers);
            Thread.sleep(5000);
        }
        List<User> users = vkConnector.getUsers(userIds, tokenProvider.getToken());
        for (User user : users) {
//            tasks.add(new FriendInviteTask(vkConnector, user, String.format(greetingTemplate, user.getFirstName(), me.getFirstName(), me.getFirstName())));
            tasks.add(new FriendInviteTask(vkConnector, user, String.format(greetingTemplate, user.getFirstName(), me.getFirstName(), userGroups.get(user.getId()).getName())));
        }
        wallPoster = new WallPoster(tasks, tokenProvider);
    }

    @Override
    public void run() {
        wallPoster.run();
    }

}
