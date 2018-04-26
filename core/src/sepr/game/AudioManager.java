package sepr.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;


/**
 * Usage -- Audio.get('path to file', Sound.class).play(AudioManager.GlobalFXvolume) // this will play the sound
 */


public class AudioManager extends AssetManager {

    public static float GlobalFXvolume = 1; //Global volume for the sound between 0 and 1
    public static float GlobalMusicVolume = 1; //Global volume for the music between 0 and 1
    private static ArrayList<String> currentPlayingMusic = new ArrayList<String>(); //list of playing music
    private static AudioManager instance = null; // set initial instance to be null

    /**
     * AudioManager is a singleton class that instantiated using getInstance therefore only one instance of a class is allowed at a time
     */

    protected AudioManager() {
        // Exists only to defeat instantiation.
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }


    /**
     * loads the music file specified by the filepath into memory and plays it and sets to looping
     *
     * @param filePath the filepath of the location of the sound
     */

    public void loadMusic(String filePath) {

        this.load(filePath, Music.class);
        this.finishLoading();
        this.get(filePath, Music.class).play(); //plays the music
        currentPlayingMusic.add(filePath);
        this.get(filePath, Music.class).setVolume(AudioManager.GlobalMusicVolume);
        this.get(filePath, Music.class).setLooping(true); //sets looping

    }


    /**
     * loads all the sound files that are used during game play so they can be played at anytime
     */


    public void loadSounds() {

        this.load("sound/Other/click.mp3", Sound.class);

        this.load("sound/Allocation/Colin_Insuffiecient_Gangmembers.wav", Sound.class);
        this.load("sound/Allocation/Colin_EmptySet.wav", Sound.class);
        this.load("sound/Allocation/Colin_Might_I_interest_you_in_taking_the_union_of_our_forces.wav", Sound.class);


        this.load("sound/Battle Phrases/Colin_An_Unlikely_Victory.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Far_better_than_I_expected.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_I_couldnt_have_done_it_better_myself.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Multiplying_by_the_identity_matrix_is_more_fasinating_than_your_last_move.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Seems_Risky_To_Me.wav", Sound.class);
        this.load("sound/Battle Phrases/Colin_Well_Done.wav", Sound.class);

        this.load("sound/Invalid Move/Colin_Your_request_does_not_pass_easily_through_my_mind.wav", Sound.class);
        this.load("sound/Invalid Move/Colin_You_would_find_more_success_trying_to_invert_a_singular_matrix.wav", Sound.class);
        this.load("sound/Invalid Move/Colin_Your_actions_are_questionable.wav", Sound.class);

        this.load("sound/Minigame/Colin_That_was_a_poor_performance.wav", Sound.class);

        this.load("sound/PVC/Colin_Just_remember_the_PVC_is_not_unlike_the_empty_string.wav", Sound.class);
        this.load("sound/PVC/Colin_The_PVC_has_been_captured.wav", Sound.class);
        this.load("sound/PVC/Colin_You_have_captured_the_PVC.wav", Sound.class);


        this.load("sound/Timer/Colin_Im_afraid_this_may_be_a_matter_for_another_time.wav", Sound.class);
        this.load("sound/Timer/Colin_Sorry_One_could_find_the_inverse_of_a_3x3_matrix_in_ a_shorter_amount_of_time.wav", Sound.class);

        this.load("sound/Victory/Colin_Congratulations.wav", Sound.class);
        this.load("sound/Victory/Colin_Congratulations_your_grandson_would_be_proud_of_you.wav", Sound.class);
        this.load("sound/Victory/Colin_Well_Done.wav", Sound.class);
        this.load("sound/Victory/Colin_You_are_victorious.wav", Sound.class);

        this.finishLoading();
    }


    /**
     * remove a sound by memory according to its filepath
     */

    public void disposeMusic(String filePath) {
        try {
            this.get(filePath, Music.class).dispose(); // remove the introMusic from memory to to increase performance
        } catch (GdxRuntimeException e) {

        }
    }


    /**
     * sets the music volume of currently running sounds to the GlobalMusicVolume
     */

    public void setMusicVolume() {

        for (String x : currentPlayingMusic) {
            this.get(x, Music.class).setVolume(GlobalMusicVolume);


        }
    }


}