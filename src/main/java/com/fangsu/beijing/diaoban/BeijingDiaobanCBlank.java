package com.fangsu.beijing.diaoban;

import com.fangsu.blockEntities.RouteDrawer;
import com.fangsu.drawing.diaoban.BaseDiaobanDrawing;
import com.fangsu.scripting.GraphicsTexture;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingDiaobanCBlank extends BaseDiaobanDrawing {

    @Override
    public void draw(GraphicsTexture gt, List<RouteSelectInfo> routes,
                     Map<String, Object> drawState, int arrowDirection, int texW, int texH) {
        Graphics2D g = gt.graphics;
        int x = 0, y = 0, w = texW, h = texH;

        RouteDrawer.RouteDrawInfo drawInfo = buildDrawInfo(routes, arrowDirection, texW, texH);

        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);

        if (drawInfo.routeInfo() != null) {
            Color routeColor = drawInfo.routeInfo().routeColor;

            g.setColor(routeColor);
            g.fillRect(x + (int) (h * 0.1), heightPercent(y, h, 0.4), w - (int) (h * 0.2), (int) (h * 0.2));
            g.fillOval(x, heightPercent(y, h, 0.4), (int) (h * 0.2), (int) (h * 0.2));
            g.fillOval(widthPercent(x, w, 1) - (int) (h * 0.2), heightPercent(y, h, 0.4), (int) (h * 0.2), (int) (h * 0.2));
        }
    }

    private static int widthPercent(int x, int w, double p) {
        return (int) (x + w * p);
    }

    private static int heightPercent(int y, int h, double p) {
        return (int) (y + h * p);
    }
}
