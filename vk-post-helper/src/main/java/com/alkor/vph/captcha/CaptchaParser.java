package com.alkor.vph.captcha;

import java.io.IOException;

/**
 * Author: akorobitsyn
 * Date: 03.07.13
 * Time: 14:29
 */
public interface CaptchaParser {

    String parseCaptcha(String url) throws IOException;

}
