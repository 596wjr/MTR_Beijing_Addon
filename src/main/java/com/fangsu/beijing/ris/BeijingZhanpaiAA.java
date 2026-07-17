package com.fangsu.beijing.ris;

import com.fangsu.drawing.ris.BaseRisDrawing;
import com.fangsu.mtr.LocalRoute;
import com.fangsu.mtr.LocalRouteDetail;
import com.fangsu.scripting.GraphicsTexture;
import com.fangsu.ui.RouteSelectInfo;
import com.fangsu.ui.RouteSelectionScreen;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class BeijingZhanpaiAA extends BaseRisDrawing {

    @Override
    public void draw(GraphicsTexture gt, List<RouteSelectInfo> routes,
                     Map<String, Object> drawState, int arrowDirection, int texW, int texH) {
        Graphics2D g = gt.graphics;
        if (!routes.isEmpty()) {
            LocalRoute route1 = routes.get(0).route;
            LocalRouteDetail routeDetail1 = route1.asRouteDetail();
            String routeName = (route1.lightRailRouteNumber.isEmpty() ? route1.name : route1.lightRailRouteNumber).split("\\|")[0];
            String[] stations = new String[routeDetail1.drawStations.size()];
            int index = route1.getPlatformIdIndex(routes.get(0).localPlatform.id);
            for (int i = 0; i < stations.length; i++) {
                stations[i] = routeDetail1.drawStations.get(i).stationName;
            }
            BeijingZhanpaiDrawer.drawZhanpaiA(g, stations, BeijingZhanpaiDrawer.ConnectionType.NONE, stations[0], stations[stations.length - 1],
                    routeName, index, 0, 0, 1024, 512);
        }

    }
}
