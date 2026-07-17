package com.fangsu.beijing.pids;

import com.fangsu.blockEntities.BlockEntityPids;
import com.fangsu.drawing.pids.BasePidsDrawing;
import com.fangsu.scripting.G2dTextHelper;
import com.fangsu.scripting.GraphicsTexture;
import com.fangsu.scripting.JsFunctions;
import com.fangsu.scripting.TextUtil;
import com.fangsu.utils.MtrUtil;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingPidsB extends BasePidsDrawing {
    private static final String FONT_PATH = "mtrsteamloco:fonts/source-han-sans.otf";
    private static final String FONT_BOLD_PATH = "mtrsteamloco:fonts/source-han-sans-bold.otf";

    @Override
    public void draw(GraphicsTexture gt, List<MtrUtil.PidsArrivalInfo> arrivalInfoList,
                     Map<String, Object> drawState, int texW, int texH,
                     BlockEntityPids.DrawInfoPids drawInfo) {
        Graphics2D g = gt.graphics;
        int x = 0;
        int y = 0;
        int w = texW;
        int h = texH;

        boolean isCjk = System.currentTimeMillis() % 6000 < 3000;
        Font font;
        try {
            font = (Font) JsFunctions.loadResource("font", FONT_PATH);
        } catch (Exception e) {
            font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }
        Font fontBold;
        try {
            fontBold = (Font) JsFunctions.loadResource("font", FONT_BOLD_PATH);
        } catch (Exception e) {
            fontBold = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        }

        final var info1 = arrivalInfoList.size() >= 1 ? arrivalInfoList.get(0) : null;
        final var route1 = info1 != null ? MtrUtil.getRouteById(info1.routeId) : null;
        final var info2 = arrivalInfoList.size() >= 2 ? arrivalInfoList.get(1) : null;

        // background

        g.setColor(new Color(47, 81, 150));
        g.fillRect(x, y, w, h);
        g.setColor(Color.ORANGE);
        g.fillRect(x, heightPercent(y, h, 0.6), w, Math.round(h * 0.0125f));
        g.fillRect(widthPercent(x, w, 0.35), y, Math.round(h * 0.0125f), Math.round(h * 0.6f));

        g.setStroke(new BasicStroke(h * 0.005f));
        g.drawRoundRect(widthPercent(x, w, 0.025), heightPercent(y, h, 0.15), Math.round(w * 0.3f), Math.round(h * 0.125f), Math.round(h * 0.05f), Math.round(h * 0.05f));
        g.drawRoundRect(widthPercent(x, w, 0.025), heightPercent(y, h, 0.3), Math.round(w * 0.3f), Math.round(h * 0.25f), Math.round(h * 0.05f), Math.round(h * 0.05f));
        g.drawLine(widthPercent(x, w, 0.025), heightPercent(y, h, 0.425), widthPercent(x, w, 0.325), heightPercent(y, h, 0.425));

        g.setColor(Color.BLACK);
        int gap1 = Math.round(w * 0.0125f);
        String lineName = route1 != null ? TextUtil.getCjkMatching(route1.name, isCjk) : "";
        int lineNameStringWidth = Math.min(G2dTextHelper.getUnifiedStringWidth(g, font, lineName, Math.round(h * 0.05f)), Math.round(w * 0.15f));
        StringBuilder dateString = new StringBuilder();
        dateString.append(JsFunctions.formatDate(isCjk));
        dateString.append("  ");
        dateString.append(JsFunctions.formatWeekday(isCjk));
        G2dTextHelper.drawStrUnifiedWithStretch(g, font, dateString.toString(),
                widthPercent(x, w, 0.025) + lineNameStringWidth + 2 * gap1, heightPercent(y, h, 0.15), Math.round(h * 0.05f),
                Math.round(w * 0.3f) - lineNameStringWidth - 2 * gap1, 0);


    }


    private static int heightPercent(int y, int h, double p) {
        return (int) Math.round(y + h * p);
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) Math.round(x + w * p);
    }
}
