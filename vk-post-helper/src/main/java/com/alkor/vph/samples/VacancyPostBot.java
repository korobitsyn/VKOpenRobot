package com.alkor.vph.samples;

import com.alkor.vph.VKBot;
import com.alkor.vph.captcha.AntigateCaptchaParser;
import com.alkor.vph.samples.hh.HHConnector;
import com.alkor.vph.samples.hh.entities.Vacancy;
import com.alkor.vph.tasks.GroupWallPostTask;
import com.alkor.vph.tasks.VKBotTask;
import com.alkor.vph.tasks.VacancyGroupPostTask;
import com.alkor.vph.vk.VKConnector;
import com.alkor.vph.vk.VKConnectorImpl;
import com.alkor.vph.vk.VKTokenProvider;
import com.alkor.vph.vk.VKTokenProviderImpl;
import com.alkor.vph.vk.entities.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 03.09.13
 * Time: 18:44
 */
public class VacancyPostBot {

    private final String vkToken = "7b07f2b695ce0e3803afca3d6f9f64890ef8ee065a85003f21262cd2fa50f8b07d25b6e7f21a3a9276b71";

    private final String antigateToken = "e36ae5781fbcd185906c0325d14e5156";

    private String vacancyQuery = "java";

    private final VKBot vkBot;

    private VKConnector vkConnector;

    private HHConnector hhConnector;

    public static void main(String[] params) throws IOException, InterruptedException {
        VacancyPostBot vacancyPostBot = new VacancyPostBot();
        vacancyPostBot.run();
    }

    public VacancyPostBot() throws IOException {
        vkConnector = VKConnectorImpl.createInstance();
        VKTokenProvider vkTokenProvider = VKTokenProviderImpl.createInstance(vkToken);

        hhConnector = new HHConnector();

        List<Integer> areas = new ArrayList<Integer>();
        areas.add(1);
        areas.add(2);
        List<Vacancy> vacancies = hhConnector.vacancies(areas, vacancyQuery, true, 1, 100);

        List<VKBotTask> tasks = new ArrayList<VKBotTask>();
        for (Vacancy vacancy : vacancies) {
            tasks.add(new VacancyGroupPostTask(vkConnector, vacancy));
        }

        vkBot = VKBot.createInstance(tasks, vkTokenProvider, "vacancy-posts.txt", AntigateCaptchaParser.createInstance(antigateToken));

    }

    public void run() {
        Thread thread = new Thread(vkBot);
        thread.run();
    }

}
