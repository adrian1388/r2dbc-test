package com.digivisor.r2dbctest;

import java.net.URI;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.util.SerialParameters;

//import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
//import io.r2dbc.postgresql.PostgresqlConnectionFactory;
//import io.r2dbc.spi.Connection;
//import io.r2dbc.spi.ConnectionFactory;
//import reactor.core.publisher.Mono;

@Component
public class TestService {

    protected final Log logger = LogFactory.getLog(getClass());
    
//    Publisher<? extends Connection> connectionPublisher;
//    private final Mono<Connection> connectionMono;

	public TestService(
//
//        @Value("${spring.datasource.url}")
//        String dbUrlString,
//
//        @Value("${spring.datasource.username}")
//        String dbUser,
//
//        @Value("${spring.datasource.password}")
//        String dbPassword
    ) {
//        URI dbUrl = URI.create(dbUrlString.substring(5));
//
//		ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
//        	    .host(dbUrl.getHost())
////        	    .port(5432).  // optional, defaults to 5432
//        	    .username(dbUser)
//        	    .password(dbPassword)
//        	    .database(dbUrl.getPath().substring(1))  // optional
//        	    .build());
//
//        this.connectionPublisher = connectionFactory.create();
//
//        // Alternative: Creating a Mono using Project Reactor
//        this.connectionMono = Mono.from(this.connectionPublisher);
        super();
	}
    

    @Scheduled(fixedDelay = 1000 * 10)
    public void testModbus() {
    	logger.info("Starting Serial");
    	SerialPort[] ports = SerialPort.getCommPorts();
    	for (SerialPort port: ports) {
    	    logger.info(port.getSystemPortName());
    	}
        // Create master
        SerialParameters parameters = new SerialParameters();
        parameters.setPortName("cu.Bluetooth-Incoming-Port");
        parameters.setOpenDelay(1000);
        parameters.setEncoding(Modbus.SERIAL_ENCODING_RTU);
        
        
    	ModbusSerialMaster master;
    	try {
    	    // master = new ModbusTCPMaster(<address>);  // Uses port 502 and a timeout of 3000ms
    	    // master = new ModbusTCPMaster(<address>, <port>); // Uses a timeout of 3000ms
    	    master = new ModbusSerialMaster(parameters, 1000);
    	    master.connect();
        	logger.info("Master: " + master);
    	}
    	catch (Exception e) {
    	    logger.error("Cannot connect to slave - %s", e);
    	}
    }
}
