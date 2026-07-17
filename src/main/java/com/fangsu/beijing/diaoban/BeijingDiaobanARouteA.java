package com.fangsu.beijing.diaoban;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.diaoban.BaseDiaobanDrawing;
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
import java.util.List;
import java.util.Map;

public class BeijingDiaobanARouteA extends BaseDiaobanDrawing {

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

        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        RouteDrawer.RouteDrawInfo drawInfo = buildDrawInfo(routes, arrowDirection, texW, texH);
        LocalRouteDetail routeInfo = !routes.isEmpty() ? (routes.get(0).route).asRouteDetail() : new LocalRoute().asRouteDetail();

        if (drawInfo.routeInfo() != null) {
            Shape originalClip = g.getClip();
            AffineTransform originalTransform = g.getTransform();

            Font font = getFont();
            Font fontBold = getFontBold();
            Color routeColor = drawInfo.routeInfo().routeColor;
            int arrowSide = arrowDirection;
            LocalRoute.CircularState circularState = drawInfo.routeInfo().circularState;

            if (circularState == LocalRoute.CircularState.NONE) {
                int distance = (int) ((w * 0.8) / (drawInfo.routeInfo().drawStations.size() - 1));
                Color colorGray = new Color(127, 132, 137);

                g.setColor(colorGray);
                g.fillRect(widthPercent(x, w, 0.1), heightPercent(y, h, 0.56), (int) (w * 0.8), (int) (h * 0.08));

                if (arrowSide != 2) {
                    g.fillOval(widthPercent(x, w, 0.9) - (int) (h * 0.04), heightPercent(y, h, 0.56), (int) (h * 0.08), (int) (h * 0.08));
                    g.setColor(routeColor);
                    g.fillRect(widthPercent(x, w, 0.1), heightPercent(y, h, 0.56),
                            distance * (drawInfo.routeInfo().drawStations.size() - drawInfo.index() - 1), (int) (h * 0.08));
                    g.fillOval(widthPercent(x, w, 0.1) - (int) (h * 0.04), heightPercent(y, h, 0.56), (int) (h * 0.08), (int) (h * 0.08));
                } else {
                    g.fillOval(widthPercent(x, w, 0.1) - (int) (h * 0.04), heightPercent(y, h, 0.56), (int) (h * 0.08), (int) (h * 0.08));
                    g.setColor(routeColor);
                    g.fillRect(widthPercent(x, w, 0.1), heightPercent(y, h, 0.56),
                            distance * (drawInfo.routeInfo().drawStations.size() - drawInfo.index() - 1), (int) (h * 0.08));
                    g.fillOval(widthPercent(x, w, 0.9) - (int) (h * 0.04), heightPercent(y, h, 0.56), (int) (h * 0.08), (int) (h * 0.08));
                }

                for (int i = 0; i < drawInfo.routeInfo().drawStations.size(); i++) {
                    int currentX = (arrowSide != 2 ? widthPercent(x, w, 0.1) : widthPercent(x, w, 0.9))
                            + (arrowSide != 2 ? 1 : -1) * distance * i;
                    LocalRouteDetail.StationDetails thisStn = drawInfo.routeInfo().drawStations.get(i);
                    boolean hasPassed = i < drawInfo.index();
                    boolean isTransfer = thisStn.transInfo != null && thisStn.transInfo.size() > 0;

                    if (isTransfer) {
                        double h0025 = h * 0.025;
                        double h015 = h * 0.15;
                        double h0165 = h * 0.165;
                        double h0204 = h * 0.204;
                        int numTrans = thisStn.transInfo.size();
                        double segmentWidth = h0025 / numTrans;

                        int[] clipX = {currentX + (int) h0025, currentX + (int) (h * 0.05),
                                currentX - (int) h015, currentX - (int) h0165, currentX - (int) h0165};
                        int[] clipY = {heightPercent(y, h, 0.6), heightPercent(y, h, 0.6),
                                heightPercent(y, h, 0.85), heightPercent(y, h, 0.85), heightPercent(y, h, 0.825)};
                        g.setClip(new Polygon(clipX, clipY, 5));

                        for (int j = 0; j < numTrans; j++) {
                            int thisX = currentX + (int) h0025 + (int) (segmentWidth * j);
                            ColorNameTuple thisTrans = thisStn.transInfo.get(j);
                            g.setColor(hasPassed ? new Color(124, 124, 124) : thisTrans.routeColor);
                            g.fillPolygon(
                                    new int[]{thisX, thisX + (int) segmentWidth + 1,
                                            thisX - (int) h0204 + (int) segmentWidth + 1,
                                            thisX - (int) h0204},
                                    new int[]{heightPercent(y, h, 0.6), heightPercent(y, h, 0.6),
                                            heightPercent(y, h, 0.85), heightPercent(y, h, 0.85)}, 4);
                        }
                        g.setClip(originalClip);
                    }

                    if (isTransfer || i == drawInfo.index()) {
                        g.setColor(hasPassed ? new Color(124, 124, 124) : routeColor);
                        g.fillOval(currentX - (int) (h * 0.05), heightPercent(y, h, 0.55), (int) (h * 0.1), (int) (h * 0.1));
                        if (i == drawInfo.index()) {
                            g.setColor(Color.RED);
                            g.fillOval(currentX - (int) (h * 0.04), heightPercent(y, h, 0.56), (int) (h * 0.08), (int) (h * 0.08));
                            if (isTransfer) {
                                g.drawImage(loadImg("fangsu:ris/imgtrans.png"),
                                        currentX - (int) (h * 0.04), heightPercent(y, h, 0.56),
                                        (int) (h * 0.08), (int) (h * 0.08), null);
                            }
                        } else {
                            g.setColor(Color.WHITE);
                            g.fillOval(currentX - (int) (h * 0.04), heightPercent(y, h, 0.56), (int) (h * 0.08), (int) (h * 0.08));
                            g.drawImage(JsFunctions.changeImageColor(loadImg("fangsu:ris/imgtrans.png"),
                                            hasPassed ? new Color(124, 124, 124) : routeColor),
                                    currentX - (int) (h * 0.04), heightPercent(y, h, 0.56),
                                    (int) (h * 0.08), (int) (h * 0.08), null);
                        }
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillOval(currentX - (int) (h * 0.025), heightPercent(y, h, 0.575), (int) (h * 0.05), (int) (h * 0.05));
                    }

                    AffineTransform transform = AffineTransform.getRotateInstance(-0.25 * Math.PI, currentX, heightPercent(y, h, 0.6));
                    g.setTransform(transform);
                    g.setColor(hasPassed ? new Color(124, 124, 124) : Color.BLACK);
                    if (i == drawInfo.index()) {
                        int textLength = JsFunctions.jsGetDLStringWidth(g, fontBold, font, thisStn.stationName, h * 0.15);
                        g.setColor(routeColor);
                        g.fillRoundRect(currentX + (int) (h * 0.05), heightPercent(y, h, 0.6) - (int) (h * 0.2),
                                textLength + (int) (h * 0.02), (int) (h * 0.17), (int) (h * 0.01), (int) (h * 0.01));
                        g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
                    }
                    JsFunctions.jsDrawStrDl(g, fontBold, font, thisStn.stationName,
                            currentX + (int) (h * 0.075), heightPercent(y, h, 0.6) - (int) (h * 0.2), h * 0.15, 0, 0);

                    if (isTransfer) {
                        JsFunctions.jsDrawStrDl(g, fontBold, font, thisStn.transInfo.get(0).routeName,
                                currentX - (int) (h * 0.09), heightPercent(y, h, 0.6) - (int) (h * 0.025), h * 0.05, 2, 2);
                    }
                    if (thisStn.transInfo != null && thisStn.transInfo.size() > 1) {
                        JsFunctions.jsDrawStrDl(g, fontBold, font,
                                thisStn.transInfo.get(thisStn.transInfo.size() - 1).routeName,
                                currentX - (int) (h * 0.01), heightPercent(y, h, 0.6) + (int) (h * 0.065), h * 0.05, 2, 2);
                    }
                    g.setTransform(originalTransform);
                }
            } else {
                // 环线绘制
                drawCircularState(g, font, fontBold, routeInfo, drawInfo, routeColor, circularState, arrowSide, x, y, w, h, originalClip, originalTransform);
            }
        }
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }

    // ==================== 环线绘制相关方法 ====================

    private void drawCircularState(Graphics2D g, Font font, Font fontBold,
                                   LocalRouteDetail routeInfo, RouteDrawer.RouteDrawInfo drawInfo,
                                   Color routeColor, LocalRoute.CircularState circularState, int arrowSide,
                                   int x, int y, int w, int h, Shape originalClip, AffineTransform originalTransform) {
        // SIS右条→上条, SIS左条→下条; SIS顶弧→右弧, SIS底弧→左弧
        double bt = h * 0.06;
        double topY = h * 0.15, botY = h * 0.79, bLen = w * 0.76, bX0 = w * 0.12, bX1 = w * 0.88;
        double cy = h * 0.5, oR = (botY - topY + bt) / 2.0;
        int aW = (int) bt;

        int sc = routeInfo.drawStations.size() - 1;
        if (sc < 1) return;
        int topN = (int) Math.floor((sc - 1) / 2.0) + 1, botN = sc - topN;

        java.util.List<LocalRouteDetail.StationDetails> topL = new java.util.ArrayList<>(), botL = new java.util.ArrayList<>();
        for (int i = 0; i < sc; i++) {
            if (circularState == LocalRoute.CircularState.CLOCKWISE) {
                if (i < topN) topL.add(routeInfo.drawStations.get(i));
                else botL.add(routeInfo.drawStations.get(i));
            } else {
                if (i < botN) botL.add(routeInfo.drawStations.get(i));
                else topL.add(routeInfo.drawStations.get(i));
            }
        }
        if (circularState == LocalRoute.CircularState.CLOCKWISE) java.util.Collections.reverse(botL);
        else java.util.Collections.reverse(topL);

        double topS = bLen / Math.max(1, topN - 1), botS = bLen / Math.max(1, botN - 1);
        int gs = drawInfo.index(), ge = (int) Math.floor(gs - sc / 2.0);
        boolean hc = true;
        if (ge < 0) {
            ge += sc;
            hc = false;
        }

        if (circularState == LocalRoute.CircularState.CLOCKWISE) {
            if (hc) {
                double p3 = 1.0, p2 = (1.0 - (double) (gs - topN) / botN) * 0.7, p1 = p2 + 0.3;
                Color c1 = gc(routeColor, p1), c2 = gc(routeColor, p2), c3 = gc(routeColor, p3);
                drawGradientArcH(g, bX1, cy, oR, aW, routeColor, routeColor, false, 80);  // 右弧全色
                drawGradientArcH(g, bX0, cy, oR, aW, c1, c2, true, 80);                   // 左弧渐变c1→c2
                g.setColor(routeColor);
                g.fillRect((int) bX0, (int) topY, (int) bLen, aW);
                g.fillRect((int) bX0, (int) botY, (int) bLen, aW);
                drawGradientRectH(g, (int) bX0, (int) botY, Math.abs((gs - topN) * (int) botS) + 1, aW, c3, c1);
                drawGradientRectH(g, (int) bX0, (int) topY, Math.abs((ge - botN + 1) * (int) topS) + 1, aW, routeColor, c2);
            } else {
                double p3 = 1.0, p2 = (1.0 - (double) gs / topN) * 0.7, p1 = p2 + 0.3;
                Color c1 = gc(routeColor, p1), c2 = gc(routeColor, p2), c3 = gc(routeColor, p3);
                drawGradientArcH(g, bX1, cy, oR, aW, c2, c1, false, 80);  // 右弧渐变
                drawGradientArcH(g, bX0, cy, oR, aW, routeColor, routeColor, true, 80);   // 左弧全色
                g.setColor(routeColor);
                g.fillRect((int) bX0, (int) topY, (int) bLen, aW);
                g.fillRect((int) bX0, (int) botY, (int) bLen, aW);
                drawGradientRectH(g, (int) (bX1 - Math.abs((topN - (ge - botN) - 1) * (int) topS)), (int) topY, Math.abs((topN - (ge - botN) - 1) * (int) topS) + 1, aW, c2, routeColor);
                drawGradientRectH(g, (int) (bX1 - Math.abs(gs * (int) topS)), (int) topY, Math.abs(gs * (int) topS) + 1, aW, c1, c3);
            }
        } else {
            if (hc) {
                double p3 = 1.0, p2 = (1.0 - (double) (gs - botN) / topN) * 0.7, p1 = p2 + 0.3;
                Color c1 = gc(routeColor, p1), c2 = gc(routeColor, p2), c3 = gc(routeColor, p3);
                drawGradientArcH(g, bX1, cy, oR, aW, c2, c1, false, 80);  // 右弧渐变
                drawGradientArcH(g, bX0, cy, oR, aW, routeColor, routeColor, true, 80);   // 左弧全色
                g.setColor(routeColor);
                g.fillRect((int) bX0, (int) topY, (int) bLen, aW);
                g.fillRect((int) bX0, (int) botY, (int) bLen, aW);
                drawGradientRectH(g, (int) bX0, (int) topY, Math.abs((gs - botN) * (int) topS) + 1, aW, c3, c1);
                drawGradientRectH(g, (int) bX0, (int) botY, Math.abs((ge - topN + 1) * (int) botS) + 1, aW, routeColor, c2);
            } else {
                double p3 = 1.0, p2 = (1.0 - (double) gs / botN) * 0.7, p1 = p2 + 0.3;
                Color c1 = gc(routeColor, p1), c2 = gc(routeColor, p2), c3 = gc(routeColor, p3);
                drawGradientArcH(g, bX1, cy, oR, aW, routeColor, routeColor, false, 80);  // 右弧全色
                drawGradientArcH(g, bX0, cy, oR, aW, c1, c2, true, 80);                   // 左弧渐变
                g.setColor(routeColor);
                g.fillRect((int) bX0, (int) topY, (int) bLen, aW);
                g.fillRect((int) bX0, (int) botY, (int) bLen, aW);
                drawGradientRectH(g, (int) (bX1 - Math.abs(gs * (int) botS)), (int) botY, Math.abs(gs * (int) botS) + 1, aW, c1, c3);
                drawGradientRectH(g, (int) (bX1 - Math.abs((topN - (ge - botN) - 1) * (int) botS)), (int) botY, Math.abs((topN - (ge - botN) - 1) * (int) botS) + 1, aW, c2, routeColor);
            }
        }

        // —— 上条站点(SIS右条, 从右往左) ——
        for (int i = 0; i < topN; i++) {
            double sxD = bX1 - topS * i;
            int sx = (int) sxD;
            LocalRouteDetail.StationDetails stn = topL.get(i);
            int idx = circularState == LocalRoute.CircularState.CLOCKWISE ? drawInfo.index() : topN - (drawInfo.index() - botN) - 1;
            boolean isCur = (i == idx), isTr = stn.transInfo != null && !stn.transInfo.isEmpty();
            if (isTr) drawCircularTransfer(g, font, fontBold, stn, sx, (int) (topY), w, h, false);
            drawCircularStationCircle(g, routeColor, sx, (int) (topY + bt / 2), h, isCur, isTr);
            g.setColor(Color.BLACK);
            int nH = (int) (h * 0.15);
            if (isCur) {
                int tLen = JsFunctions.jsGetDLStringWidth(g, fontBold, font, stn.stationName, nH);
                g.setColor(routeColor);
                g.fillRoundRect(sx - tLen / 2 - (int) (h * 0.015), (int) (topY - nH * 1.2 - h * 0.08),
                        tLen + (int) (h * 0.03), (int) (h * 0.2), (int) (h * 0.05), (int) (h * 0.05));
                g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
            }
            JsFunctions.jsDrawStrDl(g, fontBold, font, stn.stationName, sx, (int) (topY - h * 0.04), nH, 1, 1);
        }
        // —— 下条站点(SIS左条, 从左往右) ——
        for (int i = 0; i < botN; i++) {
            double sxD = bX0 + botS * i;
            int sx = (int) sxD;
            LocalRouteDetail.StationDetails stn = botL.get(i);
            int idx = circularState == LocalRoute.CircularState.CLOCKWISE ? botN - (drawInfo.index() - topN) - 1 : drawInfo.index();
            boolean isCur = (i == idx), isTr = stn.transInfo != null && !stn.transInfo.isEmpty();
            if (isTr) drawCircularTransfer(g, font, fontBold, stn, sx, (int) (botY + bt), w, h, true);
            drawCircularStationCircle(g, routeColor, sx, (int) (botY + bt / 2), h, isCur, isTr);
            g.setColor(Color.BLACK);
            int nH = (int) (h * 0.15);
            if (isCur) {
                int tLen = JsFunctions.jsGetDLStringWidth(g, fontBold, font, stn.stationName, nH);
                g.setColor(routeColor);
                g.fillRoundRect(sx - tLen / 2 - (int) (h * 0.015), (int) (botY + bt + h * 0.08),
                        tLen + (int) (h * 0.03), (int) (h * 0.2), (int) (h * 0.05), (int) (h * 0.05));
                g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
            }
            JsFunctions.jsDrawStrDl(g, fontBold, font, stn.stationName, sx, (int) (botY + bt + h * 0.15), nH, 1, 1);
        }
    }

    /**
     * 环线站点圆圈 — 无已通过灰色状态
     */
    private void drawCircularStationCircle(Graphics2D g, Color routeColor, int cx, int cy, int h,
                                           boolean isCurrent, boolean isTransfer) {
        if (isTransfer || isCurrent) {
            g.setColor(routeColor);
            g.fillOval(cx - (int) (h * 0.05), cy - (int) (h * 0.05), (int) (h * 0.1), (int) (h * 0.1));
            if (isCurrent) {
                g.setColor(Color.RED);
                g.fillOval(cx - (int) (h * 0.04), cy - (int) (h * 0.04), (int) (h * 0.08), (int) (h * 0.08));
                if (isTransfer) {
                    java.awt.image.BufferedImage img = loadImg("fangsu:ris/imgtrans.png");
                    if (img != null)
                        g.drawImage(img, cx - (int) (h * 0.04), cy - (int) (h * 0.04), (int) (h * 0.08), (int) (h * 0.08), null);
                }
            } else {
                g.setColor(Color.WHITE);
                g.fillOval(cx - (int) (h * 0.04), cy - (int) (h * 0.04), (int) (h * 0.08), (int) (h * 0.08));
                java.awt.image.BufferedImage img = loadImg("fangsu:ris/imgtrans.png");
                if (img != null)
                    g.drawImage(JsFunctions.changeImageColor(img, routeColor),
                            cx - (int) (h * 0.04), cy - (int) (h * 0.04), (int) (h * 0.08), (int) (h * 0.08), null);
            }
        } else {
            g.setColor(Color.WHITE);
            g.fillOval(cx - (int) (h * 0.025), cy - (int) (h * 0.025), (int) (h * 0.05), (int) (h * 0.05));
        }
    }

    /**
     * 环线换乘标签 — 方框不旋转, 高度h/10, 文字水平
     */
    private void drawCircularTransfer(Graphics2D g, Font f, Font fb,
                                      LocalRouteDetail.StationDetails stn, int sx, int baseY,
                                      int w, int h, boolean pointUp) {
        if (stn.transInfo == null || stn.transInfo.isEmpty()) return;
        int n = stn.transInfo.size(), bH = Math.max(1, (int) (h * 0.1));
        int bY = pointUp ? baseY - bH - (int) (h * 0.03) : baseY + (int) (h * 0.03);
        for (int j = 0; j < n; j++) {
            ColorNameTuple t = stn.transInfo.get(j);
            int tW = calcCircTextW(g, fb, f, t, bH), maxW = (int) (w * 0.12);
            int bW = Math.min(tW, maxW) + (int) (h * 0.02);
            g.setColor(t.routeColor);
            g.fillRoundRect(sx - bW / 2, bY + bH * j, bW, bH, (int) (h * 0.01), (int) (h * 0.01));
            g.setColor(ColorUtil.isLightColor(t.routeColor) ? Color.BLACK : Color.WHITE);
            if (RouteNameUtil.isNumLine(t.routeName)) {
                String cj = String.valueOf(RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(t.routeName)));
                int w1 = G2dTextHelper.drawStrUnified(g, fb, cj, sx - bW / 2 + (int) (h * 0.01), bY + bH * j + bH * 0.7f, bH * 0.25f, 0);
                JsFunctions.jsDrawStrDl(g, f, f, "号线|" + TextUtil.getNonCjkParts(t.routeName),
                        sx - bW / 2 + (int) (h * 0.01) + w1, bY + bH * j + bH * 0.45, bH * 0.15, 0, 1);
            } else {
                JsFunctions.jsDrawStrDl(g, fb, f, t.routeName,
                        sx - bW / 2 + (int) (h * 0.01), bY + bH * j + bH * 0.45, bH * 0.2, 0, 1);
            }
        }
    }

    private int calcCircTextW(Graphics2D g, Font fb, Font f, ColorNameTuple t, int bH) {
        if (RouteNameUtil.isNumLine(t.routeName))
            return (int) (G2dTextHelper.getUnifiedStringWidth(g, fb,
                    String.valueOf(RouteNameUtil.getCJKLineName(TextUtil.getCjkParts(t.routeName))), bH * 0.25f)
                    + JsFunctions.jsGetDLStringWidth(g, f, f, "号线|" + TextUtil.getNonCjkParts(t.routeName), bH * 0.15));
        return JsFunctions.jsGetDLStringWidth(g, fb, f, t.routeName, bH * 0.2);
    }

    // ==================== 渐变绘制工具方法（从RisA移植） ====================

    /**
     * 横向渐变矩形
     */
    private static void drawGradientRectH(Graphics2D g, int x, int y, int width, int height, Color leftColor, Color rightColor) {
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(x, y + height / 2f, leftColor,
                x + width, y + height / 2f, rightColor, false);
        java.awt.Paint origPaint = g.getPaint();
        g.setPaint(gradient);
        g.fillRect(x, y, width, height);
        g.setPaint(origPaint);
    }

    /**
     * 横向渐变弧 — isLeft=true绘制左半弧(上→下), isLeft=false绘制右半弧(下→上)
     */
    private static void drawGradientArcH(Graphics2D g, double centerX, double centerY,
                                         double outerRadius, int width, Color startColor, Color endColor,
                                         boolean isLeft, int segments) {
        double innerRadius = outerRadius - width;
        double angleIncrement = 180.0 / segments;
        double startAngle = isLeft ? 90 : 270;
        double direction = -1; // 顺时针
        for (int i = 0; i < segments; i++) {
            double t = (double) i / segments;
            Color segColor = interpolateColor(startColor, endColor, isLeft ? t : 1 - t);
            g.setColor(segColor);
            double a1 = startAngle + direction * i * angleIncrement;
            double a2 = startAngle + direction * (i + 1) * angleIncrement;
            double[] in1 = getArcPoint(a1, innerRadius, centerX, centerY);
            double[] out1 = getArcPoint(a1, outerRadius, centerX, centerY);
            double[] out2 = getArcPoint(a2, outerRadius, centerX, centerY);
            double[] in2 = getArcPoint(a2, innerRadius, centerX, centerY);
            java.awt.geom.Path2D.Double path = new java.awt.geom.Path2D.Double();
            path.moveTo(in1[0], in1[1]);
            path.lineTo(out1[0], out1[1]);
            path.lineTo(out2[0], out2[1]);
            path.lineTo(in2[0], in2[1]);
            path.closePath();
            g.fill(path);
        }
    }

    private static double[] getArcPoint(double angleDeg, double radius, double centerX, double centerY) {
        double cos = Math.cos(angleDeg * Math.PI / 180.0);
        double sin = -Math.sin(angleDeg * Math.PI / 180.0);
        return new double[]{centerX + radius * cos, centerY + radius * sin};
    }

    private static Color interpolateColor(Color c1, Color c2, double t) {
        t = Math.max(0, Math.min(1, t));
        return new Color(
                (int) Math.round(c1.getRed() + t * (c2.getRed() - c1.getRed())),
                (int) Math.round(c1.getGreen() + t * (c2.getGreen() - c1.getGreen())),
                (int) Math.round(c1.getBlue() + t * (c2.getBlue() - c1.getBlue()))
        );
    }

    /**
     * 从线路颜色渐变到白色 — t=1全线路色, t=0最浅
     */
    private static Color gc(Color startColor, double t) {
        t = Math.max(0, Math.min(1, t));
        int r = startColor.getRed(), g = startColor.getGreen(), b = startColor.getBlue();
        double tr = (r + 2.0 * 255) / 3.0;
        double tg = (g + 2.0 * 255) / 3.0;
        double tb = (b + 2.0 * 255) / 3.0;
        return new Color(
                (int) Math.round(Math.max(0, Math.min(255, r + t * (tr - r)))),
                (int) Math.round(Math.max(0, Math.min(255, g + t * (tg - g)))),
                (int) Math.round(Math.max(0, Math.min(255, b + t * (tb - b))))
        );
    }
}
