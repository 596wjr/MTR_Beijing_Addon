package com.fangsu.beijing.diaoban;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.diaoban.BaseDiaobanDrawing;
import com.fangsu.mtr.LocalRouteDetail;
import com.fangsu.scripting.*;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;

public class BeijingDiaobanRouteC extends BaseDiaobanDrawing {

    private static final String FONT_BOLD_PATH = "mtrsteamloco:fonts/source-han-sans-bold.otf";

    private Font cachedFontBold;

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

        RouteDrawer.RouteDrawInfo drawInfo = buildDrawInfo(routes, arrowDirection, texW, texH);

        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        Font baseFont = getFontBold();

        Shape originalClip = g.getClip();
        AffineTransform originalTransform = g.getTransform();

        if (drawInfo.routeInfo() != null) {
            Color routeColor = drawInfo.routeInfo().routeColor;

            int distance = (int) (h * 0.75);
            g.setColor(routeColor);
            g.fillRect(widthPercent(x, w, 0), heightPercent(y, h, 0.25), w, (int) (h * 0.5));

            if (w <= distance * 8) return;
            int currentX = widthPercent(x, w, 0.5) - distance * 4 * (arrowDirection == 2 ? 1 : -1);

            if (drawInfo.index() > 0) {
                g.setColor(Color.WHITE);
                JsFunctions.jsDrawStrDl(g, baseFont, baseFont,
                        drawInfo.routeInfo().drawStations.get(drawInfo.index() - 1).stationName,
                        currentX, heightPercent(y, h, 0.35), h * 0.3, 1, 1);
            }
            currentX += distance * (arrowDirection == 2 ? 1 : -1) * 2;

            int[] clipX, clipY;
            if (arrowDirection == 2) {
                clipX = new int[]{currentX - (int) (distance * 0.5), currentX - (int) (distance * 0.75),
                        currentX + (int) (distance * 0.5), currentX + (int) (distance * 0.75),
                        currentX + (int) (distance * 0.5), currentX - (int) (distance * 0.75)};
                clipY = new int[]{heightPercent(y, h, 0.5), heightPercent(y, h, 0), heightPercent(y, h, 0),
                        heightPercent(y, h, 0.5), heightPercent(y, h, 1), heightPercent(y, h, 1)};
            } else {
                clipX = new int[]{currentX - (int) (distance * 0.75), currentX - (int) (distance * 0.5),
                        currentX + (int) (distance * 0.75), currentX + (int) (distance * 0.5),
                        currentX + (int) (distance * 0.75), currentX - (int) (distance * 0.5)};
                clipY = new int[]{heightPercent(y, h, 0.5), heightPercent(y, h, 0), heightPercent(y, h, 0),
                        heightPercent(y, h, 0.5), heightPercent(y, h, 1), heightPercent(y, h, 1)};
            }
            g.setClip(new Polygon(clipX, clipY, 6));
            g.setColor(routeColor);
            g.fillRect(currentX - distance, y, distance * 2, h);

            g.setColor(Color.WHITE);
            g.fillRect(currentX - distance, heightPercent(y, h, 0.04), distance * 2, (int) (h * 0.02));
            g.fillRect(currentX - distance, heightPercent(y, h, 0.09), distance * 2, (int) (h * 0.02));
            g.fillRect(currentX - distance, heightPercent(y, h, 0.14), distance * 2, (int) (h * 0.02));
            g.fillRect(currentX - distance, heightPercent(y, h, 0.19), distance * 2, (int) (h * 0.02));

            g.fillRect(currentX - distance, heightPercent(y, h, 0.79), distance * 2, (int) (h * 0.02));
            g.fillRect(currentX - distance, heightPercent(y, h, 0.84), distance * 2, (int) (h * 0.02));
            g.fillRect(currentX - distance, heightPercent(y, h, 0.89), distance * 2, (int) (h * 0.02));
            g.fillRect(currentX - distance, heightPercent(y, h, 0.94), distance * 2, (int) (h * 0.02));
            g.setClip(originalClip);

            G2dTextHelper.drawStrMultiLinesWithStretch(g, baseFont, baseFont,
                    currentX, heightPercent(y, h, 0.65), (int) (h * 0.3), Math.round(distance * 0.8f), 1, 1,
                    drawInfo.routeInfo().drawStations.get(drawInfo.index()).stationName.split("\\|"));

            currentX += distance * (arrowDirection == 2 ? 1 : -1);

            g.drawImage(loadImg(arrowDirection == 2 ? "fangsu:sign/ar.png" : "fangsu:sign/al.png"),
                    currentX - (int) (h * 0.2), heightPercent(y, h, 0.4), (int) (h * 0.2), (int) (h * 0.2), null);

            currentX += distance * (arrowDirection == 2 ? 1 : -1);

            if (drawInfo.index() < drawInfo.routeInfo().drawStations.size() - 2) {
                G2dTextHelper.drawStrMultiLinesWithStretch(g, baseFont, baseFont,
                        currentX, heightPercent(y, h, 0.65), Math.round(h * 0.3f), Math.round(distance * 0.8f), 1, 1,
                        drawInfo.routeInfo().drawStations.get(drawInfo.index() + 1).stationName.split("\\|"));
            }

            int forwardStations = drawInfo.routeInfo().drawStations.size() - drawInfo.index() - 1;
            List<LocalRouteDetail.StationDetails> drawStationsList = new java.util.ArrayList<>();
            if (forwardStations > 1) {
                if (forwardStations == 2)
                    drawStationsList.add(drawInfo.routeInfo().drawStations.get(drawInfo.index() + 2));
                else if (forwardStations == 3) {
                    drawStationsList.add(drawInfo.routeInfo().drawStations.get(drawInfo.index() + 2));
                    drawStationsList.add(drawInfo.routeInfo().drawStations.get(drawInfo.index() + 3));
                } else if (forwardStations == 4) {
                    drawStationsList.add(drawInfo.routeInfo().drawStations.get(drawInfo.index() + 3));
                    drawStationsList.add(drawInfo.routeInfo().drawStations.get(drawInfo.index() + 4));
                } else {
                    drawStationsList.add(drawInfo.routeInfo().drawStations.get(drawInfo.index() + 3));
                    drawStationsList.add(drawInfo.routeInfo().drawStations.get(drawInfo.index() + 5));
                }
                for (int i = 0; i < drawStationsList.size(); i++) {
                    currentX += distance * (arrowDirection == 2 ? 1 : -1);
                    g.fillOval(currentX - (int) (h * 0.06), heightPercent(y, h, 0.48), (int) (h * 0.02), (int) (h * 0.02));
                    g.fillOval(currentX - (int) (h * 0.01), heightPercent(y, h, 0.48), (int) (h * 0.02), (int) (h * 0.02));
                    g.fillOval(currentX + (int) (h * 0.04), heightPercent(y, h, 0.48), (int) (h * 0.02), (int) (h * 0.02));
                    currentX += distance * (arrowDirection == 2 ? 1 : -1);
                    G2dTextHelper.drawStrMultiLinesWithStretch(g, baseFont, baseFont,
                            currentX, heightPercent(y, h, 0.65), Math.round(h * 0.3f), Math.round(distance * 0.8f), 1, 1,
                            drawStationsList.get(i).stationName.split("\\|"));
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
