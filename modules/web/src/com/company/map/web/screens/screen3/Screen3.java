package com.company.map.web.screens.screen3;

import com.company.map.entity.Punto;
import com.company.map.service.PuntoService;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Layout;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.addon.leaflet.shared.PopupState;
import org.vaadin.addon.leaflet.shared.Control;
import javax.inject.Inject;
import java.util.*;
import java.util.Map;

public class Screen3 extends AbstractWindow {

    private static final double DEFAULT_LATITUDE = -33.462136;
    private static final double DEFAULT_LONGITUDE = -70.701047;
    private static final double ZOOM_LEVEL = 12;

    private LMap map;
    private String popupTemplate;
    private PopupState popupState;
    private Point popupAnchor;
    private Point iconSize;
    private Point iconAnchor;

    @Inject
    private VBoxLayout mapContainer;
    @Inject
    private PuntoService puntoService;

    List<LMarker> listLevel1 = new ArrayList<>();
    List<LMarker> listLevel2 = new ArrayList<>();
    List<LMarker> listLevel3 = new ArrayList<>();

    @Override
    public void init(Map<String, Object> params) {
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
        map.setControls(new ArrayList<Control>(Arrays.asList(Control
                .values())));

        LTileLayer capa1 = new LTileLayer();
        capa1.setUrl("http://www.google.cn/maps/vt?lyrs=s@189&gl=cn&x={x}&y={y}&z={z}");
        LWmsLayer capa2 = new LWmsLayer();
        capa2.setUrl("http://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png");
        LTileLayer capa3 = new LTileLayer();
        capa3.setUrl("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");

        List<Punto> listPuntos = puntoService.listAll();
        getListByNivel(listPuntos);

        LLayerGroup group1 = new LLayerGroup();
        LLayerGroup group2 = new LLayerGroup();
        LLayerGroup group3 = new LLayerGroup();

        addListToGroup(listLevel1,group1);
        addListToGroup(listLevel2,group2);
        addListToGroup(listLevel3,group3);

        group1.addComponent(capa1);
        group2.addComponent(capa2);
        group3.addComponent(capa3);

        map.addBaseLayer(group1,"Nivel 1");
        map.addBaseLayer(group2,"Nivel 2");
        map.addBaseLayer(group3,"Nivel 3");

        group1.setActive(true);
        group2.setActive(false);
        group3.setActive(false);

    }
    private void addMapToContainer() {
        Layout layout = (Layout) WebComponentsHelper.unwrap(mapContainer);
        layout.addComponent(map);
    }
    private void getListByNivel(List<Punto> listPuntos){
        listLevel1.clear();
        listLevel2.clear();
        listLevel3.clear();
        for(int i=0; i<listPuntos.size(); i++){
            Integer nivel = listPuntos.get(i).getNivel();
            if(nivel!=null){
                if( nivel>=1 && nivel<=3 ){
                    Punto punto = listPuntos.get(i);
                    if(punto!=null){
                        LMarker marker = paintPunto(punto);
                        if(marker!=null){
                            if (nivel==1)
                                listLevel1.add(marker);
                            if (nivel==2)
                                listLevel2.add(marker);
                            if (nivel==3)
                                listLevel3.add(marker);
                        }
                    }
                }
            }
        }
    }

    private LMarker paintPunto(Punto punto){
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

            }
            return marker;
        }
        return null;
    }

    private void addListToGroup(List<LMarker> list, LLayerGroup group){
        for ( int i=0; i<list.size();i++ ){
            LMarker maker = list.get(i);
            group.addComponent(maker);
        }
    }

}