package com.fangsu.beijing.lcds;

import com.fangsu.mtr.DrawableRoute;
import com.fangsu.scripting.*;
import com.fangsu.train.LcdBase;
import com.fangsu.train.LcdInfo;
import com.fangsu.train.TrainStatus;
import com.fangsu.utils.ColorUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static com.fangsu.scripting.G2dTextHelper.*;
import static com.fangsu.scripting.TextUtil.getCjkMatching;

public class BeijingLcd extends LcdBase {
    private static final int STATUS_NO_ROUTE = 0;
    private static final int STATUS_WAITING = 1;
    private static final int STATUS_LEAVING = 2;
    private static final int STATUS_ON_ROUTE = 3;
    private static final int STATUS_ARRIVED = 4;
    private static final int STATUS_CHANGING = 5;
    private static final int STATUS_RETURNING = 6;


    @Override
    public void draw(Graphics2D g, TrainStatus train, LcdInfo lcdInfo, Map<String, Object> state, String side,
                     int x, int y, int w, int h, Runnable completeCallback) {
        //配置
        boolean withCyberDoorlight = getOrDefault(lcdInfo.extra(), "withDoorLight", true, JsonElement::getAsBoolean);
        String title = getOrDefault(lcdInfo.extra(), "title", "方速轨交|FangSu Railway", JsonElement::getAsString);
        boolean ledStimulator = getOrDefault(lcdInfo.extra(), "ledStimulator", false, JsonElement::getAsBoolean);
        String logo = getOrDefault(lcdInfo.extra(), "logo", "fangsu:lcd_resources/logo.png", JsonElement::getAsString);

        //资源
        Image imgLogo;
        Image imgDoor;
        Font cjkFont;
        Font nonCjkFont;
        try {
            imgLogo = (BufferedImage) JsFunctions.loadResource("img", logo);
        } catch (Exception e) {
            imgLogo = null;
        }
        try {
            imgDoor = (BufferedImage) JsFunctions.loadResource("img", "fangsu:lcd_resources/door.png");
        } catch (Exception e) {
            imgDoor = null;
        }
        try {
            cjkFont = (Font) JsFunctions.loadResource("font", "fangsu:fonts/source-han-sans-bold.otf");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            nonCjkFont = (Font) JsFunctions.loadResource("font", "fangsu:fonts/source-han-sans-bold.otf");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Shape originalClip = g.getClip();
        DrawableRoute route = train.drawableRoute;
        boolean isRunning = train.trainStatus == STATUS_ON_ROUTE || train.trainStatus == STATUS_CHANGING || train.trainStatus == STATUS_LEAVING;

        if (route == null || train.trainStatus == STATUS_NO_ROUTE || train.trainStatus == STATUS_WAITING) {
            g.setColor(Color.black);
            g.fillRect(x, y, w, h);
            return;
        }

        int nextStationGlobalIndex = train.getThisRoutePlatformsNextIndexGlobal();
        List<DrawableRoute.DrawableRouteStation> stations = route.getStations(nextStationGlobalIndex);
        DrawableRoute.DrawableRouteStation nextStation = null;
        for (int i = 0; i < stations.size(); i++) {
            var station = stations.get(i);
            if (station.passingStatus == 2) {
                nextStation = station;
                break;
            }
        }
        if (nextStation == null) {
            // 没有"下一站"（例如已到达终点），使用最后一个在运行区间内的站
            for (int i = stations.size() - 1; i >= 0; i--) {
                var station = stations.get(i);
                if (station.passingStatus == 3 || station.passingStatus == 2 || station.passingStatus == 1) {
                    nextStation = station;
                    break;
                }
            }
        }
        if (nextStation == null && !stations.isEmpty()) {
            nextStation = stations.get(stations.size() - 1);
        }
        if (nextStation == null) {
            nextStation = new DrawableRoute.DrawableRouteStation("", List.of(), 0);
        }
        String routeName = route.routeName;
        Color routeColor = route.routeColor;

        Function<Double, Double> widthPercent = (p) -> x + w * p;
        Function<Double, Double> heightPercent = (p) -> y + h * p;

        //background
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        //顶部
        drawGradientLine(g, widthPercent.apply(0.1).floatValue(), heightPercent.apply(0.22).floatValue(), widthPercent.apply(0.5).floatValue(), heightPercent.apply(0.22).floatValue(), Color.GRAY, h / 100);
        drawGradientLine(g, widthPercent.apply(0.9).floatValue(), heightPercent.apply(0.22).floatValue(), widthPercent.apply(0.5).floatValue(), heightPercent.apply(0.22).floatValue(), Color.GRAY, h / 100);

        if (imgLogo != null)
            g.drawImage(imgLogo, (int) (x + h * 0.03), heightPercent.apply(0.03).intValue(), (int) (h * 0.14), (int) (h * 0.14), null);
        int currentX = x + h / 5;
        g.setColor(new Color(1, 39, 118));
        currentX += drawStrMultiLines(g, cjkFont, nonCjkFont, currentX, heightPercent.apply(0.17).intValue(), (int) (h * 0.14), 0, 1, title.split("\\|"));
        currentX += h * 0.05;
        currentX += drawLineBox(g, cjkFont, nonCjkFont, routeName, routeColor, currentX, heightPercent.apply(0.02).intValue(), (int) (h * 0.15), 0);
        currentX += h * 0.05;

        drawGradientLine(g, currentX, heightPercent.apply(0.01).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));
        drawGradientLine(g, currentX, heightPercent.apply(0.19).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));

