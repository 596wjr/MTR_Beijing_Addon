package com.fangsu.beijing.diaoban;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.diaoban.BaseDiaobanDrawing;
import com.fangsu.mtr.LocalRoute;
import com.fangsu.mtr.LocalRouteDetail;
import com.fangsu.scripting.*;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingDiaobanAStationA extends BaseDiaobanDrawing {

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

    private static java.awt.image.BufferedImage loadImg(String path) {
        try {
            return (java.awt.image.BufferedImage) JsFunctions.loadResource("img", path);
        } catch (Exception e) {
            return null;
        }
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
        LocalRouteDetail routeInfo = drawInfo.routeInfo();

        if (routeInfo != null) {
            LocalRoute.CircularState circularState = routeInfo.circularState;
            int headAlign = arrowDirection;

            String headText = circularState == LocalRoute.CircularState.NONE
                    ? routeInfo.drawStations.get(routeInfo.drawStations.size() - 1).stationName
                    : routeInfo.drawStations.get(drawInfo.index() + 1).stationName;

            g.setColor(Color.BLACK);
            if (drawInfo.index() == routeInfo.drawStations.size() - 1) {
                // 终点站
                if (TextUtil.hasCjkPart(headText)) {
                    if (TextUtil.hasNonCjkPart(headText)) {
                        JsFunctions.jsDrawStrDl(g, fontBold, font, "终点站|Terminus",
                                widthPercent(x, w, 0.5), heightPercent(y, h, 0.3), h * 0.4, 1, 1);
                    } else {
                        G2dTextHelper.drawStrUnified(g, fontBold, "终点站",
                                widthPercent(x, w, 0.5), heightPercent(y, h, 0.65), (float) (h * 0.3), 1);
                    }
                } else {
                    G2dTextHelper.drawStrUnified(g, fontBold, "Terminus",
                            widthPercent(x, w, 0.5), heightPercent(y, h, 0.65), (float) (h * 0.3), 1);
                }
            } else {
                int cjkWidth;
                int nonCjkWidth;

                cjkWidth = TextUtil.hasCjkPart(headText)
                        ? (TextUtil.hasNonCjkPart(headText)
                        ? G2dTextHelper.getUnifiedStringWidth(g, font, "开往 ", (float) (h * 0.2))
                        + G2dTextHelper.getUnifiedStringWidth(g, fontBold, TextUtil.getCjkParts(headText), (float) (h * 0.275))
                        : G2dTextHelper.getUnifiedStringWidth(g, font, "开往 ", (float) (h * 0.25))
                        + G2dTextHelper.getUnifiedStringWidth(g, fontBold, TextUtil.getCjkParts(headText), (float) (h * 0.3)))
                        : 0;
                nonCjkWidth = TextUtil.hasNonCjkPart(headText)
                        ? (TextUtil.hasCjkPart(headText)
                        ? G2dTextHelper.getUnifiedStringWidth(g, font, "To " + TextUtil.getNonCjkParts(headText), (float) (h * 0.1))
                        : G2dTextHelper.getUnifiedStringWidth(g, font, "To " + TextUtil.getNonCjkParts(headText), (float) (h * 0.3)))
                        : 0;
                int strWidth = Math.max(cjkWidth, nonCjkWidth);

                int currentX;
                switch (headAlign) {
                    case 0:
                        currentX = widthPercent(x, w, 0.5) - cjkWidth / 2;
                        if (TextUtil.hasCjkPart(headText)) {
                            if (TextUtil.hasNonCjkPart(headText)) {
                                currentX += G2dTextHelper.drawStrUnified(g, font, "开往 ", currentX,
                                        heightPercent(y, h, 0.575), (float) (h * 0.275), 0);
                                currentX += G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getCjkParts(headText), currentX,
                                        heightPercent(y, h, 0.575), (float) (h * 0.275), 0);
                                G2dTextHelper.drawStrUnified(g, font, "To " + TextUtil.getNonCjkParts(headText),
                                        widthPercent(x, w, 0.5), heightPercent(y, h, 0.7), (float) (h * 0.1), 1);
                            } else {
                                currentX += G2dTextHelper.drawStrUnified(g, font, "开往 ", currentX,
                                        heightPercent(y, h, 0.65), (float) (h * 0.3), 0);
                                currentX += G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getCjkParts(headText), currentX,
                                        heightPercent(y, h, 0.65), (float) (h * 0.3), 0);
                            }
                        } else {
                            G2dTextHelper.drawStrUnified(g, font, "To " + TextUtil.getNonCjkParts(headText),
                                    widthPercent(x, w, 0.5), heightPercent(y, h, 0.65), (float) (h * 0.3), 1);
                        }
                        break;
                    case 1:
                        g.drawImage(loadImg("fangsu:sign/alb.png"),
                                widthPercent(x, w, 0.5) - strWidth / 2 - (int) (h * 0.25),
                                heightPercent(y, h, 0.3), (int) (h * 0.4), (int) (h * 0.4), null);
                        currentX = widthPercent(x, w, 0.5) - strWidth / 2 + (int) (h * 0.225);
                        if (TextUtil.hasCjkPart(headText)) {
                            if (TextUtil.hasNonCjkPart(headText)) {
                                G2dTextHelper.drawStrUnified(g, font, "To " + TextUtil.getNonCjkParts(headText),
                                        currentX, heightPercent(y, h, 0.7), (float) (h * 0.1), 0);
                                currentX += G2dTextHelper.drawStrUnified(g, font, "开往 ", currentX,
                                        heightPercent(y, h, 0.575), (float) (h * 0.275), 0);
                                currentX += G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getCjkParts(headText), currentX,
                                        heightPercent(y, h, 0.575), (float) (h * 0.275), 0);
                            } else {
                                currentX += G2dTextHelper.drawStrUnified(g, font, "开往 ", currentX,
                                        heightPercent(y, h, 0.65), (float) (h * 0.3), 0);
                                currentX += G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getCjkParts(headText), currentX,
                                        heightPercent(y, h, 0.65), (float) (h * 0.3), 0);
                            }
                        } else {
                            G2dTextHelper.drawStrUnified(g, font, "To " + TextUtil.getNonCjkParts(headText),
                                    currentX, heightPercent(y, h, 0.65), (float) (h * 0.3), 0);
                        }
                        break;
                    case 2:
                        g.drawImage(loadImg("fangsu:sign/arb.png"),
                                widthPercent(x, w, 0.5) + strWidth / 2 - (int) (h * 0.2),
                                heightPercent(y, h, 0.3), (int) (h * 0.4), (int) (h * 0.4), null);
                        currentX = widthPercent(x, w, 0.5) + strWidth / 2 - (int) (h * 0.225);
                        if (TextUtil.hasCjkPart(headText)) {
                            if (TextUtil.hasNonCjkPart(headText)) {
                                G2dTextHelper.drawStrUnified(g, font, "To " + TextUtil.getNonCjkParts(headText),
                                        currentX, heightPercent(y, h, 0.7), (float) (h * 0.1), 2);
                                currentX -= G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getCjkParts(headText), currentX,
                                        heightPercent(y, h, 0.575), (float) (h * 0.275), 2);
                                currentX -= G2dTextHelper.drawStrUnified(g, font, "开往 ", currentX,
                                        heightPercent(y, h, 0.575), (float) (h * 0.275), 2);
                            } else {
                                currentX -= G2dTextHelper.drawStrUnified(g, font, "开往 ", currentX,
                                        heightPercent(y, h, 0.65), (float) (h * 0.3), 0);
                                currentX -= G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getCjkParts(headText), currentX,
                                        heightPercent(y, h, 0.65), (float) (h * 0.3), 0);
                            }
                        } else {
                            G2dTextHelper.drawStrUnified(g, font, "To " + TextUtil.getNonCjkParts(headText),
                                    currentX, heightPercent(y, h, 0.65), (float) (h * 0.3), 2);
                        }
                        break;
                }
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
