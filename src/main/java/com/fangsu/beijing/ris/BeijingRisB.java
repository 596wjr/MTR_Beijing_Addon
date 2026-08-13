package com.fangsu.beijing.ris;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.ris.BaseRisDrawing;
import com.fangsu.mtr.LocalRoute;
import com.fangsu.mtr.LocalRouteDetail;
import com.fangsu.scripting.*;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingRisB extends BaseRisDrawing {

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
                cachedFontBold = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
            }
        }
        return cachedFontBold;
    }

    @Override
    public void draw(GraphicsTexture gt, List<RouteSelectInfo> routes,
                     Map<String, Object> drawState, int arrowDirection, int texW, int texH) {
        RouteDrawer.RouteDrawInfo drawInfo = buildDrawInfo(routes, arrowDirection, texW, texH);

        int x = drawInfo.texArea[0];
        int y = drawInfo.texArea[1];
        int w = drawInfo.texArea[2] - drawInfo.texArea[0];
        int h = drawInfo.texArea[3] - drawInfo.texArea[1];

        Graphics2D g = gt.graphics;

        g.setColor(Color.WHITE);
        g.fillRect(drawInfo.texArea[0], drawInfo.texArea[1], drawInfo.texArea[2], drawInfo.texArea[3]);

        LocalRouteDetail routeInfo = drawInfo.routeInfo;

        if (routeInfo != null) {
            Font font = getFont();
            Font fontBold = getFontBold();
            LocalRoute.CircularState circularState = routeInfo.circularState;

            g.setColor(routeInfo.routeColor);
            g.fillRect(x, heightPercent(y, h, 0.55), w, (int) (h * 0.05));

            g.setColor(Color.BLACK);

            // Draw current station name (centered)
            // 原 jsDrawStrDl: bd=1(居中偏移), d=1(行内居中)
            int mainStrH = (int) (h * 0.325);
            String[] mainLines = routeInfo.drawStations.get(drawInfo.index).stationName.split("\\|");
            int maxMainWidth = (int) (w * 0.7);
            G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, fontBold,
                    widthPercent(x, w, 0.5),
                    heightPercent(y, h, 0.15) + mainStrH,
                    mainStrH, maxMainWidth, 1, 1, mainLines);

            if (drawInfo.index == routeInfo.drawStations.size() - 1 && circularState == LocalRoute.CircularState.NONE) {
                String[] termLines = "终点站|Terminals".split("\\|");
                int termStrH = (int) (h * 0.2);
                int maxTerWidth = (int) (w * 0.6);
                G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, fontBold,
                        widthPercent(x, w, 0.5),
                        heightPercent(y, h, 0.7) + termStrH,
                        termStrH, maxTerWidth, 1, 1, termLines);
            } else {
                int nextIndex = drawInfo.index + 1 == routeInfo.drawStations.size() ? 0 : drawInfo.index + 1;
                // "下一站|Next Station" 保持原样用 jsDrawStrDl（文字短不需要缩放）
                JsFunctions.jsDrawStrDl(g, font, font,
                        "下一站|Next Station",
                        widthPercent(x, w, 0.1), heightPercent(y, h, 0.75), h * 0.15, 0, 0);
                // 下一站站名: 原 jsDrawStrDl(bd=2, d=1)
                int nextStrH = (int) (h * 0.2);
                String[] nextLines = routeInfo.drawStations.get(nextIndex).stationName.split("\\|");
                int nextWidth = G2dTextHelper.getMultiLinesWidth(g, fontBold, fontBold, nextStrH, nextLines);
                int maxNextWidth = (int) (w * 0.5);
                G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, fontBold,
                        widthPercent(x, w, 0.7),
                        heightPercent(y, h, 0.7) + nextStrH,
                        nextStrH, maxNextWidth, 1, 1, nextLines);
            }
        }
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }
}
