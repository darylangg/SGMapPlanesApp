package org.sgmap.planes.bean;

import org.connectivity.common.protobuf.ConnectivityProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectivityBean {
    private Logger log = LoggerFactory.getLogger(ConnectivityBean.class);
    private static ConnectivityBean instance = null;

    private long lastPing = 0;

    public synchronized static ConnectivityBean getInstance() {
        if (instance == null) {
            instance = new ConnectivityBean();
        }
        return instance;
    }

    public void ping(){
        lastPing = System.currentTimeMillis();
    }

    public byte[] getLatestConn(){
        ConnectivityProto.SystemConnectivity latestConn;
        boolean connected = System.currentTimeMillis() - lastPing <= 60000;
        ConnectivityProto.SystemConnectivity.Base.Connectivity conn =  connected ? ConnectivityProto.SystemConnectivity.Base.Connectivity.UP : ConnectivityProto.SystemConnectivity.Base.Connectivity.DOWN;

        latestConn = ConnectivityProto.SystemConnectivity.newBuilder()
                .setCommon(ConnectivityProto.SystemConnectivity.Common.newBuilder()
                        .setMessageId("sgmap-planes")
                        .setSourceId("edge-ing")
                        .setDestId("app")
                        .setTimestamp(System.currentTimeMillis())
                        .build())
                .setBase(ConnectivityProto.SystemConnectivity.Base.newBuilder()
                        .setConnectivity(conn)
                        .build())
                .build();
        return latestConn.toByteArray();
    }

    public boolean pingExpired(){
        return (System.currentTimeMillis() - lastPing > 60000);
    }
}
