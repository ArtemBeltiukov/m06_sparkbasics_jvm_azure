package hotels.services;


import org.apache.spark.sql.SparkSession;

public class SparkService {

    private final PropertiesService PROPERTIES = new PropertiesService();

    public SparkSession sparkSession() {
        // Create spark session
        SparkSession ss = SparkSession
                .builder()
                .master("k8s://https://test-dns-13510061.hcp.westeurope.azmk8s.io:443")
                .config("spark.kubernetes.container.image", "fuskero/art180:latest")
                .config("spark.executor.memory", "2G")
                .config("spark.executor.cores", "1")
                .config("spark.cores.max", "4")
                .config("spark.submit.deployMode", "cluster")
                .config("spark.shuffle.service.enabled", "false")
                .config("spark.dynamicAllocation.enabled", "false")
                .appName("hotels")
                .getOrCreate();

        ss.sparkContext().hadoopConfiguration().set("fs.azure.account.auth.type." + PROPERTIES.getProperty("azure.path"), PROPERTIES.getProperty("fs.azure.account.auth.type"));
        ss.sparkContext().hadoopConfiguration().set("fs.azure.account.oauth.provider.type." + PROPERTIES.getProperty("azure.path"), PROPERTIES.getProperty("fs.azure.account.oauth.provider.type"));
        ss.sparkContext().hadoopConfiguration().set("fs.azure.account.oauth2.client.id." + PROPERTIES.getProperty("azure.path"), PROPERTIES.getProperty("fs.azure.account.oauth2.client.id"));
        ss.sparkContext().hadoopConfiguration().set("fs.azure.account.oauth2.client.secret." + PROPERTIES.getProperty("azure.path"), PROPERTIES.getProperty("fs.azure.account.oauth2.client.secret"));
        ss.sparkContext().hadoopConfiguration().set("fs.azure.account.oauth2.client.endpoint." + PROPERTIES.getProperty("azure.path"), PROPERTIES.getProperty("fs.azure.account.oauth2.client.endpoint"));
        ss.sparkContext().hadoopConfiguration().set("fs.azure.account.key."+PROPERTIES.getProperty("azure.storageaccount"), PROPERTIES.getProperty("azure.storageaccount.key"));

        return ss;
    }
}
