package io.github.vincemann.springrapid.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@TestConfiguration
@Slf4j
public class H2ServerConfiguration {



    // TCP port for remote connections, default 9092
    @Value("${h2.tcp.port:9092}")
    private String h2TcpPort;


    // Web port, default 8082
    @Value("${h2.web.port:8082}")
    private String h2WebPort;


    /**
     * TCP connection to connect with SQL clients to the embedded h2 database.
     * <p>
     * <p>
     * <p>
     * Connect to "jdbc:h2:tcp://localhost:9092/mem:testdb", username "sa", password: password.
     */

    @Bean
    @ConditionalOnExpression("${h2.tcp.enabled:false}")
    public Server h2TcpServer() throws SQLException {
        try {
            return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", h2TcpPort).start();
        }catch (Exception e){
            log.warn("Could not start tcp H2 Server",e);
            return null;
        }
    }


    /**
     * Web console for the embedded h2 database.
     * <p>
     * <p>
     * <p>
     * Go to http://localhost:8082 and connect to the database "jdbc:h2:mem:testdb", username "sa", password empty.
     */

    @Bean
    @ConditionalOnExpression("${h2.web.enabled:true}")
    public Server h2WebServer() throws SQLException {
        try {
            return Server.createWebServer("-web", "-webAllowOthers", "-webPort", h2WebPort).start();
        }catch (Exception e){
            log.warn("Could not start web H2 Server",e);
            return null;
        }
    }

}