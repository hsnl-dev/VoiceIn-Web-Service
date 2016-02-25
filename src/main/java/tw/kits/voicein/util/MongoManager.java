package tw.kits.voicein.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MongoManager {

    private static MongoManager instance;
    public final static String DB_NAME = "voicein";
    public final static String DB_URI = "mongodb://hsnl-dev:hsnl33564hsnl33564@ds013908.mongolab.com:13908/voicein";
    public final static String MAPPING_PACKAGE = "tw.kits.voicein.model";

    private final Morphia morphia = new Morphia();

    private String mongoUri;
    private MongoClient mongo;

    private MongoManager() {
        morphia.mapPackage(MAPPING_PACKAGE);
        MongoClientURI uri = new MongoClientURI(DB_URI);
        mongo = new MongoClient(uri);
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public MongoClient getClient() {
        return mongo;
    }

    public Datastore getDs() {
        return morphia.createDatastore(mongo, DB_NAME);
    }

    //singleton
    public static MongoManager getInstatnce() {
        if (instance == null) {
            synchronized (MongoManager.class) {
                if (instance == null) {
                    instance = new MongoManager();
                }
            }
        }
        return instance;
    }
}
