package jmri.jmrix.sprog.serialdriver;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import jmri.jmrix.sprog.SprogConstants.SprogMode;
import jmri.jmrix.sprog.SprogPortController;
import jmri.jmrix.sprog.SprogSystemConnectionMemo;
import jmri.jmrix.sprog.SprogTrafficController;
import jmri.jmrix.sprog.update.SprogType;

/**
 * Implements SerialPortAdapter for the Sprog system.
 * <p>
 * This connects an Sprog command station via a serial com port. Also used for
 * the USB SPROG, which appears to the computer as a serial port.
 * <p>
 * The current implementation only handles the 9,600 baud rate, and does not use
 * any other options at configuration time.
 *
 * Updated January 2010 for gnu io (RXTX) - Andrew Berridge.
 *
 * @author Bob Jacobsen Copyright (C) 2001, 2002
 */
public class SerialDriverAdapter extends SprogPortController {

    public SerialDriverAdapter() {
        super(new SprogSystemConnectionMemo(SprogMode.SERVICE));
        // Set the username to match name, once refactored to handle multiple connections or user setable names/prefixes then this can be removed
        this.baudRate = 9600;
        this.getSystemConnectionMemo().setUserName(Bundle.getMessage("SprogProgrammerTitle"));
        // create the traffic controller
        this.getSystemConnectionMemo().setSprogTrafficController(new SprogTrafficController(this.getSystemConnectionMemo()));
    }

    public SerialDriverAdapter(SprogMode sm) {
        super(new SprogSystemConnectionMemo(sm));
        this.baudRate = 9600;
        this.getSystemConnectionMemo().setUserName("SPROG");
        // create the traffic controller
        this.getSystemConnectionMemo().setSprogTrafficController(new SprogTrafficController(this.getSystemConnectionMemo()));
    }

    public SerialDriverAdapter(SprogMode sm, int baud, SprogType type) {
        super(new SprogSystemConnectionMemo(sm, type));
        this.baudRate = baud;
        this.getSystemConnectionMemo().setUserName("SPROG");
        // create the traffic controller
        this.getSystemConnectionMemo().setSprogTrafficController(new SprogTrafficController(this.getSystemConnectionMemo()));
    }

    public SerialDriverAdapter(SprogMode sm, int baud) {
        super(new SprogSystemConnectionMemo(sm));
        this.baudRate = baud;
        this.getSystemConnectionMemo().setUserName("SPROG");
        // create the traffic controller
        this.getSystemConnectionMemo().setSprogTrafficController(new SprogTrafficController(this.getSystemConnectionMemo()));
    }

    private int baudRate = -1;

    @Override
    public String openPort(String portName, String appName) {
        // get and open the primary port
        activeSerialPort = activatePort(portName, log);
        if (activeSerialPort == null) {
            log.error("failed to connect SPROG to {}", portName);
            return Bundle.getMessage("SerialPortNotFound", portName);
        }
        log.info("Connecting SPROG to {} {}", portName, activeSerialPort);

        // try to set it for communication via SerialDriver
        setBaudRate(activeSerialPort, baudRate);
        configureLeads(activeSerialPort, true, true);
        setFlowControl(activeSerialPort, FlowControl.NONE);

        // get and save stream
        serialStream = new DataInputStream(activeSerialPort.getInputStream());
        log.trace("SerialDriverAdapter serialStream: {}", serialStream);

        // purge contents, if any
        purgeStream(serialStream);

        // add Sprog Traffic Controller as event listener
        activeSerialPort.addDataListener( new com.fazecast.jSerialComm.SerialPortDataListener() {
            @Override 
            public int getListeningEvents() {
                log.trace("getListeningEvents");
                return com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }
            @Override
            public void serialEvent(com.fazecast.jSerialComm.SerialPortEvent event) {
                log.trace("serial event start");
                // invoke
                getSystemConnectionMemo().getSprogTrafficController().handleOneIncomingReply();
                log.trace("serial event end");
            }
        }
        );

        // report status?
        if (log.isInfoEnabled()) {
            log.info("{} port opened at {} baud, sees  DTR: {} RTS: {} DSR: {} CTS: {}  name: {}", 
                    portName, activeSerialPort.getBaudRate(), activeSerialPort.getDTR(), 
                    activeSerialPort.getRTS(), activeSerialPort.getDSR(), activeSerialPort.getCTS(), 
                    activeSerialPort);
        }

        opened = true;
        return null; // indicates OK return

    }

    /**
     * Set the flow control. This method hide the 
     * actual serial port behind this object
     * @param flow Set flow control to RTS/CTS when true
     */
    public void setHandshake(FlowControl flow) {
        setFlowControl(activeSerialPort, flow);
    }

    // base class methods for the SprogPortController interface
    @Override
    public DataInputStream getInputStream() {
        
        if (!opened) {
            log.error("getInputStream called before load(), stream not available", new Exception("traceback"));
            return null;
        }
        return serialStream;
    }

    @Override
    public DataOutputStream getOutputStream() {
        if (!opened) {
            log.error("getOutputStream called before load(), stream not available", new Exception("traceback"));
        }
        return new DataOutputStream(activeSerialPort.getOutputStream());
    }

    /**
     * {@inheritDoc}
     * Currently only 9,600 bps
     */
    @Override
    public String[] validBaudRates() {
        return new String[]{"9,600 bps"};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] validBaudNumbers() {
        return new int[]{9600};
    }

    @Override
    public int defaultBaudIndex() {
        return 0;
    }

    DataInputStream serialStream = null;

    protected int numSlots = 1;

    /**
     * Set up all of the other objects to operate with an Sprog command station
     * connected to this port.
     */
    @Override
    public void configure() {
        // connect to the traffic controller
        this.getSystemConnectionMemo().getSprogTrafficController().connectPort(this);

        log.debug("Configure command station");
        this.getSystemConnectionMemo().configureCommandStation(numSlots, getOptionState("TrackPowerState"));
        this.getSystemConnectionMemo().configureManagers();

        if (getOptionState("TrackPowerState") != null && getOptionState("TrackPowerState").equals(Bundle.getMessage("PowerStateOn"))) {
            try {
                this.getSystemConnectionMemo().getPowerManager().setPower(jmri.PowerManager.ON);
            } catch (jmri.JmriException e) {
                log.error("Error setting power on {}", e.toString());
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SerialDriverAdapter.class);

}
