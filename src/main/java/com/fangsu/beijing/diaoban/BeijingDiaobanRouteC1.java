package com.fangsu.beijing.diaoban;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.diaoban.BaseDiaobanDrawing;
import com.fangsu.mtr.LocalRouteDetail;
import com.fangsu.scripting.*;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;
import com.fangsu.utils.ColorUtil;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingDiaobanRouteC1 extends BaseDiaobanDrawing {

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

    private static java.awt.image.BufferedImage loadImg(String path) {
        try {
            return (java.awt.image.BufferedImage) JsFunctions.loadResource("img", path);
        } catch (Exception e) {
            return null;
        }
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
            Shape originalClip = g.getClip();
            Color routeColor = drawInfo.routeInfo().routeColor;
            int arrowSide = arrowDirection;

            int distance = (int) ((w * 0.8) / (drawInfo.routeInfo().drawStations.size() - 1));
            g.setColor(routeColor);
            g.fillRect(x + (int) (w * 0.1), y + (int) (h * 0.47), (int) (w * 0.8), (int) (h * 0.06));
            g.setColor(new Color(124, 124, 124));
            int passedWidth = arrowSide != 2 ? (int) (w * 0.8) - drawInfo.index() * distance : 0;
            g.fillRect(x + (int) (w * 0.1) + passedWidth, y + (int) (h * 0.47),
                    drawInfo.index() * distance, (int) (h * 0.06));

            for (int i = 0; i < drawInfo.routeInfo().drawStations.size(); i++) {
                int currentX = x + (int) (w * 0.1) + (arrowSide == 2 ? i * distance : (int) (w * 0.8) - i * distance);
                boolean nameOnTop = true;
                boolean hasPassed = i < drawInfo.index();
                LocalRouteDetail.StationDetails thisStn = drawInfo.routeInfo().drawStations.get(i);

                if (thisStn.transInfo != null && thisStn.transInfo.size() > 0) {
                    int[] clipX = {currentX - (int) (h * 0.03), currentX - (int) (h * 0.03), currentX,
                            currentX + (int) (h * 0.03), currentX + (int) (h * 0.03)};
                    int[] clipY = {y + (int) (h * 0.5), y + (int) (h * 0.6), y + (int) (h * 0.625),
                            y + (int) (h * 0.6), y + (int) (h * 0.5)};
                    g.setClip(new Polygon(clipX, clipY, 5));

                    StringBuilder finalCjkName = new StringBuilder();
                    StringBuilder finalNonCjkName = new StringBuilder();
                    for (int j = 0; j < thisStn.transInfo.size(); j++) {
                        int thisX = currentX - (int) (h * 0.03) + (int) ((h * 0.06) / thisStn.transInfo.size()) * j;
                        var thisTrans = thisStn.transInfo.get(j);
                        g.setColor(hasPassed ? new Color(124, 124, 124) : thisTrans.routeColor);
                        g.fillRect(thisX, y + (int) (h * 0.3), (int) ((h * 0.06) / thisStn.transInfo.size()), (int) (h * 0.4));
                        if (TextUtil.hasCjkPart(thisTrans.routeName)) {
                            if (finalCjkName.length() == 0)
                                finalCjkName.append("换乘").append(TextUtil.getCjkParts(thisTrans.routeName));
                            else finalCjkName.append("  ").append(TextUtil.getCjkParts(thisTrans.routeName));
                        }
                        if (TextUtil.hasNonCjkPart(thisTrans.routeName)) {
                            if (finalNonCjkName.length() == 0)
                                finalNonCjkName.append("Transfer to ").append(TextUtil.getNonCjkParts(thisTrans.routeName));
                            else finalNonCjkName.append("  ").append(TextUtil.getNonCjkParts(thisTrans.routeName));
                        }
                    }
                    g.setClip(originalClip);
                    g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
                    JsFunctions.jsDrawStrDl(g, fontBold, fontBold,
                            finalCjkName + " | " + finalNonCjkName,
                            currentX, y + (int) (h * 0.65), h * 0.075, 1, 1);

                    g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
                    g.fillOval(currentX - (int) (h * 0.06), y + (int) (h * 0.44), (int) (h * 0.12), (int) (h * 0.12));
                    g.setColor(Color.WHITE);
                    g.fillOval(currentX - (int) (h * 0.05), y + (int) (h * 0.45), (int) (h * 0.1), (int) (h * 0.1));
                    g.drawImage(JsFunctions.changeImageColor(loadImg("fangsu:ris/imgtrans.png"),
                                    hasPassed ? new Color(124, 124, 124) : Color.BLACK),
                            currentX - (int) (h * 0.04), y + (int) (h * 0.46),
                            (int) (h * 0.08), (int) (h * 0.08), null);
                } else {
                    g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
                    g.fillOval(currentX - (int) (h * 0.04), y + (int) (h * 0.46), (int) (h * 0.08), (int) (h * 0.08));
                    g.setColor(Color.WHITE);
                    g.fillOval(currentX - (int) (h * 0.03), y + (int) (h * 0.47), (int) (h * 0.06), (int) (h * 0.06));
                }

                if (i == drawInfo.index()) {
                    int strWidth = JsFunctions.jsGetDLStringWidth(g, fontBold, fontBold, thisStn.stationName, h * 0.2);
                    g.setColor(routeColor);
                    g.fillRoundRect(currentX - strWidth / 2 - (int) (h * 0.015),
                            y + (int) (h * (nameOnTop ? 0.225 : 0.575)),
                            strWidth + (int) (h * 0.03), (int) (h * 0.2), (int) (h * 0.05), (int) (h * 0.05));
                    g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
                    JsFunctions.jsDrawStrDl(g, fontBold, fontBold, thisStn.stationName,
                            currentX, y + (int) (h * (nameOnTop ? 0.25 : 0.6)), h * 0.15, 1, 1);
                } else {
                    g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
                    JsFunctions.jsDrawStrDl(g, fontBold, fontBold, thisStn.stationName,
                            currentX, y + (int) (h * (nameOnTop ? 0.25 : 0.6)), h * 0.15, 1, 1);
                }
            }
        }
    }
}
