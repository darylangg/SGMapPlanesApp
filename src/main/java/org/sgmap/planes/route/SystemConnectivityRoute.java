package org.sgmap.planes.route;

import org.apache.camel.builder.RouteBuilder;
import org.connectivity.common.protobuf.ConnectivityProto;
import org.sgmap.planes.bean.ConnectivityBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemConnectivityRoute extends RouteBuilder {
    @Value("${planes.connectivity.system.exchange}")
    private String connectivityExchange;

    @Value("${planes.connectivity.system.queue}")
    private String connectivityQueue;

    @Value("${planes.connectivity.interval.milli}")
    private String connectMilli;

    @Override
    public void configure() throws Exception {
        from("rabbitmq:"+connectivityExchange+"?queue="+connectivityQueue+"&exclusive=true&connectionFactory=#rabbitWebConnectionFactory")
            .routeId("System Connectivity Route")
                .bean(ConnectivityBean.getInstance(), "ping")
            .process(e->{
                byte[] data = e.getMessage().getBody(byte[].class);
                ConnectivityProto.SystemConnectivity inc = ConnectivityProto.SystemConnectivity.parseFrom(data);
                ConnectivityProto.SystemConnectivity.Common common = inc.getCommon()
                        .toBuilder()
                        .setSourceId("app")
                        .setDestId("all")
                        .build();
                ConnectivityProto.SystemConnectivity out = inc.toBuilder()
                        .setCommon(common)
                        .build();
                e.getMessage().setBody(out.toByteArray());
                System.out.println(out);
            })
            .to("rabbitmq:"+connectivityExchange+".app?exchangeType=fanout&skipQueueDeclare=true&skipQueueBind=true&autoDelete=true&connectionFactory=#rabbitAppConnectionFactory");

        from("timer://connectivityTimer?period="+connectMilli+"&fixedRate=true")
                .routeId("Web Connectivity Check Route")
                .choice().when(method(ConnectivityBean.getInstance(), "pingExpired"))
                .log("Disconnected from system")
                .process(e->{
                    ConnectivityProto.SystemConnectivity out = ConnectivityProto.SystemConnectivity.newBuilder()
                            .setBase(ConnectivityProto.SystemConnectivity.Base.newBuilder()
                                    .setConnectivity(ConnectivityProto.SystemConnectivity.Base.Connectivity.DISCONNECTED_FR_DMZ)
                                    .build())
                            .setCommon(ConnectivityProto.SystemConnectivity.Common.newBuilder()
                                    .setMessageId("sgmap-planes")
                                    .setSourceId("app")
                                    .setDestId("all")
                                    .setTimestamp(System.currentTimeMillis())
                                    .build())
                            .build();
                    e.getMessage().setBody(out.toByteArray());
                    System.out.println(out);
                })
                .to("rabbitmq:"+connectivityExchange+".app?exchangeType=fanout&skipQueueDeclare=true&skipQueueBind=true&autoDelete=true&connectionFactory=#rabbitAppConnectionFactory");

    }
}
