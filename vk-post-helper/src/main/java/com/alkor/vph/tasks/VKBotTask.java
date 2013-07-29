package com.alkor.vph.tasks;

import com.alkor.vph.vk.entities.Captcha;
import com.alkor.vph.vk.entities.MethodResult;

import java.io.IOException;

/**
 * Author: akorobitsyn
 * Date: 08.07.13
 * Time: 15:36
 */
public interface VKBotTask {

    String getTaskId();

    MethodResult post(Captcha captcha, String token) throws IOException, InterruptedException;

    boolean shouldBeSkipped();

}
