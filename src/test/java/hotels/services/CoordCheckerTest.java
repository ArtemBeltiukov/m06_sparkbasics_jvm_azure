package hotels.services;

import ch.hsr.geohash.GeoHash;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import hotels.entity.Hotel;
import hotels.entity.Weather;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class CoordCheckerTest {

    private final CoordChecker coordChecker = new CoordChecker();

    @Test
    void getCoordsForHotel() {
        Hotel hotel = new Hotel();
        hotel.setLatitude("37.754154");
        hotel.setLongitude("-100.040754");
        hotel.setAddress("2000 W Wyatt Earp Blvd");
        JOpenCageLatLng coordsForHotel = coordChecker.getCoordsForHotel(hotel);
        assertEquals(hotel.getLatitude(), coordsForHotel.getLat().toString());
        assertEquals(hotel.getLongitude(), coordsForHotel.getLng().toString());
    }

    @Test
    void getGeoHash() {
        Hotel hotel = new Hotel();
        hotel.setLatitude("37.754154");
        hotel.setLongitude("-100.040754");
        hotel.setAddress("2000 W Wyatt Earp Blvd");
        hotel.setGeoHash("9y8y");
        GeoHash geoHash = coordChecker.getGeoHash(hotel);
        assertEquals(hotel.getGeoHash(), geoHash.toBase32());
    }

    @Test
    void testGetGeoHash() {
        Weather hotel = new Weather();
        hotel.setLat(37.754154);
        hotel.setLng(-100.040754);
        hotel.setGeoHash("9y8y");
        GeoHash geoHash = coordChecker.getGeoHash(hotel);
        assertEquals(hotel.getGeoHash(), geoHash.toBase32());
    }

    @Test
    void compareWithOpenCage() {
        Hotel hotel = new Hotel();
        hotel.setLatitude("37.754154");
        hotel.setLongitude("-100.040754");
        hotel.setAddress("2000 W Wyatt Earp Blvd");
        JOpenCageLatLng coordsForHotel = coordChecker.getCoordsForHotel(hotel);
        assertNotNull(coordsForHotel);
    }
}