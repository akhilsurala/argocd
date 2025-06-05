import React, { useEffect, useRef, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMapEvents, useMap, FeatureGroup } from 'react-leaflet';

import styled from "styled-components";

import { LayersControl } from 'react-leaflet';
import 'leaflet-draw/dist/leaflet.draw.css';
import 'leaflet-draw';
// import
import SearchField from './SearchField';

import "leaflet/dist/leaflet.css";
import Toolbar from './Toolbar';
const OpenStreetMap = ({ showPolygonDraw = true, value, handleChange, setMessage, polygonCoordinate, setPolygonCoordinate, area,
    setArea }) => {


    //TODO later we have to placed center point also by api
    const [centerPoint, setCenterPoint] = useState([]);
    const { BaseLayer } = LayersControl;

    const MapEvents = () => {
        const map = useMapEvents({
            click: (e) => {
                const { lat, lng } = e.latlng;
                // handleChange();
                console.log("latlng", { lat, lng });
            },

        });

        return null;
    };


    useEffect(() => {
        handleChange({ lat: centerPoint[0], lng: centerPoint[1] });
    }, [centerPoint])


    return (
        <Container>
            <MapContainer

                center={[28.59864066859687, 77.36236453056337]}
                zoom={polygonCoordinate.length ? 17 : 5}
                maxZoom={19}
                style={{ height: '100%', width: '100%' }}

            >
                <MapEvents />
                <LayersControl position="topright">
                    <BaseLayer checked name="Street View">
                        <TileLayer maxNativeZoom={19} maxZoom={19} url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors' />
                    </BaseLayer>
                    <BaseLayer name="Satellite View">
                        <TileLayer maxNativeZoom={19} maxZoom={19} url="https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}" attribution='&copy; <a href="https://www.arcgis.com/">ArcGIS</a> contributors' />
                    </BaseLayer>
                </LayersControl>

                <Toolbar
                    polygonCoordinate={polygonCoordinate}
                    setPolygonCoordinate={setPolygonCoordinate}
                    setCenterPoint={setCenterPoint}
                    setMessage={setMessage}
                    area={area}
                    setArea={setArea}
                />
                {/* {value && (
                    <Marker position={value}>
                        <Popup>
                            Coordinates: {value.lat}, {value.lng}
                        </Popup>
                    </Marker>
                )} */}
                <SearchField />

            </MapContainer>
        </Container>
    );
};

export default OpenStreetMap;

const Container = styled.div`
height: 80%;
  /* .leaflet-container {
    transform: rotate(90deg);
    transform-origin: left center;
    height: 100vh;
    width: 100vh;
  } */
`;



