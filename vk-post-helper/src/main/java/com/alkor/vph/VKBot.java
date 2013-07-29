package com.alkor.vph;

import com.alkor.vph.captcha.CaptchaParser;
import com.alkor.vph.tasks.VKBotTask;
import com.alkor.vph.vk.VKTokenProvider;
import com.alkor.vph.vk.entities.Captcha;
import com.alkor.vph.vk.entities.MethodResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 08.07.13
 * Time: 15:35
 */
public class VKBot implements Runnable {

    private final Log log = LogFactory.getLog(VKBot.class);

    private final VKTokenProvider vkTokenProvider;

    private final CaptchaParser captchaParser;

    private final String wallPostFile;

    private final long sleepTime;

    private final List<VKBotTask> tasks;

    private int currentTask = 0;


    public static VKBot createInstance(List<VKBotTask> tasks, VKTokenProvider vkTokenProvider, CaptchaParser captchaParser) {
        return new VKBot(tasks, vkTokenProvider, captchaParser, "completed-tasks.txt", 5000);
    }

    public static VKBot createInstance(List<VKBotTask> tasks, VKTokenProvider vkTokenProvider, CaptchaParser captchaParser, String wallPostFile, long sleepTime) {
        return new VKBot(tasks, vkTokenProvider, captchaParser, wallPostFile, sleepTime);
    }

    @Override
    public void run() {
        Captcha captcha = null;
        while (currentTask < tasks.size()) {
            try {
                VKBotTask VKBotTask = tasks.get(currentTask);
                currentTask++;

                if (checkGroup(VKBotTask.getTaskId())) {
                    continue;
                }
                if (VKBotTask.shouldBeSkipped()) {
                    continue;
                }

                MethodResult wallPostResult = VKBotTask.post(captcha, vkTokenProvider.getToken());
                captcha = null;

                log.info(VKBotTask);
                log.info(wallPostResult);

                if (wallPostResult.isSuccess()) {
                    writeGroup(VKBotTask.getTaskId());
                } else {
                    if (wallPostResult.getErrorCode() == 5) {
                        changeToken();
                        continue;
                    }
                    if (wallPostResult.getErrorCode() == 14) {
                        captcha = wallPostResult.getCaptcha();
                        captcha.setCaptchaKey(captchaParser.parseCaptcha(captcha.getCaptchaImg()));
                        log.info(String.format("Captcha requested: %s", captcha));

                        currentTask--;
                        continue;
                    }
                    if (wallPostResult.getErrorCode() == 214) {
                        if ("Access to adding post denied: access to the wall is closed".equals(wallPostResult.getMessage())) {
                            writeGroup(VKBotTask.getTaskId());
                        } else if ("Access to adding post denied: too many messages sent".equals(wallPostResult.getMessage())) {
                            changeToken();
                        }
                    }
                }
                Thread.sleep(sleepTime);
            } catch (Exception e) {
                log.error("Task stopped by exception", e);
                break;
            }
        }
    }

    private VKBot(List<VKBotTask> tasks, VKTokenProvider vkTokenProvider, CaptchaParser captchaParser, String wallPostFile, long sleepTime) {
        this.vkTokenProvider = vkTokenProvider;
        this.captchaParser = captchaParser;
        this.tasks = tasks;
        this.wallPostFile = wallPostFile;
        this.sleepTime = sleepTime;

        log.info(String.format("Start token is %s", vkTokenProvider.getToken()));
    }

    private void changeToken() {
        currentTask--;
        vkTokenProvider.switchToken();
        log.info(String.format("Token has been changed to %s", vkTokenProvider.getToken()));
    }

    private void writeGroup(String gid) throws IOException {
        BufferedWriter output = new BufferedWriter(new FileWriter(wallPostFile, true));
        try {
            output.append(String.valueOf(gid));
            output.newLine();
        } finally {
            output.close();
        }
    }

    private boolean checkGroup(String gid) throws IOException {
        File file = new File(wallPostFile);
        if (!file.exists()) {
            return false;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, "UTF-8"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals(String.valueOf(gid))) {
                    return true;
                }
            }
        } finally {
            fileInputStream.close();
        }
        return false;
    }

}
