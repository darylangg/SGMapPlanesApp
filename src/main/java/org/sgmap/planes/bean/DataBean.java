package org.sgmap.planes.bean;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.camel.Exchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.sgmap.common.protobuf.GeoJSONProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataBean {
    private Logger log = LoggerFactory.getLogger(DataBean.class);
    private static DataBean instance = null;
    private static boolean poll = true;

    public synchronized static DataBean getInstance() {
        if (instance == null) {
            instance = new DataBean();
        }
        return instance;
    }

    public byte[] unpackData(Exchange e) throws JSONException, InvalidProtocolBufferException {
        HashMap<String, Object> incBody = (HashMap<String, Object>) e.getMessage().getBody();
        JSONArray ret = new JSONArray();
        ArrayList<GeoJSONProto.Feature> features = new ArrayList<>();

        for (Map.Entry<String, Object> set : incBody.entrySet()){
            if (!set.getKey().equals("full_count") && !set.getKey().equals("version") && !set.getKey().equals("stats")){
                ArrayList<Object> planeData = (ArrayList<Object>) set.getValue();

                double latitude = (double) planeData.get(1);
                double longitude = (double) planeData.get(2);
                Integer track = (Integer) planeData.get(3);
                Integer altitude = (Integer) planeData.get(4);
                Integer speed = (Integer) planeData.get(5);
                Integer timestamp = (Integer) planeData.get(10);
                String planeID = (String) planeData.get(16);
                String airline = (String) planeData.get(18);

                GeoJSONProto.Feature newFeature = GeoJSONProto.Feature.newBuilder()
                        .putProperties("planeID", GeoJSONProto.Value.newBuilder()
                                .setStringValue(planeID)
                                .build())
                        .putProperties("airline", GeoJSONProto.Value.newBuilder()
                                .setStringValue(airline)
                                .build())
                        .putProperties("altitude", GeoJSONProto.Value.newBuilder()
                                .setUintValue(altitude)
                                .build())
                        .putProperties("speed", GeoJSONProto.Value.newBuilder()
                                .setUintValue(speed)
                                .build())
                        .putProperties("bearing", GeoJSONProto.Value.newBuilder()
                                .setUintValue(track)
                                .build())
                        .putProperties("timestamp", GeoJSONProto.Value.newBuilder()
                                .setUintValue(timestamp)
                                .build())
                        .setType(GeoJSONProto.GeomType.POINT)
                        .addGeometry(latitude)
                        .addGeometry(longitude)
                        .build();

                features.add(newFeature);
            }
        }

        GeoJSONProto.FeatureCollection newCollection = GeoJSONProto.FeatureCollection.newBuilder()
                .addAllFeatures(features)
                .build();

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String ISOStringDate = dateFormat.format(date);

        GeoJSONProto.Data retProto = GeoJSONProto.Data.newBuilder()
                .setCommon(GeoJSONProto.Data.Common.newBuilder()
                        .setMessageId("SGMapPlanes")
                        .setSourceId("Web")
                        .setDestId("App")
                        .setTimestamp(ISOStringDate)
                        .build())
                .setBase(GeoJSONProto.Data.Base.newBuilder()
                        .setFeatureCollection(newCollection))
                .build();

        return retProto.toByteArray();
    }
}
