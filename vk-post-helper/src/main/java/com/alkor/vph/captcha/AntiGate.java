package com.alkor.vph.captcha;

/**
 * Author: akorobitsyn
 * Date: 03.07.13
 * Time: 12:50
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class AntiGate {
    private String key = "";
    private String boundary = "---------FGf4Fh3fdlKR148fdh";
    private int count = 100;
    private int sleap = 1000;

    public void setSleap(int sleap) {
        this.sleap = sleap;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText(String file) {
        try {
            String postImage = postImage(file);
            if (postImage.startsWith("OK")) {
                String substring = postImage.substring(3);

                int id = Integer.parseInt(substring);
                String result = "";
                for (int i = 0; i<count; i++) {
                    result = getResult(id);
                    if (result.startsWith("OK")) {
                        return result.substring(3);
                    }
                    Thread.sleep(sleap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getText(byte[] bytes) {
        try {
            String postImage = postImage(bytes);
            if (postImage.startsWith("OK")) {
                String substring = postImage.substring(3);

                int id = Integer.parseInt(substring);
                String result = "";
                for (int i = 0; i<count; i++) {
                    result = getResult(id);
                    if (result.startsWith("OK")) {
                        return result.substring(3);
                    }
                    try {
                        Thread.sleep(sleap);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getResult(int id) {
        try {
            URL url = new URL("http://antigate.com/res.php?key=" + key
                    + "&action=get&id=" + id);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();

            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "*/*");

            httpURLConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder stringBuilderHttp = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilderHttp.append(line);
            }
            bufferedReader.close();
            httpURLConnection.disconnect();
            return stringBuilderHttp.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String postImage(String file) throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);

        byte[] buffer = new byte[16384];

        for (int len = fileInputStream.read(buffer); len > 0; len = fileInputStream
                .read(buffer)) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        fileInputStream.close();

        byte[] content = byteArrayOutputStream.toByteArray();

        return postImage(content);
    }

    public String postImage(byte[] content) throws Exception {

        URL url = new URL("http://antigate.com/in.php");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setAllowUserInteraction(false);

        String fileContent = new String(content, "8859_1");

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("--" + boundary + "\r\n");
        stringBuilder
                .append("Content-Disposition: form-data; name=\"method\"\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append("post\r\n");
        stringBuilder.append("--" + boundary + "\r\n");
        stringBuilder
                .append("Content-Disposition: form-data; name=\"key\"\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append(key);
        stringBuilder.append("\r\n");
        stringBuilder.append("--" + boundary + "\r\n");
        stringBuilder
                .append("Content-Disposition: form-data; name=\"file\"; filename=\"capcha.jpg\"\r\n");
        stringBuilder.append("Content-Type: image/pjpeg\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append(fileContent + "\r\n");
        stringBuilder.append("--" + boundary + "--");

        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);

        httpURLConnection.setRequestProperty("Content-Length", new Integer(
                stringBuilder.length()).toString());
        httpURLConnection.setRequestProperty("Accept", "*/*");

        httpURLConnection.connect();

        DataOutputStream dataOutputStream = new DataOutputStream(
                httpURLConnection.getOutputStream());

        dataOutputStream.writeBytes(stringBuilder.toString());
        dataOutputStream.close();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream()));
        StringBuilder stringBuilderHttp = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilderHttp.append(line);
        }
        bufferedReader.close();
        httpURLConnection.disconnect();

        return stringBuilderHttp.toString();
    }
}