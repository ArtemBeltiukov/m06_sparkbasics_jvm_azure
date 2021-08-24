package hotels;

import ch.hsr.geohash.GeoHash;
import hotels.entity.Hotel;
import hotels.entity.Weather;
import hotels.services.CoordChecker;
import hotels.services.PropertiesService;
import hotels.services.SparkService;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.*;
import scala.Tuple2;

public class Main {

    private final static PropertiesService PROPERTIES = new PropertiesService();

    public static void main(String[] args) {
        // Initializers
        SparkService sparkService = new SparkService();
        CoordChecker cc = new CoordChecker();
        SparkSession ss = sparkService.sparkSession();

        // create dataset
        Dataset<Hotel> hotelDataset = ss.read()
                .option("header", true)
                .csv("abfss://" + PROPERTIES.getProperty("azure.blobName") + "@" + PROPERTIES.getProperty("azure.path") + "/hotels")
                .withColumn("geoHash", functions.lit(null))
                .as(Encoders.bean(Hotel.class));
        Dataset<Weather> weatherDataset = ss.read()
                .option("header", false)
                .parquet("abfss://" + PROPERTIES.getProperty("azure.blobName") + "@" + PROPERTIES.getProperty("azure.path") + "/weather")
                .withColumn("geoHash", functions.lit(null))
                .as(Encoders.bean(Weather.class));
        // Filtering and get geohash
        JavaRDD<Hotel> hotelJavaRDD = hotelDataset
                .filter((FilterFunction<Hotel>) x -> x.getLongitude() != null && x.getLatitude() != null)
                .filter((FilterFunction<Hotel>) x -> !x.getLongitude().equals("NA") && !x.getLatitude().equals("NA"))
//                .filter(cc::compareWithOpenCage)
                .toJavaRDD()
                .map(x -> {
                    GeoHash geoHash = cc.getGeoHash(x);
                    if (geoHash != null) {
                        x.setGeoHash(geoHash.toBase32());
                    }
                    return x;
                });
        // get geohash
        JavaRDD<Weather> weatherJavaRDD = weatherDataset.toJavaRDD().map(x -> {
            x.setGeoHash(cc.getGeoHash(x).toBase32());
            return x;
        });

        weatherDataset = ss.createDataset(weatherJavaRDD.rdd(), Encoders.bean(Weather.class));
        hotelDataset = ss.createDataset(hotelJavaRDD.rdd(), Encoders.bean(Hotel.class));
        // join hotel data to weather data
        Dataset<Tuple2<Weather, Hotel>> result
                = weatherDataset
                .joinWith(hotelDataset, weatherDataset.col("geoHash").equalTo(hotelDataset.col("geoHash")), "left");

        Dataset<Tuple2<Weather, Hotel>> cache = result.cache();
        // save result
        System.out.println(cache.count());
        cache
                .write()
                .mode(SaveMode.Overwrite)
                .parquet("wasbs://data@stmsixwesteurope.blob.core.windows.net/data");
    }
}