        int leftWidth = currentX - x;
        currentX = (int) (x + w - h * 0.1);

        drawGradientLine(g, currentX, heightPercent.apply(0.01).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));
        drawGradientLine(g, currentX, heightPercent.apply(0.19).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));

        g.setColor(new Color(1, 39, 118));
        currentX -= h * 0.05;
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Sunday=1
////        currentX -= G2dTextHelper.drawStrUnified(g, nonCjkFont, .h + ":" + getWorldTime().m, currentX, heightPercent(0.16), h * 0.12, 2);
        currentX -= h * 0.025;
        String timeText1 = padZero(month) + "月" + padZero(day) + "日";
        String timeText2 = "星期" + new String[]{"日", "一", "二", "三", "四", "五", "六"}[dayOfWeek - 1];
        double timeTextWidth = Math.max(getUnifiedStringWidth(g, cjkFont, timeText1, h * 0.05f), getUnifiedStringWidth(g, cjkFont, timeText2, h * 0.05f));
        drawStrUnified(g, cjkFont, timeText1, currentX - timeTextWidth * 0.5, heightPercent.apply(0.08), h * 0.05, 1);
        drawStrUnified(g, cjkFont, timeText2, currentX - timeTextWidth * 0.5, heightPercent.apply(0.14), h * 0.05, 1);
        currentX -= timeTextWidth;
        currentX -= h * 0.025;
