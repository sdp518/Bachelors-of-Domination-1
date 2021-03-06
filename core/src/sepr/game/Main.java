package sepr.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import sepr.game.saveandload.SaveLoadManager;

import java.util.HashMap;

//TODO at the end of the game, the allocation still comes up, you have to end your turn, congratulations and poor performance voice lines come up at the same time.

/**
 * executable http://www.sidmeiers.me/documents/code/BachelorsOfDomination3.0.zip
 *
 * main game class used for controlling what screen is currently being displayed
 */
public class Main extends Game implements ApplicationListener {
	private MiniGameScreen miniGameScreen;
	private MenuScreen menuScreen;
	private GameScreen gameScreen;
	private OptionsScreen optionsScreen;
	private OptionsScreen inGameOptionsScreen;
	private GameSetupScreen gameSetupScreen;
	private LoadScreen saveScreen;
	private SaveLoadManager saveLoadManager;
	private AudioManager Audio = AudioManager.getInstance();


	/**
	 * Setup the screens and set the first screen as the menu
	 */
	@Override
	public void create () {
		new WidgetFactory(); // setup widget factory for generating UI components
		new DialogFactory(); // setup dialog factory for generating dialogs

		// TODO Applying preferences after instantiating gameScreen causes quit button bug
		applyPreferences();

		this.menuScreen = new MenuScreen(this);
		this.gameScreen = new GameScreen(this);
		this.optionsScreen = new OptionsScreen(this, EntryPoint.MENU_SCREEN);
		this.inGameOptionsScreen = new OptionsScreen(this, EntryPoint.GAME_SCREEN);
		this.gameSetupScreen = new GameSetupScreen(this);
		this.saveLoadManager = new SaveLoadManager(gameScreen);
		this.miniGameScreen = new MiniGameScreen( this, gameScreen);
		this.saveScreen = new LoadScreen(this, EntryPoint.GAME_SCREEN, saveLoadManager);


		this.setMenuScreen();
	}

	private void refreshScreens() {
		this.menuScreen = new MenuScreen(this);
		this.gameScreen = new GameScreen(this);
		this.optionsScreen = new OptionsScreen(this, EntryPoint.MENU_SCREEN);
		this.inGameOptionsScreen = new OptionsScreen(this, EntryPoint.GAME_SCREEN);
		this.gameSetupScreen = new GameSetupScreen(this);
		this.saveLoadManager = new SaveLoadManager(gameScreen);
		this.miniGameScreen = new MiniGameScreen( this, gameScreen);
		this.saveScreen = new LoadScreen(this, EntryPoint.GAME_SCREEN, saveLoadManager);
	}

	public void setMiniGameScreen() {
		miniGameScreen = new MiniGameScreen(this,gameScreen);
		miniGameScreen.setupGame(gameScreen.getPlayerById(gameScreen.getCurrentPlayerPointer()));
		this.setScreen(miniGameScreen);
		miniGameScreen.startGame();
	}

	public void setMenuScreen() {
		this.refreshScreens();
		this.setScreen(menuScreen);
	}

	/**
	 * NEW ASSESSMENT 4
	 * changes the screen currently being displayed to the menu and re-instantiates game screen
	 */
	public void exitToMenu() {
		this.refreshScreens();
		this.setScreen(menuScreen);
	}

	/**
	 * displays the game screen and starts a game with the passed properties
	 *
	 * @param players hashmap of players who should be present in the game
	 * @param turnTimerEnabled whether or not this game should have a turn timer on
	 * @param maxTurnTime the maximum time of a turn, in seconds, if the turn tumer is enabled
	 * @param allocateNeutralPlayer should the neutral player be given sectors to start with
	 */
	public void setGameScreen(HashMap<Integer, Player> players, boolean turnTimerEnabled, int maxTurnTime, boolean allocateNeutralPlayer) {
		gameScreen.setupGame(players, turnTimerEnabled, maxTurnTime, allocateNeutralPlayer);
		this.saveScreen = new LoadScreen(this, EntryPoint.GAME_SCREEN, saveLoadManager);
		this.setScreen(gameScreen);
		gameScreen.startGame();
	}

	public void returnGameScreen() {
		this.setScreen(gameScreen);
		gameScreen.resetCameraPosition();
		gameScreen.resume();
	}

	/*public LoadScreen getSaveScreen() {
		return this.saveScreen;
	}*/

	/**
	 * change the screen currently being displayed to the options screen
	 */
	public void setOptionsScreen() {
		this.setScreen(optionsScreen);
	}

	/**
	 * change the screen currently being displayed to the in-game options screen
	 */
	public void setInGameOptionsScreen() {
		this.setScreen(inGameOptionsScreen);
	}

	/**
	 * change the screen currently being displayed to the game setup screen
	 */
	public void setGameSetupScreen() {
		this.setScreen(gameSetupScreen);
	}

	public void setLoadScreen() {
		LoadScreen loadScreen = new LoadScreen(this, EntryPoint.MENU_SCREEN, saveLoadManager);
		this.setScreen(loadScreen);
	}

	public void setSaveScreen() {
		this.setScreen(saveScreen);
	}

	public LoadScreen getSaveScreen() {
		this.saveLoadManager.loadFromFile();
		this.saveScreen = new LoadScreen(this, EntryPoint.GAME_SCREEN, saveLoadManager);
		this.setSaveScreen();
		return this.saveScreen;
	}

	/**
	 * Applies the players options preferences
	 * Sets the
	 *      Music Volume
	 *      FX Volume
	 *      Screen Resolution
	 *      Fullscreen
	 *      Colourblind Mode
	 * A default setting should be applied for any missing preferences
	 */
	public void applyPreferences() {
		Preferences prefs = Gdx.app.getPreferences(OptionsScreen.PREFERENCES_NAME);

		AudioManager.GlobalFXvolume = prefs.getFloat(OptionsScreen.FX_VOL_PREF);
		AudioManager.GlobalMusicVolume = prefs.getFloat(OptionsScreen.MUSIC_VOL_PREF);
		Audio.setMusicVolume();

		int screenWidth = prefs.getInteger(OptionsScreen.RESOLUTION_WIDTH_PREF, 1920);
		int screenHeight = prefs.getInteger(OptionsScreen.RESOLUTION_HEIGHT_PREF, 1080);
		Gdx.graphics.setWindowedMode(screenWidth, screenHeight);

		if (prefs.getBoolean(OptionsScreen.FULLSCREEN_PREF)) {
			// change game to fullscreen
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		menuScreen.dispose();
		optionsScreen.dispose();
		gameSetupScreen.dispose();
		gameScreen.dispose();
	}

	//Test code:
    public GameScreen getGameScreen() {
	    return this.gameScreen;
    }

    public SaveLoadManager getSaveLoadManager() {
        return saveLoadManager;
    }
}

