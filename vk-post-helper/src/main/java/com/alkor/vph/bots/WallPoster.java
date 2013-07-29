package com.alkor.vph.bots;

import com.alkor.vph.captcha.AntigateCaptchaParser;
import com.alkor.vph.captcha.CaptchaParser;
import com.alkor.vph.vk.TokenProvider;
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
public class WallPoster implements Runnable {

    private final Log log = LogFactory.getLog(WallPoster.class);

    private CaptchaParser captchaParser = new AntigateCaptchaParser();

    private final String wallPostFile = "wall-posts.txt";

    private TokenProvider tokenProvider;

    private final List<BotTask> tasks;

    private int currentTask = 0;

    public WallPoster(List<BotTask> tasks, TokenProvider tokenProvider) {
        this.tasks = tasks;
        this.tokenProvider = tokenProvider;
        log.info(String.format("Start token is %s", tokenProvider.getToken()));
    }

    @Override
    public void run() {
        Captcha captcha = null;
        while (currentTask < tasks.size()) {
            try {
                BotTask botTask = tasks.get(currentTask);
                currentTask++;

                if (checkGroup(botTask.getTaskId())) {
                    continue;
                }
                if (botTask.shouldBeSkipped()) {
                    continue;
                }

                MethodResult wallPostResult = botTask.post(captcha, tokenProvider.getToken());
                captcha = null;

                log.info(botTask);
                log.info(wallPostResult);

                if (wallPostResult.isSuccess()) {
                    writeGroup(botTask.getTaskId());
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
                            writeGroup(botTask.getTaskId());
                        } else if ("Access to adding post denied: too many messages sent".equals(wallPostResult.getMessage())) {
                            changeToken();
                        }
                    }
                }
                Thread.sleep(5000);
            } catch (Exception e) {
                log.error("Task stopped by exception", e);
                break;
            }
        }
    }

    private void changeToken() {
        currentTask--;
        tokenProvider.switchToken();
        log.info(String.format("Token has been changed to %s", tokenProvider.getToken()));
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
