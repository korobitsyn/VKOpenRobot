package com.alkor.vph.samples;

import com.alkor.vph.tasks.GroupWallPostTask;
import com.alkor.vph.VKBot;
import com.alkor.vph.tasks.VKBotTask;
import com.alkor.vph.captcha.AntigateCaptchaParser;
import com.alkor.vph.vk.VKConnectorImpl;
import com.alkor.vph.vk.VKTokenProviderImpl;
import com.alkor.vph.vk.VKConnector;
import com.alkor.vph.vk.VKTokenProvider;
import com.alkor.vph.vk.entities.Group;
import com.alkor.vph.vk.entities.Post;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 02.07.13
 * Time: 10:40
 */
public class GroupWallPostBot {

    private final static String vkToken = "aac84d2ada6df8e122cdebfdf788ebd0f6ae8bb047395a3fd90c0891b9c8a12a0026707c131856f60fa29";

    private final static String antigateToken = "e36ae5781fbcd185906c0325d14e5156";

    private final static String groupQuery = "GitHub";

    private final static List<String> attachments = new ArrayList<String>();
    {
        attachments.add("photo-41942656_303510672");
        attachments.add("photo-41942656_303510740");
        attachments.add("photo-41942656_303510832");
        attachments.add("photo-41942656_305383385");
        attachments.add("https://play.google.com/store/apps/details?id=com.alkor.compass.world");
    }

    private final static String messageTextFileName = "subway-compass-ad.txt";

    private final static int groupsCount = 10;

    private final VKBot vkBot;

    public static void main(String[] params) throws IOException, InterruptedException {
        GroupWallPostBot groupWallPostBot = new GroupWallPostBot();
        groupWallPostBot.run();
    }

    public GroupWallPostBot() throws IOException {
        VKConnector vkConnector = VKConnectorImpl.createInstance();
        VKTokenProvider vkTokenProvider = VKTokenProviderImpl.createInstance(vkToken);

        FileInputStream fileInputStream = new FileInputStream(messageTextFileName);
        String ad = null;
        try {
            ad = IOUtils.toString(fileInputStream, "UTF-8");
        } finally {
            fileInputStream.close();
        }
        Post post = new Post();
        post.setMessage(ad);
        post.setAttachments(attachments);

        List<Group> groups = vkConnector.searchGroups(groupQuery, groupsCount, vkTokenProvider.getToken());
        List<VKBotTask> tasks = new ArrayList<VKBotTask>();
        for (Group group : groups) {
            tasks.add(new GroupWallPostTask(vkConnector, group, post));
        }
        vkBot = VKBot.createInstance(tasks, vkTokenProvider, AntigateCaptchaParser.createInstance(antigateToken));
    }

    public void run() {
        Thread thread = new Thread(vkBot);
        thread.run();
    }

}
