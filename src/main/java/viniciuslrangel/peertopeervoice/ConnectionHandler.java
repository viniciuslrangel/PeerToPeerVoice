package viniciuslrangel.peertopeervoice;

import javax.swing.*;
import java.io.IOException;
import java.net.*;

/**
 * Created by <viniciuslrangel> on 20 Feb 2019, 3:00 PM (UTC-3).
 */
public class ConnectionHandler {

    private final JFrame parent;

    public ConnectionHandler(JFrame parent) {
        this.parent = parent;
    }

    public ConnectionInfo newConnection() throws IOException {
        InetAddress addr;
        for(;;) {
            try {
                String addrRaw = JOptionPane.showInputDialog(parent, "Partner IP");
                if(addrRaw.isEmpty()) {
                    continue;
                }
                addr = InetAddress.getByName(addrRaw);
                break;
            } catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(null, "Invalid IP");
            }
        }
        if (JOptionPane.showOptionDialog(
                parent,
                "What side is this?",
                "Choose side",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"A", "B"},
                "A"
        ) == JOptionPane.YES_OPTION) {
            return serverConnection(addr);
        } else {
            return clientConnection(addr);
        }
    }

    private ConnectionInfo serverConnection(InetAddress addr) throws IOException {
        DatagramSocket s = null;
        int otherPort = 0;
        try {
            s = new DatagramSocket();
            JOptionPane.showMessageDialog(parent, "Port: " + s.getLocalPort());
            for(;;) {
                try {
                    String otherPortRaw = JOptionPane.showInputDialog(parent, "Other port");
                    otherPort = Integer.parseInt(otherPortRaw);
                    break;
                } catch (NumberFormatException ignored){
                    JOptionPane.showMessageDialog(null, "Invalid Port");
                }
            }
            s.send(new DatagramPacket(new byte[0], 0, addr, otherPort));
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return new ConnectionInfo(s, addr, otherPort);
    }

    private ConnectionInfo clientConnection(InetAddress addr) throws IOException {
        int otherPort;
        for(;;) {
            try {
                String otherPortRaw = JOptionPane.showInputDialog(parent, "Other port");
                otherPort = Integer.parseInt(otherPortRaw);
                break;
            } catch (NumberFormatException ignored){
                JOptionPane.showMessageDialog(null, "Invalid Port");
            }
        }
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
            JOptionPane.showMessageDialog(parent, "Port: " + s.getLocalPort());
            s.send(new DatagramPacket(new byte[0], 0, addr, otherPort));
        } catch(SocketException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return new ConnectionInfo(s, addr, otherPort);
    }

    static class ConnectionInfo {
        final DatagramSocket socket;
        final InetAddress address;
        final int port;
        ConnectionInfo(DatagramSocket socket, InetAddress address, int port) {
            this.socket = socket;
            this.address = address;
            this.port = port;
        }
    }

}
