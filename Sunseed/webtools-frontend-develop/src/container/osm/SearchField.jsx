import React from "react";
import { TextField } from "@mui/material";
import { GeoSearchControl, OpenStreetMapProvider } from "leaflet-geosearch";

import { useMap } from "react-leaflet";
import '../../../node_modules/leaflet-geosearch/dist/geosearch.css';
import "./SearchField.css";
export default function SearchField() {

    const searchControl = new GeoSearchControl({
        provider: new OpenStreetMapProvider(),
        style: "bar",
        showMarker: true,
        showPopup: false,
        marker: {
            icon: new L.Icon.Default(),
            draggable: false,
        },
        classNames: {},
        popupFormat: ({ query, result }) => result.label,
        resultFormat: ({ result }) => result.label,
        maxMarkers: 1,
        retainZoomLevel: false,
        animateZoom: true,
        autoClose: false,
        searchLabel: "Enter address",
        keepResult: false,
        updateMap: true,
    });

    const map = useMap();

    React.useEffect(() => {
        map.addControl(searchControl);
        return () => map.removeControl(searchControl);
    }, [map, searchControl]);



    return null
}
