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

import java.util.Map;

public class Main {

    private final static PropertiesService PROPERTIES = new PropertiesService();

    public static void main(String[] args) {
        // Initializers
        SparkService sparkService = new SparkService();
        CoordChecker cc = new CoordChecker();
        SparkSession ss = sparkService.sparkSession();

        Dataset<Tuple2<Weather, Hotel>> filter = ss.read().parquet("/module6/result").as(Encoders.tuple(Encoders.bean(Weather.class), Encoders.bean(Hotel.class)))
                .filter((FilterFunction<Tuple2<Weather, Hotel>>) x -> x._2() != null);

        Dataset<Row> avg = filter.groupBy(filter.col("_1.year"), filter.col("_1.month"), filter.col("_1.day"), filter.col("_2.name"), filter.col("_2.geoHash")).avg("_1.avg_tmpr_f", "_1.avg_tmpr_c", "_1.lng", "_1.lat").orderBy(filter.col("_1.year"), filter.col("_1.month"), filter.col("_1.day"));
        avg.show(300, false);
        System.out.println(avg.count());

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
                .filter(cc::compareWithOpenCage)
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
        Dataset<Row> result
                = weatherDataset
                .joinWith(hotelDataset, weatherDataset.col("geoHash").equalTo(hotelDataset.col("geoHash")), "left")
                .filter((FilterFunction<Tuple2<Weather, Hotel>>) x -> x._2() != null)
                .groupBy(
                        filter.col("_1.year"),
                        filter.col("_1.month"),
                        filter.col("_1.day"),
                        filter.col("_2.name"),
                        filter.col("_2.geoHash"))
                .avg("_1.avg_tmpr_f", "_1.avg_tmpr_c", "_1.lng", "_1.lat")
                .orderBy(filter.col("_1.year"), filter.col("_1.month"), filter.col("_1.day"));

        // save result
        result
                .coalesce(1)
                .write()
                .mode(SaveMode.Overwrite)
                .option("header", "true")
                .parquet(PROPERTIES.getProperty("azure.saveFolder"));

    }
}
