package hotels.entity;

import java.io.Serializable;

public class Weather implements Serializable {

    private double lng;
    private double lat;
    private double avg_tmpr_f;
    private double avg_tmpr_c;
    private String wthr_date;
    private String geoHash;
    private String year;
    private String month;
    private String day;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAvg_tmpr_f() {
        return avg_tmpr_f;
    }

    public void setAvg_tmpr_f(double avg_tmpr_f) {
        this.avg_tmpr_f = avg_tmpr_f;
    }

    public double getAvg_tmpr_c() {
        return avg_tmpr_c;
    }

    public void setAvg_tmpr_c(double avg_tmpr_c) {
        this.avg_tmpr_c = avg_tmpr_c;
    }

    public String getWthr_date() {
        return wthr_date;
    }

    public void setWthr_date(String wthr_date) {
        this.wthr_date = wthr_date;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "lng=" + lng +
                ", lat=" + lat +
                ", avg_tmpr_f=" + avg_tmpr_f +
                ", avg_tmpr_c=" + avg_tmpr_c +
                ", wthr_date='" + wthr_date + '\'' +
                ", geoHash='" + geoHash + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
