package com.alkor.vph.hh.entities;

/**
 * Author: akorobitsyn
 * Date: 05.07.13
 * Time: 15:31
 */
public class VacancyDetails {

    private Vacancy vacancy;
    private String schedule;
    private String experience;

    public VacancyDetails(Vacancy vacancy) {
        this.vacancy = vacancy;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    @Override
    public String toString() {
        return "VacancyDetails{" +
                "vacancy=" + vacancy +
                ", schedule='" + schedule + '\'' +
                ", experience='" + experience + '\'' +
                '}';
    }
}
