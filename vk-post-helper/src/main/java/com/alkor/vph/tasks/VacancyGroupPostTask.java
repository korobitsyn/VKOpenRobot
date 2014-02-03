package com.alkor.vph.tasks;

import com.alkor.vph.samples.hh.entities.Vacancy;
import com.alkor.vph.vk.VKConnector;
import com.alkor.vph.vk.entities.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 03.09.13
 * Time: 19:01
 */
public class VacancyGroupPostTask implements VKBotTask {

    private final static String postTemplate = "%s%s\n%s - %s";

    private final static long gid = -57947576;

    private final Log log = LogFactory.getLog(VacancyGroupPostTask.class);

    private VKConnector vkConnector;

    private Vacancy vacancy;

    private long minSalary = 100000;

    public VacancyGroupPostTask(VKConnector vkConnector, Vacancy vacancy) {
        this.vkConnector = vkConnector;
        this.vacancy = vacancy;
    }

    @Override
    public String getTaskId() {
        return "vacancy-" + String.valueOf(vacancy.getId());
    }

    @Override
    public MethodResult post(Captcha captcha, String token) throws IOException, InterruptedException {
        Post post = createPost(vacancy, token);
        WallPostResult wallPostResult = vkConnector.wallPost(gid, post, captcha, token);
        if (wallPostResult.getPostId() == -13) {
            wallPostResult.setSuccess(false);
            wallPostResult.setErrorCode(5);
            wallPostResult.setMessage("PostId = -13");
        }
        //TODO: set group status
        //vkConnector.setStatus(post.getMessage(), token);
        return wallPostResult.getMethodResult();
    }

    @Override
    public boolean shouldBeSkipped() {
        if (vacancy.getMaxSalary() != null && vacancy.getMaxSalary() < minSalary) {
            return true;
        }
        if (vacancy.getMinSalary() == null || vacancy.getMinSalary() < minSalary) {
            return true;
        }
        return false;
    }

    private Post createPost(Vacancy vacancy, String token) {
        Post post = new Post();
        String salary = "";
        if (vacancy.getMinSalary() != null) {

            if (vacancy.getMinSalary().equals(vacancy.getMaxSalary())) {
                salary = String.valueOf(vacancy.getMinSalary());
            } else {
                salary = " от " + String.valueOf(vacancy.getMinSalary());
                if (vacancy.getMaxSalary() != null) {
                    salary += (" до " + String.valueOf(vacancy.getMaxSalary()));
                }
            }
        } else {
            if (vacancy.getMaxSalary() != null) {
                salary = " до " + String.valueOf(vacancy.getMaxSalary());
            }
        }
        if (!salary.isEmpty()) {
            salary += (" " + vacancy.getSalaryCurrencyName());
        }
        String location = vacancy.getAreaName();
        if (vacancy.getMetro() != null) {
            location += (", м." + vacancy.getMetro());
        }
        String message = String.format(postTemplate, vacancy.getName(), salary, vacancy.getEmployerName(), location);
        post.setMessage(message);

        List<String> attachments = new ArrayList<String>();
        attachments.add(vacancy.getUrl());
        try {
            attachments.add(savePhoto(vacancy.getEmployerIconUrl(), token));
        } catch (Exception e) {
            log.error("Employer url hasn't been saved", e);
        }
        post.setAttachments(attachments);

        post.setLatitude(vacancy.getLatitude());
        post.setLongitude(vacancy.getLongitude());
        post.setFromGroup(true);
        return post;
    }

    private String savePhoto(String photoUrl, String token) throws IOException, InterruptedException {
        URL url = new URL(photoUrl);
        InputStream is = url.openStream();
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return vkConnector.uploadWallPhoto(token, bytes);
    }


}
