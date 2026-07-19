package com.fangsu.beijing.pids;

import com.fangsu.blockEntities.BlockEntityPids;
import com.fangsu.drawing.pids.BasePidsDrawing;
import com.fangsu.scripting.GraphicsTexture;
import com.fangsu.scripting.JsFunctions;
import com.fangsu.scripting.MinecraftClientUtil;
import com.fangsu.scripting.TextUtil;
import com.fangsu.utils.MtrUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import static com.fangsu.scripting.G2dTextHelper.drawStrUnified;

public class BeijingPidsA extends BasePidsDrawing {

    private BufferedImage cachedBackground;
    private Font cachedBaseFont;

    @Override
    public void draw(GraphicsTexture gt, List<MtrUtil.PidsArrivalInfo> arrivalInfoList,
                     Map<String, Object> drawState, int w, int h,
                     BlockEntityPids.DrawInfoPids drawInfo) {
        int x = 0;
        int y = 0;
        Graphics2D g = gt.graphics;

        // === 加载资源 ===
        BufferedImage bgImage = loadBackground();
        Font baseFont = loadBaseFont();

        // === 绘制背景 ===
        if (bgImage != null) {
            g.drawImage(bgImage, x, y, w, h, null);
        }

        // === 额外图片叠加（支持 png/gif，通过方速 loadResource 统一通路） ===
        Map<String, Object> extraConfig = drawInfo.extraConfig;
        String extraImage = extraConfig != null ? (String) extraConfig.get("imgPath") : null;
        String extraImageType = extraConfig != null ? (String) extraConfig.get("imgType") : null;
        if (extraImageType == null) extraImageType = "local";
        boolean shouldRenderExtraImage = extraImage != null && !extraImage.isEmpty();

        if (shouldRenderExtraImage) {
            BufferedImage extraBufferedImage = null;
            try {
                // "gif" 和 "png"/"img" 都通过 JsFunctions.loadResource 加载，
                // loadResource("gif", ...) 内部通过 GifHelper 返回当前帧 BufferedImage
                if ("local".equals(extraImageType)) {
                    if (extraImage.toLowerCase().endsWith(".gif"))
                        extraBufferedImage = (BufferedImage) JsFunctions.loadResource("gif", extraImage);
                    else
                        extraBufferedImage = (BufferedImage) JsFunctions.loadResource("img", extraImage);
                }
            } catch (Exception e) {
//                JsFunctions.setWarnInfo("Failed to load extra image " + extraImage);
                shouldRenderExtraImage = false;
            }
            if (extraBufferedImage != null) {
                g.drawImage(extraBufferedImage,
                        (int) (x + w * 0.40625), (int) (y + h * 0.09375),
                        (int) (w * 0.59375), (int) (h * 0.72), null);
            }
        }

        // === 初始化绘制状态 ===
        long currentTime = System.currentTimeMillis();
        if (!drawState.containsKey("drawBeginTime") || !drawState.containsKey("drawFlag")) {
            drawState.put("drawBeginTime", currentTime);
            drawState.put("drawFlag", true);
        }
        if (!drawState.containsKey("scollADrawBeginTime") || !drawState.containsKey("scollADrawFlag")) {
            drawState.put("scollADrawBeginTime", currentTime);
            drawState.put("scollADrawFlag", true);
        }
        if (!drawState.containsKey("scollBDrawBeginTime") || !drawState.containsKey("scollBDrawFlag")) {
            drawState.put("scollBDrawBeginTime", currentTime);
            drawState.put("scollBDrawFlag", true);
        }

        long drawBeginTime = (long) drawState.get("drawBeginTime");
        boolean drawFlag = (boolean) drawState.get("drawFlag");
        long scollADrawBeginTime = (long) drawState.get("scollADrawBeginTime");
        boolean scollADrawFlag = (boolean) drawState.get("scollADrawFlag");
        long scollBDrawBeginTime = (long) drawState.get("scollBDrawBeginTime");
        boolean scollBDrawFlag = (boolean) drawState.get("scollBDrawFlag");

        // === 读取可配置的滚动文本（来自 extraConfig，带默认值） ===
        // 用户文本按 | 分割，每段对应一种语言；滚动时根据 flag 取模切换
        String scrollTextA = extraConfig != null ? (String) extraConfig.get("scrollTextA") : null;
        if (scrollTextA == null || scrollTextA.isEmpty()) {
            scrollTextA = "欢迎使用北京地铁扩展!|Welcome to Beijing MTR Addon!";
        }
        String[] scrollTextAParts = scrollTextA.split("\\|", -1);
        String currentScrollTextA = scrollTextAParts.length > 0
                ? scrollTextAParts[scollADrawFlag ? 0 : Math.min(1, scrollTextAParts.length - 1)]
                : "";

        String scrollTextB = extraConfig != null ? (String) extraConfig.get("scrollTextB") : null;
        if (scrollTextB == null || scrollTextB.isEmpty()) {
            scrollTextB = "欢迎乘坐方速轨道交通!|Welcome to FangSu Railway!";
        }
        String[] scrollTextBParts = scrollTextB.split("\\|", -1);
        String currentScrollTextB = scrollTextBParts.length > 0
                ? scrollTextBParts[scollBDrawFlag ? 0 : Math.min(1, scrollTextBParts.length - 1)]
                : "";

        // === 绘制时间 ===
        g.setColor(rgbToColor(254, 243, 100));
        WorldTime worldTime = getWorldTime();
        String timeStr = worldTime.h + ":" + worldTime.m;
        drawStrUnified(g, baseFont, timeStr, w * 0.1875, h * 0.2, (float) (h * 0.075), 1);

        // === 绘制日期和星期 ===
        String dateStr = formatDate(drawFlag);
        String weekdayStr = formatWeekday(drawFlag);
        drawStrUnified(g, baseFont, dateStr, w * 0.3, h * 0.07, (float) (h * 0.075), 1);
        drawStrUnified(g, baseFont, weekdayStr, w * 0.7, h * 0.07, (float) (h * 0.075), 1);

        double drawTotalTime = 5;

        // === 静态标签 ===
        drawStrUnified(g, baseFont, drawFlag ? "列车开往" : "Train to", w * 0.01, h * 0.33, (float) (h * 0.04), 0);
        drawStrUnified(g, baseFont, drawFlag ? "本次列车" : "This train", w * 0.01, h * 0.45, (float) (h * 0.04), 0);
        drawStrUnified(g, baseFont, drawFlag ? "下次列车" : "Next train", w * 0.01, h * 0.65, (float) (h * 0.04), 0);

        // === 到站信息 ===
        MtrUtil.PidsArrivalInfo arrivalInfo1 = arrivalInfoList.size() > 0 ? arrivalInfoList.get(0) : null;
        MtrUtil.PidsArrivalInfo arrivalInfo2 = arrivalInfoList.size() > 1 ? arrivalInfoList.get(1) : null;

        if (arrivalInfo1 != null) {
            String destination = (arrivalInfo1.customDestination != null && !arrivalInfo1.customDestination.isEmpty())
                    ? arrivalInfo1.customDestination : arrivalInfo1.destination;
            String matchingDest = TextUtil.getCjkMatching(destination, drawFlag);
            if (matchingDest.isEmpty()) matchingDest = destination;

            double scrollTime = drawScrollText(
                    g, baseFont, matchingDest,
                    w * 0.2, w * 0.15, h * 0.375,
                    baseFont.deriveFont((float) (h * 0.07)),
                    drawBeginTime, 2
            );
            drawTotalTime = Math.max(drawTotalTime, scrollTime);

            String arrivalText = getDispArrival(
                    (int) ((arrivalInfo1.arrivalMillis - System.currentTimeMillis()) / 1000),
                    drawFlag
            );
            drawStrUnified(g, baseFont, arrivalText, w * 0.1875, h * 0.55, (float) (h * 0.1), 1);
        }

        if (arrivalInfo2 != null) {
            String arrivalText2 = getDispArrival(
                    (int) ((arrivalInfo2.arrivalMillis - System.currentTimeMillis()) / 1000),
                    drawFlag
            );
            drawStrUnified(g, baseFont, arrivalText2, w * 0.1875, h * 0.75, (float) (h * 0.1), 1);
        }

        // === 可配置滚动文本 A ===
        double scollADrawTotalTime = drawScrollText(
                g, baseFont, currentScrollTextA,
                w * 0.775, 0, h * 0.875,
                baseFont.deriveFont((float) (h * 0.05)),
                scollADrawBeginTime, true
        );

        // === 可配置滚动文本 B ===
        double scollBDrawTotalTime = drawScrollText(
                g, baseFont, currentScrollTextB,
                w * 0.775, 0, h * 0.965,
                baseFont.deriveFont((float) (h * 0.05)),
                scollBDrawBeginTime, true
        );

        // === 更新状态计时器 ===
        if (drawBeginTime + drawTotalTime * 1000 < System.currentTimeMillis()) {
            drawState.put("drawBeginTime", System.currentTimeMillis());
            drawState.put("drawFlag", !drawFlag);
        }
        if (scollADrawBeginTime + scollADrawTotalTime * 1000 < System.currentTimeMillis()) {
            drawState.put("scollADrawBeginTime", System.currentTimeMillis());
            drawState.put("scollADrawFlag", !scollADrawFlag);
        }
        if (scollBDrawBeginTime + scollBDrawTotalTime * 1000 < System.currentTimeMillis()) {
            drawState.put("scollBDrawBeginTime", System.currentTimeMillis());
            drawState.put("scollBDrawFlag", !scollBDrawFlag);
        }

        gt.upload();
    }

