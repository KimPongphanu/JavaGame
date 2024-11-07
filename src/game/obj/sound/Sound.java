package game.obj.sound;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {

    private Clip clip; 
    private final URL shoot;
    private final URL hit;
    private final URL destroy;
//    private final URL bgsong1;

    public Sound() {
        this.shoot = this.getClass().getClassLoader().getResource("game/obj/sound/shoot.wav");
        this.hit = this.getClass().getClassLoader().getResource("game/obj/sound/hit.wav");
        this.destroy = this.getClass().getClassLoader().getResource("game/obj/sound/destroy.wav");
//        this.bgsong1 = this.getClass().getClassLoader().getResource("game/obj/sound/song1.wav");
    }
    
    public void soundShoot() {
        play(shoot);
    }

    public void soundHit() {
        play(hit);
    }

    public void soundDestroy() {
        play(destroy);
    }

//    public void soundSong1() {
//        play(bgsong1);
//    }

    public void play(URL url) {
        try {
            if (clip != null && clip.isOpen()) {
                clip.close(); // ปิด clip ก่อนที่จะเล่นใหม่
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip(); // กำหนดค่า clip ระดับคลาส
            clip.open(audioIn);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });
            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            System.err.println(e);
        }
    }
}
