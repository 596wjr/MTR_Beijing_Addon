package com.fangsu.beijing.pids;

import com.fangsu.beijing.util.DrawUtil;
import com.fangsu.beijing.util.TextWrapper;
import com.fangsu.blockEntities.BlockEntityPids;
import com.fangsu.drawing.pids.BasePidsDrawing;
import com.fangsu.mappings.ResourceLocation;
import com.fangsu.scripting.G2dTextHelper;
import com.fangsu.scripting.GraphicsTexture;
import com.fangsu.scripting.JsFunctions;
import com.fangsu.scripting.TextUtil;
import com.fangsu.utils.LocalResourceUtil;
import com.fangsu.utils.MtrUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class BeijingPidsB extends BasePidsDrawing {
    private static final ResourceLocation FONT_PATH = new ResourceLocation("mtrsteamloco", "fonts/source-han-sans.otf");
    private static final ResourceLocation FONT_BOLD_PATH = new ResourceLocation("mtrsteamloco", "fonts/source-han-sans-bold.otf");

    private static final Color MAIN_COLOR = new Color(143, 236, 255);
    private static final Color DETAIL_COLOR = new Color(87, 192, 255);

    private static final float LEFT_PERCENT = 0.4f;
    private static final float LEFT_TOP_PERCENT = 0.2f;
    private static final float LEFT_MIDDLE_PERCENT = (1f - LEFT_TOP_PERCENT) / 2f;
    private static final float RIGHT_TOP_PERCENT = 0.65f;

    private static final float STROKE_SIZE = 0.005f;
    private static final float STROKE_SIZE_BOLD = 0.01f;
    private static final float CORNER_RADIUS = 0.0125f;

    private static final float COMMON_FONT_SIZE = 0.075f;
    private static final float SMALL_FONT_SIZE = 0.06f;
    private static final float LARGE_FONT_SIZE = 0.15f;
    private static final float MSG_FONT_SIZE = 0.1f;

    private static final float SCROLL_SPEED = 0.25f;

    @Override
    public void draw(GraphicsTexture gt, List<MtrUtil.PidsArrivalInfo> arrivalInfoList,
                     Map<String, Object> drawState, int w, int h,
                     BlockEntityPids.DrawInfoPids drawInfo) {
        Graphics2D g = gt.graphics;
        int x = 0;
        int y = 0;

        final float strokeSize = h * STROKE_SIZE;
        final float strokeSizeBold = h * STROKE_SIZE_BOLD;
        final BasicStroke stroke = new BasicStroke(strokeSize);
        final BasicStroke strokeBold = new BasicStroke(strokeSizeBold);
        final int corner = Math.round(w * CORNER_RADIUS);

        final int leftX = widthPercent(x, w, LEFT_PERCENT);

        final int commonFontSize = Math.round(h * COMMON_FONT_SIZE);
        final int smallFontSize = Math.round(h * SMALL_FONT_SIZE);
        final int largeFontSize = Math.round(h * LARGE_FONT_SIZE);
        final int msgFontSize = Math.round(h * MSG_FONT_SIZE);

        final boolean isCjk = System.currentTimeMillis() % 6000 < 3000;
        final Font font = LocalResourceUtil.loadFont(FONT_PATH);
        final Font fontBold = LocalResourceUtil.loadFont(FONT_BOLD_PATH);

        final var info1 = arrivalInfoList.size() >= 1 ? arrivalInfoList.get(0) : null;
        final var route1 = info1 != null ? MtrUtil.getRouteById(info1.routeId) : null;
        final var info2 = arrivalInfoList.size() >= 2 ? arrivalInfoList.get(1) : null;
        final var route2 = info2 != null ? MtrUtil.getRouteById(info2.routeId) : null;

        // background

        g.setColor(new Color(0, 20, 0x40));
        g.fillRect(x, y, w, h);

        g.setColor(new Color(255, 191, 0));
        g.setStroke(strokeBold);
        g.drawLine(x, heightPercent(y, h, LEFT_TOP_PERCENT), leftX, heightPercent(y, h, LEFT_TOP_PERCENT));
        g.drawLine(x, Math.round(y + h - strokeSizeBold), leftX, Math.round(y + h - strokeSizeBold));
        g.drawLine(leftX, heightPercent(y, h, RIGHT_TOP_PERCENT), x + w, heightPercent(y, h, RIGHT_TOP_PERCENT));
        g.drawLine(leftX, y, leftX, y + h);
        g.setStroke(stroke);
        g.drawLine(x + corner, heightPercent(y, h, LEFT_TOP_PERCENT + LEFT_MIDDLE_PERCENT), leftX - corner, heightPercent(y, h, LEFT_TOP_PERCENT + LEFT_MIDDLE_PERCENT));

        //img
        {
            final String imgPath = (String) drawInfo.extraConfig.get("imgPath");
            BufferedImage img;
            try {
                if (imgPath.endsWith("gif")) img = (BufferedImage) JsFunctions.loadResource("gif", imgPath);
                else img = (BufferedImage) JsFunctions.loadResource("img", imgPath);
            } catch (Exception e) {
                img = null;
            }
            if (img != null) {
                g.drawImage(img, Math.round(leftX + strokeSizeBold / 2), y,
                        Math.round(w * (1 - LEFT_PERCENT) - strokeSizeBold / 2), Math.round(h * RIGHT_TOP_PERCENT), null);
            }
        }

        //top
        {
            g.setColor(Color.WHITE);
            int leftWidth = Math.round(w * LEFT_PERCENT - corner * 4);
            leftWidth -= G2dTextHelper.drawStrMultiLines(g, fontBold, fontBold, x + corner * 2, y + corner + msgFontSize, msgFontSize, 0, 0, "方速轨交", "FangSu Railway");
            if (route1 != null)
                DrawUtil.drawLineNameBoxWithStretch(g, fontBold, font, font, route1.name, new Color(route1.color),
                        leftX - corner * 2, y + corner, leftWidth - corner, msgFontSize, 2, false, false
                );
            ZonedDateTime now = ZonedDateTime.now();
            DateTimeFormatter formatter;
            if (isCjk) {
                formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
            } else {
                formatter = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.US);
            }
            String date = now.format(formatter);
            String time = now.format(DateTimeFormatter.ofPattern("HH:mm"));

            g.setColor(MAIN_COLOR);
            G2dTextHelper.drawStrUnified(g, font, date, x + corner * 2, heightPercent(y, h, LEFT_TOP_PERCENT) - corner, smallFontSize, 0);
            G2dTextHelper.drawStrUnified(g, font, time, leftX - corner * 2, heightPercent(y, h, LEFT_TOP_PERCENT) - corner, smallFontSize, 2);
        }

        //trains
        final String terminusStr = isCjk ? "终点站" : "Terminus";
        final String thisTrainStr = isCjk ? "本次列车" : "This Train";
        final String nextTrainStr = isCjk ? "下次列车" : "Next Train";
        final String minStr = isCjk ? "分钟" : "min";
        final String secStr = isCjk ? "秒" : "sec";
        final String carStr = isCjk ? "编组" : " Cars";
        final int terminusWidth = G2dTextHelper.getUnifiedStringWidth(g, font, terminusStr, commonFontSize);
        final int thisTrainWidth = G2dTextHelper.getUnifiedStringWidth(g, font, thisTrainStr, commonFontSize);
        final int nextTrainWidth = G2dTextHelper.getUnifiedStringWidth(g, font, nextTrainStr, commonFontSize);
        final int minWidth = G2dTextHelper.getUnifiedStringWidth(g, font, minStr, commonFontSize);
        final int secWidth = G2dTextHelper.getUnifiedStringWidth(g, font, secStr, commonFontSize);
        final int carWidth = G2dTextHelper.getUnifiedStringWidth(g, font, carStr, commonFontSize);

        final int terminusLeftWidth = Math.round(w * LEFT_PERCENT) - corner * 2 - terminusWidth;

        //train1
        {
            final int beginY = heightPercent(y, h, LEFT_TOP_PERCENT);
            final String terminus = route1 != null ? MtrUtil.getDestinationByRoute(route1) : "";
            final long timeMillis = info1 != null ? info1.arrivalMillis - System.currentTimeMillis() : Long.MIN_VALUE;
            final int timeSec = Math.round(timeMillis / 1000f);
            final int timeMin = (int) (timeSec / 60f);
            final String timeStr = timeSec < 30 ? "" : timeSec < 60 ? secStr : minStr;
            final String countStr = timeMillis == Long.MIN_VALUE ? "" :
                    timeSec < 1 ? (isCjk ? "列车到站" : "Arrived") :
                            timeSec < 30 ? (isCjk ? "即将到站" : "Arriving") :
                                    (timeSec < 60 ? timeSec : timeMin) + "";

            final int timeWidth = G2dTextHelper.getUnifiedStringWidth(g, font, timeStr, commonFontSize);
            final int timeLeftWidth = Math.round(w * LEFT_PERCENT) - corner * 4 - thisTrainWidth - timeWidth;

            g.setColor(MAIN_COLOR);
            G2dTextHelper.drawStrUnified(g, font, terminusStr, x + corner, beginY + corner + commonFontSize, commonFontSize, 0);
            g.setColor(DETAIL_COLOR);
            G2dTextHelper.drawStrUnifiedWithStretch(g, font, TextUtil.getCjkMatching(terminus, isCjk), leftX - corner,
                    beginY + corner + commonFontSize, commonFontSize, terminusLeftWidth, 2);

            g.setColor(MAIN_COLOR);
            G2dTextHelper.drawStrUnified(g, font, thisTrainStr, x + corner, beginY + corner + commonFontSize + (largeFontSize / 2) + commonFontSize, commonFontSize, 0);
            G2dTextHelper.drawStrUnified(g, font, timeStr, leftX - corner, beginY + corner + commonFontSize + (largeFontSize / 2) + commonFontSize, commonFontSize, 2);
            g.setColor(DETAIL_COLOR);
            G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, countStr, ((x + corner + thisTrainWidth) + (leftX - corner - timeWidth)) / 2,
                    beginY + corner + commonFontSize + largeFontSize, largeFontSize, timeLeftWidth, 1);

            g.setColor(MAIN_COLOR);
            G2dTextHelper.drawStrUnified(g, font, carStr, leftX - corner, beginY + Math.round(h * LEFT_MIDDLE_PERCENT) - corner, commonFontSize, 2);
            g.setColor(DETAIL_COLOR);
            if (info1 != null)
                G2dTextHelper.drawStrUnified(g, font, info1.trainCars + "", leftX - corner - carWidth, beginY + Math.round(h * LEFT_MIDDLE_PERCENT) - corner, msgFontSize, 2);
        }

        //train2
        {
            final int beginY = heightPercent(y, h, LEFT_TOP_PERCENT + LEFT_MIDDLE_PERCENT);
            final String terminus = route2 != null ? MtrUtil.getDestinationByRoute(route2) : "";
            final long timeMillis = info2 != null ? info2.arrivalMillis - System.currentTimeMillis() : Long.MIN_VALUE;
            final int timeSec = Math.round(timeMillis / 1000f);
            final int timeMin = (int) (timeSec / 60f);
            final String timeStr = timeSec < 30 ? "" : timeSec < 60 ? secStr : minStr;
            final String countStr = timeMillis == Long.MIN_VALUE ? "" :
                    timeSec < 1 ? (isCjk ? "列车到站" : "Arrived") :
                            timeSec < 30 ? (isCjk ? "即将到站" : "Arriving") :
                                    (timeSec < 60 ? timeSec : timeMin) + "";

            final int timeWidth = G2dTextHelper.getUnifiedStringWidth(g, font, timeStr, commonFontSize);
            final int timeLeftWidth = Math.round(w * LEFT_PERCENT) - corner * 4 - nextTrainWidth - timeWidth;

            g.setColor(MAIN_COLOR);
            G2dTextHelper.drawStrUnified(g, font, terminusStr, x + corner, beginY + corner + commonFontSize, commonFontSize, 0);
            g.setColor(DETAIL_COLOR);
            G2dTextHelper.drawStrUnifiedWithStretch(g, font, TextUtil.getCjkMatching(terminus, isCjk), leftX - corner,
                    beginY + corner + commonFontSize, commonFontSize, terminusLeftWidth, 2);

            g.setColor(MAIN_COLOR);
            G2dTextHelper.drawStrUnified(g, font, nextTrainStr, x + corner, beginY + corner + commonFontSize + (largeFontSize / 2) + commonFontSize, commonFontSize, 0);
            G2dTextHelper.drawStrUnified(g, font, timeStr, leftX - corner, beginY + corner + commonFontSize + (largeFontSize / 2) + commonFontSize, commonFontSize, 2);
            g.setColor(DETAIL_COLOR);
            G2dTextHelper.drawStrUnifiedWithStretch(g, fontBold, countStr, ((x + corner + nextTrainWidth) + (leftX - corner - timeWidth)) / 2,
                    beginY + corner + commonFontSize + largeFontSize, largeFontSize, timeLeftWidth, 1);

            g.setColor(MAIN_COLOR);
            G2dTextHelper.drawStrUnified(g, font, carStr, leftX - corner, beginY + Math.round(h * LEFT_MIDDLE_PERCENT) - corner, commonFontSize, 2);
            g.setColor(DETAIL_COLOR);
            if (info2 != null)
                G2dTextHelper.drawStrUnified(g, font, info2.trainCars + "", leftX - corner - carWidth, beginY + Math.round(h * LEFT_MIDDLE_PERCENT) - corner, msgFontSize, 2);
        }

        //scrolling
        {
            final int drawHeight = Math.round(h * (1 - RIGHT_TOP_PERCENT));
            String line = (String) drawInfo.extraConfig.get("scrollText");
            List<String> lines;
            g.setFont(font.deriveFont(Font.PLAIN, msgFontSize));
            //cache
            if (line.equals(drawState.get("scrollText"))) {
                try {
                    lines = (List<String>) drawState.get("wrappedLines");
                } catch (Exception ignored) {
                    lines = List.of();
                }
            } else {
                lines = TextWrapper.wrapTextPreserveNewlines(g, line, font, Math.round(w * (1 - LEFT_PERCENT)) - corner * 2, msgFontSize);
                drawState.put("scrollText", line);
                drawState.put("wrappedLines", lines);
            }
            final Shape oriClip = g.getClip();

            // 计算顶部边界
            int top = heightPercent(y, h, RIGHT_TOP_PERCENT);
            int speed = Math.round(h * SCROLL_SPEED);
            int lineHeight = Math.round(1.1f * msgFontSize);
            int totalHeight = lineHeight * lines.size();
            int totalTime = Math.round((float) totalHeight / speed);
            g.setClip(new Rectangle(leftX, top, Math.round(w * (1 - LEFT_PERCENT)), drawHeight));

            // 滚动
            float elapsed = (System.currentTimeMillis() / 1000f) % totalTime;
            int offset = (int) ((elapsed * speed));
            int yPos = top + lineHeight;  // 向上移动
            for (String l : lines) {
                g.drawString(l, leftX + corner, yPos);
                yPos += lineHeight;
            }

            g.setClip(oriClip);
        }
    }


    private static int heightPercent(int y, int h, double p) {
        return (int) Math.round(y + h * p);
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) Math.round(x + w * p);
    }
}
