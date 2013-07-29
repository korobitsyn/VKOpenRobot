package com.alkor.vph.bots;

import com.alkor.vph.vk.TokenProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: akorobitsyn
 * Date: 03.07.13
 * Time: 14:53
 */
public class GroupWallPostTokenProvider implements TokenProvider {

    private List<String> tokens = new ArrayList<String>();
    private int index = 0;

    //getting token
    //https://oauth.vk.com/authorize?client_id=3741208&scope=groups,wall,photos,friends,status,offline&redirect_uri=http://oauth.vk.com/blank.html&display=page&response_type=token

    public GroupWallPostTokenProvider() {
//        tokens.add("aac84d2ada6df8e122cdebfdf788ebd0f6ae8bb047395a3fd90c0891b9c8a12a0026707c131856f60fa29"); //me

//        tokens.add("37cb0b3816c2b56fcf76918bc5586124fe7c95a40076024e047531386786395568b6e26f0673384f71362"); //Галина Харламова
        tokens.add("018c85e49278e1e37c6f90f814ed7ce2edefc1d29c9a5d17bf93946cd22312090f6317e90a2eafd60efe2"); //Рената Токарева
//        tokens.add("9855cb62100ce5bc90d820385f8d917f44b4589f0659e7c0d3063c7d476f64c7a5351e12fb638e3531ec1"); //Яна Лаврова (рекрутер)
//        tokens.add("3bb12487ec0a667b3d6def3ef580eda05ef59fc5b83e3de32334aacfba4ee9f5503639807ed6ae8b0af92"); //Кирилл Волков
        tokens.add("a78433840238e2ee0554b3fcd20c2e744b52594f123049ebc0d22707d30cda940a4bef40663ba9c2028aa"); //Василий Новиков
        tokens.add("ce9788a31c9049ed916f74a69fc1381589047b2023ebabc31938fb218287254d7df9daeac6a699c22caca"); //Мирослава Долгорукова

        tokens.add("dfb24f983364cc811d55cd008379aa57f3b56416980ce6ffd598bdfc4f9e0a861e16c66dd916a483d6172");
        tokens.add("3d640c2c80c4a9aef839873f5c51c281ae46c45769c47701bf473bf049ff900100187bbe8e095f250f3a7");
        tokens.add("ffa13b94ca5d1bebd6fea4501cb3244869679fe3ca2b34d1ae2c02b3c57e1ded251fd78a9a1ac9c58c63f");
        tokens.add("830ef9cd201cb1ad017bcf2c4ba674bc033c411db028ddb7ab6c1084c0b294990d4349319df5e8703daa2"); //Тамара Козлова
    }

    @Override
    public String getToken() {
        return tokens.get(index);
    }

    @Override
    public void switchToken() {
        index++;
        if (index >= tokens.size()) {
            index = 0;
        }
    }
}
