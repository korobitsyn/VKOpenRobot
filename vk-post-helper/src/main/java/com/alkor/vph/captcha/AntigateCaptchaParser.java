package com.alkor.vph.captcha;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;

/**
 * Author: akorobitsyn
 * Date: 03.07.13
 * Time: 14:33
 */
public class AntigateCaptchaParser implements CaptchaParser {

    private static final String notParsedCaptchaFileNameTemplate = "not-parsed-captcha-%d.jpg";

    private final AntiGate antiGate;

    private int parseTryCount = 3;

    private String notParsedCaptchaPath = "";

    public static CaptchaParser createInstance(String key) {
        return new AntigateCaptchaParser(key);
    }

    public int getParseTryCount() {
        return parseTryCount;
    }

    public void setParseTryCount(int parseTryCount) {
        this.parseTryCount = parseTryCount;
    }

    public String getNotParsedCaptchaPath() {
        return notParsedCaptchaPath;
    }

    public void setNotParsedCaptchaPath(String notParsedCaptchaPath) {
        this.notParsedCaptchaPath = notParsedCaptchaPath;
    }

    @Override
    public String parseCaptcha(String captchaUrl) throws IOException {
        URL url = new URL(captchaUrl);
        for (int i = 0; i < parseTryCount; i++) {
            InputStream is = url.openStream();
            byte[] bytes = IOUtils.toByteArray(is);
            is.close();

            String text = antiGate.getText(bytes);
            if (text != "") {
                return text;
            } else {
                File file = new File(notParsedCaptchaPath, String.format(notParsedCaptchaFileNameTemplate, Calendar.getInstance().getTime().getTime()));
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                IOUtils.write(bytes, fileOutputStream);
                fileOutputStream.close();
            }
        }
        return null;
    }

    private AntigateCaptchaParser(String key) {
        antiGate = new AntiGate();
        antiGate.setKey(key);
    }

}
