package viniciuslrangel.peertopeervoice;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Created by <viniciuslrangel> on 20 Feb 2019, 2:31 PM (UTC-3).
 */
public class Gui extends JFrame {

    private static AudioFormat AUDIO_FORMAT = new AudioFormat(8000.0f, 16, 1, true, true);
    private static int BUFFER_SIZE = 512;

    private JComboBox<String> inputBox;
    private JComboBox<String> outputBox;
    private JButton connectButton;
    private JPanel contentPane;
    private JLabel statusLbl;

    private final Mixer mixer;
    private final ConnectionHandler con;

    private volatile ConnectionHandler.ConnectionInfo info;

    private volatile TargetDataLine targetLine;
    private volatile SourceDataLine audioOutputStream;

    private Thread inputThread;
    private Thread outputThread;

    Gui(Mixer mixer) {
        this.mixer = mixer;
        this.con = new ConnectionHandler(this);

        DefaultComboBoxModel<String> inputModel = new DefaultComboBoxModel<>(mixer.getInputNameList());
        inputBox.setModel(inputModel);

        DefaultComboBoxModel<String> outputModel = new DefaultComboBoxModel<>(mixer.getOutputNameList());
        outputBox.setModel(outputModel);

        setTitle("Voice P2P - viniciuslrangel");
        setContentPane(contentPane);
        setMinimumSize(new Dimension(320, 160));
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        connectButton.addActionListener(e -> {
            if (this.info != null) {
                info = null;
                info.socket.close();
                statusLbl.setText("Disconnected");
                connectButton.setText("Connect");
                return;
            }
            try {
                ConnectionHandler.ConnectionInfo info = this.con.newConnection();
                if (info == null || info.socket == null) {
                    return;
                }
                this.info = info;
                statusLbl.setText("Connected");
                connectButton.setText("Disconnect");
                setupAudio();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Connection error\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                statusLbl.setText("Connection error");
                ex.printStackTrace();
            }
        });
        inputThread = new Thread(this::inputLoop, "Input loop");
        inputThread.start();
        outputThread = new Thread(this::outputLoop, "Output loop");
        outputThread.start();
    }

    private void inputLoop() {
        TargetDataLine currentLine = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            if (currentLine != targetLine) {
                if (currentLine != null) {
                    currentLine.stop();
                    currentLine.close();
                }
                currentLine = targetLine;
                if(currentLine != null) {
                    try {
                        currentLine.open(AUDIO_FORMAT);
                        currentLine.start();
                    } catch (LineUnavailableException e) {
                        currentLine = null;
                        e.printStackTrace();
                    }
                }
            }
            if (currentLine == null || info == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    int total = currentLine.read(buffer, 0, buffer.length);
                    info.socket.send(new DatagramPacket(buffer, 0, total, info.address, info.port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void outputLoop() {
        SourceDataLine currentOutput = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            if (currentOutput != audioOutputStream) {
                if (currentOutput != null) {
                    currentOutput.drain();
                    currentOutput.close();
                }
                currentOutput = audioOutputStream;
                if(currentOutput != null) {
                    try {
                        currentOutput.open(AUDIO_FORMAT);
                        currentOutput.start();
                    } catch (LineUnavailableException e) {
                        currentOutput = null;
                        e.printStackTrace();
                    }
                }
            }
            if (currentOutput == null || info == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    info.socket.receive(packet);
                    currentOutput.write(packet.getData(), 0, packet.getLength());
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void setupInput() {

        Line.Info input;

        Object selectedItem = inputBox.getSelectedItem();
        if (selectedItem instanceof String) {
            String inputSelectedName = (String) selectedItem;
            input = mixer.getInputByName(inputSelectedName);
        } else {
            JOptionPane.showMessageDialog(this, "Select valid input");
            return;
        }

        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(input);
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, "Could not open input audio stream");
            e.printStackTrace();
        }
    }

    private void setupOutput() {

        Line.Info output;

        Object selectedItem = outputBox.getSelectedItem();
        if (selectedItem instanceof String) {
            String outputSelectedName = (String) selectedItem;
            output = mixer.getOutputByName(outputSelectedName);
        } else {
            JOptionPane.showMessageDialog(this, "Select valid output");
            return;
        }

        try {
            audioOutputStream = (SourceDataLine) AudioSystem.getLine(output);
        } catch (LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, "Could not open output audio stream");
            e.printStackTrace();
        }
    }

    private void setupAudio() {
        setupInput();
        setupOutput();
    }


}
