package com.alkor.vph.samples.hh;

import com.alkor.vph.samples.hh.entities.Vacancy;
import com.alkor.vph.samples.hh.entities.VacancyDetails;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 04.07.13
 * Time: 18:33
 */
public class HHConnector {

    private final Log log = LogFactory.getLog(HHConnector.class);

    private static final String clientId = "NMA08TJGGSL8TJR5G76LT6AE3GAPT9E792NKM2LG1K8EV6738DL473I7MV8O90LG";
    private static final String clientSecret = "TDCDH4HJR399CHVL2VL5JSA6T4JJST2T96HKN4VHV4KK6PSHNQQPP1T5GDV97EOU";
    private final HttpClient client = new DefaultHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    private final static String getTokenUrl = "https://m.hh.ru/oauth/token";
    private final static String meUrl = "https://api.hh.ru/me";
    private final static String vacanciesUrl = "https://api.hh.ru/vacancies?%stext=%s&only_with_salary=%b&period=%d&per_page=%d";
    private final static String vacancyDetailsUrl = "https://api.hh.ru/vacancies/%d";


    //https://m.hh.ru/oauth/authorize?response_type=code&client_id=NMA08TJGGSL8TJR5G76LT6AE3GAPT9E792NKM2LG1K8EV6738DL473I7MV8O90LG
    //Current token: NEI0JVHCT6E0T6PVFNHAQ3AQJBDIBRIR5KKJVOJL3CBTB31BVI26OSNBM75EKEHL

    public String getToken(String code) throws IOException {

        String request = getTokenUrl;
        log.info(request);
        HttpPost httpPost = new HttpPost(getTokenUrl);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
        nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));
        nameValuePairs.add(new BasicNameValuePair("code", code));

        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = client.execute(httpPost);
        String responseBody = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        log.info(responseBody);

        return responseBody;
    }

    public String me(String token) throws IOException {
        String request = meUrl;
        log.info(request);
        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("Authorization", "Bearer " + token);
        HttpResponse response = client.execute(httpGet);

        String responseBody = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        log.info(responseBody);
        return responseBody;
    }

    public List<Vacancy> vacancies(List<Integer> areas, String text, boolean onlyWithSalary, int period, int count) throws IOException {
        String areasUrl = "";
        for (Integer area : areas) {
            areasUrl += String.format("area=%d&", area);
        }
        String request = String.format(vacanciesUrl, areasUrl, text, onlyWithSalary, period, count);
        log.info(request);

        HttpGet httpGet = new HttpGet(request);

        HttpResponse response = client.execute(httpGet);

        String responseBody = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        log.info(responseBody);

        JsonNode jsonNode = objectMapper.readTree(responseBody);

        JsonNode itemsJsonNode = jsonNode.get("items");
        List<Vacancy> vacancies = new ArrayList<Vacancy>();
        for (JsonNode itemJsonNode : itemsJsonNode) {
            Vacancy vacancy = new Vacancy();

            vacancy.setId(itemJsonNode.get("id").asLong());
            vacancy.setName(itemJsonNode.get("name").asText());
            JsonNode salaryJsonNode = itemJsonNode.get("salary");
            if (salaryJsonNode != null) {
                if (!salaryJsonNode.get("from").isNull()) {
                    vacancy.setMinSalary(Long.valueOf(salaryJsonNode.get("from").asLong()));
                }
                if (!salaryJsonNode.get("to").isNull()) {
                    vacancy.setMaxSalary(Long.valueOf(salaryJsonNode.get("to").asLong()));
                }
                if (!salaryJsonNode.get("currency").isNull()) {
                    vacancy.setSalaryCurrency(salaryJsonNode.get("currency").asText());
                }
            }
            vacancy.setAreaName(itemJsonNode.get("area").get("name").asText());

            JsonNode employerJsonNode = itemJsonNode.get("employer");
            vacancy.setEmployerName(employerJsonNode.get("name").asText());
            if (employerJsonNode.get("logo_urls") != null && !employerJsonNode.get("logo_urls").isNull()) {
                vacancy.setEmployerIconUrl(employerJsonNode.get("logo_urls").get("original").asText());
            }

            JsonNode addressJsonNode = itemJsonNode.get("address");
            if (!addressJsonNode.isNull()) {
                if (!addressJsonNode.get("metro").isNull()) {
                    vacancy.setMetro(addressJsonNode.get("metro").get("station_name").asText());
                }
                vacancy.setLatitude(addressJsonNode.get("lat").asDouble());
                vacancy.setLongitude(addressJsonNode.get("lng").asDouble());

            }
            vacancy.setUrl(itemJsonNode.get("alternate_url").asText());
            vacancy.setCreated(itemJsonNode.get("created_at").asText());

            vacancies.add(vacancy);
        }

        return vacancies;
    }

    public VacancyDetails vacancyDetails(Vacancy vacancy) throws IOException {
        String request = String.format(vacancyDetailsUrl, vacancy.getId());
        log.info(request);
        HttpGet httpGet = new HttpGet(request);

        HttpResponse response = client.execute(httpGet);

        String responseBody = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        VacancyDetails vacancyDetails = new VacancyDetails(vacancy);
        if (!jsonNode.get("schedule").isNull()) {
            vacancyDetails.setSchedule(jsonNode.get("schedule").get("id").asText());
        }
        if (!jsonNode.get("experience").isNull()) {
            vacancyDetails.setExperience(jsonNode.get("experience").get("id").asText());
        }
        log.info(responseBody);
        return vacancyDetails;
    }


}
