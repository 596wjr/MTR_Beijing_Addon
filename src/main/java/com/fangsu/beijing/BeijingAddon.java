package com.fangsu.beijing;

import com.fangsu.MainClient;
import com.fangsu.beijing.sign.BeijingExitSignItem;
import com.fangsu.drawing.diaoban.DiaobanDrawManager;
import com.fangsu.drawing.pids.PidsDrawManager;
import com.fangsu.drawing.ris.RisDrawManager;
import com.fangsu.drawing.sign.SignItemFactory;
import com.fangsu.drawing.sis.SisDrawManager;
import com.fangsu.train.LcdManager;
import com.fangsu.beijing.diaoban.*;
import com.fangsu.beijing.lcds.*;
import com.fangsu.beijing.pids.*;
import com.fangsu.beijing.ris.*;
import com.fangsu.beijing.sis.*;
import com.google.gson.JsonObject;

public class BeijingAddon {
    public static void init() {
//        throw new IllegalStateException("??北京地铁扩展没公开你怎么拿到的");
        MainClient.addResourceRunnable(() -> {
            LcdManager.getInstance().injectLcd("beijing", BeijingLcd::new);
            PidsDrawManager.registerJavaDrawing(
                    "pids_beijing_a",
                    BeijingPidsA::new
            );
            PidsDrawManager.registerJavaDrawing(

                    "pids_beijing_b",

                    BeijingPidsB::new
            );
            RisDrawManager.registerJavaDrawing(

                    "fangsu:ris/beijing_ris_a.js",

                    BeijingRisA::new
            );
            RisDrawManager.registerJavaDrawing(

                    "fangsu:ris/beijing_ris_b.js",

                    BeijingRisB::new
            );
            RisDrawManager.registerJavaDrawing(
                    "beijing_zhanpai_a_a", BeijingZhanpaiAA::new
            );
            SisDrawManager.registerJavaDrawing(

                    "fangsu:sis/beijing_sis_a.js",

                    BeijingSisA::new
            );
            SisDrawManager.registerJavaDrawing(

                    "fangsu:sis/beijing_sis_out_a.js",

                    BeijingSisOuterA::new
            );
            // 注册吊板 Java 绘制
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_a_station_a",
                    "beijing_diaoban_a_station_a",
                    "北京地铁风格a-终点站",
                    BeijingDiaobanAStationA::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_a_station_b",
                    "beijing_diaoban_a_station_b",
                    "北京地铁风格a-当前站",
                    BeijingDiaobanAStationB::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_a_route",
                    "beijing_diaoban_a_route",
                    "北京地铁风格a-线路图",
                    BeijingDiaobanARouteA::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_route_b",
                    "beijing_diaoban_route_b",
                    "北京地铁风格b-线路图",
                    BeijingDiaobanRouteB::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_route_c",
                    "beijing_diaoban_route_c",
                    "北京地铁风格c-线路图",
                    BeijingDiaobanRouteC::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_c_blank",
                    "beijing_diaoban_c_blank",
                    "北京地铁风格c-空",
                    BeijingDiaobanCBlank::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_c3_station_a",
                    "beijing_diaoban_c3_station_a",
                    "北京地铁风格c3-当前站",
                    BeijingDiaobanC3StationA::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_c3_station_b",
                    "beijing_diaoban_c3_station_b",
                    "北京地铁风格c3-终点站",
                    BeijingDiaobanC3StationB::new
            );
            DiaobanDrawManager.registerJavaDrawing(
                    "fangsu.diaoban.beijing_diaoban_route_c1",
                    "beijing_diaoban_route_c1",
                    "北京地铁风格c1-线路图",
                    BeijingDiaobanRouteC1::new
            );
            SignItemFactory.add(BeijingExitSignItem.TYPE, BeijingExitSignItem::new);
            SignItemFactory.EDITOR_ITEMS.add(new BeijingExitSignItem(new JsonObject()));
        });

    }
}