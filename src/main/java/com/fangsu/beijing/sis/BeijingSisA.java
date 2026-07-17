package com.fangsu.beijing.sis;

import com.fangsu.blockEntities.BlockEntitySis;
import com.fangsu.drawing.sis.BaseSisDrawing;
import com.fangsu.mtr.DrawableRoute;
import com.fangsu.mtr.LocalRoute;
import com.fangsu.scripting.*;
import com.fangsu.utils.ColorUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.Map;

public class BeijingSisA extends BaseSisDrawing {

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
            g.drawImage(imgIcon, widthPercent(x, w, 0.865), heightPercent(y, h, 0), (int) (w * 0.135), (int) (w * 0.135), null);
        }

        g.setColor(Color.BLACK);

        if (drawInfo.station != null) {
            g.setColor(Color.WHITE);
            String[] finalStationName = drawInfo.station.name.split("\\|");
            for (int i = 0; i < finalStationName.length; i++) {
                String stationName = finalStationName[i];
                if (TextUtil.isCjk(stationName)) {
                    if (!stationName.trim().endsWith("站")) finalStationName[i] = stationName.trim() + "站";
                } else {
                    if (!stationName.trim().toLowerCase().endsWith("station"))
                        finalStationName[i] = stationName.trim() + " station";
                }
            }
            // 左侧站名（左对齐）
            G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, widthPercent(x, w, 0), heightPercent(y, h, 0.6), (int) (h * 0.3), (int) (w * 0.2), 0, 0, finalStationName);
//            JsFunctions.jsDrawStrDl(g, fontBold, fontBold, finalStationName,
//                    widthPercent(x, w, 0), heightPercent(y, h, 0.3), h * 0.3, 0, 0);
            // 右侧站名（右对齐）
            G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, widthPercent(x, w, 0.8), heightPercent(y, h, 0.6), (int) (h * 0.3), (int) (w * 0.2), 2, 2, finalStationName);
//            JsFunctions.jsDrawStrDl(g, fontBold, fontBold, finalStationName,
//                    widthPercent(x, w, 0.8), heightPercent(y, h, 0.3), h * 0.3, 2, 2);

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

                int currentX1 = widthPercent(x, w, 0.4);
                int currentX2 = widthPercent(x, w, 0.4);
                for (Map.Entry<String, Color> entry : routeList) {
                    Color routeColor = entry.getValue();
                    String routeName = entry.getKey();

                    g.setColor(routeColor);
                    g.fillRect(currentX1 - (int) (w * 0.05), heightPercent(y, h, 0), (int) (w * 0.05), h);
                    g.fillRect(currentX2, heightPercent(y, h, 0), (int) (w * 0.05), h);

                    g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
                    if (RouteNameUtil.isNumLine(routeName)) {
                        String cjkLineName = String.valueOf(RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(routeName)));
                        G2dTextHelper.drawStrUnified(g, fontBold, cjkLineName, currentX1 - (int) (w * 0.025), heightPercent(y, h, 0.55), (float) (h * 0.25), 1);
                        JsFunctions.jsDrawStrDl(g, font, font, "号线|" + TextUtil.getNonCjkParts(routeName),
                                currentX1 - (int) (w * 0.025), heightPercent(y, h, 0.6), h * 0.15, 1, 1);
                        G2dTextHelper.drawStrUnified(g, fontBold, cjkLineName, currentX2 + (int) (w * 0.025), heightPercent(y, h, 0.55), (float) (h * 0.25), 1);
                        JsFunctions.jsDrawStrDl(g, font, font, "号线|" + TextUtil.getNonCjkParts(routeName),
                                currentX2 + (int) (w * 0.025), heightPercent(y, h, 0.6), h * 0.15, 1, 1);
                    } else {
                        String cjkName = TextUtil.getCjkParts(routeName);
                        double cjkChrHeight = (h * 0.5) / Math.max(cjkName.length(), 1);
                        for (int j = 0; j < cjkName.length(); j++) {
                            G2dTextHelper.drawStrUnified(g, fontBold, String.valueOf(cjkName.charAt(j)),
                                    currentX1 - (int) (w * 0.025), (int) (heightPercent(y, h, 0.15) + cjkChrHeight * (j + 1)),
                                    (float) cjkChrHeight, 1);
                            G2dTextHelper.drawStrUnified(g, fontBold, String.valueOf(cjkName.charAt(j)),
                                    currentX2 + (int) (w * 0.025), (int) (heightPercent(y, h, 0.15) + cjkChrHeight * (j + 1)),
                                    (float) cjkChrHeight, 1);
                        }
                        G2dTextHelper.drawStrUnified(g, font, TextUtil.getNonCjkParts(routeName),
                                currentX1 - (int) (w * 0.025), heightPercent(y, h, 0.75), (float) (h * 0.035), 1);
                        G2dTextHelper.drawStrUnified(g, font, TextUtil.getNonCjkParts(routeName),
                                currentX2 + (int) (w * 0.025), heightPercent(y, h, 0.75), (float) (h * 0.035), 1);
                    }
                    currentX1 -= (int) (w * 0.05);
                    currentX2 += (int) (w * 0.05);
                }
            }
        }

        g.setColor(Color.BLACK);
        String exitName = extraConfig.containsKey("exitName") ? (String) extraConfig.get("exitName") : "A";
        JsFunctions.jsDrawStrDl(g, fontBold, fontBold, exitName,
                widthPercent(x, w, 0.8325), heightPercent(y, h, 0.3), h * 0.4, 1, 1);
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }
}
