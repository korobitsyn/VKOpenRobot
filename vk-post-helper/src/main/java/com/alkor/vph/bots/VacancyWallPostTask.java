package com.alkor.vph.bots;

import com.alkor.vph.hh.entities.Vacancy;
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
 * Date: 08.07.13
 * Time: 16:14
 */
public class VacancyWallPostTask implements BotTask {

    private final Log log = LogFactory.getLog(VacancyWallPostTask.class);

    private String postTemplate = "%s%s\n%s - %s";

    private long aid = 176650017;

    private VKConnector vkConnector;

    private Vacancy vacancy;

    public VacancyWallPostTask(VKConnector vkConnector, Vacancy vacancy) {
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
        WallPostResult wallPostResult = vkConnector.wallPost(null, post, captcha, token);
        vkConnector.setStatus(post.getMessage(), token);
        return wallPostResult.getMethodResult();
    }

    @Override
    public boolean shouldBeSkipped() {
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
        return post;
    }

    private String savePhoto(String photoUrl, String token) throws IOException, InterruptedException {
        URL url = new URL(photoUrl);
        InputStream is = url.openStream();
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();

        String myWallUploadServer = vkConnector.getMyWallUploadServer(token);
        UploadedPhoto uploadedPhoto = vkConnector.uploadPhoto(myWallUploadServer, bytes);
        //Thread.sleep(5000);

        String result = vkConnector.saveMyWallPhoto(uploadedPhoto, token);
        //Thread.sleep(5000);
        return result;
    }

    @Override
    public String toString() {
        return "VacancyWallPostTask{" +
                "postTemplate='" + postTemplate + '\'' +
                ", aid=" + aid +
                ", vkConnector=" + vkConnector +
                ", vacancy=" + vacancy +
                '}';
    }
}
