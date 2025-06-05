import { useEffect } from 'react'
import { useMap } from 'react-leaflet';
import L from 'leaflet';
import './leaflet.measure' // from https://github.com/aprilandjan/leaflet.measure

const ShowMeasure = () => {
    var panelDiv = L.DomUtil.create('tiraLagAgua');
    if (L.Browser.chrome) {
        L.Browser.passiveEvents = true
    }

    const map = useMap()
    useEffect(() => {
        if (!map) {
            return
        }
        L.control.measure({
            position: 'topright',
            lineColor: 'red',
            lineWeight: 4
        }).addTo(map)
    }, [map])
    return null
}

export default ShowMeasure