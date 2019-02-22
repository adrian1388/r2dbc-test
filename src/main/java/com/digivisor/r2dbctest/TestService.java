package com.digivisor.r2dbctest;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister;
import com.ghgande.j2mod.modbus.slave.ModbusSlave;
import com.ghgande.j2mod.modbus.slave.ModbusSlaveFactory;
import com.ghgande.j2mod.modbus.util.SerialParameters;

//import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
//import io.r2dbc.postgresql.PostgresqlConnectionFactory;
//import io.r2dbc.spi.Connection;
//import io.r2dbc.spi.ConnectionFactory;
//import reactor.core.publisher.Mono;

@Component
@RestController
public class TestService {

    protected final Log logger = LogFactory.getLog(getClass());
    
    protected static ModbusSerialMaster master;

    protected static ModbusSlave slave;
    
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
    

    @Scheduled(fixedDelay = 1000 * 60 * 100)
    public void testModbus() {
    	logger.info("Starting Serial");
    	SerialPort[] ports = SerialPort.getCommPorts();
    	for (SerialPort port: ports) {
    	    logger.info(port.getSystemPortName());
    	}
    	
    	
    	
        // Create master
        SerialParameters parameters = new SerialParameters();
        parameters.setPortName("\\COM6");// For Windows: "\\COM6" // For Linux: "/dev/ttyUSB0"
        parameters.setBaudRate(9600);
        parameters.setDatabits(8);
        parameters.setParity(AbstractSerialConnection.NO_PARITY);
        parameters.setStopbits(AbstractSerialConnection.ONE_STOP_BIT);
        parameters.setEncoding(Modbus.SERIAL_ENCODING_RTU);
        parameters.setEcho(false);
//        parameters.setOpenDelay(1000);
        
        
//    	ModbusSerialMaster master = null;
    	try {
    	    master = new ModbusSerialMaster(parameters, 0);
//    	    slave = ModbusSlaveFactory.createSerialSlave(parameters);
    	    
    	    
    	    master.connect();
//    	    slave.open();
        	logger.info("MASTER: " + master);
//        	logger.info("SLAVE: " + slave);
    	    logger.info("PARAMETERS: " + parameters);
    	}
    	catch (Exception e) {
    	    logger.info("PARAMETERS: " + parameters);
    	    logger.error("Cannot connect to slave - %s", e);
    	    

//            if (slave != null) {
//                slave.close();
//            }
    	}
    	
    	
    	AbstractSerialConnection con = master.getConnection();
        System.out.println(String.format(
            "connected to %s: %s at %5d baud, %2d data bits, %2d stop bits\n available ports:%s",
            con.getDescriptivePortName(), parameters.getPortName(), con.getBaudRate(),
            con.getNumDataBits(), con.getNumStopBits(),
            con.getCommPorts().toString()));
        master.setRetries(0);
    }
    
    

    @GetMapping(value = "readregisters")
    public String readReg(
    	@RequestParam("unitId") int unitId,
    	@RequestParam("ref") int ref,
    	@RequestParam("count") int count
    ) {

    	String output = "";
    	Register[] slaveResponse = new Register[2];
    	try {
//    		System.out.println("TRY 1");
    		slaveResponse = master.readMultipleRegisters(unitId, ref, count);
//            logger.info("Failed to read multiple input register 1 length 5, 45,   : " + slaveResponse[0].getValue());
//            logger.info("Failed to read multiple input register 2 length 5, 9999, : " + slaveResponse[1].getValue());
    		
    		for (int i = 0; i < slaveResponse.length - 1; i++) {
    			System.out.println("reg" + i + " = " + slaveResponse[i]);
    			System.out.println("reg" + (i+1) + " = " + slaveResponse[i+1]);
    			
//    			ByteBuffer buffer = ByteBuffer.wrap(slaveResponse[i].toBytes());
    			System.out.println("SUMA " + i + " = " + (slaveResponse[i].getValue() + slaveResponse[i+1].getValue()));
//    			float second = buffer.getFloat();
    			System.out.println("ToFLOAT: " + toFloat(slaveResponse[i].toBytes(), slaveResponse[i+1].toBytes()));
//    			System.out.println("second: " + second);
//    			System.out.println("TO bytes size 1: " + slaveResponse[i].toBytes().length);
//    			System.out.println("TO bytes size 2: " + slaveResponse[i+1].toBytes().length);
    			if (unitId == 10) {
    				output += "reg[" + i + "] = " + toFloat(slaveResponse[i].toBytes(), slaveResponse[i+1].toBytes()) + "<br/>";
    			} else {
    				output += "reg[" + i + "] = " + slaveResponse[i] + "<br/>";
    			}
    			i++;
    		}
            
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			logger.error("ERROR READING REGISTER: " + e);
			e.printStackTrace();
		}

    	return output;
    }
    
    
    public static short twoBytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    public static float toFloat(byte[] b1, byte[] b2) {
    	int asInt = (b2[1] & 0xFF) 
                | ((b2[0] & 0xFF) << 8) 
                | ((b1[1] & 0xFF) << 16) 
                | ((b1[0] & 0xFF) << 24);
    	System.out.println("INT: " + asInt);
        return Float.intBitsToFloat(asInt);
    }

    @GetMapping(value = "registers")
    public String writeReg(
    	@RequestParam("unitId") int unitId,
    	@RequestParam("ref") int ref,
    	@RequestParam("count") int count
    ) {

    	int slaveResponse = 0;
    	try {
//    		int before = master.readInputRegisters(unitId, ref, count)[0].getValue();
//			System.out.println("BEFORE: " + before);
//            int newValue = 31;

    		slaveResponse = master.writeSingleRegister(unitId, ref, new SimpleInputRegister(count));
//    				readMultipleRegisters(unitId, ref, count);
//            logger.info("Failed to read multiple input register 1 length 5, 45,   : " + slaveResponse[0].getValue());
//            logger.info("Failed to read multiple input register 2 length 5, 9999, : " + slaveResponse[1].getValue());
//    		int after = master.readInputRegisters(unitId, ref, count)[0].getValue();
			System.out.println("slaveResponse: " + slaveResponse);
//			master.writeSingleRegister(unitId, 1, new SimpleInputRegister(before));
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			logger.error("ERROR WRITING REGISTER: " + e);
			e.printStackTrace();
		}

    	return "" + slaveResponse;
    }
}
