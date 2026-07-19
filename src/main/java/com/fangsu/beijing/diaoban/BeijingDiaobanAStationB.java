package com.fangsu.beijing.diaoban;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.diaoban.BaseDiaobanDrawing;
import com.fangsu.scripting.*;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingDiaobanAStationB extends BaseDiaobanDrawing {

    private static final String FONT_PATH = "mtrsteamloco:fonts/source-han-sans.otf";
    private static final String FONT_BOLD_PATH = "mtrsteamloco:fonts/source-han-sans-bold.otf";

    private Font cachedFont;
    private Font cachedFontBold;

    private Font getFont() {
        if (cachedFont == null) {
            try {
                cachedFont = (Font) JsFunctions.loadResource("font", FONT_PATH);
            } catch (Exception e) {
                cachedFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
            }
        }
        return cachedFont;
    }

    private Font getFontBold() {
        if (cachedFontBold == null) {
            try {
                cachedFontBold = (Font) JsFunctions.loadResource("font", FONT_BOLD_PATH);
            } catch (Exception e) {
                cachedFontBold = new Font(Font.SANS_SERIF, Font.BOLD, 12);
            }
        }
        return cachedFontBold;
    }

    @Override
    public void draw(GraphicsTexture gt, List<RouteSelectInfo> routes,
                     Map<String, Object> drawState, int arrowDirection, int texW, int texH) {
        Graphics2D g = gt.graphics;
        int x = 0, y = 0, w = texW, h = texH;

        Font font = getFont();
        Font fontBold = getFontBold();

        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        RouteDrawer.RouteDrawInfo drawInfo = buildDrawInfo(routes, arrowDirection, texW, texH);

        if (drawInfo.routeInfo() != null) {
            var thisStn = drawInfo.routeInfo().drawStations.get(drawInfo.index());

            g.setColor(drawInfo.routeInfo().routeColor);
            String routeName = drawInfo.routeInfo().routeName;
            if (JsFunctions.isNumLine(routeName) && TextUtil.hasCjkPart(routeName) && TextUtil.hasNonCjkPart(routeName)) {
                int currentX = widthPercent(x, w, 0.1)
                        + G2dTextHelper.drawStrUnified(g, fontBold,
                        String.valueOf(JsFunctions.getCJKLineName(TextUtil.getCjkParts(routeName))),
                        widthPercent(x, w, 0.1), heightPercent(y, h, 0.725), (float) (h * 0.2), 0);
                JsFunctions.jsDrawStrDl(g, fontBold, font, "号线|" + TextUtil.getNonCjkParts(routeName),
                        currentX, heightPercent(y, h, 0.525), h * 0.2, 0, 0);
                currentX = widthPercent(x, w, 0.9)
                        - JsFunctions.jsGetDLStringWidth(g, fontBold, font, "号线|" + TextUtil.getNonCjkParts(routeName), h * 0.2);
                G2dTextHelper.drawStrUnified(g, fontBold,
                        String.valueOf(JsFunctions.getCJKLineName(TextUtil.getCjkParts(routeName))),
                        currentX, heightPercent(y, h, 0.725), (float) (h * 0.225), 2);
            } else {
                JsFunctions.jsDrawStrDl(g, fontBold, font, TextUtil.getNonExtraParts(routeName),
                        widthPercent(x, w, 0.1), heightPercent(y, h, 0.525), h * 0.2, 0, 1);
                JsFunctions.jsDrawStrDl(g, fontBold, font, TextUtil.getNonExtraParts(routeName),
                        widthPercent(x, w, 0.9), heightPercent(y, h, 0.525), h * 0.2, 2, 1);
            }

            g.setColor(Color.BLACK);
            JsFunctions.jsDrawStrDl(g, fontBold, font, thisStn.stationName,
                    widthPercent(x, w, 0.5), heightPercent(y, h, 0.275), h * 0.45, 1, 1);
        }
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }
}