//
//
        drawGradientLine(g, currentX, heightPercent.apply(0.01).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));
        drawGradientLine(g, currentX, heightPercent.apply(0.19).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));

        g.setColor(new Color(1, 39, 118));

        currentX -= h * 0.1;
        currentX -= drawStrMultiLines(g, cjkFont, nonCjkFont, currentX, heightPercent.apply(0.17).intValue(), (int) (h * 0.14), 2, 1,
                TextUtil.addPrefix(stations.get(stations.size() - 1).stationName, "开往: ", "Destination: ", true).split("\\|"));
        currentX -= h * 0.1;

        drawGradientLine(g, currentX, heightPercent.apply(0.01).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));
        drawGradientLine(g, currentX, heightPercent.apply(0.19).floatValue(), currentX, heightPercent.apply(0.1).floatValue(), Color.GRAY, (int) (h * 0.01));

        int rightWdith = x + w - currentX;
        int centralWidth = w - leftWidth - rightWdith;

        int centralTextWidth1 = getMultiLinesWidth(
                g,
                cjkFont,
                nonCjkFont,
                (float) (h * 0.14),
                isRunning ? new String[]{"下一站", "Next Station"} : new String[]{"本站", "This Station"}
        );
        StringBuilder text2 = new StringBuilder();
        for (String s : nextStation.stationName.split("\\|")) {
            if (!text2.isEmpty()) text2.append(" · ");
            text2.append(s);
        }
        int centralTextWidth2 = getUnifiedStringWidth(g, cjkFont, text2.toString(), h * 0.08f);

        currentX = (int) (x + (w - leftWidth - rightWdith) * 0.5 + leftWidth - (centralTextWidth1 + centralTextWidth2 + h * 0.2) * 0.5);
        currentX += drawStrMultiLines(
                g,
                cjkFont,
                nonCjkFont,
                currentX,
                heightPercent.apply(0.17).intValue(),
                (int) (h * 0.14),
                0,
                1,
                isRunning ? new String[]{"下一站", "Next Station"} : new String[]{"本站", "This Station"}
        );
        currentX += h * 0.1;
        currentX += drawStrUnified(g, cjkFont, text2.toString(), currentX, heightPercent.apply(0.15), h * 0.08, 0);

        g.setStroke(new BasicStroke(h * 0.001f));

        //cyber doorlight
        if (withCyberDoorlight) {
            drawGradientLine(g, x + h * 0.3f, heightPercent.apply(0.3).floatValue(), x + h * 0.3f, heightPercent.apply(0.6).floatValue(), Color.GRAY, h / 100);
            drawGradientLine(g, x + h * 0.3f, heightPercent.apply(0.9).floatValue(), x + h * 0.3f, heightPercent.apply(0.6).floatValue(), Color.GRAY, h / 100);

            boolean isOpen = false;
            boolean isLeft = side.contains("left");
            if (isLeft) isOpen = train.doorLeftOpen[0];
            else isOpen = train.doorRightOpen[0];
            g.setColor(Color.GRAY);
            g.fillOval((int) (x + h * 0.05), heightPercent.apply(0.3).intValue(), h / 5, h / 5);
            g.fillOval((int) (x + h * 0.05), heightPercent.apply(0.6).intValue(), h / 5, h / 5);
            g.setColor(
                    isOpen && ((train.doorValue() > 0 && train.doorValue() < 0.2) || (train.doorValue() > 0.4 && train.doorValue() < 0.6) || train.doorValue() >= 0.8) ?
                            Color.YELLOW : new Color(154, 169, 173)
            );
            g.fillOval((int) (x + h * 0.065), heightPercent.apply(0.315).intValue(), (int) (h * 0.17), (int) (h * 0.17));
            g.setColor(new Color(156, 162, 191));
            g.fillOval((int) (x + h * 0.065), heightPercent.apply(0.615).intValue(), (int) (h * 0.17), (int) (h * 0.17));
            g.setColor(Color.BLACK);
            drawStrUnified(g, cjkFont, "开关门", x + h * 0.15, heightPercent.apply(0.575), h * 0.06, 1);
            drawStrUnified(g, cjkFont, "门隔离", x + h * 0.15, heightPercent.apply(0.875), h * 0.06, 1);
        }

        //route
        if (isRunning || ledStimulator) {
            boolean isLeft = side.contains("left");
            boolean isReverse = isLeft == train.isReverse;
            drawRoute(g, cjkFont, nonCjkFont, train, route,
                    withCyberDoorlight ? (int) (x + h * 0.4) : x, y,
                    withCyberDoorlight ? (int) (w - h * 0.4) : w, h, isReverse
            );
        } else {
            // 到站屏
            boolean isLeftSide = side.contains("left");
            boolean doorOpenOnThisSide = isLeftSide ? train.doorLeftOpen[0] : train.doorRightOpen[0];
            boolean doorOpenOnOtherSide = isLeftSide ? train.doorRightOpen[0] : train.doorLeftOpen[0];

            int arrivedX = withCyberDoorlight ? (int) (x + h * 0.4) : x;
            int arrivedW = withCyberDoorlight ? (int) (w - h * 0.4) : w;
            int arrivedContentY = (int) (y + h * 0.28);
            int arrivedContentH = (int) (h * 0.65);


            // --- 中间：站名（多行显示模式，居中）---
            String stationCjkName = TextUtil.getCjkMatching(nextStation.stationName, true);
            String stationNonCjkName = TextUtil.getCjkMatching(nextStation.stationName, false);
            int stationNameSize = (int) (arrivedContentH * 0.55);
            int stationNameY = arrivedContentY + stationNameSize;
            g.setColor(Color.BLACK);
            drawStrMultiLines(g, cjkFont, nonCjkFont,
                    x + w / 2, stationNameY, stationNameSize, 1, 1,
                    stationCjkName.isEmpty() ? " " : stationCjkName,
                    stationNonCjkName.isEmpty() ? " " : stationNonCjkName);


        }
    }

    private static void drawRoute(Graphics2D g, Font cjkFont, Font nonCjkFont, TrainStatus train, DrawableRoute route, int x, int y, int w, int h, boolean reverseDrawing) {
        Function<Double, Double> widthPercent = (p) -> x + w * p;
        Function<Double, Double> heightPercent = (p) -> y + h * p;
        final Shape originalClip = g.getClip();

        Color passedColor = Color.GRAY;
        Color routeColor = route.routeColor;
        String routeName = route.routeName;

        List<DrawableRoute.DrawableRouteStation> stations = route.getStations(train.getThisRoutePlatformsNextIndexGlobal());

        int lineSize = (int) (h * 0.025);
        int circleSize = (int) (h * 0.065);
        int stationCount = Math.max(stations.size(), 2);
        int distances = (int) ((w * 0.7) / (stationCount - 1));
        int basicY = heightPercent.apply(0.6).intValue();
        int basicX = widthPercent.apply(reverseDrawing ? 0.8 : 0.2).intValue();
        double arrowPos = ((System.currentTimeMillis() % 1000) / 1000.0);

        //lineBox
        for (int i = 1; i < stations.size(); i++) {
            var station = stations.get(i);
            int passingStatus = station.passingStatus;
            int currentX = basicX + (reverseDrawing ? -1 : 1) * distances * i;

            if (passingStatus == 2 || passingStatus == 3) {
                g.setColor(routeColor);
            } else g.setColor(passedColor);

            // 计算线段矩形（保证覆盖两个站点之间的区域）
            int prevX = basicX + (reverseDrawing ? -1 : 1) * distances * (i - 1);
            int lineX = Math.min(prevX, currentX) - 1;
            int lineW = Math.abs(currentX - prevX) + 2;
            Rectangle lineBox = new Rectangle(lineX, basicY - lineSize / 2, lineW, lineSize);
            g.setClip(lineBox);
            g.fill(lineBox);

            g.setColor(Color.WHITE);
            double offset = (passingStatus == 2) ? 1 - arrowPos : 0.5;
            // 箭头指向列车前进方向：正向指向右，逆向指向左
            int arrowPosX = (int) (currentX - (reverseDrawing ? -1 : 1) * distances * offset);
            g.fillPolygon(buildArrowPolygon(arrowPosX - lineSize, basicY - lineSize / 2, lineSize / 3 * 2, lineSize, reverseDrawing));
            g.fillPolygon(buildArrowPolygon(arrowPosX + lineSize, basicY - lineSize / 2, lineSize / 3 * 2, lineSize, reverseDrawing));

            g.setClip(originalClip);
        }

        //station
        for (int i = 0; i < stations.size(); i++) {
            var station = stations.get(i);
            int passingStatus = station.passingStatus;
            int currentX = basicX + (reverseDrawing ? -1 : 1) * distances * i;
            boolean nameOnTop = i % 2 == 0;
            boolean isStnAvailable = passingStatus == 2 || passingStatus == 3;

            if (!station.transInfo.isEmpty()) {
                int transBarTop = basicY + (nameOnTop ? (int) (h * 0.1) : (int) (h * -0.2));
                int transBarBottom = basicY + (nameOnTop ? (int) (h * 0.18) : (int) (h * -0.12));
                g.setClip(
                        new Polygon(
                                new int[]{(int) (currentX - circleSize * 0.35), (int) (currentX - circleSize * 0.35), currentX, (int) (currentX + circleSize * 0.35), (int) (currentX + circleSize * 0.35)},
                                new int[]{
                                        basicY, transBarTop, (int) (basicY + h * 0.09 * (nameOnTop ? 1 : -1)), transBarTop, basicY
                                },
                                5
                        )
                );
                int currentY = transBarTop;
                for (int j = 0; j < station.transInfo.size(); j++) {
                    var thisTrans = station.transInfo.get(j);
                    g.setColor(isStnAvailable ? thisTrans.routeColor : passedColor);
                    int transBarWidth = Math.max(1, (int) ((circleSize * 0.7) / station.transInfo.size()));
                    int transBarX = (int) (currentX - circleSize * 0.35 + (j * circleSize * 0.7) / station.transInfo.size());
                    g.fillRect(transBarX, transBarTop, transBarWidth, transBarBottom - transBarTop);
                }
                g.setClip(originalClip);
                currentY = transBarTop;
                for (int j = 0; j < station.transInfo.size(); j++) {
                    var thisTrans = station.transInfo.get(j);
                    drawLineBox(g, cjkFont, nonCjkFont, thisTrans.routeName, thisTrans.routeColor, currentX, currentY, (int) (h * 0.08), 1);
                    currentY += (int) (h * 0.09 * (nameOnTop ? 1 : -1));
                }
            }

            g.setColor(isStnAvailable ? routeColor : passedColor);
            g.fillOval((int) (currentX - circleSize * 0.5), (int) (basicY - circleSize * 0.5), circleSize, circleSize);
            g.setColor((isStnAvailable ? routeColor : passedColor).brighter());
            g.fillOval((int) (currentX - circleSize * 0.4), (int) (basicY - circleSize * 0.4), (int) (circleSize * 0.8), (int) (circleSize * 0.8));
            if (passingStatus < 2) {
                g.setColor(new Color(167, 168, 168));
                // 已通过站点画"×"标记，"×"方向与列车前进方向一致
                if (reverseDrawing)
                    g.fillPolygon(
                            new int[]{(int) (currentX - circleSize * 0.2), currentX, (int) (currentX + circleSize * 0.2), currentX, (int) (currentX - circleSize * 0.2), currentX},
                            new int[]{(int) (basicY - circleSize * 0.3), (int) (basicY - circleSize * 0.3), basicY, (int) (basicY + circleSize * 0.3), (int) (basicY + circleSize * 0.3), basicY},
                            6
                    );
                else
                    g.fillPolygon(
                            new int[]{(int) (currentX + circleSize * 0.2), currentX, (int) (currentX - circleSize * 0.2), currentX, (int) (currentX + circleSize * 0.2), currentX},
                            new int[]{(int) (basicY - circleSize * 0.3), (int) (basicY - circleSize * 0.3), basicY, (int) (basicY + circleSize * 0.3), (int) (basicY + circleSize * 0.3), basicY},
                            6
                    );
            }
            if (passingStatus == 2) {
                g.setColor(routeColor);
                g.fillOval(
                        (int) (currentX - circleSize * 0.4 * (arrowPos)),
                        (int) (basicY - circleSize * 0.4 * (arrowPos)),
                        (int) (circleSize * 0.8 * (arrowPos)),
                        (int) (circleSize * 0.8 * (arrowPos))
                );
                g.setColor(routeColor.brighter());
                g.fillOval(
                        (int) (currentX - circleSize * 0.3 * (arrowPos)),
                        (int) (basicY - circleSize * 0.3 * (arrowPos)),
                        (int) (circleSize * 0.6 * (arrowPos)),
                        (int) (circleSize * 0.6 * (arrowPos))
                );
            }

            g.setColor(passingStatus == 2 ? new Color(1, 39, 118) : isStnAvailable ? Color.BLACK : passedColor);
            drawStrMultiLines(
                    g,
                    cjkFont,
                    nonCjkFont,
                    currentX,
                    (int) (basicY + (nameOnTop ? h * -0.1 : h * 0.225)),
                    (int) (h * 0.125 + (passingStatus == 2 ? h * -0.025 * Math.abs((Math.sin(System.currentTimeMillis() % 1000 / 1000.0) + 1) / 2) : 0)),
                    1,
                    1,
                    station.stationName.split("\\|")
            );
        }
    }

    private static Polygon buildArrowPolygon(int x, int y, int w, int h, boolean isLeft) {
        if (isLeft) {
            // 指向左的箭头 <|
            return new Polygon(
                    new int[]{x + w, x + w / 2, x, x + w / 2, x + w, x + w / 2},
                    new int[]{y, y, y + h / 2, y + h, y + h, y + h / 2},
                    6
            );
        } else
            // 指向右的箭头 |>
            return new Polygon(
                    new int[]{x, x + w / 2, x + w, x + w / 2, x, x + w / 2},
                    new int[]{y, y, y + h / 2, y + h, y + h, y + h / 2},
                    6
            );
    }

    private static int drawLineBox(Graphics2D g, Font cjkFont, Font nonCjkFont, String routeName, Color routeColor, int x, int y, int h, int d) {
        int textWidth = 0;
        if (RouteNameUtil.isNumLine(routeName)) {
            textWidth += getUnifiedStringWidth(g, nonCjkFont, RouteNameUtil.getCJKLineName(getCjkMatching(routeName, true)), h * 0.9f);
            textWidth += G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, h * 0.85f, "号线", getCjkMatching(routeName, false));
        } else {
            textWidth = G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, h * 0.85f, routeName.split("\\|"));
        }
        float finalWidth = textWidth + h * 0.5f;
        float currentX = (float) (x + (d == 2 ? -finalWidth : d == 1 ? finalWidth * -0.5 : 0));
        g.setColor(routeColor);
        g.fillRoundRect((int) currentX, y, (int) finalWidth, h, (int) (h * 0.25), (int) (h * 0.25));
        currentX += h * 0.25f;
        g.setColor(ColorUtil.isLightColor(routeColor) ? Color.BLACK : Color.WHITE);
        if (RouteNameUtil.isNumLine(routeName)) {
            currentX += drawStrUnified(g, nonCjkFont, RouteNameUtil.getCJKLineName(getCjkMatching(routeName, true)), currentX, y + h * 0.9, h * 0.8, 0);
            currentX += drawStrMultiLines(g, cjkFont, nonCjkFont, (int) currentX, (int) (y + h * 0.9), (int) (h * 0.75), 0, 0, "号线", getCjkMatching(routeName, false));
        } else {
            currentX = drawStrMultiLines(g, cjkFont, nonCjkFont, (int) currentX, (int) (y + h * 0.9), (int) (h * 0.75), 0, 1, routeName.split("\\|"));
        }
        return (int) finalWidth;
    }

    /**
     * 仅计算线路框的宽度，不实际绘制
     */
    private static int getLineBoxWidth(Graphics2D g, Font cjkFont, Font nonCjkFont, String routeName, int h) {
        int textWidth;
        if (RouteNameUtil.isNumLine(routeName)) {
            textWidth = getUnifiedStringWidth(g, nonCjkFont, RouteNameUtil.getCJKLineName(getCjkMatching(routeName, true)), h * 0.9f);
            textWidth += G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, h * 0.85f, "号线", getCjkMatching(routeName, false));
        } else {
            textWidth = G2dTextHelper.getMultiLinesWidth(g, cjkFont, nonCjkFont, h * 0.85f, routeName.split("\\|"));
        }
        return textWidth + (int) (h * 0.5f);
    }

    private static void drawGradientLine(Graphics2D g2, float x1, float y1, float x2, float y2, Color color, int lineWidth) {
        var transparentColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);

        // 创建线性渐变
        var gradient = new LinearGradientPaint(
                x1,
                y1, // 渐变起点
                x2,
                y2, // 渐变终点
                new float[]{0.0f, 1.0f}, // 渐变位置
                new Color[]{
                        transparentColor, color
                } // 颜色数组
        );

        // 保存原始画笔状态
        var originalPaint = g2.getPaint();
        var originalStroke = g2.getStroke();

        // 设置渐变画笔并绘制
        g2.setPaint(gradient);
        g2.setStroke(new BasicStroke(lineWidth));
        g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

        // 恢复原始画笔状态（可选）
        g2.setPaint(originalPaint);
        g2.setStroke(originalStroke);
    }

    private static String padZero(int n) {
        return String.format("%02d", n);
    }

    private static <T> T getOrDefault(JsonObject json, String key, T defaultValue, Function<JsonElement, T> function) {
        if (json.has(key)) {
            return function.apply(json.get(key));
        }
        return defaultValue;
    }


}