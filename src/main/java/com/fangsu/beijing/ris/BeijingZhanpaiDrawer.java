package com.fangsu.beijing.ris;

import com.fangsu.scripting.G2dTextHelper;
import com.fangsu.scripting.JsFunctions;
import com.fangsu.scripting.TextUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;

public final class BeijingZhanpaiDrawer {
    private static final String FONT_SYHT = "mtrsteamloco:fonts/source-han-sans.otf";
    private static final String FONT_SYHT_BOLD = "mtrsteamloco:fonts/source-han-sans-bold.otf";
    private static Font syht;
    private static Font syhtBold;

    public static void drawZhanpaiA(Graphics2D g, String[] stns, ConnectionType connectionType,
                                    String from, String to, String routeName, int index, int x, int y, int w, int h) {
        loadResources();

        if (connectionType == ConnectionType.NONE || connectionType == ConnectionType.BEGIN) {
            //routeName
            //h*0.05-h*0.25;w*0-w*0.3
            g.setColor(Color.WHITE);
            G2dTextHelper.drawStrUnifiedWithStretch(g, syhtBold, routeName, Math.round(x + w * 0.15f), Math.round(y + h * 0.2125f), Math.round(h * 0.15f), Math.round(w * 0.25f), 1);

            String[] fromSpilt = from.split("\\|");
            String[] toSpilt = to.split("\\|");
            String destStr = fromSpilt[0] + "-" + toSpilt[0];
            g.setColor(Color.BLACK);
            G2dTextHelper.drawStrUnifiedWithStretch(g, syht, destStr, Math.round(x + w * 0.65f), Math.round(y + h * 0.1625f), Math.round(h * 0.1f), Math.round(w * 0.7f), 1);

        }

        int textTotalWidth = Math.round(w * 0.9f);
        boolean arrowLeft = connectionType == ConnectionType.ODD || connectionType == ConnectionType.EVEN || connectionType == ConnectionType.ODD_END;
        boolean isReversed = connectionType == ConnectionType.EVEN || connectionType == ConnectionType.EVEN_END;
        if (arrowLeft)
            textTotalWidth -= Math.round(w * 0.05f);
        int textBasicScale = Math.round(h * 0.065f);
        int distance = Math.round(textTotalWidth / (stns.length + (stns.length * 0.5f - 1)));
        int actualTextWidth = Math.min(distance, textBasicScale);
        double widthScale = (double) actualTextWidth / (double) textBasicScale;
        int maxHeight = Math.round(h * 0.45f);

        //stations
        g.setColor(Color.WHITE);
        AffineTransform originalTransform = g.getTransform();
        final int beginY = Math.round(y + h * 0.425f);
        for (int i = 0; i < stns.length; i++) {
            int drawIndex = isReversed ? stns.length - i - 1 : i;
            String stn = stns[drawIndex].split("\\|")[0];
            int currentX = Math.round(x + w * 0.05f) + (arrowLeft ? Math.round(w * 0.05f) : 0) + Math.round(distance * i * 1.5f);

            if (drawIndex == index) {
                g.fillOval(currentX, beginY - Math.round(textBasicScale * 1.5f), textBasicScale, textBasicScale);
            }

            if (TextUtil.isCjk(stn)) {
                g.setFont(syht.deriveFont(Font.PLAIN, textBasicScale));
                int eachHeight = (maxHeight - textBasicScale) / (stn.length() - 1);
                double heightScale = Math.min(1d, (double) eachHeight / textBasicScale);
                int step = Math.max(eachHeight, textBasicScale);
                AffineTransform transform = new AffineTransform();
                transform.translate(currentX, beginY);
                transform.scale(widthScale, heightScale);
                g.setTransform(transform);
                for (int j = 0; j < stn.length(); j++) {
                    int currentY = j * step;
                    char chr = stn.charAt(j);
                    g.drawString(chr + "", 0, currentY);
                }
            } else {
                AffineTransform transform = new AffineTransform();
                transform.translate(currentX, beginY);
                transform.rotate(Math.toRadians(90));
                g.setTransform(transform);

                //noinspection SuspiciousNameCombination
                G2dTextHelper.drawStrUnifiedWithStretch(g, syht, stn, 0, 0, actualTextWidth, maxHeight, 0);
            }
            g.setTransform(originalTransform);
        }

    }

    private static void loadResources() {
        try {
            syht = (Font) JsFunctions.loadResource("font", FONT_SYHT);
        } catch (Exception e) {
            syht = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }
        try {
            syhtBold = (Font) JsFunctions.loadResource("font", FONT_SYHT_BOLD);
        } catch (Exception e) {
            syhtBold = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
        }
    }

    public enum ConnectionType {
        NONE, BEGIN, ODD, EVEN, ODD_END, EVEN_END
    }
}
