package com.alkor.vph.captcha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Author: akorobitsyn
 * Date: 03.07.13
 * Time: 14:30
 */
public class ConsoleCaptchaParser implements CaptchaParser {

    @Override
    public String parseCaptcha(String url) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(String.format("Enter captcha: %s", url));
        return br.readLine();
    }


}
