package com.fangsu.beijing.util;

import com.fangsu.scripting.G2dTextHelper;
import com.fangsu.scripting.RouteNameUtil;
import com.fangsu.scripting.TextUtil;
import com.fangsu.utils.ColorUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class DrawUtil {
    private DrawUtil() {
    }

    public static int getLineNameBoxWidth(Graphics2D g, Font digitFont, Font cjkFont, Font nonCjkFont, String routeName, int height, boolean forceNormal) {
        boolean isNumLine = RouteNameUtil.isNumLine(routeName);
        int gap = Math.round(height * 0.1f);

        int width = gap * 2;
        if (isNumLine && !forceNormal) {
            String name = RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(routeName));
            width += G2dTextHelper.getUnifiedStringWidth(g, digitFont, name, height * 0.7f);
            width += G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, height * 0.65f, "号线", TextUtil.getNonCjkParts(routeName));
        } else {
            width += G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, height * 0.7f, TextUtil.getNonExtraParts(routeName).split("\\|"));
        }

        return width;
    }

    public static int drawLineNameBox(Graphics2D g, Font digitFont, Font cjkFont, Font nonCjkFont, String routeName, Color routeColor, int x, int y, int height, int align, boolean forceNormal) {
        int width = getLineNameBoxWidth(g, digitFont, cjkFont, nonCjkFont, routeName, height, forceNormal);
        drawLineNameBox(g, digitFont, cjkFont, nonCjkFont, routeName, routeColor, x, y, width, height, align, forceNormal);
        return width;
    }

    public static int drawLineNameBoxWithStretch(Graphics2D g, Font digitFont, Font cjkFont, Font nonCjkFont, String routeName, Color routeColor, int x, int y, int maxWidth, int height, int align, boolean forceNormal, boolean forceStretch) {
        int width = getLineNameBoxWidth(g, digitFont, cjkFont, nonCjkFont, routeName, height, forceNormal);
        int stretchRate = maxWidth / width;
        int actualRate = forceStretch || stretchRate > 1 ? stretchRate : 1;
        var origTrans = g.getTransform();
        var trans = new AffineTransform();
        trans.translate(x, y);
        trans.scale(actualRate, 1);
        drawLineNameBox(g, digitFont, cjkFont, nonCjkFont, routeName, routeColor, x, y, width, height, align, forceNormal);
        g.setTransform(origTrans);
        return width * actualRate;
    }

    private static void drawLineNameBox(Graphics2D g, Font digitFont, Font cjkFont, Font nonCjkFont, String routeName, Color routeColor, int x, int y, int width, int height, int align, boolean forceNormal) {
        boolean isNumLine = RouteNameUtil.isNumLine(routeName);
        boolean isLightColor = ColorUtil.isLightColor(routeColor);
        int gap = Math.round(height * 0.1f);
        Color origColor = g.getColor();
        Font origFont = g.getFont();

        int textHeight = Math.round(height * 0.7f);
        int textGap = Math.round(height * 0.15f);

        int beginX = switch (align) {
            case 1 -> x - Math.round(x - width * 0.5f);
            case 2 -> x - width;
            default -> x;
        };
        g.setColor(routeColor);
        g.fillRoundRect(beginX, y, width, height, gap, gap);
        g.setColor(isLightColor ? Color.BLACK : Color.WHITE);
        if (isNumLine && !forceNormal) {
            int currentX = beginX + gap;
            String name = RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(routeName));
            currentX += G2dTextHelper.drawStrUnified(g, digitFont, name, currentX, y + textHeight + textGap, textHeight, 0);
            G2dTextHelper.drawStrMultiLines(g, cjkFont, nonCjkFont, currentX, y + textGap - textHeight, textHeight, 0, "号线", TextUtil.getNonCjkParts(routeName));
        } else {
            G2dTextHelper.drawStrMultiLines(g, cjkFont, nonCjkFont, beginX + gap, y + textGap - textHeight, textHeight, 1, TextUtil.getNonExtraParts(routeName).split("\\|"));
        }

        g.setFont(origFont);
        g.setColor(origColor);
    }
}