    private BufferedImage loadBackground() {
        if (cachedBackground == null) {
            try {
                cachedBackground = (BufferedImage) JsFunctions.loadResource("img", "fangsu:pids/beijing/beijing_a.png");
            } catch (Exception e) {
                cachedBackground = null;
            }
        }
        return cachedBackground;
    }

    private Font loadBaseFont() {
        if (cachedBaseFont == null) {
            try {
                cachedBaseFont = (Font) JsFunctions.loadResource("systemFont", "方正姚体");
            } catch (Exception e) {
                cachedBaseFont = new Font("SansSerif", Font.PLAIN, 12);
            }
        }
        return cachedBaseFont;
    }

    /**
     * 绘制滚动文字，返回一次完整滚动周期的时间（秒）。
     */
    private double drawScrollText(Graphics2D g, Font baseFont, String str,
                                  double maxX, double x, double y,
                                  Font font, long beginTime, Object forceScroll) {
        g.setFont(font);
        int thisWidth = g.getFontMetrics().stringWidth(str);

        if (thisWidth <= maxX && forceScroll != Boolean.TRUE) {
            if (forceScroll instanceof Number) {
                int forceVal = ((Number) forceScroll).intValue();
                switch (forceVal) {
                    case 1:
                        g.drawString(str, (int) (x + (maxX - thisWidth) * 0.5), (int) y);
                        return 0;
                    case 2:
                        g.drawString(str, (int) (x + maxX - thisWidth), (int) y);
                        return 0;
                    default:
                        break;
                }
            }
            g.drawString(str, (int) x, (int) y);
            return 0;
        }

        Shape originalClip = g.getClip();
        double totalTextLength = g.getFontMetrics().stringWidth(str) + maxX;
        double speed = maxX * 0.25;
        double totalTime = totalTextLength / speed;

        int fontHeight = g.getFontMetrics().getHeight();
        g.setClip(new java.awt.Rectangle(
                (int) x, (int) (y - fontHeight - 2),
                (int) maxX, (int) (fontHeight * 1.25)
        ));

        double elapsed = (System.currentTimeMillis() - beginTime) / 1000.0;
        double progress = (elapsed - 0.1) % totalTime;
        if (progress < 0) progress = 0;
        g.drawString(str, (int) (x + maxX - progress * speed), (int) y);

        g.setClip(originalClip);
        return totalTime;
    }

