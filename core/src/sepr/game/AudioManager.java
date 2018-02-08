package sepr.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.audio.Mp3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.lwjgl.Sys;




/**
 * Usage -- Audio.get('path to file', Sound.class).play() // this will play the sound
 */


public class AudioManager extends AssetManager {
    private static AudioManager instance = null;
    protected AudioManager() {
        // Exists only to defeat instantiation.
    }
    public static AudioManager getInstance() {
        if(instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }


    public void loadSounds() {
        this.load("sound/Allocation/Colin_Insuffiecient Funds.wav", Sound.class);
        this.load("sound/Allocation/Colin_Insuffiecient Funds.wav", Sound.class);
        this.load("sound/Allocation/Colin_EmptySet.wav", Sound.class);
        this.finishLoading();
    }

    public void updateSounds()
        {
            this.update();
        }
    public void dispose()
    {
        this.dispose();
    }


}