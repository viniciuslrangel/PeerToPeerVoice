package viniciuslrangel.peertopeervoice;

import javax.swing.*;
import java.awt.*;

/**
 * Created by <viniciuslrangel> on 20 Feb 2019, 2:31 PM (UTC-3).
 */
public class Gui extends JFrame {
    private JComboBox<String> inputBox;
    private JComboBox<String> outputBox;
    private JButton connectButton;
    private JPanel contentPane;

    private final Mixer mixer;

    private final ConnectionHandler con;

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

        });

    }


}
