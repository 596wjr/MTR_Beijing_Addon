package com.fangsu.beijing.diaoban;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.diaoban.BaseDiaobanDrawing;
import com.fangsu.scripting.*;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;
import com.fangsu.utils.ColorUtil;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingDiaobanC3StationA extends BaseDiaobanDrawing {

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

        RouteDrawer.RouteDrawInfo drawInfo = buildDrawInfo(routes, arrowDirection, texW, texH);

        if (drawInfo.routeInfo() != null) {
            Font font = getFont();
            Font fontBold = getFontBold();
            int headAlign = arrowDirection;

            int widthStationName = JsFunctions.jsGetDLStringWidth(g, fontBold, fontBold,
                    drawInfo.routeInfo().drawStations.get(drawInfo.routeInfo().drawStations.size() - 1).stationName, h * 0.5);
            int widthRouteName;
            String routeName = drawInfo.routeInfo().routeName;
            if (JsFunctions.isNumLine(routeName) && TextUtil.hasCjkPart(routeName) && TextUtil.hasNonCjkPart(routeName)) {
                widthRouteName = JsFunctions.jsGetDLStringWidth(g, fontBold, fontBold,
                        "号线|" + TextUtil.getNonCjkParts(routeName), h * 0.2)
                        + G2dTextHelper.getUnifiedStringWidth(g, fontBold,
                        String.valueOf(JsFunctions.getCJKLineName(TextUtil.getCjkParts(routeName))), (float) (h * 0.225));
            } else {
                widthRouteName = JsFunctions.jsGetDLStringWidth(g, fontBold, fontBold, routeName, h * 0.2);
            }
            int nextStationWidth = JsFunctions.jsGetDLStringWidth(g, fontBold, fontBold, "下一站|Next Station", h * 0.2)
                    + JsFunctions.jsGetDLStringWidth(g, fontBold, fontBold,
                    drawInfo.routeInfo().drawStations.get(drawInfo.index() + 1).stationName, h * 0.2)
                    + (int) (h * 0.2);

            int maxWidth = Math.max(widthRouteName, nextStationWidth);
            int leftBaseX = widthPercent(x, w, 0.5) - (int) ((h * 0.3 + maxWidth + widthStationName) * 0.5) + maxWidth / 2;
            int rightBaseX = widthPercent(x, w, 0.5) + (int) ((h * 0.3 + maxWidth + widthStationName) * 0.5) - widthStationName / 2;

            g.setColor(ColorUtil.isLightColor(drawInfo.routeInfo().routeColor) ? Color.BLACK : Color.WHITE);

            if (JsFunctions.isNumLine(routeName) && TextUtil.hasCjkPart(routeName) && TextUtil.hasNonCjkPart(routeName)) {
                int numWidth = G2dTextHelper.getUnifiedStringWidth(g, fontBold,
                        String.valueOf(JsFunctions.getCJKLineName(TextUtil.getCjkParts(routeName))), (float) (h * 0.225));
                int lineNameWidth = JsFunctions.jsGetDLStringWidth(g, fontBold, fontBold,
                        "号线|" + TextUtil.getNonCjkParts(routeName), h * 0.2);
                G2dTextHelper.drawStrUnified(g, fontBold,
                        String.valueOf(JsFunctions.getCJKLineName(TextUtil.getCjkParts(routeName))),
                        leftBaseX + numWidth / 2.0 - lineNameWidth / 2.0,
                        heightPercent(y, h, 0.45), h * 0.225, 2);
            } else {
                JsFunctions.jsDrawStrDl(g, fontBold, fontBold, routeName,
                        leftBaseX, heightPercent(y, h, 0.25), h * 0.2, 1, 1);
            }

            JsFunctions.jsDrawStrDl(g, fontBold, fontBold, "下一站|Next Station",
                    leftBaseX - (int) (h * 0.1), heightPercent(y, h, 0.55), h * 0.2, 2, 2);
            JsFunctions.jsDrawStrDl(g, fontBold, fontBold,
                    drawInfo.routeInfo().drawStations.get(drawInfo.index() + 1).stationName,
                    leftBaseX + (int) (h * 0.1), heightPercent(y, h, 0.55), h * 0.2, 0, 0);

            JsFunctions.jsDrawStrDl(g, fontBold, fontBold,
                    drawInfo.routeInfo().drawStations.get(drawInfo.routeInfo().drawStations.size() - 1).stationName,
                    rightBaseX, heightPercent(y, h, 0.25), h * 0.5, 1, 1);
        }
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }
}
