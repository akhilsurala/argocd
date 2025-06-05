import React, { useEffect } from 'react'

import L from "leaflet";

import { useMap } from "react-leaflet";
import 'leaflet-draw/dist/leaflet.draw.css';
import 'leaflet-draw';

import "leaflet/dist/leaflet.css";
export default function Toolbar({ polygonCoordinate, setPolygonCoordinate, setCenterPoint, setMessage, area,
    setArea }) {
    const mapRef = useMap()
    useEffect(() => {
        const map = mapRef;
        if (!map) return;

        // Initialize Leaflet Draw
        const drawnItems = new L.FeatureGroup();
        map.addLayer(drawnItems);
        // Event handler for when a shape is being drawn

        const drawControl = new L.Control.Draw({
            draw: {
                marker: false, // Disable marker drawing
                polyline: false, // Enable polyline drawing
                polygon: true, // Enable polygon drawing
                circlemarker: false, // Disable circlemarker drawing
                circle: false, // Disable circle drawing
                rectangle: false // Disable rectangle drawing
            },
            edit: {
                featureGroup: drawnItems, // Set the feature group for editing
                remove: true // Enable shape removal
            }
        });
        const drawControlAfterDrawing = new L.Control.Draw({
            draw: {
                marker: false, // Disable marker drawing
                polyline: false, // Enable polyline drawing
                polygon: false, // Enable polygon drawing
                circlemarker: false, // Disable circlemarker drawing
                circle: false, // Disable circle drawing
                rectangle: false // Disable rectangle drawing
            },
            edit: {
                featureGroup: drawnItems, // Set the feature group for editing
                remove: true // Enable shape removal
            }
        });

        // Hiding controls for in case polygon already defined
        if (polygonCoordinate.length) {
            map.addControl(drawControlAfterDrawing);
        } else {
            map.addControl(drawControl);
        }

        map.on("draw:deleted", function (e) {
            map.addControl(drawControl);
            map.removeControl(drawControlAfterDrawing);
            setPolygonCoordinate([])
        });

        // Function to show the popup with the area information
        const showPopup = (layer) => {
            const areaInSquareMeters = L.GeometryUtil.geodesicArea(layer.getLatLngs()[0]);
            const areaInAcres = areaInSquareMeters * 0.000247105;
            layer.bindPopup(`Area: ${areaInAcres} acres`).openPopup();
        };


        const calculateArea = () => {
            const layers = drawnItems.getLayers();
            if (layers.length === 0) return;
            layers.forEach(layer => {
                console.log(layer);
                const polygon = layer;
                const center = polygon.getBounds().getCenter();
                setCenterPoint([center.lat, center.lng]);
                const areaInSquareMeters = L.GeometryUtil.geodesicArea(polygon.getLatLngs()[0]);
                const areaInAcres = areaInSquareMeters * 0.000247105;
                setMessage("")
                if (areaInAcres < 0.1) {
                    setMessage("Min area supported is a grid of 0.1 acre.")
                } else if (areaInAcres > 20) {
                    setMessage("Max area supported is 20 acres.")
                }
                setArea(areaInAcres);

                // console.log("Area:", areaInAcres);

            });
        };

        const drawPolygon = (coordinates) => {
            if (coordinates.length) {
                const map = mapRef;
                if (!map) return;
                const polygon = new L.Polygon(coordinates);
                drawnItems.addLayer(polygon);
                calculateArea();
                showPopup(polygon);

            }
        };

        drawPolygon(polygonCoordinate);
        // Event handler for when a shape is drawn
        map.on(L.Draw.Event.CREATED, function (e) {
            const { layerType, layer } = e;
            if (layerType === 'polyline' || layerType === 'polygon') {
                setPolygonCoordinate(layer.getLatLngs()[0])
                // console.log("array", layer.getLatLngs())
                drawnItems.addLayer(layer); // Add the drawn layer to the map
                calculateArea(); // Calculate the area
                showPopup(layer); // Show the popup


                map.removeControl(drawControl);
                map.addControl(drawControlAfterDrawing);
            }
        });

        map.on(L.Draw.Event.EDITED, function (e) {
            if (!e || !e.layers) {
                console.error("Event object is undefined or doesn't have layers.");
                return;
            }

            const layers = e.layers; // This should be the collection of edited layers
            layers.eachLayer((layer) => {
                if (layer.getLatLngs) {
                    // Log layer to debug if it has the correct structure
                    console.log("Edited Layer:", layer);

                    // Update the coordinates
                    calculateArea(layer);
                    showPopup(layer); // Recalculate area and center
                } else {
                    console.error("Layer does not have getLatLngs method.");
                }
            });
        });

        console.log(map.listens(L.Draw.Event.EDITED));


        return () => {
            // Clean up event listeners
            map.off(L.Draw.Event.CREATED);
        };
    }, []);
    return null;
}
