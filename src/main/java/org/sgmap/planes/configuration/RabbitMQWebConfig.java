package org.sgmap.planes.configuration;

import org.sgmap.common.utilities.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.rabbitmq.client.ConnectionFactory;

import javax.net.ssl.SSLSocketFactory;

@Configuration
public class RabbitMQWebConfig {
    @Value("${planes.amqp.web.router.user}")
    public String username;

    @Value("${planes.amqp.web.router.password}")
    public String password;

    @Value("${planes.amqp.web.router.host}")
    public String host;

    @Value("${planes.amqp.web.router.port}")
    public Integer port;

    @Value("${planes.amqp.web.router.useSSL}")
    protected boolean webUseSSL;

    @Value("${planes.amqp.web.router.ssl_ca}")
    protected String caPathWeb;

    @Value("${planes.amqp.web.router.ssl_cert}")
    protected String certPathWeb;

    @Value("${planes.amqp.web.router.ssl_key}")
    protected String keyPathWeb;

    @Value("${planes.amqp.web.router.ssl_keyPassword}")
    protected String keyPWWeb;

    @Value("${planes.amqp.app.max_channel}")
    protected String maxChannel;

    @Bean
    public ConnectionFactory rabbitWebConnectionFactory(){
        Logger logger = LoggerFactory.getLogger(RabbitMQWebConfig.class);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setRequestedChannelMax(Integer.parseInt(maxChannel));

        if (webUseSSL) {
            try {
                SSLSocketFactory socketFactory = null;
                socketFactory = Util.getSocketFactory(caPathWeb, certPathWeb, keyPathWeb, keyPWWeb);
                connectionFactory.setSocketFactory(socketFactory);

            } catch (Exception e) {
                logger.info(e.toString());
                logger.info("unable to setup rabbit mq client with ssl connection");
            }
        }
        return connectionFactory;
    }
}
