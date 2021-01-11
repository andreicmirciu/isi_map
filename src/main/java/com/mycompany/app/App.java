/**
 * Copyright 2019 Esri
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.mycompany.app;

import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.OAuthConfiguration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.symbology.ClassBreaksRenderer;
import com.esri.arcgisruntime.symbology.ClassBreaksRenderer.ClassBreak;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer.UniqueValue;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.mapping.Viewpoint;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class App extends Application {

    private final int hexRed = 0xFFFF0000;
    private final int hexBlue = 0xFF0000FF;
    private final int hexGreen = 0xFF00FF00;
    private final int hexPurple = 0xFF551A8B;
    private final int hexBlack = 0xFF000000;
    private final int hexOrange = 0xFFFFA500;

    private GraphicsOverlay graphicsOverlay;

    private MapView mapView;

    private void addTrailheadsLayer() {
        if (mapView != null) {
            String url = "https://services5.arcgis.com/dB5Sg7pSAjSChj3q/arcgis/rest/services/maramures/FeatureServer";
            /*ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(url);
            FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
            ArcGISMap map = mapView.getMap();
            map.getOperationalLayers().add(featureLayer);
            featureLayer.addDoneLoadingListener(() -> {
                if (featureLayer.getLoadStatus() == LoadStatus.LOADED) {
                    mapView.setViewpointGeometryAsync(featureLayer.getFullExtent());
                } else {
                    featureLayer.getLoadError().getCause().printStackTrace();
                }
            });*/
            FeatureLayer featureLayer = addFeatureLayer(url);
            String pictureURI = "http://static.arcgis.com/images/Symbols/NPS/npsPictograph_0231b.png";
            PictureMarkerSymbol pictureMarkerSymbol = new PictureMarkerSymbol(pictureURI);
            SimpleRenderer simpleRenderer = new SimpleRenderer(pictureMarkerSymbol);
            featureLayer.setRenderer(simpleRenderer);
        }
    }

    private void setupPortalItem() {
        String portalItemId = "ff52e100893b4bfcae52acf56ee537a9"; // tourist spots
        String portalItemId2 = "9d5303028fbd4b1ebe09a1be88236551"; // restaurants
        Portal portal = new Portal("http://www.arcgis.com", false);
        PortalItem portalItem = new PortalItem(portal, portalItemId);
        PortalItem portalItem2 = new PortalItem(portal, portalItemId2);
        portalItem.addDoneLoadingListener(() -> {
            if (portalItem.getLoadStatus() == LoadStatus.LOADED) {
                setupFeatureLayerFromPortalItem(portalItem);
                setupFeatureLayerFromPortalItem(portalItem2);
            } else {
                new Alert(AlertType.ERROR, "Portal Item: " + portalItem.getLoadError().getMessage()).show();
            }
        });
        portalItem.loadAsync();
    }

    private void setupFeatureLayerFromPortalItem(PortalItem portalItem) {
        int layerId = 0;
        FeatureLayer layer = new FeatureLayer(portalItem, layerId);
        layer.addDoneLoadingListener(() -> {
            if (layer.getLoadStatus() == LoadStatus.LOADED) {
                mapView.getMap().getOperationalLayers().add(layer);
                mapView.setViewpoint(new Viewpoint(layer.getFullExtent()));
            } else {
                new Alert(AlertType.ERROR, "Feature Layer: " + layer.getLoadError().getMessage()).show();
            }
        });
        layer.loadAsync();
    }

    private FeatureLayer addFeatureLayer(String uri) {

        String url = "https://services5.arcgis.com/dB5Sg7pSAjSChj3q/arcgis/rest/services/maramures/FeatureServer";
        ServiceFeatureTable table = new ServiceFeatureTable(url);
        FeatureLayer featureLayer = new FeatureLayer(table);
        ArcGISMap map = mapView.getMap();
        map.getOperationalLayers().add(featureLayer);

        return featureLayer;
    }

    private void addTrailsLayer() {

        String url = "https://services5.arcgis.com/dB5Sg7pSAjSChj3q/arcgis/rest/services/maramures/FeatureServer";
        FeatureLayer featureLayer = addFeatureLayer(url);
//        UniqueValueRenderer trailsUniqueValueRenderer = new UniqueValueRenderer();
//        trailsUniqueValueRenderer.getFieldNames().add("USE_BIKE");
//        featureLayer.setRenderer(trailsUniqueValueRenderer);
//        SimpleLineSymbol bikeTrailSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, hexBlue, 2.0f);
//        SimpleLineSymbol noBikeTrailSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DOT, hexRed, 2.0f);
//        UniqueValue trailsForBike = new UniqueValue("Bike trails", "Bike", bikeTrailSymbol, Arrays.asList("Yes"));
//        UniqueValue trailsNoBike = new UniqueValue("No bike trails", "No Bike", noBikeTrailSymbol, Arrays.asList("No"));
//        trailsUniqueValueRenderer.getUniqueValues().add(trailsForBike);
//        trailsUniqueValueRenderer.getUniqueValues().add(trailsNoBike);
    }

    private void addOpenSpaceLayer() {

        String openSpacesFeaturesUrl =
                "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Parks_and_Open_Space/FeatureServer/0";
        FeatureLayer featureLayer = addFeatureLayer(openSpacesFeaturesUrl);
        SimpleLineSymbol fillOutlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlack, 0.5f);
        SimpleFillSymbol greenClassSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, hexGreen, fillOutlineSymbol);
        SimpleFillSymbol purpleClassSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, hexPurple, fillOutlineSymbol);
        SimpleFillSymbol orangeClassSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, hexOrange, fillOutlineSymbol);
        ClassBreak greenClassBreak = new ClassBreak("Under 1,629", "0 - 1629", 0.0, 1629.0, greenClassSymbol);
        ClassBreak purpleClassBreak = new ClassBreak("1,629 to 3,754", "1629 - 3754", 1629.0, 3754.0, purpleClassSymbol);
        ClassBreak orangeClassBreak = new ClassBreak("3,754 to 11,438", "3754 - 11438", 3754.0, 11438.0, orangeClassSymbol);

        List<ClassBreak> acreageBreaks = new ArrayList<>();
        acreageBreaks.add(greenClassBreak);
        acreageBreaks.add(purpleClassBreak);
        acreageBreaks.add(orangeClassBreak);
        ClassBreaksRenderer openSpacesClassBreaksRenderer = new ClassBreaksRenderer("GIS_ACRES", acreageBreaks);
        featureLayer.setRenderer(openSpacesClassBreaksRenderer);
    }

    private void setupGraphicsOverlay() {
        if (mapView != null) {
            graphicsOverlay = new GraphicsOverlay();
            mapView.getGraphicsOverlays().add(graphicsOverlay);
        }
    }

    private void addPointGraphic() {
        if (graphicsOverlay != null) {
            SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, hexRed, 10.0f);
            pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
            Point point = new Point(-118.29507, 34.13501, SpatialReferences.getWgs84());
            Graphic pointGraphic = new Graphic(point, pointSymbol);
            graphicsOverlay.getGraphics().add(pointGraphic);
        }
    }

    private void addPolylineGraphic() {
        if (graphicsOverlay != null) {
            PointCollection polylinePoints = new PointCollection(SpatialReferences.getWgs84());
            polylinePoints.add(new Point(-118.29026, 34.1816));
            polylinePoints.add(new Point(-118.26451, 34.09664));
            Polyline polyline = new Polyline(polylinePoints);
            SimpleLineSymbol polylineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 3.0f);
            Graphic polylineGraphic = new Graphic(polyline, polylineSymbol);
            graphicsOverlay.getGraphics().add(polylineGraphic);
        }
    }

    private void addPolygonGraphic() {
        if (graphicsOverlay != null) {
            PointCollection polygonPoints = new PointCollection(SpatialReferences.getWgs84());
            polygonPoints.add(new Point(-118.27653, 34.15121));
            polygonPoints.add(new Point(-118.24460, 34.15462));
            polygonPoints.add(new Point(-118.22915, 34.14439));
            polygonPoints.add(new Point(-118.23327, 34.12279));
            polygonPoints.add(new Point(-118.25318, 34.10972));
            polygonPoints.add(new Point(-118.26486, 34.11625));
            polygonPoints.add(new Point(-118.27653, 34.15121));
            Polygon polygon = new Polygon(polygonPoints);
            SimpleFillSymbol polygonSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, hexGreen,
                    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, hexBlue, 2.0f));
            Graphic polygonGraphic = new Graphic(polygon, polygonSymbol);
            graphicsOverlay.getGraphics().add(polygonGraphic);
        }
    }

    private void setupMap() {
        if (mapView != null) {
            Basemap.Type basemapType = Basemap.Type.NATIONAL_GEOGRAPHIC;
            double latitude = 47.5534033;
            double longitude = 24.0110;
            int levelOfDetail = 10;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);

            mapView.setMap(map);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            // set the title and size of the stage and show it
            stage.setTitle("Maramures County App");
            stage.setWidth(800);
            stage.setHeight(700);
            stage.show();

            // create a JavaFX scene with a stack pane as the root node and add it to the scene
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane);
            stage.setScene(scene);

            // create a MapView to display the map and add it to the stack pane
            mapView = new MapView();
            stackPane.getChildren().add(mapView);

//            // set up an oauth config with url to portal, a client id and a re-direct url
//            OAuthConfiguration oAuthConfiguration = new OAuthConfiguration("https://www.arcgis.com/", "0TVYXiXVOQtjv3d8", "urn:ietf:wg:oauth:2.0:oob");
//
//            // set up the authentication manager to handle authentication challenges
//            DefaultAuthenticationChallengeHandler defaultAuthenticationChallengeHandler = new DefaultAuthenticationChallengeHandler();
//            AuthenticationManager.setAuthenticationChallengeHandler(defaultAuthenticationChallengeHandler);
//            // add the OAuth configuration
//            AuthenticationManager.addOAuthConfiguration(oAuthConfiguration);

            setupMap();
            setupPortalItem();
            //setupGraphicsOverlay();
            //addPointGraphic();
            //addPolylineGraphic();
            //addPolygonGraphic();
            //addOpenSpaceLayer();
            addTrailsLayer();
            //addTrailheadsLayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {
        if (mapView != null) {
            mapView.dispose();
        }
    }
}
