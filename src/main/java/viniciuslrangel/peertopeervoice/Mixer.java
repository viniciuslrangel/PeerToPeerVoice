package viniciuslrangel.peertopeervoice;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by <viniciuslrangel> on 20 Feb 2019, 1:22 PM (UTC-3).
 */
class Mixer {

    static Mixer createDefault() {
        return new Mixer(AudioSystem.getMixerInfo());
    }

    private List<Info> sources = new ArrayList<>();
    private List<Info> targets = new ArrayList<>();

    private Mixer(javax.sound.sampled.Mixer.Info[] infoList) {
        for (javax.sound.sampled.Mixer.Info info : infoList) {
            javax.sound.sampled.Mixer mixer = AudioSystem.getMixer(info);

            Line.Info[] source = mixer.getSourceLineInfo();
            if (source.length > 0) {
                sources.add(new Info(mixer, source[0]));
            }

            Line.Info[] target = mixer.getTargetLineInfo();
            if (target.length > 0) {
                targets.add(new Info(mixer, target[0]));
            }

        }
    }

    String[] getInputNameList() {
        return getStrings(targets);
    }

    String[] getOutputNameList() {
        return getStrings(sources);
    }

    private String[] getStrings(List<Info> targets) {
        String[] names = new String[targets.size()];
        for (int i = 0, targetSize = targets.size(); i < targetSize; i++) {
            Info target = targets.get(i);
            names[i] = target.mixer.getMixerInfo().getName();
        }
        return names;
    }

    public static class Info {
        private final javax.sound.sampled.Mixer mixer;
        private final Line.Info line;

        Info(javax.sound.sampled.Mixer mixer, Line.Info line) {
            this.mixer = mixer;
            this.line = line;
        }

        public javax.sound.sampled.Mixer getMixer() {
            return mixer;
        }

        public Line.Info getLine() {
            return line;
        }
    }

}
