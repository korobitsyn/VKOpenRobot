package com.alkor.vph.bots;

import com.alkor.vph.vk.TokenProvider;

/**
 * Author: akorobitsyn
 * Date: 08.07.13
 * Time: 17:08
 */
public class VacancyTokenProvider implements TokenProvider {
    @Override
    public String getToken() {
        return "ececb239bd0268bcab13a1b4742d26357506b9ef0d6f96a1f131ac52d58ba9d42fddc5e13fae642b304e5"; //Яна Лаврова (рекрутер)
    }

    @Override
    public void switchToken() {
        //TODO: do nothing
    }
}
