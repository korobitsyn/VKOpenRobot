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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 02.07.13
 * Time: 10:40
 */
public class GroupWallPostBot {

    private final String vkToken = "d15e7313f15314650e9d0111669e198d2c5ad43eee4a3b667f80e4f6846ee9303b42a6c0c804538dbff8c";

    private final String antigateToken = "e36ae5781fbcd185906c0325d14e5156";

    private final static String groupQuery = "метро";

    private List<String> photoFileNames = Arrays.asList("content/screen1.png", "content/screen2.png", "content/screen3.png", "content/screen4.png", "content/screen5.png", "content/screen6.png");

    private String attachedLink = "https://play.google.com/store/apps/details?id=com.alkor.compass.world";

    private final static String messageTextFileName = "content/group-wall-post.txt";

    private final static int groupsCount = 1000;

    private final VKBot vkBot;

    private VKConnector vkConnector;

    private String savePhoto(String photoFileName, String token) throws IOException, InterruptedException {
        InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(photoFileName);
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(fileInputStream);
        } finally {
            fileInputStream.close();
        }
        return vkConnector.uploadWallPhoto(token, bytes);
    }

    public static void main(String[] params) throws IOException, InterruptedException {
        GroupWallPostBot groupWallPostBot = new GroupWallPostBot();
        groupWallPostBot.run();
    }

    public GroupWallPostBot() throws IOException, InterruptedException {
        vkConnector = VKConnectorImpl.createInstance();
        VKTokenProvider vkTokenProvider = VKTokenProviderImpl.createInstance(vkToken);

        InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(messageTextFileName);
        String ad = null;
        try {
            ad = IOUtils.toString(fileInputStream, "UTF-8");
        } finally {
            fileInputStream.close();
        }

        List<String> attachments = new ArrayList<>();
        attachments.add(attachedLink);
        for (String photoFileName : photoFileNames) {
            attachments.add(savePhoto(photoFileName, vkToken));
        }
        Post post = new Post();
        post.setMessage(ad);
        post.setAttachments(attachments);

        List<Group> groups = vkConnector.searchGroups(groupQuery, groupsCount, vkTokenProvider.getToken());
        List<VKBotTask> tasks = new ArrayList<VKBotTask>();
        for (Group group : groups) {
            tasks.add(new GroupWallPostTask(vkConnector, group, post));
        }
        vkBot = VKBot.createInstance(tasks, vkTokenProvider, "group-wall-posts.txt", AntigateCaptchaParser.createInstance(antigateToken));
    }

    public void run() {
        Thread thread = new Thread(vkBot);
        thread.run();
    }

}
