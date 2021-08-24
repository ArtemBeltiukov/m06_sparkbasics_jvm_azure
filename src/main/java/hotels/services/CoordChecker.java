package hotels.services;

import ch.hsr.geohash.GeoHash;
import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;
import hotels.entity.Hotel;
import hotels.entity.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;

public class CoordChecker implements Serializable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final PropertiesService PROPERTIES = new PropertiesService();

    public JOpenCageLatLng getCoordsForHotel(Hotel hotel) {
        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(PROPERTIES.getProperty("openCage.apiKey"));
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(hotel.getAddress());
        request.setRestrictToCountryCode(hotel.getCountry());
        JOpenCageLatLng firstResultLatLng;
        try {
            JOpenCageResponse response = jOpenCageGeocoder.forward(request);
            firstResultLatLng = response.getFirstPosition();
        } catch (Exception e) {
            LOGGER.warn("cant get hotel coords for " + hotel);
            return null;
        }
        return firstResultLatLng;
    }

    public GeoHash getGeoHash(Hotel hotel) {
        GeoHash geoHash = null;
        try {
            geoHash = GeoHash.withCharacterPrecision(Double.parseDouble(hotel.getLatitude()), Double.parseDouble(hotel.getLongitude()), 4);
        } catch (NumberFormatException e) {
            LOGGER.warn("cant transform lat: " + hotel.getLatitude() + " or lng: " + hotel.getLongitude() + " to double");
        }
        return geoHash;
    }

    public GeoHash getGeoHash(Weather weather) {
        return GeoHash.withCharacterPrecision(weather.getLat(), weather.getLng(), 4);
    }

    public boolean compareWithOpenCage(Hotel hotel) {
        JOpenCageLatLng coordsForHotel = getCoordsForHotel(hotel);
        if (coordsForHotel != null && (!coordsForHotel.getLng().toString().equals(hotel.getLongitude()) || !coordsForHotel.getLat().toString().equals(hotel.getLatitude()))) {
            hotel.setLongitude(coordsForHotel.getLng().toString());
            hotel.setLatitude(coordsForHotel.getLat().toString());
        }
        return coordsForHotel != null;
    }
}