    private String getDispArrival(int time, boolean flag) {
        if (time <= 2) return flag ? "列车到站" : "Arrived";
        else if (time <= 20) return flag ? "即将进站" : "Arriving";
        else if (time <= 60) return time + (flag ? " 秒" : " sec");
        else if (time <= 3600) return (time / 60) + (flag ? " 分" : " min");
        return (time / 3600) + (flag ? " 时" : " hour");
    }

    /**
     * 通过方速源码中的 MinecraftClientUtil 间接获取世界时间，
     * 不直接引用 Minecraft 类。
     */
    private WorldTime getWorldTime() {
        int totalTicks = MinecraftClientUtil.worldDayTime();
        int hours = ((totalTicks / 1000) + 6) % 24;
        int minutes = (int) ((totalTicks % 1000) / 16.67);

        return new WorldTime(
                hours < 10 ? "0" + hours : String.valueOf(hours),
                minutes < 10 ? "0" + minutes : String.valueOf(minutes)
        );
    }

    private static Color rgbToColor(int r, int g, int b) {
        return new Color(r, g, b);
    }

    private static String formatDate(boolean flag) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int month = cal.get(java.util.Calendar.MONTH) + 1;
        int day = cal.get(java.util.Calendar.DATE);
        return flag ? month + "月" + day + "日" : (cal.get(java.util.Calendar.YEAR) + "/" +
                (month < 10 ? "0" + month : month) + "/" +
                (day < 10 ? "0" + day : day));
    }

    private static String formatWeekday(boolean flag) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        String[] cnWeek = {"日", "一", "二", "三", "四", "五", "六"};
        String[] enWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return flag ? "星期" + cnWeek[dayOfWeek - 1] : enWeek[dayOfWeek - 1];
    }

    private static class WorldTime {
        final String h;
        final String m;

        WorldTime(String h, String m) {
            this.h = h;
            this.m = m;
        }
    }
}
