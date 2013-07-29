package com.alkor.vph.captcha;

import org.apache.commons.io.IOUtils;

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

    private AntiGate antiGate;

    private ConsoleCaptchaParser consoleCaptchaParser = new ConsoleCaptchaParser();

    public AntigateCaptchaParser() {
        antiGate = new AntiGate();
        antiGate.setKey("e36ae5781fbcd185906c0325d14e5156");
    }

    @Override
    public String parseCaptcha(String captchaUrl) throws IOException {
        URL url = new URL(captchaUrl);
        for (int i = 0; i < 3; i++) {
            InputStream is = url.openStream();
            byte[] bytes = IOUtils.toByteArray(is);
            is.close();

            String text = antiGate.getText(bytes);
            if (text != "") {
                return text;
            } else {
                FileOutputStream fileOutputStream = new FileOutputStream("not-parsed-captcha-" + Calendar.getInstance().getTime().getTime() + ".jpg");
                IOUtils.write(bytes, fileOutputStream);
                fileOutputStream.close();
            }
        }
        return consoleCaptchaParser.parseCaptcha(captchaUrl);
    }

}
