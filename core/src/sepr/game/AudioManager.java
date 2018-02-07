package sepr.game;

import com.badlogic.gdx.*;
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

public class AudioManager  { //
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


    public void loadSounds()
    {
        Sound Colin_Insufficient_Funds = Gdx.audio.newSound(Gdx.files.internal("sound/Allocation/Colin_Insuffiecient Funds.wav"));
        Sound Colin_Not_enough_gang_members = Gdx.audio.newSound(Gdx.files.internal("sound/Allocation/Colin_Insuffiecient Funds.wav"));
        Sound Colin_What_do_allocation_and_the_empty_set_have_in_common = Gdx.audio.newSound(Gdx.files.internal("sound/Alloation/Colin_What do allocation and the empty set have in common"));
    }
}