package com.alkor.vph.tasks;

import com.alkor.vph.vk.VKConnector;
import com.alkor.vph.vk.entities.AddFriendResult;
import com.alkor.vph.vk.entities.Captcha;
import com.alkor.vph.vk.entities.MethodResult;
import com.alkor.vph.vk.entities.User;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Author: akorobitsyn
 * Date: 09.07.13
 * Time: 19:40
 */
public class FriendInviteTask implements VKBotTask {

    private static final long MIN_AGE = 18l * 365l * 24l * 60l * 60l * 1000l;

    private DateFormat formatter = new SimpleDateFormat("d.M.yyyy");

    private VKConnector vkConnector;

    private User user;

    private String greeting;

    public FriendInviteTask(VKConnector vkConnector, User user, String greeting) {
        this.vkConnector = vkConnector;
        this.user = user;
        this.greeting = greeting;
    }

    @Override
    public String getTaskId() {
        return "firend-invite-" + user.getId();
    }

    @Override
    public MethodResult post(Captcha captcha, String token) throws IOException, InterruptedException {
        AddFriendResult addFriendResult = vkConnector.addFriend(user.getId(), greeting, captcha, token);
        return addFriendResult.getMethodResult();
    }

    @Override
    public boolean shouldBeSkipped() {
        if (user.getCityId() != 1 && user.getCityId() != 2) {
            return true;
        }
        if (user.getBdate() == null) {
             return true;
        }
        try {
            Date date = formatter.parse(user.getBdate());
            if (Calendar.getInstance().getTime().getTime() - date.getTime() < MIN_AGE) {
                return true;
            }
            System.out.println();
        } catch (ParseException e) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "FriendInviteTask{" +
                "vkConnector=" + vkConnector +
                ", user=" + user +
                ", greeting='" + greeting + '\'' +
                '}';
    }
}
