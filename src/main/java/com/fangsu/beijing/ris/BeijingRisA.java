package com.fangsu.beijing.ris;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.ris.BaseRisDrawing;
import com.fangsu.mtr.ColorNameTuple;
import com.fangsu.mtr.LocalRoute;
import com.fangsu.mtr.LocalRouteDetail;
import com.fangsu.scripting.*;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;
import com.fangsu.utils.ColorUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class BeijingRisA extends BaseRisDrawing {

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
            Color routeColor = routeInfo.routeColor;
            int arrowSide = arrowDirection;
            LocalRoute.CircularState circularState = routeInfo.circularState;

            int currentY = heightPercent(y, h, 0.2);
            String sideText;
            if (circularState == LocalRoute.CircularState.NONE) {
                sideText = routeInfo.drawStations.get(routeInfo.drawStations.size() - 1).stationName;
            } else {
                int nextIdx = Math.min(drawInfo.index + 1, routeInfo.drawStations.size() - 1);
                sideText = routeInfo.drawStations.get(nextIdx).stationName;
            }

            Shape originalClip = g.getClip();
            AffineTransform originalTransform = g.getTransform();

            switch (arrowSide) {
                case 0:
                case 1:
                    g.setColor(routeColor);
                    g.fillRect(x, y, (int) (w * 0.2), h);
                    if (arrowSide == 1) {
                        BufferedImage al = loadImg("fangsu:sign/al.png");
                        if (al != null)
                            g.drawImage(al, widthPercent(x, w, 0.05), heightPercent(y, h, 0.05), (int) (w * 0.1), (int) (w * 0.1), null);
                    }
                    g.setColor(Color.WHITE);

                    if (TextUtil.hasCjkPart(sideText)) {
                        if (circularState == LocalRoute.CircularState.NONE) {
                            G2dTextHelper.drawStrUnified(g, font, "开", widthPercent(x, w, 0.15), currentY, w * 0.1f, 2);
                            currentY += h * 0.06;
                            G2dTextHelper.drawStrUnified(g, font, "往", widthPercent(x, w, 0.15), currentY, w * 0.1f, 2);
                            currentY += h * 0.06;
                        } else {
                            currentY -= h * 0.02;
                            G2dTextHelper.drawStrUnified(g, font, "下", widthPercent(x, w, 0.15), currentY, w * 0.06f, 2);
                            currentY += h * 0.04;
                            G2dTextHelper.drawStrUnified(g, font, "一", widthPercent(x, w, 0.15), currentY, w * 0.06f, 2);
                            currentY += h * 0.04;
                            G2dTextHelper.drawStrUnified(g, font, "站", widthPercent(x, w, 0.15), currentY, w * 0.06f, 2);
                            currentY += h * 0.06;
                        }
                        String text = TextUtil.getCjkParts(sideText);
                        for (int i = 0; i < text.length(); i++) {
                            char chr = text.charAt(i);
                            if (TextUtil.isCjk(String.valueOf(chr))) {
                                G2dTextHelper.drawStrUnified(g, fontBold, String.valueOf(chr), widthPercent(x, w, 0.15), currentY, w * 0.1f, 2);
                                currentY += h * 0.06;
                            } else {
                                AffineTransform transform = AffineTransform.getRotateInstance(0.5 * Math.PI, widthPercent(x, w, 0.07), currentY - w * 0.025);
                                g.setTransform(transform);
                                currentY += G2dTextHelper.drawStrUnified(g, fontBold, String.valueOf(chr), widthPercent(x, w, 0.07), currentY - w * 0.025, w * 0.1f, 2);
                                g.setTransform(originalTransform);
                            }
                        }
                        currentY += w * 0.05;
                    }
                    if (TextUtil.hasNonCjkPart(sideText)) {
                        if (circularState == LocalRoute.CircularState.NONE) {
                            AffineTransform transform = AffineTransform.getRotateInstance(0.5 * Math.PI, widthPercent(x, w, 0.075), currentY - w * 0.05);
                            g.setTransform(transform);
                            currentY += G2dTextHelper.drawStrUnified(g, fontBold, "To " + TextUtil.getNonCjkParts(sideText), widthPercent(x, w, 0.075), currentY - w * 0.05, w * 0.075f, 0);
                            g.setTransform(originalTransform);
                        } else {
                            AffineTransform transform = AffineTransform.getRotateInstance(0.5 * Math.PI, widthPercent(x, w, 0.1), currentY - w * 0.05);
                            g.setTransform(transform);
                            currentY += Math.max(
                                    G2dTextHelper.drawStrUnified(g, font, "Next Station", widthPercent(x, w, 0.1), currentY - w * 0.05, w * 0.05f, 0),
                                    G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getNonCjkParts(sideText), widthPercent(x, w, 0.1), currentY, w * 0.05f, 0)
                            );
                            g.setTransform(originalTransform);
                        }
                    }
                    break;
                case 2:
                    g.setColor(routeColor);
                    g.fillRect(widthPercent(x, w, 0.8), y, (int) (w * 0.2), h);
                    BufferedImage ar = loadImg("fangsu:sign/ar.png");
                    if (ar != null)
                        g.drawImage(ar, widthPercent(x, w, 0.85), heightPercent(y, h, 0.05), (int) (w * 0.1), (int) (w * 0.1), null);
                    g.setColor(Color.WHITE);

                    if (TextUtil.hasCjkPart(sideText)) {
                        if (circularState == LocalRoute.CircularState.NONE) {
                            G2dTextHelper.drawStrUnified(g, font, "开", widthPercent(x, w, 0.85), currentY, w * 0.1f, 0);
                            currentY += h * 0.06;
                            G2dTextHelper.drawStrUnified(g, font, "往", widthPercent(x, w, 0.85), currentY, w * 0.1f, 0);
                            currentY += h * 0.06;
                        } else {
                            currentY -= h * 0.02;
                            G2dTextHelper.drawStrUnified(g, font, "下", widthPercent(x, w, 0.85), currentY, w * 0.06f, 0);
                            currentY += h * 0.04;
                            G2dTextHelper.drawStrUnified(g, font, "一", widthPercent(x, w, 0.85), currentY, w * 0.06f, 0);
                            currentY += h * 0.04;
                            G2dTextHelper.drawStrUnified(g, font, "站", widthPercent(x, w, 0.85), currentY, w * 0.06f, 0);
                            currentY += h * 0.06;
                        }
                        String text = TextUtil.getCjkParts(sideText);
                        for (int i = 0; i < text.length(); i++) {
                            char chr = text.charAt(i);
                            if (TextUtil.isCjk(String.valueOf(chr))) {
                                G2dTextHelper.drawStrUnified(g, fontBold, String.valueOf(chr), widthPercent(x, w, 0.85), currentY, w * 0.1f, 0);
                                currentY += h * 0.06;
                            } else {
                                AffineTransform transform = AffineTransform.getRotateInstance(0.5 * Math.PI, widthPercent(x, w, 0.87), currentY - w * 0.025);
                                g.setTransform(transform);
                                currentY += G2dTextHelper.drawStrUnified(g, fontBold, String.valueOf(chr), widthPercent(x, w, 0.87), currentY - w * 0.025, w * 0.1f, 2);
                                g.setTransform(originalTransform);
                            }
                        }
                        currentY += w * 0.05;
                    }
                    if (TextUtil.hasNonCjkPart(sideText)) {
                        if (circularState == LocalRoute.CircularState.NONE) {
                            AffineTransform transform = AffineTransform.getRotateInstance(0.5 * Math.PI, widthPercent(x, w, 0.875), currentY - w * 0.05);
                            g.setTransform(transform);
                            currentY += G2dTextHelper.drawStrUnified(g, fontBold, "To " + TextUtil.getNonCjkParts(sideText), widthPercent(x, w, 0.875), currentY - w * 0.05, w * 0.075f, 0);
                            g.setTransform(originalTransform);
                        } else {
                            AffineTransform transform = AffineTransform.getRotateInstance(0.5 * Math.PI, widthPercent(x, w, 0.9), currentY - w * 0.05);
                            g.setTransform(transform);
                            currentY += Math.max(
                                    G2dTextHelper.drawStrUnified(g, font, "Next Station", widthPercent(x, w, 0.9), currentY - w * 0.05, w * 0.05f, 0),
                                    G2dTextHelper.drawStrUnified(g, fontBold, TextUtil.getNonCjkParts(sideText), widthPercent(x, w, 0.9), currentY, w * 0.05f, 0)
                            );
                            g.setTransform(originalTransform);
                        }
                    }
                    break;
            }

            boolean routeAtRight = arrowSide != 2;

            if (circularState == LocalRoute.CircularState.NONE) {
                // Draw line segments
                for (int i = 0; i < routeInfo.drawStations.size(); i++) {
                    int stationCurrentY = heightPercent(y, h, 0.05) + (int) (((h * 0.9) / (routeInfo.drawStations.size() - 1)) * (routeInfo.drawStations.size() - i - 1));
                    boolean hasPassed = i <= drawInfo.index;

                    if (i > 0) {
                        g.setColor(hasPassed ? new Color(124, 124, 124) : routeColor);
                        g.fillRect(widthPercent(x, w, routeAtRight ? 0.385 : 0.585), stationCurrentY, (int) (w * 0.03), (int) ((h * 0.9) / (routeInfo.drawStations.size() - 1)));
                    }
                    if (i == 1) {
                        g.setColor(hasPassed ? new Color(124, 124, 124) : routeColor);
                        g.fillOval(widthPercent(x, w, routeAtRight ? 0.385 : 0.585), stationCurrentY + (int) ((h * 0.9) / (routeInfo.drawStations.size() - 1)) - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03));
                    }
                    if (i == routeInfo.drawStations.size() - 1) {
                        g.setColor(hasPassed ? new Color(124, 124, 124) : routeColor);
                        g.fillOval(widthPercent(x, w, routeAtRight ? 0.385 : 0.585), (int) (h * 0.04), (int) (w * 0.03), (int) (w * 0.03));
                    }
                }

                // Draw stations
                for (int i = 0; i < routeInfo.drawStations.size(); i++) {
                    int stationCurrentY = heightPercent(y, h, 0.05) + (int) (((h * 0.9) / (routeInfo.drawStations.size() - 1)) * (routeInfo.drawStations.size() - i - 1));
                    LocalRouteDetail.StationDetails thisStn = routeInfo.drawStations.get(i);
                    boolean hasPassed = i < drawInfo.index;
                    boolean isTransfer = !thisStn.transInfo.isEmpty();

                    if (isTransfer) {
                        drawTransferLabels(g, font, fontBold, thisStn, stationCurrentY, hasPassed, x, w, routeAtRight);
                    }

                    if (isTransfer || i == drawInfo.index) {
                        g.setColor(hasPassed ? new Color(124, 124, 124) : routeColor);
                        g.fillOval(widthPercent(x, w, routeAtRight ? 0.38 : 0.58), stationCurrentY - (int) (w * 0.02), (int) (w * 0.04), (int) (w * 0.04));
                        if (i == drawInfo.index) {
                            g.setColor(Color.RED);
                            g.fillOval(widthPercent(x, w, routeAtRight ? 0.385 : 0.585), stationCurrentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03));
                            BufferedImage aub = loadImg("fangsu:sign/aub.png");
                            if (aub != null)
                                g.drawImage(aub, widthPercent(x, w, routeAtRight ? 0.44 : 0.52), stationCurrentY - (int) (w * 0.02), (int) (w * 0.04), (int) (w * 0.04), null);
                            if (isTransfer) {
                                BufferedImage imgtrans = loadImg("fangsu:ris/imgtrans.png");
                                if (imgtrans != null)
                                    g.drawImage(imgtrans, widthPercent(x, w, routeAtRight ? 0.385 : 0.585), stationCurrentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03), null);
                            }
                        } else {
                            g.setColor(Color.WHITE);
                            g.fillOval(widthPercent(x, w, routeAtRight ? 0.385 : 0.585), stationCurrentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03));
                            BufferedImage imgtrans = loadImg("fangsu:ris/imgtrans.png");
                            if (imgtrans != null) {
                                Color imgColor = hasPassed ? new Color(124, 124, 124) : routeColor;
                                g.drawImage(JsFunctions.changeImageColor(imgtrans, imgColor), widthPercent(x, w, routeAtRight ? 0.385 : 0.585), stationCurrentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03), null);
                            }
                        }
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillOval(widthPercent(x, w, routeAtRight ? 0.39 : 0.59), stationCurrentY - (int) (w * 0.01), (int) (w * 0.02), (int) (w * 0.02));
                    }

                    int thisX = widthPercent(x, w, 0.5);
                    g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
                    int maxNameWidth = routeAtRight ? (int) (w * 0.44) : (int) (w * 0.44);
                    if (i == drawInfo.index) {
                        int textLength = 0;
                        textLength += G2dTextHelper.getUnifiedStringWidth(g, fontBold, TextUtil.getCjkParts(thisStn.stationName), w * 0.04f);
                        textLength += G2dTextHelper.getUnifiedStringWidth(g, fontBold, TextUtil.getNonCjkParts(thisStn.stationName), w * 0.025f);
                        int bgWidth = Math.min(textLength + (int) (w * 0.02), maxNameWidth + (int) (w * 0.04));
                        g.setColor(routeColor);
                        if (routeAtRight) {
                            g.fillRoundRect(widthPercent(x, w, 0.49), stationCurrentY - (int) (w * 0.03), bgWidth, (int) (w * 0.06), (int) (w * 0.01), (int) (w * 0.01));
                        } else {
                            g.fillRoundRect(widthPercent(x, w, 0.49) - bgWidth, stationCurrentY - (int) (w * 0.03), bgWidth, (int) (w * 0.06), (int) (w * 0.01), (int) (w * 0.01));
                        }
                        g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
                    }

                    if (routeAtRight) {
                        thisX += G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, TextUtil.getCjkParts(thisStn.stationName), thisX, stationCurrentY + (int) (w * 0.02), w * 0.04f, maxNameWidth, 0);
                        thisX += G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, TextUtil.getNonCjkParts(thisStn.stationName), thisX, stationCurrentY + (int) (w * 0.02), w * 0.025f, maxNameWidth, 0);
                    } else {
                        thisX -= G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, TextUtil.getCjkParts(thisStn.stationName), thisX, stationCurrentY + (int) (w * 0.02), w * 0.04f, maxNameWidth, 2);
                        thisX -= G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, TextUtil.getNonCjkParts(thisStn.stationName), thisX, stationCurrentY + (int) (w * 0.02), w * 0.025f, maxNameWidth, 2);
                    }
                }
            } else {
                // Circular state handling
                drawCircularState(g, font, fontBold, routeInfo, drawInfo, routeColor, circularState, x, y, w, h, routeAtRight, originalClip, originalTransform);
            }
        }
    }

    // ==================== Helper Methods ====================

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }

    private static Polygon createTransferClip(boolean routeAtRight, int x, int w, int currentY, double ws) {
        int wsInt = (int) ws;
        if (routeAtRight) {
            return new Polygon(
                    new int[]{widthPercent(x, w, 0.35), widthPercent(x, w, 0.36), widthPercent(x, w, 0.4), widthPercent(x, w, 0.4), widthPercent(x, w, 0.36)},
                    new int[]{currentY, (int) (currentY - wsInt * 0.015), (int) (currentY - wsInt * 0.015), (int) (currentY + wsInt * 0.015), (int) (currentY + wsInt * 0.015)},
                    5
            );
        } else {
            return new Polygon(
                    new int[]{widthPercent(x, w, 0.65), widthPercent(x, w, 0.64), widthPercent(x, w, 0.6), widthPercent(x, w, 0.6), widthPercent(x, w, 0.64)},
                    new int[]{currentY, (int) (currentY - wsInt * 0.015), (int) (currentY - wsInt * 0.015), (int) (currentY + wsInt * 0.015), (int) (currentY + wsInt * 0.015)},
                    5
            );
        }
    }

    private void drawCircularState(Graphics2D g, Font font, Font fontBold,
                                   LocalRouteDetail routeInfo, RouteDrawer.RouteDrawInfo drawInfo,
                                   Color routeColor, LocalRoute.CircularState circularState,
                                   int x, int y, int w, int h, boolean routeAtRight,
                                   Shape originalClip, AffineTransform originalTransform) {
        java.util.List<LocalRouteDetail.StationDetails> leftStations = new java.util.ArrayList<>();
        java.util.List<LocalRouteDetail.StationDetails> rightStations = new java.util.ArrayList<>();
        int leftBaseX = widthPercent(x, w, routeAtRight ? 0.45 : 0.25);
        int rightBaseX = widthPercent(x, w, routeAtRight ? 0.75 : 0.55);

        int stationCount = routeInfo.drawStations.size() - 1;
        int leftStationCount = (int) Math.floor((stationCount - 1) / 2.0) + 1;
        int rightStationCount = stationCount - leftStationCount;
        for (int i = 0; i < stationCount; i++) {
            if (circularState == LocalRoute.CircularState.CLOCKWISE) {
                if (i < rightStationCount) rightStations.add(routeInfo.drawStations.get(i));
                else leftStations.add(routeInfo.drawStations.get(i));
            } else {
                if (i < leftStationCount) leftStations.add(routeInfo.drawStations.get(i));
                else rightStations.add(routeInfo.drawStations.get(i));
            }
        }
        if (circularState == LocalRoute.CircularState.CLOCKWISE) java.util.Collections.reverse(leftStations);
        else java.util.Collections.reverse(rightStations);

        int leftStep = (int) ((h * 0.7) / (leftStationCount - 1));
        int rightStep = (int) ((h * 0.7) / (rightStationCount - 1));
        int gradientStart = drawInfo.index;
        int gradientEnd = (int) Math.floor(gradientStart - stationCount / 2.0);
        boolean halfCrossed = true;
        if (gradientEnd < 0) {
            gradientEnd += stationCount;
            halfCrossed = false;
        }

        // Draw gradient arcs and rects
        if (circularState == LocalRoute.CircularState.CLOCKWISE) {
            if (halfCrossed) {
                double part3 = 1.0;
                double part2 = (1.0 - (double) (gradientStart - rightStationCount) / leftStationCount) * 0.7;
                double part1 = part2 + 0.3;
                Color color1 = getGradientColor(routeColor, part1);
                Color color2 = getGradientColor(routeColor, part2);
                Color color3 = getGradientColor(routeColor, part3);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.15), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), routeColor, routeColor, true, 80);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.85), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), color1, color2, false, 80);
                g.setColor(routeColor);
                g.fillRect(leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                g.fillRect(rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                drawGradientRect(g, rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.85) - Math.abs(gradientEnd - rightStationCount + 1) * rightStep, (int) (w * 0.03), Math.abs((gradientEnd - rightStationCount + 1) * rightStep) + 1, routeColor, color2);
                drawGradientRect(g, leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.85) - (gradientStart - rightStationCount) * leftStep, (int) (w * 0.03), Math.abs((gradientStart - rightStationCount) * leftStep) + 1, color3, color1);
            } else {
                double part3 = 1.0;
                double part2 = (1.0 - (double) gradientStart / rightStationCount) * 0.7;
                double part1 = part2 + 0.3;
                Color color1 = getGradientColor(routeColor, part1);
                Color color2 = getGradientColor(routeColor, part2);
                Color color3 = getGradientColor(routeColor, part3);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.15), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), color2, color1, true, 80);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.85), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), routeColor, routeColor, false, 80);
                g.setColor(routeColor);
                g.fillRect(leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                g.fillRect(rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                drawGradientRect(g, rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), Math.abs(gradientStart * rightStep) + 1, color1, color3);
                drawGradientRect(g, leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), Math.abs((leftStationCount - (gradientEnd - rightStationCount) - 1) * leftStep) + 1, color2, routeColor);
            }
        } else {
            if (halfCrossed) {
                double part3 = 1.0;
                double part2 = (1.0 - (double) (gradientStart - leftStationCount) / rightStationCount) * 0.7;
                double part1 = part2 + 0.3;
                Color color1 = getGradientColor(routeColor, part1);
                Color color2 = getGradientColor(routeColor, part2);
                Color color3 = getGradientColor(routeColor, part3);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.15), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), routeColor, routeColor, true, 80);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.85), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), color2, color1, false, 80);
                g.setColor(routeColor);
                g.fillRect(leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                g.fillRect(rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                drawGradientRect(g, leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.85) - Math.abs(gradientEnd - leftStationCount + 1) * leftStep, (int) (w * 0.03), Math.abs((gradientEnd - leftStationCount + 1) * leftStep) + 1, routeColor, color2);
                drawGradientRect(g, rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.85) - (gradientStart - leftStationCount) * rightStep, (int) (w * 0.03), Math.abs((gradientStart - leftStationCount) * rightStep) + 1, color3, color1);
            } else {
                double part3 = 1.0;
                double part2 = (1.0 - (double) gradientStart / leftStationCount) * 0.7;
                double part1 = part2 + 0.3;
                Color color1 = getGradientColor(routeColor, part1);
                Color color2 = getGradientColor(routeColor, part2);
                Color color3 = getGradientColor(routeColor, part3);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.15), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), color1, color2, true, 80);
                drawGradientArc(g, 0.5 * (leftBaseX + rightBaseX), heightPercent(y, h, 0.85), 0.5 * (rightBaseX - leftBaseX) + w * 0.015, (int) (w * 0.03), routeColor, routeColor, false, 80);
                g.setColor(routeColor);
                g.fillRect(leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                g.fillRect(rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), (int) (h * 0.7 + 1));
                drawGradientRect(g, leftBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), Math.abs(gradientStart * leftStep) + 1, color1, color3);
                drawGradientRect(g, rightBaseX - (int) (w * 0.015), heightPercent(y, h, 0.15), (int) (w * 0.03), Math.abs((leftStationCount - (gradientEnd - rightStationCount) - 1) * leftStep) + 1, color2, routeColor);
            }
        }

        // Draw right stations
        for (int i = 0; i < rightStationCount; i++) {
            int stationCurrentY = heightPercent(y, h, 0.15) + rightStep * i;
            LocalRouteDetail.StationDetails thisStn = rightStations.get(i);
            int rightIndex = circularState == LocalRoute.CircularState.CLOCKWISE ? drawInfo.index : rightStationCount - (drawInfo.index - leftStationCount) - 1;
            boolean hasPassed = false;
            boolean isTransfer = !thisStn.transInfo.isEmpty();

            if (isTransfer) {
                // 环线右侧换乘：标签在圆点左边
                drawCircleTransfer(g, font, fontBold, thisStn, stationCurrentY, hasPassed, w,
                        rightBaseX, false);
            }

            // 右列图标恒在圆点右侧；图案按环线方向切换（CLOCKWISE→adb、ANTICLOCKWISE→aub）
            drawStationNode(g, routeColor, hasPassed, isTransfer, i, rightIndex, stationCurrentY, w,
                    rightBaseX, "fangsu:sign/" + (circularState == LocalRoute.CircularState.CLOCKWISE ? "adb" : "aub") + ".png",
                    true);

            g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
            int circleStrH = (int) (h * 0.035);
            int maxCircleNameWidth = (int) (w * 0.18);
            if (i == rightIndex) {
                // 站名框：超过 0.18w 时与压缩后的文本等宽截断（JS textLength 全宽，但全宽会顶出绘制区域/与换乘框重合）
                int textLength = Math.min(jsGetDLStringWidth(g, fontBold, font, thisStn.stationName, h * 0.035f), maxCircleNameWidth + (int) (w * 0.04));
                g.setColor(routeColor);
                g.fillRoundRect(rightBaseX + (int) (w * 0.05), stationCurrentY - (int) (w * 0.03), textLength + (int) (w * 0.02), (int) (w * 0.06), (int) (w * 0.01), (int) (w * 0.01));
                g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
            }
            // 站名压缩到 0.18w：align=0 左对齐，左端恒 = rightBaseX + 0.06w（压缩只改右端，不产生偏移）
            G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, rightBaseX + (int) (w * 0.06), stationCurrentY - (int) (w * 0.03) - circleStrH, circleStrH,
                    maxCircleNameWidth,
                    0, thisStn.stationName.split("\\|"));
        }

        // Draw left stations
        for (int i = 0; i < leftStationCount; i++) {
            int stationCurrentY = heightPercent(y, h, 0.15) + leftStep * i;
            LocalRouteDetail.StationDetails thisStn = leftStations.get(i);
            int leftIndex = circularState == LocalRoute.CircularState.CLOCKWISE ? leftStationCount - (drawInfo.index - rightStationCount) - 1 : drawInfo.index;
            boolean hasPassed = false;
            boolean isTransfer = !thisStn.transInfo.isEmpty();

            if (isTransfer) {
                // 环线左侧换乘：标签在圆点右边
                drawCircleTransfer(g, font, fontBold, thisStn, stationCurrentY, hasPassed, w,
                        leftBaseX, true);
            }

            // 左列图标恒在圆点左侧；图案按环线方向切换（CLOCKWISE→aub、ANTICLOCKWISE→adb，与右列相反）
            drawStationNode(g, routeColor, hasPassed, isTransfer, i, leftIndex, stationCurrentY, w,
                    leftBaseX, "fangsu:sign/" + (circularState == LocalRoute.CircularState.CLOCKWISE ? "aub" : "adb") + ".png",
                    false);

            g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
            int circleStrH = (int) (h * 0.035);
            int maxCircleNameWidth = (int) (w * 0.18);
            if (i == leftIndex) {
                // 站名框：超过 0.18w 时与压缩后的文本等宽截断
                int textLength = Math.min(jsGetDLStringWidth(g, fontBold, font, thisStn.stationName, h * 0.035f), maxCircleNameWidth + (int) (w * 0.04));
                g.setColor(routeColor);
                g.fillRoundRect(leftBaseX - (int) (w * 0.07) - textLength, stationCurrentY - (int) (w * 0.03), textLength + (int) (w * 0.02), (int) (w * 0.06), (int) (w * 0.01), (int) (w * 0.01));
                g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
            }
            // 原 jsDrawStrDl: bd=2 → x偏移-width, d=2 → 行内右对齐
            // 站名压缩到 0.18w。align=2 时块右端 = x + min(测量宽, maxWidth)，故 x 必须用
            // 压缩后宽度计算：x = 锚点 - min(测量宽, maxWidth) → 右端恒 = 锚点。
            // 之前 x 用未压缩 leftStrWidth → 偏移量 = 压缩量（「压缩越大偏移越多」的根因）
            int leftStrWidth = Math.min(G2dTextHelper.getMultiLinesWidth(g, fontBold, font, circleStrH, thisStn.stationName.split("\\|")), maxCircleNameWidth);
            G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, leftBaseX - (int) (w * 0.06) - leftStrWidth, stationCurrentY - (int) (w * 0.03) - circleStrH, circleStrH, maxCircleNameWidth, 2, thisStn.stationName.split("\\|"));
        }
    }

    private void drawStationNode(Graphics2D g, Color routeColor, boolean hasPassed, boolean isTransfer,
                                 int i, int targetIndex, int currentY, int w,
                                 int baseX, String imgPath, boolean iconOnRight) {
        if (isTransfer || i == targetIndex) {
            g.setColor(hasPassed ? new Color(124, 124, 124) : routeColor);
            g.fillOval(baseX - (int) (w * 0.02), currentY - (int) (w * 0.02), (int) (w * 0.04), (int) (w * 0.04));
            if (i == targetIndex) {
                g.setColor(Color.RED);
                g.fillOval(baseX - (int) (w * 0.015), currentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03));
                // 图标位置固定由列决定（右列在圆点右侧、左列在左侧），图案由调用方按环线方向选定
                BufferedImage img = loadImg(imgPath);
                if (img != null) {
                    int imgX = iconOnRight ? baseX + (int) (w * 0.0225) : baseX - (int) (w * 0.0525);
                    g.drawImage(img, imgX, currentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03), null);
                }
                if (isTransfer) {
                    BufferedImage imgtrans = loadImg("fangsu:ris/imgtrans.png");
                    if (imgtrans != null)
                        g.drawImage(imgtrans, baseX - (int) (w * 0.015), currentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03), null);
                }
            } else {
                g.setColor(Color.WHITE);
                g.fillOval(baseX - (int) (w * 0.015), currentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03));
                BufferedImage imgtrans = loadImg("fangsu:ris/imgtrans.png");
                if (imgtrans != null) {
                    Color imgColor = hasPassed ? new Color(124, 124, 124) : routeColor;
                    g.drawImage(JsFunctions.changeImageColor(imgtrans, imgColor), baseX - (int) (w * 0.015), currentY - (int) (w * 0.015), (int) (w * 0.03), (int) (w * 0.03), null);
                }
            }
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(baseX - (int) (w * 0.01), currentY - (int) (w * 0.01), (int) (w * 0.02), (int) (w * 0.02));
        }
    }

    private void drawTransferLabels(Graphics2D g, Font font, Font fontBold,
                                    LocalRouteDetail.StationDetails thisStn, int currentY,
                                    boolean hasPassed, int x, int w, boolean routeAtRight) {
        if (thisStn.transInfo.isEmpty()) return;

        Shape originalClip = g.getClip();
        g.setClip(createTransferClip(routeAtRight, x, w, currentY, w));

        for (int j = 0; j < thisStn.transInfo.size(); j++) {
            int thisY = (int) (currentY - w * 0.015 + ((w * 0.03) / thisStn.transInfo.size()) * j);
            ColorNameTuple thisTrans = thisStn.transInfo.get(j);
            g.setColor(hasPassed ? new Color(124, 124, 124) : thisTrans.routeColor);
            g.fillRect(widthPercent(x, w, 0.35), thisY, (int) (w * 0.3), (int) ((w * 0.03) / thisStn.transInfo.size() + 1));
        }

        g.setClip(originalClip);
        g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);

        for (int j = 0; j < thisStn.transInfo.size(); j++) {
            ColorNameTuple thisTrans = thisStn.transInfo.get(j);
            int boxY = (int) (currentY - w * 0.03 * thisStn.transInfo.size() * 0.5 + w * 0.03 * j);
            // 与 JS 一致：文本按全宽绘制，maxWidth 传未压缩宽度（不触发压缩）。
            // 传 0.22w 截断会导致超长文本被压缩时各行右端不对齐（见 drawTransTextStretch 注释）
            int transStrH = (int) (w * 0.02);
            String cjkName = String.valueOf(getCJKLineName(TextUtil.getCjkParts(thisTrans.routeName)));
            int cjkNameW = G2dTextHelper.getUnifiedStringWidth(g, fontBold, cjkName, w * 0.02f);
            String dlStr = "号线" + (TextUtil.getNonCjkParts(thisTrans.routeName).isEmpty() ? "" : "|" + TextUtil.getNonCjkParts(thisTrans.routeName));
            int dlWidth = jsGetDLStringWidth(g, fontBold, font, dlStr, w * 0.02f);
            int textWidth;
            if (isNumLine(thisTrans.routeName)) {
                textWidth = dlWidth + cjkNameW;
            } else {
                textWidth = jsGetDLStringWidth(g, fontBold, font, thisTrans.routeName, w * 0.02f);
            }

            g.setColor(hasPassed ? new Color(124, 124, 124) : thisTrans.routeColor);
            if (routeAtRight) {
                g.fillRect(widthPercent(x, w, 0.33) - textWidth - (int) (w * 0.02), boxY, textWidth + (int) (w * 0.02), (int) (w * 0.03 + 1));
                g.setColor(hasPassed ? Color.WHITE : ColorUtil.isLightColor(thisTrans.routeColor) ? Color.BLACK : Color.WHITE);
                if (isNumLine(thisTrans.routeName)) {
                    int textX = widthPercent(x, w, 0.32);
                    // 原 JS: textX -= drawStrDL(号线|..., bd=2, d=0)
                    // 然后 textX -= drawStrUnified(cjkName, align=2)
                    G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, textX - dlWidth, boxY + (int) (w * 0.005) - transStrH, transStrH, dlWidth, 0, dlStr.split("\\|"));
                    textX -= dlWidth;
                    G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, cjkName, textX, boxY + (int) (w * 0.02), w * 0.02f, cjkNameW, 2);
                } else {
                    // 原 jsDrawStrDl: bd=2(偏移-width), d=1(居中)
                    int transWidth = jsGetDLStringWidth(g, fontBold, font, thisTrans.routeName, transStrH);
                    G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, widthPercent(x, w, 0.32) - transWidth, boxY + (int) (w * 0.005) - transStrH, transStrH, transWidth, 1, thisTrans.routeName.split("\\|"));
                }
            } else {
                g.fillRect(widthPercent(x, w, 0.67), boxY, textWidth + (int) (w * 0.02), (int) (w * 0.03 + 1));
                g.setColor(hasPassed ? Color.WHITE : ColorUtil.isLightColor(thisTrans.routeColor) ? Color.BLACK : Color.WHITE);
                if (isNumLine(thisTrans.routeName)) {
                    int textX = widthPercent(x, w, 0.68);
                    textX += G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, cjkName, textX, boxY + (int) (w * 0.02), w * 0.02f, cjkNameW, 0);
                    G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, textX, boxY + (int) (w * 0.005) - transStrH, transStrH, dlWidth, 0, dlStr.split("\\|"));
                } else {
                    int transWidth = jsGetDLStringWidth(g, fontBold, font, thisTrans.routeName, transStrH);
                    G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, widthPercent(x, w, 0.68), boxY + (int) (w * 0.005) - transStrH, transStrH, transWidth, 1, thisTrans.routeName.split("\\|"));
                }
            }
        }
    }

    /**
     * 环线换乘绘制：在圆点附近绘制换乘标签
     *
     * @param baseX      圆点中心X坐标
     * @param isLeftSide 是否为左侧站点
     */
    private void drawCircleTransfer(Graphics2D g, Font font, Font fontBold,
                                    LocalRouteDetail.StationDetails thisStn, int currentY,
                                    boolean hasPassed, int w, int baseX, boolean isLeftSide) {
        if (thisStn.transInfo.isEmpty()) return;

        Shape originalClip = g.getClip();

        // 裁剪区域（五边形），围绕站点圆点。
        // 右侧列: [baseX - w*0.05, baseX - w*0.04, baseX, baseX, baseX - w*0.04]（开口朝左，色条向圆点左边延伸）
        // 左侧列: 镜像 [baseX + w*0.05, baseX + w*0.04, baseX, baseX, baseX + w*0.04]（开口朝右）
        int clipOffset = (int) (w * 0.05);
        if (isLeftSide) {
            g.setClip(new Polygon(
                    new int[]{baseX + clipOffset, baseX + clipOffset - (int) (w * 0.01), baseX, baseX, baseX + clipOffset - (int) (w * 0.01)},
                    new int[]{currentY, (int) (currentY - w * 0.015), (int) (currentY - w * 0.015), (int) (currentY + w * 0.015), (int) (currentY + w * 0.015)},
                    5
            ));
        } else {
            g.setClip(new Polygon(
                    new int[]{baseX - clipOffset, baseX - clipOffset + (int) (w * 0.01), baseX, baseX, baseX - clipOffset + (int) (w * 0.01)},
                    new int[]{currentY, (int) (currentY - w * 0.015), (int) (currentY - w * 0.015), (int) (currentY + w * 0.015), (int) (currentY + w * 0.015)},
                    5
            ));
        }

        // 绘制换乘色条（被五边形裁剪）。
        // 宽度用有限值（覆盖五边形即可）：JS 原版为 fillRect(widthPercent(0.1), thisY, w*0.9, ...)，
        // 用 Integer.MAX_VALUE 在 Graphics2D 带 transform 时可能溢出导致整条不绘制
        for (int j = 0; j < thisStn.transInfo.size(); j++) {
            int thisY = (int) (currentY - w * 0.015 + ((w * 0.03) / thisStn.transInfo.size()) * j);
            ColorNameTuple thisTrans = thisStn.transInfo.get(j);
            g.setColor(hasPassed ? new Color(124, 124, 124) : thisTrans.routeColor);
            g.fillRect(baseX - (int) (w * 0.06), thisY, (int) (w * 0.12), (int) ((w * 0.03) / thisStn.transInfo.size() + 1));
        }

        g.setClip(originalClip);
        g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);

        // 绘制换乘线路标签
        for (int j = 0; j < thisStn.transInfo.size(); j++) {
            ColorNameTuple thisTrans = thisStn.transInfo.get(j);
            int boxY = (int) (currentY - w * 0.03 * thisStn.transInfo.size() * 0.5 + w * 0.03 * j);
            // 与 JS 一致：标签框取文本全宽，不截断不压缩（JS 的框宽 = textWidth + w*0.02，无 maxWidth 参数）
            int textWidth = calcTransTextWidth(g, fontBold, font, thisTrans, w);

            g.setColor(hasPassed ? new Color(124, 124, 124) : thisTrans.routeColor);
            if (isLeftSide) {
                // 左侧：标签在圆点右边，文本左端固定（JS: leftBaseX + w*0.08 起，向右延伸）
                int labelX = baseX + (int) (w * 0.07);
                g.fillRect(labelX, boxY, textWidth + (int) (w * 0.02), (int) (w * 0.03 + 1));
                g.setColor(hasPassed ? Color.WHITE : ColorUtil.isLightColor(thisTrans.routeColor) ? Color.BLACK : Color.WHITE);
                drawTransTextStretch(g, fontBold, font, thisTrans, labelX + (int) (w * 0.01), boxY, w, textWidth, true);
            } else {
                // 右侧：标签在圆点左边，文本右端固定（JS: rightBaseX - w*0.08 处右对齐）
                // 框左端 = baseX - w*0.07 - textWidth - w*0.02（0.02 为左侧内边距，JS 原版有，漏掉会
                // 导致文本左端比框左缘还靠左，看起来整体偏左）
                int labelX = baseX - (int) (w * 0.07) - textWidth - (int) (w * 0.02);
                g.fillRect(labelX, boxY, textWidth + (int) (w * 0.02) + 1, (int) (w * 0.03));
                g.setColor(hasPassed ? Color.WHITE : ColorUtil.isLightColor(thisTrans.routeColor) ? Color.BLACK : Color.WHITE);
                drawTransTextStretch(g, fontBold, font, thisTrans, baseX - (int) (w * 0.08), boxY, w, textWidth, false);
            }
        }
    }

    private int calcTransTextWidth(Graphics2D g, Font fontBold, Font font, ColorNameTuple thisTrans, int w) {
        if (isNumLine(thisTrans.routeName)) {
            String nonCjk = TextUtil.getNonCjkParts(thisTrans.routeName);
            String dlStr = "号线" + (nonCjk.isEmpty() ? "" : "|" + nonCjk);
            return jsGetDLStringWidth(g, fontBold, font, dlStr, w * 0.02f)
                    + G2dTextHelper.getUnifiedStringWidth(g, fontBold, String.valueOf(getCJKLineName(TextUtil.getCjkParts(thisTrans.routeName))), w * 0.02f);
        } else {
            return jsGetDLStringWidth(g, fontBold, font, thisTrans.routeName, w * 0.02f);
        }
    }

    /**
     * 绘制换乘线路标签文本（环线）。
     *
     * @param textX         文本锚点X：左列 = 左端；右列 = 右端（对应 JS leftBaseX+w*0.08 / rightBaseX-w*0.08）
     * @param textWidth     文本总宽度（与标签框宽度一致，全宽不压缩）
     * @param isLeftSide    是否为左侧站点
     *
     * 注意 y 换算：drawStrMultiLinesWithStretch 的 y 参数比 JS drawStrDL 的 y 多一个行高 h（对照
     * drawTransferLabels 中 boxY + w*0.005 - transStrH 的正确写法），此处号线传 boxY - w*0.015
     * （= boxY + w*0.005 - w*0.02）；drawStrUnifiedWithStretch 的 y 即基线（= JS drawStrUnified），
     * cjk 保持 boxY + w*0.02。
     * 与 JS 一致：文本按全宽绘制，不压缩（JS 原版无 maxWidth 参数，标签框同样取全宽；
     * 之前加 0.22w 截断后，非数字线路两行文本压缩时中文行（行0）不压缩、英文行（行1）
     * 压缩到 maxWidth，导致中文行右端严重偏离 textX——「压缩文本往左偏移」的根因，已移除）。
     */
    private void drawTransTextStretch(Graphics2D g, Font fontBold, Font font, ColorNameTuple thisTrans,
                                      int textX, int boxY, int w, int textWidth, boolean isLeftSide) {
        if (isNumLine(thisTrans.routeName)) {
            String cjkStr = String.valueOf(getCJKLineName(TextUtil.getCjkParts(thisTrans.routeName)));
            String nonCjk = TextUtil.getNonCjkParts(thisTrans.routeName);
            // 无非中文字段时就是单行「号线」（split 会丢弃尾部空串），与 JS drawStrDL 一致
            String dlStr = "号线" + (nonCjk.isEmpty() ? "" : "|" + nonCjk);
            int cjkW = G2dTextHelper.getUnifiedStringWidth(g, fontBold, cjkStr, w * 0.02f);
            int dlW = jsGetDLStringWidth(g, fontBold, font, dlStr, w * 0.02f);
            if (isLeftSide) {
                // 左端固定：cjk 在左、号线接在右侧（JS: textX = leftBaseX + w*0.08; += cjk; += 号线）
                int cx = textX;
                cx += G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, cjkStr, cx, boxY + (int) (w * 0.02), w * 0.02f, cjkW, 0);
                G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, cx, boxY - (int) (w * 0.015), (int) (w * 0.02), dlW, 0, dlStr.split("\\|"));
            } else {
                // 右端固定：号线右端 = textX，cjk 右端 = 号线左端（JS: textX = rightBaseX - w*0.08; -= 号线; -= cjk）
                G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, textX - dlW, boxY - (int) (w * 0.015), (int) (w * 0.02), dlW, 0, dlStr.split("\\|"));
                G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, cjkStr, textX - dlW - cjkW, boxY + (int) (w * 0.02), w * 0.02f, cjkW, 2);
            }
        } else {
            if (isLeftSide) {
                G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, textX, boxY - (int) (w * 0.015), (int) (w * 0.02), textWidth, 0, thisTrans.routeName.split("\\|"));
            } else {
                G2dTextHelper.drawStrMultiLinesWithStretch(g, fontBold, font, textX - textWidth, boxY - (int) (w * 0.015), (int) (w * 0.02), textWidth, 0, thisTrans.routeName.split("\\|"));
            }
        }
    }

    // ==================== Drawing Utility Methods ====================

    private static void drawGradientRect(Graphics2D g2d, int x, int y, int width, int height, Color topColor, Color bottomColor) {
        Point2D.Float startPoint = new Point2D.Float(x + width / 2f, y);
        Point2D.Float endPoint = new Point2D.Float(x + width / 2f, y + height);
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(startPoint, topColor, endPoint, bottomColor, false);
        Paint originalPaint = g2d.getPaint();
        g2d.setPaint(gradient);
        g2d.fillRect(x, y, width, height);
        g2d.setPaint(originalPaint);
    }

    private static void drawGradientArc(Graphics2D g, double centerX, double centerY, double outerRadius, int width, Color startColor, Color endColor, boolean isTop, int segments) {
        double innerRadius = outerRadius - width;
        double angleIncrement = 180.0 / segments;
        double startAngle = isTop ? 180 : 0;
        double endAngle = isTop ? 360 : 180;
        double direction = -1;

        for (int i = 0; i < segments; i++) {
            double progress = (double) i / segments;
            double angle1 = startAngle + direction * i * angleIncrement;
            double angle2 = startAngle + direction * (i + 1) * angleIncrement;

            double t = isTop ? progress : 1 - progress;
            Color segmentColor = interpolateColor(startColor, endColor, t);
            g.setColor(segmentColor);

            Path2D.Double path = new Path2D.Double();

            double[] innerStart = getArcPoint(angle1, innerRadius, centerX, centerY);
            double[] outerStart = getArcPoint(angle1, outerRadius, centerX, centerY);
            double[] outerEnd = getArcPoint(angle2, outerRadius, centerX, centerY);
            double[] innerEnd = getArcPoint(angle2, innerRadius, centerX, centerY);

            path.moveTo(innerStart[0], innerStart[1]);
            path.lineTo(outerStart[0], outerStart[1]);
            path.lineTo(outerEnd[0], outerEnd[1]);
            path.lineTo(innerEnd[0], innerEnd[1]);
            path.closePath();

            g.fill(path);
        }
    }

    private static double[] getArcPoint(double angleDeg, double radius, double centerX, double centerY) {
        double cos = Math.cos(angleDeg * Math.PI / 180.0);
        double sin = -Math.sin(angleDeg * Math.PI / 180.0);
        return new double[]{centerX + radius * cos, centerY + radius * sin};
    }

    private static Color interpolateColor(Color color1, Color color2, double t) {
        int r = (int) Math.round(color1.getRed() + t * (color2.getRed() - color1.getRed()));
        int g = (int) Math.round(color1.getGreen() + t * (color2.getGreen() - color1.getGreen()));
        int b = (int) Math.round(color1.getBlue() + t * (color2.getBlue() - color1.getBlue()));
        int a = (int) Math.round(color1.getAlpha() + t * (color2.getAlpha() - color1.getAlpha()));
        return new Color(r, g, b, a);
    }

    private static Color getGradientColor(Color startColor, double t) {
        t = Math.max(0, Math.min(1, t));
        int r = startColor.getRed();
        int g = startColor.getGreen();
        int b = startColor.getBlue();
        double targetR = (r + 2 * 255) / 3.0;
        double targetG = (g + 2 * 255) / 3.0;
        double targetB = (b + 2 * 255) / 3.0;
        int resultR = (int) Math.round(Math.max(0, Math.min(255, r + t * (targetR - r))));
        int resultG = (int) Math.round(Math.max(0, Math.min(255, g + t * (targetG - g))));
        int resultB = (int) Math.round(Math.max(0, Math.min(255, b + t * (targetB - b))));
        return new Color(resultR, resultG, resultB);
    }

    private static int jsDrawStrDl(Graphics2D g, Font cjkFont, Font nonCjkFont, String str, double x, double y, double h, int bd, int d) {
        String drawStr = str == null ? "" : str;
        int width = G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, (float) h, drawStr.split("\\|"));
        return G2dTextHelper.drawStrMultiLines(g, cjkFont, nonCjkFont, (int) x - (bd == 1 ? width / 2 : bd == 2 ? width : 0), (int) y - (int) h, (int) h, d, drawStr.split("\\|"));
    }

    private static int jsGetDLStringWidth(Graphics2D g, Font cjkFont, Font nonCjkFont, String str, double h) {
        String drawStr = str == null ? "" : str;
        return G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, (float) h, drawStr.split("\\|"));
    }

    private static String getCJKLineName(String lineStr) {
        return RouteNameUtil.getCJKLineName(lineStr);
    }

    private static boolean isNumLine(String lineStr) {
        return RouteNameUtil.isNumLine(lineStr);
    }
}
