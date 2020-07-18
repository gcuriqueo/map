package com.company.map.web.screens.screen1;

import com.company.map.entity.Punto;
import com.company.map.service.PuntoService;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.ExternalResource;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.addon.leaflet.shared.PopupState;
import com.vaadin.ui.Layout;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class Screen1 extends AbstractWindow {

    private static final double DEFAULT_LATITUDE = -33.462136;
    private static final double DEFAULT_LONGITUDE = -70.701047;
    private static final double ZOOM_LEVEL = 11;

    private LMap map;
    private LLayerGroup puntoMarkers;
    private String popupTemplate;
    private PopupState popupState;
    private Point popupAnchor;
    private Point iconSize;
    private Point iconAnchor;

    @Inject
    private VBoxLayout mapContainer;
    @Inject
    private PuntoService puntoService;

    @Override
    public void init(Map<String, Object> params) {
        puntoMarkers = new LLayerGroup();
        popupTemplate = createPopupTemplate();
        popupState = createPopupState();

        iconSize = new Point(25, 41);
        iconAnchor = new Point(12, 41);
        popupAnchor = new Point(0, -32);
    }
    private String createPopupTemplate() {
        return "<center>" +
                "<strong></strong>" +
                "<b>"+getMessage("popup.latitud")+"</b>"+ ": %s</br>" +
                "<b>"+getMessage("popup.longitud")+"</b>"+ ": %s</br>" +
                "<b>"+getMessage("popup.color")+"</b>"+ ": %s</br>" +
                "<b>"+getMessage("popup.nivel")+"</b>"+ ": %s</br>" +
                "</center>";
    }
    private String getPuntoPopup (Punto punto){
        return String.format(
                popupTemplate,
                punto.getLatitud(),
                punto.getLongitud(),
                punto.getColor(),
                punto.getNivel()
        );
    }
    private PopupState createPopupState() {
        PopupState popupState = new PopupState();
        popupState.autoClose = false;
        popupState.closeOnClick = false;
        return popupState;
    }

    @Override
    public void ready() {
        initMap();
        addMapToContainer();
    }
    private void initMap() {
        map = new LMap();
        map.setZoomLevel(ZOOM_LEVEL);
        map.addLayer(new LOpenStreetMapLayer());
        map.setCenter(new Point(DEFAULT_LATITUDE, DEFAULT_LONGITUDE));
        List<Punto> list = puntoService.listAll();
        getListPunto(list);
    }
    private void addMapToContainer() {
        Layout layout = (Layout) WebComponentsHelper.unwrap(mapContainer);
        layout.addComponent(map);
    }
    private void getListPunto(List<Punto> list){
        for(int i=0; i<list.size(); i++){
            Punto punto = list.get(i);
            paintPunto(punto);
        }
    }
    private void paintPunto(Punto punto){
        Double latitud = punto.getLatitud();
        Double longitud = punto.getLongitud();
        if (latitud!= null && longitud!=null) {
            Point point = new Point(latitud, longitud);
            LMarker marker = new LMarker(point);
            marker.setIconSize(iconSize);
            marker.setIconAnchor(iconAnchor);
            String popup = getPuntoPopup(punto);
            marker.setPopup(popup);
            marker.setPopupAnchor(popupAnchor);
            marker.setPopupState(popupState);
            Integer color = punto.getColor();
            if(color!=null){
                if (color == 1)
                    marker.setIcon(new ExternalResource("https://raw.githubusercontent.com/gcuriqueo/map/master/icons/marker-icon-green.png"));
                if (color == 2)
                    marker.setIcon(new ExternalResource("https://raw.githubusercontent.com/gcuriqueo/map/master/icons/marker-icon-gold.png"));
                if (color == 3)
                    marker.setIcon(new ExternalResource("https://raw.githubusercontent.com/gcuriqueo/map/master/icons/marker-icon-orange.png"));
                if (color == 4)
                    marker.setIcon(new ExternalResource("https://raw.githubusercontent.com/gcuriqueo/map/master/icons/marker-icon-red.png"));
                if (color == 5)
                    marker.setIcon(new ExternalResource("https://raw.githubusercontent.com/gcuriqueo/map/master/icons/marker-icon-blue.png"));

                puntoMarkers.addComponent(marker);
                map.addComponent(puntoMarkers);
            }
        }
    }
}