package com.fangsu;

import com.fangsu.MainClient;
import com.fangsu.lcds.BeijingLcd;
import com.fangsu.train.LcdManager;

public class BeijingAddon {
    public static void init(){
//        throw new IllegalStateException("??北京地铁扩展没公开你怎么拿到的");
        MainClient.addResourceRunnable(()->{
            LcdManager.getInstance().injectLcd("beijing", BeijingLcd::new);
        });
    }
}