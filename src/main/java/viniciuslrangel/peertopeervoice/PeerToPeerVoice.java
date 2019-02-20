package viniciuslrangel.peertopeervoice;

import javax.swing.*;

/**
 * Created by <viniciuslrangel> on 20 Feb 2019, 12:53 PM (UTC-3).
 */
public class PeerToPeerVoice {

    public static void main(String... args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch(Exception ignored) {}

        Mixer mixer = Mixer.createDefault();

        new Gui(mixer);
    }

}
