package viniciuslrangel.peertopeervoice;

import javax.swing.*;

/**
 * Created by <viniciuslrangel> on 20 Feb 2019, 12:53 PM (UTC-3).
 */
public class PeerToPeerVoice {

    public static void main(String... args) {

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch(Exception ignored) {}

        Mixer mixer = Mixer.createDefault();

        new Gui(mixer);
    }

}
