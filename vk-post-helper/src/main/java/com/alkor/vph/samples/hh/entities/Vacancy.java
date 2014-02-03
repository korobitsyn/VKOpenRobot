package com.alkor.vph.samples.hh.entities;

/**
 * Author: akorobitsyn
 * Date: 05.07.13
 * Time: 12:52
 */
public class Vacancy {

    private long id;
    private String name;
    private Long minSalary;
    private Long maxSalary;
    private String salaryCurrency;
    private String areaName;
    private String employerName;
    private String employerIconUrl;
    private Double latitude;
    private Double longitude;
    private String metro;
    private String url;
    private String created;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Long minSalary) {
        this.minSalary = minSalary;
    }

    public Long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public String getSalaryCurrencyName() {
        if ("RUR".equals(salaryCurrency)) {
            return "руб.";
        } else if ("USD".equals(salaryCurrency)) {
            return "$";
        }
        return salaryCurrency;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerIconUrl() {
        return employerIconUrl;
    }

    public void setEmployerIconUrl(String employerIconUrl) {
        this.employerIconUrl = employerIconUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMetro() {
        return metro;
    }

    public void setMetro(String metro) {
        this.metro = metro;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", minSalary=" + minSalary +
                ", maxSalary=" + maxSalary +
                ", salaryCurrency='" + salaryCurrency + '\'' +
                ", areaName='" + areaName + '\'' +
                ", employerName='" + employerName + '\'' +
                ", employerIconUrl='" + employerIconUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", metro='" + metro + '\'' +
                ", url='" + url + '\'' +
                ", created=" + created +
                '}';
    }
}
