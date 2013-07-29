package com.alkor.vph.bots;

import com.alkor.vph.hh.HHConnector;
import com.alkor.vph.hh.entities.Vacancy;
import com.alkor.vph.vk.TokenProvider;
import com.alkor.vph.vk.VKConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 05.07.13
 * Time: 17:03
 */
public class VacancyPostBot implements Runnable {

    private final VKConnector vkConnector = new VKConnector();
    private HHConnector hhConnector = new HHConnector();
    private TokenProvider tokenProvider = new VacancyTokenProvider();

    private List<BotTask> tasks = new ArrayList<>();
    private WallPoster wallPoster;

    public VacancyPostBot() throws IOException {
        List<Integer> areas = new ArrayList<Integer>();
        areas.add(1);
        areas.add(2);

        List<Vacancy> vacancies = hhConnector.vacancies(areas, "", true, 1, 100);
        for (Vacancy vacancy : vacancies) {
            tasks.add(new VacancyWallPostTask(vkConnector, vacancy));
        }
        wallPoster = new WallPoster(tasks, tokenProvider);
    }

    @Override
    public void run() {
        wallPoster.run();
    }

}
