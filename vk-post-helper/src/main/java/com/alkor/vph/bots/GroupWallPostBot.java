package com.alkor.vph.bots;

import com.alkor.vph.vk.TokenProvider;
import com.alkor.vph.vk.VKConnector;
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
public class GroupWallPostBot implements Runnable {

    private final VKConnector vkConnector = new VKConnector();

    private final List<BotTask> tasks = new ArrayList<BotTask>();

    private final Post post;

    private WallPoster wallPoster;

    private TokenProvider tokenProvider = new GroupWallPostTokenProvider();

    public GroupWallPostBot(String groupQuery, int count) throws IOException, InterruptedException {
        List<String> attachments = new ArrayList<String>();
        attachments.add("photo-41942656_303510672");
        attachments.add("photo-41942656_303510740");
        attachments.add("photo-41942656_303510832");
        attachments.add("photo-41942656_305383385");
        attachments.add("https://play.google.com/store/apps/details?id=com.alkor.compass.world");

        FileInputStream fileInputStream = new FileInputStream("subway-compass-ad.txt");
        String ad = null;
        try {
            ad = IOUtils.toString(fileInputStream, "UTF-8");
        } finally {
            fileInputStream.close();
        }
        post = new Post();
        post.setMessage(ad);
        post.setAttachments(attachments);

        List<Group> groups = vkConnector.searchGroups(groupQuery, count, tokenProvider.getToken());
        for (Group group : groups) {
            tasks.add(new GroupWallPostTask(vkConnector, group, post));
        }
        wallPoster = new WallPoster(tasks, tokenProvider);
    }

    @Override
    public void run() {
        wallPoster.run();
    }

}
