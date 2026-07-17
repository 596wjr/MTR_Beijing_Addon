package com.fangsu.beijing.sis;

import com.fangsu.blockEntities.BlockEntitySis;
import com.fangsu.drawing.sis.BaseSisDrawing;
import com.fangsu.mtr.DrawableRoute;
import com.fangsu.mtr.LocalRoute;
import com.fangsu.render.sowcer.math.Vector3f;
import com.fangsu.scripting.*;
import com.fangsu.utils.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Map;

public class BeijingSisOuterA extends BaseSisDrawing {

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

    private static BufferedImage loadImg(String path) {
        try {
            return (BufferedImage) JsFunctions.loadResource("img", path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void draw(GraphicsTexture gt, Map<String, Object> drawState,
                     int arrowDirection, int texW, int texH,
                     BlockEntitySis.SISDrawInfo drawInfo) {
        int x = 0, y = 0;
        int w = texW, h = texH;
        Graphics2D g = gt.graphics;

        Font font = getFont();
        Font fontBold = getFontBold();

        Map<String, Object> extraConfig = drawInfo.extraConfig;
        if (extraConfig == null) extraConfig = new HashMap<>();

        // 加载logo
        String imgPath = extraConfig.containsKey("imgPath") ? (String) extraConfig.get("imgPath") : "mtr:textures/block/sign/logo_grayscale.png";
        BufferedImage imgIcon = loadImg(imgPath);
        if (imgIcon != null) {
            g.drawImage(imgIcon, widthPercent(x, w, 0.85) - (int) (h * 0.4), heightPercent(y, h, 0.05), (int) (h * 0.4), (int) (h * 0.4), null);
            g.drawImage(imgIcon, widthPercent(x, w, 0.15), heightPercent(y, h, 0.55), (int) (h * 0.4), (int) (h * 0.4), null);
        }

        g.setColor(Color.BLACK);

        if (drawInfo.station != null) {
            String dispStationName = drawInfo.station.name;
            g.setColor(Color.WHITE);
            // 右侧站名
            JsFunctions.jsDrawStrDl(g, fontBold, fontBold, dispStationName,
                    widthPercent(x, w, 0.85) - (int) (h * 0.45), heightPercent(y, h, 0.125), h * 0.175, 2, 2);
            // 左侧站名
            JsFunctions.jsDrawStrDl(g, fontBold, fontBold, dispStationName,
                    widthPercent(x, w, 0.15) + (int) (h * 0.45), heightPercent(y, h, 0.625), h * 0.175, 0, 0);
        }

        if (drawInfo.routes != null && drawInfo.routes.length > 0) {
            // 去重+排序线路
            Map<String, Color> routeMap = new LinkedHashMap<>();
            for (LocalRoute route : drawInfo.routes) {
                DrawableRoute drawable = JsFunctions.jsRouteToObj(route);
                if (!routeMap.containsKey(TextUtil.getNonExtraParts(drawable.routeName))) {
                    routeMap.put(TextUtil.getNonExtraParts(drawable.routeName), drawable.routeColor);
                }
            }
            List<Map.Entry<String, Color>> routeList = new ArrayList<>(routeMap.entrySet());
            routeList.sort(Map.Entry.comparingByKey());

            int currentX1 = widthPercent(x, w, 0.05);
            int currentX2 = widthPercent(x, w, 0.95);
            for (int i = 0; i < routeList.size(); i += 2) {
                if (i + 1 < routeList.size()) {
                    Map.Entry<String, Color> entry1 = routeList.get(i);
                    Map.Entry<String, Color> entry2 = routeList.get(i + 1);
                    String name1 = entry1.getKey();
                    String name2 = entry2.getKey();
                    Color color1 = entry1.getValue();
                    Color color2 = entry2.getValue();

                    double width1 = calcRouteWidth(g, fontBold, fontBold, name1, h * 0.075);
                    double width2 = calcRouteWidth(g, fontBold, fontBold, name2, h * 0.075);
                    double thisWidth = Math.max(width1, width2);

                    g.setColor(Color.WHITE);
                    int thisBaseX = (int) (currentX1 + thisWidth * 0.5 + h * 0.0125);
                    drawRouteNameBox(g, font, fontBold, name1, thisBaseX, heightPercent(y, h, 0.1), h * 0.075);
                    drawRouteNameBox(g, font, fontBold, name2, thisBaseX, heightPercent(y, h, 0.2), h * 0.075);
                    thisBaseX = (int) (currentX2 - thisWidth * 0.5 - h * 0.0125);
                    drawRouteNameBox(g, font, fontBold, name1, thisBaseX, heightPercent(y, h, 0.6), h * 0.075);
                    drawRouteNameBox(g, font, fontBold, name2, thisBaseX, heightPercent(y, h, 0.7), h * 0.075);

                    g.setColor(color1);
                    g.fillRect(currentX1, heightPercent(y, h, 0.18), (int) (thisWidth + h * 0.025), (int) (h * 0.02));
                    g.fillRect((int) (currentX2 - thisWidth - h * 0.0125), heightPercent(y, h, 0.68), (int) (thisWidth + h * 0.025), (int) (h * 0.02));
                    g.setColor(color2);
                    g.fillRect(currentX1, heightPercent(y, h, 0.28), (int) (thisWidth + h * 0.025), (int) (h * 0.02));
                    g.fillRect((int) (currentX2 - thisWidth - h * 0.0125), heightPercent(y, h, 0.78), (int) (thisWidth + h * 0.025), (int) (h * 0.02));

                    currentX1 += thisWidth + h * 0.025;
                    currentX2 -= thisWidth + h * 0.025;
                } else {
                    Map.Entry<String, Color> entry = routeList.get(i);
                    String routeName = entry.getKey();
                    Color routeColor = entry.getValue();

                    double width = calcRouteWidth(g, fontBold, fontBold, routeName, h * 0.075);

                    g.setColor(Color.WHITE);
                    int thisBaseX = (int) (currentX1 + width * 0.5 + h * 0.0125);
                    drawRouteNameBox(g, font, fontBold, routeName, thisBaseX, heightPercent(y, h, 0.1), h * 0.075);
                    thisBaseX = (int) (currentX2 - width * 0.5 - h * 0.0125);
                    drawRouteNameBox(g, font, fontBold, routeName, thisBaseX, heightPercent(y, h, 0.6), h * 0.075);

                    g.setColor(routeColor);
                    g.fillRect(currentX1, heightPercent(y, h, 0.18), (int) (width + h * 0.025), (int) (h * 0.02));
                    g.fillRect((int) (currentX2 - width - h * 0.0125), heightPercent(y, h, 0.68), (int) (width + h * 0.025), (int) (h * 0.02));

                    currentX1 -= (width + h * 0.025);
                    currentX2 += (width + h * 0.025);
                }
            }
        }

        if (drawInfo.station != null && drawInfo.block != null) {
            Vector3f stationPos = new Vector3f(drawInfo.station.getCenterVector().x(), 0, drawInfo.station.getCenterVector().z());
            Vector3f blockPos = drawInfo.block.getWorldPosVector3f();
            double dx = Math.abs(blockPos.x() - stationPos.x());
            double dz = Math.abs(blockPos.z() - stationPos.z());
            int distance = (int) (Math.round(Math.sqrt(dx * dx + dz * dz) * 8 / 50) * 50);

            g.setColor(Color.WHITE);
            int width = G2dTextHelper.drawStrUnified(g, fontBold, distance + " m",
                    widthPercent(x, w, 0.05) + (int) (h * 0.025), heightPercent(y, h, 0.4), (float) (h * 0.1), 0);
            G2dTextHelper.drawStrUnified(g, fontBold, distance + " m",
                    widthPercent(x, w, 0.95) - (int) (h * 0.025), heightPercent(y, h, 0.9), (float) (h * 0.1), 2);
            g.setStroke(new BasicStroke((float) (h * 0.007)));
            g.drawRoundRect(widthPercent(x, w, 0.05), heightPercent(y, h, 0.3), width + (int) (h * 0.05), (int) (h * 0.12), (int) (h * 0.005), (int) (h * 0.005));
            g.drawRoundRect(widthPercent(x, w, 0.95) - width - (int) (h * 0.05), heightPercent(y, h, 0.8), width + (int) (h * 0.05), (int) (h * 0.12), (int) (h * 0.005), (int) (h * 0.005));
        }
    }

    private double calcRouteWidth(Graphics2D g, Font fontBold, Font font, String routeName, double h) {
        if (RouteNameUtil.isNumLine(routeName) && TextUtil.hasCjkPart(routeName) && TextUtil.hasNonCjkPart(routeName)) {
            return JsFunctions.jsGetDLStringWidth(g, fontBold, font, "号线|" + TextUtil.getNonCjkParts(routeName), h)
                    + G2dTextHelper.getUnifiedStringWidth(g, fontBold, String.valueOf(RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(routeName))), (float) (h * 1.125));
        } else {
            return JsFunctions.jsGetDLStringWidth(g, fontBold, font, routeName, h);
        }
    }

    private void drawRouteNameBox(Graphics2D g, Font font, Font fontBold, String routeName, int x, int y, double h) {
        if (RouteNameUtil.isNumLine(routeName) && TextUtil.hasCjkPart(routeName) && TextUtil.hasNonCjkPart(routeName)) {
            double numWidth = G2dTextHelper.getUnifiedStringWidth(g, fontBold,
                    String.valueOf(RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(routeName))), (float) (h * 1.125));
            double dlWidth = JsFunctions.jsGetDLStringWidth(g, fontBold, font,
                    "号线|" + TextUtil.getNonCjkParts(routeName), h);
            int drawX = (int) (x - numWidth * 0.5 - dlWidth * 0.5);
            G2dTextHelper.drawStrUnified(g, fontBold,
                    String.valueOf(RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(routeName))),
                    (int) (drawX), (int) (y + h), (float) (h * 1.125), 0);
            JsFunctions.jsDrawStrDl(g, font, font, "号线|" + TextUtil.getNonCjkParts(routeName),
                    (int) (drawX + numWidth), y, h, 0, 0);
        } else {
            JsFunctions.jsDrawStrDl(g, font, font, routeName, x, y, h, 1, 1);
        }
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }
}
