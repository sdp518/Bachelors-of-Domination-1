package sepr.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import javafx.scene.shape.Shape;
import sepr.game.punishmentcards.*;
import sepr.game.utils.PlayerType;
import sepr.game.utils.TurnPhaseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * main class for controlling the game
 * implements screen for swapping what is being displayed with other screens, i.e. menu screens
 * input processor implemented to parse user input
 */
public class GameScreen implements Screen, InputProcessor{
    public static final int NEUTRAL_PLAYER_ID = 4;

    public AudioManager Audio = AudioManager.getInstance(); // Access to the AudioManager
    private Main main; // main stored for switching between screens

    private TurnPhaseType currentPhase = TurnPhaseType.REINFORCEMENT; // set initial phase to the reinforcement phase
    private PVC ProViceChancellor;
    private HashMap<TurnPhaseType, Phase> phases; // hashmap for storing the three phases of the game

    private SpriteBatch gameplayBatch; // sprite batch for rendering the game to
    private OrthographicCamera gameplayCamera; // camera for controlling what aspects of the game can be seen
    private Viewport gameplayViewport; // viewport for handling rendering the game at different resolutions

    private Map map; // stores state of the game: who owns which sectors
    private HashMap<Integer, Player> players; // player id mapping to the relevant player

    private HashMap<Integer, Boolean> keysDown; // mapping from key, (Input.Keys), to whether it has been pressed down

    // timer settings
    private boolean turnTimerEnabled;
    private int maxTurnTime;
    private long turnTimeStart;

    private List<Integer> turnOrder; // array of player ids in order of players' turns;
    private int currentPlayerPointer; // index of current player in turnOrder list
    private int previousPlayerPointer; // index of the previous player in turnOrder list

    private Texture mapBackground; // texture for drawing as a background behind the game

    private boolean gameSetup = false; // true once setupGame has been called

    private Random random;

    // pause menu setup - NEW ASSESSMENT 4
    private Stage pauseMenuStage = new Stage();
    private boolean timerPaused, gamePaused;
    private long pauseStartTime;
    private long pausedTime;

    // cards - NEW ASSESSMENT 4
    private ArrayList<Card> cardDeck = new ArrayList<Card>();
    private Stage cardStage = new Stage();
    private Table cardTable;
    private Image[] closedCardImages, openCardImages;
    private ArrayList<Boolean> clickedCard;
    private Stack clickedCardStack;

    /**
     * sets up rendering objects and key input handling
     * setupGame then start game must be called before a game is ready to be played
     *
     * @param main used to change screen this.phases = phases;
     */
    public GameScreen(Main main) {
        this.main = main;

        this.gameplayBatch = new SpriteBatch();
        this.gameplayCamera = new OrthographicCamera();
        this.gameplayViewport = new ScreenViewport(gameplayCamera);

        this.mapBackground = new Texture("uiComponents/mapBackground.png");

        // setup hashmap to check which keys were previously pressed
        this.keysDown = new HashMap<Integer, Boolean>();
        this.keysDown.put(Input.Keys.UP, false);
        this.keysDown.put(Input.Keys.LEFT, false);
        this.keysDown.put(Input.Keys.DOWN, false);
        this.keysDown.put(Input.Keys.RIGHT, false);
        this.keysDown.put(Input.Keys.S, false);
        this.keysDown.put(Input.Keys.L, false);

        this.random = new Random();
    }

    public GameScreen(Main main, TurnPhaseType currentPhase, Map map, HashMap<Integer, Player> players, boolean turnTimerEnabled, int maxTurnTime, long turnTimeStart, List<Integer> turnOrder, int currentPlayerPointer){
        this(main);

        setUpPhases();

        Audio.loadSounds(); //loads the sounds into memory

        this.currentPhase = currentPhase;

        this.map = map;
        this.players = players;
        this.turnTimerEnabled = turnTimerEnabled;
        this.maxTurnTime = maxTurnTime;
        this.turnTimeStart = turnTimeStart;
        this.turnOrder = turnOrder;
        this.currentPlayerPointer = currentPlayerPointer;
        this.phases.get(this.currentPhase).enterPhase(getCurrentPlayer());
        this.ProViceChancellor = new PVC((float)1.00,this); // TODO Change PVC spawn chance
        this.gameSetup = true;
    }

    /**
     * sets up a new game
     * start game must be called before the game is ready to be played
     *
     * @param players HashMap of the players in this game
     * @param turnTimerEnabled should players turns be limited
     * @param maxTurnTime time elapsed in cthis.phases = phases;urrent turn, irrelevant if turn timer not enabled
     */
    public void setupGame(HashMap<Integer, Player> players, boolean turnTimerEnabled, int maxTurnTime, boolean allocateNeutralPlayer) {
        Audio.loadSounds(); //loads the sounds into memory
        this.players = players;
        this.turnOrder = new ArrayList<Integer>();
        for (Integer i : players.keySet()) {
            if ((players.get(i).getPlayerType() != PlayerType.NEUTRAL_AI)) { // don't add the neutral player or unassigned to the turn order
                this.turnOrder.add(i);
            }
        }


        this.currentPlayerPointer = 0; // set the current player to the player in the first position of the turnOrder list

        this.turnTimerEnabled = turnTimerEnabled;
        this.maxTurnTime = maxTurnTime;
        this.ProViceChancellor = new PVC((float)1.00,this); // TODO Change PVC spawn chance
        this.map = new Map(this.players, allocateNeutralPlayer, ProViceChancellor); // setup the game map and allocate the sectors

        setUpPhases();

        // cards - NEW ASSESSMENT 4
        initCardDeck();
        setupCardUI();
        random = new Random();

        gameSetup = true; // game is now setup
    }

    public void setUpPhases(){
        // create the game phases and add them to the phases hashmap
        this.phases = new HashMap<TurnPhaseType, Phase>();
        this.phases.put(TurnPhaseType.REINFORCEMENT, new PhaseReinforce(this));
        this.phases.put(TurnPhaseType.ATTACK, new PhaseAttack(this));
        this.phases.put(TurnPhaseType.MOVEMENT, new PhaseMovement(this));
    }

    /**
     * called once game is setup to enter the first phase of the game; centre the game camera and start the turn timer
     *
     * @throws RuntimeException if this is called before the game is setup, i.e. setupGame has not been called before this
     */
    public void startGame() {
        if (!gameSetup) {
            throw new RuntimeException("Cannot start game before it is setup");
        }
        this.turnTimeStart = System.currentTimeMillis(); // set turn start time to current rime
        this.phases.get(currentPhase).enterPhase(getCurrentPlayer());
        resetCameraPosition();
    }

    /**
     * configure input so that input into the current phase's UI takes priority then unhandled input is handled by this class
     */
    private void updateInputProcessor() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(phases.get(currentPhase));
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(cardStage); // ADDED ASSESSMENT 4
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    /**
     * checks if game is over by checking how many players are in the turn order, if 1 then player has won, if 0 then the neutral player has won
     *
     * @return true if game is over else false
     */
    private boolean isGameOver() {
        return turnOrder.size() <= 1; // game is over if only one player is in the turn order
    }

    /**
     * MODIFIED Assessment 4
     * gets in seconds the amount of time remaining of the current player's turn
     *
     * @return time remaining in turn in seconds
     */
    private int getTurnTimeRemaining(){
        return maxTurnTime - (int)((System.currentTimeMillis() - (turnTimeStart + pausedTime)) / 1000);
    }

    /**
     * NEW Assessment 4
     * records the time at which the timer was paused
     */
    public void pauseTimer(){
        this.pauseStartTime = System.currentTimeMillis();
        this.timerPaused = true;
    }

    /**
     * NEW Assessment 4
     * resumes the timer
     */
    public void resumeTimer(){
        pausedTime += (System.currentTimeMillis() - pauseStartTime);
        pauseStartTime = 0;
        this.timerPaused = false;
    }

    /**
     * returns the player object corresponding to the passed id from the players hashmap
     *
     * @param id of the player object that is wanted
     * @return the player whose id matches the given one in the players hashmap
     * @throws IllegalArgumentException if the supplied id is not a key value in the players hashmap
     */
    protected Player getPlayerById(int id) throws IllegalArgumentException {
        if (!players.containsKey(id)) throw new IllegalArgumentException("Cannot fetch player as id: " + id + " does not exist");
        return players.get(id);
    }


    /**
     *
     * @return gets the player object for the player who's turn it currently is
     */
    private Player getPreviousPlayer() {
        return players.get(turnOrder.get(previousPlayerPointer));
    }

    /**
     *
     * @return gets the player object for the player who's turn it currently is
     */
    public Player getCurrentPlayer() {
        return players.get(turnOrder.get(currentPlayerPointer));
    }

    /**
     *
     * @return the sprite batch being used to render the game
     */
    protected SpriteBatch getGameplayBatch() {
        return this.gameplayBatch;
    }

    /**
     *
     * @return the map object for this game
     */
    public Map getMap() {
        return map;
    }

    /**
     * MODIFIED - ASSESSMENT 4
     * method is used for progression through the phases of a turn evaluating the currentPhase case label
     * if nextPhase is called during the movement phase then the game progresses to the next players turn
     */
    protected void nextPhase() {
        this.phases.get(currentPhase).endPhase();

        switch (currentPhase) {
            case REINFORCEMENT:
                currentPhase = TurnPhaseType.ATTACK;
                break;
            case ATTACK:
                currentPhase = TurnPhaseType.MOVEMENT;
                break;
            case MOVEMENT:
                currentPhase = TurnPhaseType.REINFORCEMENT;

                nextPlayer(); // nextPhase called during final phase of a player's turn so goto next player

                break;
        }

        this.updateInputProcessor(); // phase changed so update input handling
        if (currentPhase != TurnPhaseType.REINFORCEMENT) {
            this.phases.get(currentPhase).enterPhase(getCurrentPlayer()); // setup the new phase for the current player
        }
    }

    /**
     * MODIFIED - ASSESSMENT 4
     * called when the player ends the MOVEMENT phase of their turn to advance the game to the next Player's turn
     * increments the currentPlayerPointer and resets it to 0 if it now exceeds the number of players in the list
     * MODIFIED 23/4/18 - Fixed crash caused by eliminating a player by moving check for eliminated players to top
     */
    private void nextPlayer() {
        removeEliminatedPlayers(); // check no players have been eliminated
        previousPlayerPointer = currentPlayerPointer;
        this.currentPlayerPointer++;
        if (currentPlayerPointer == turnOrder.size()) { // reached end of players, reset to 0
            currentPlayerPointer = 0;
        }

        resetCameraPosition(); // re-centres the camera for the next player

        if (this.turnTimerEnabled) { // if the turn timer is on reset it for the next player
            this.turnTimeStart = System.currentTimeMillis();
            this.pausedTime = 0;
        }
        this.currentPhase = TurnPhaseType.REINFORCEMENT;
        this.updateInputProcessor(); // phase changed so update input handling

        this.phases.get(currentPhase).enterPhase(getCurrentPlayer()); // setup the new phase for the current player
        // TODO Check and fix if next player eliminated game crashes (prev 2 lines swapped)
    }

    /**
     * removes all players who have 0 sectors from the turn order
     */
    private void removeEliminatedPlayers() {
        List<Integer> playerIdsToRemove = new ArrayList<Integer>(); // list of players in the turn order who have 0 sectors
        for (Integer i : turnOrder) {
            boolean hasSector = false; // has a sector belonging to player i been found
            for (Integer j : map.getSectorIds()) {
                if (map.getSectorById(j).getOwnerId() == i) {
                    hasSector = true; // sector owned by player i found
                    break; // only need one sector to remain in turn order so can break once one found
                }
            }
            if (!hasSector) { // player has no sectors so remove them from the game
                playerIdsToRemove.add(i);
            }
        }

        if (playerIdsToRemove.size() > 0) { // if there are any players to remove
            turnOrder.removeAll(playerIdsToRemove);

            Audio.get("sound/Minigame/Colin_That_was_a_poor_performance.wav", Sound.class).play(AudioManager.GlobalFXvolume);

            String[] playerNames = new String[playerIdsToRemove.size()]; // array of names of players who have been removed
            for (int i = 0; i < playerIdsToRemove.size(); i++) {
                playerNames[i] = players.get(playerIdsToRemove.get(i)).getPlayerName();
            }

            DialogFactory.playersOutDialog(playerNames, phases.get(currentPhase)); // display which players have been eliminated
        }

        if (isGameOver()) { // check if game is now over
            gameOver();
        }
    }

    /** MODIFIED 23/4/18 - commented out loading of sound asset that was causing crash
     * method called when one player owns all the sectors in the map
     *
     * @throws RuntimeException if there is more than one player in the turn order when gameOver is called
     */
    private void gameOver() throws RuntimeException {
        if (turnOrder.size() == 0) { // neutral player has won
            DialogFactory.gameOverDialog(players.get(NEUTRAL_PLAYER_ID).getPlayerName(), players.get(NEUTRAL_PLAYER_ID).getCollegeName().getCollegeName(), main, phases.get(currentPhase));

        } else if (turnOrder.size() == 1){ // winner is player id at index 0 in turn order
            int voice = random.nextInt(4);

            switch (voice){
                case 0:
                    Audio.get("sound/Victory/Colin_Congratulations.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 1:
                    Audio.get("sound/Victory/Colin_Congratulations_your_grandson_would_be_proud_of_you.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 2:
                    Audio.get("sound/Victory/Colin_Well_Done.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 3:
                    Audio.get("sound/Victory/Colin_You_are_victorious.wav", Sound.class).play(AudioManager.GlobalFXvolume);
                    break;
                case 4:
                    break;
            }

            //Audio.get("", Sound.class).play(AudioManager.GlobalFXvolume);
            
            int winnerId = turnOrder.get(0); // winner will be the only player in the turn order list
            DialogFactory.gameOverDialog(players.get(winnerId).getPlayerName(), players.get(winnerId).getCollegeName().getCollegeName(), main, phases.get(currentPhase));

        } else { // more than one player in turn order so no winner found therefore throw error
            throw new RuntimeException("Game Over called but more than one player in turn order");
        }
    }

    /**
     * moves the camera in the appropriate direction if the corresponding arrow key is down
     */
    private void controlCamera() {
        if (this.keysDown.get(Input.Keys.UP)) {
            this.gameplayCamera.translate(0, 4, 0);
        }
        if (this.keysDown.get(Input.Keys.DOWN)) {
            this.gameplayCamera.translate(0, -4, 0);
        }
        if (this.keysDown.get(Input.Keys.LEFT)) {
            this.gameplayCamera.translate(-4, 0, 0);
        }
        if (this.keysDown.get(Input.Keys.RIGHT)) {
            this.gameplayCamera.translate(4, 0, 0);
        }

        // TODO Test on other screen resolutions
        // NEW ASSESSMENT 4
        this.gameplayCamera.position.x = MathUtils.clamp(this.gameplayCamera.position.x, 700, 1200);
        this.gameplayCamera.position.y = MathUtils.clamp(this.gameplayCamera.position.y, 400, 700);
    }

    /**
     * re-centres the camera and sets the zoom level back to default
     */
    public void resetCameraPosition() {
        this.gameplayCamera.position.x = 1920/2;
        this.gameplayCamera.position.y = 1080/2;
        this.gameplayCamera.zoom = 1;
    }

    /**
     * converts a point on the screen to a point in the world
     *
     * @param screenX x coordinate of point on screen
     * @param screenY y coordinate of point on screen
     * @return the corresponding world coordinates
     */
    public Vector2 screenToWorldCoords(int screenX, int screenY) {
        float x = gameplayCamera.unproject(new Vector3(screenX, screenY, 0)).x;
        float y = gameplayCamera.unproject(new Vector3(screenX, screenY, 0)).y;
        return new Vector2(x, y);
    }

    /**
     * MODIFIED Assessment 4
     * changes the screen currently being displayed to the miniGame
     */
    public void openMiniGame() {
        this.pauseTimer();
        main.setMiniGameScreen();
    }

    /**
     * changes the screen currently being displayed to the menu
     */
    public void openMenu() {
        Audio.disposeMusic("sound/Gameplay Music/wind.mp3"); //remove game play sounds from memory to save space
        Audio.loadMusic("sound/IntroMusic/introMusic.mp3"); //load and play main menu music
        main.exitToMenu();
    }

    /**
     * NEW ASSESSMENT 4
     * adds the pause menu to the pause menu stage
     */
    private void displayPauseMenu() {
        TextButton saveButton = WidgetFactory.genPauseMenuButton("SAVE/LOAD");
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Add save and load to pause menu
                //main.saveGame();
            }
        });

        TextButton optionsButton = WidgetFactory.genPauseMenuButton("OPTIONS");
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Link in-game options screen
            }
        });

        TextButton resumeButton = WidgetFactory.genPauseMenuButton("RESUME");
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resume();
            }
        });

        TextButton quitButton = WidgetFactory.genPauseMenuButton("QUIT");
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO Fix quit box appearance
                DialogFactory.leaveGameDialogBox(GameScreen.this, pauseMenuStage);
            }
        });

        Label textLabel = WidgetFactory.genMenuLabel("PAUSED");

        Table menu = new Table();
        menu.setDebug(false);
        menu.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("uiComponents/pauseMenuBackground.png"))));
        menu.top().center();
        menu.add(textLabel);
        menu.row().center();
        menu.add(resumeButton).padTop(30).padBottom(20);
        menu.row().center();
        menu.add(optionsButton).padBottom(20);
        menu.row().center();
        menu.add(saveButton).padBottom(20);
        menu.row().center();
        menu.add(quitButton);

        Table table = new Table();
        table.setDebug(false);
        table.setFillParent(true);
        table.add(menu);

        pauseMenuStage.addActor(table);
    }

    // TODO New card methods below
    /**
     * Initialises card deck which stores instances of card available for distribution
     */
    private void initCardDeck() {
        for (int i=0;i<4;i++){
            cardDeck.add(new PlagueOfGeese());
            cardDeck.add(new GoldenGoose());
            cardDeck.add(new FreshersFlu());
            cardDeck.add(new ExceptionalCircumstances());
        }
    }

    /**
     * @return the number of cards left in the card deck
     */
    public int getCardDeckSize() {
        return cardDeck.size();
    }

    /**
     * @return a random card from the deck
     */
    public Card getRandomCard() {
        return cardDeck.remove(random.nextInt(cardDeck.size()));
    }

    /**
     * Sets up the tables and stages for drawing the card UI and completes an initial drawing
     * of the corner cards
     */
    public void setupCardUI() {
        cardTable = new Table();
        cardTable.setDebug(false);
        cardTable.setTouchable(Touchable.enabled);
        cardTable.setFillParent(true);

        for (Actor a : cardStage.getActors()){
            a.addAction(Actions.removeActor());
        }
        cardStage.act();

        closedCardImages = new Image[4];
        openCardImages = new Image[4];
        clickedCard = new ArrayList<Boolean>();

        for (int i = 0; i < getCurrentPlayer().getCardHand().size(); i++) {
            closedCardImages[i] = WidgetFactory.genCardDrawable(getCurrentPlayer().getCardHand().get(i).getType());
            openCardImages[i] = WidgetFactory.genCardDrawable(getCurrentPlayer().getCardHand().get(i).getType());
            clickedCard.add(false);

            final int finalI = i;
            closedCardImages[i].addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //System.out.println("closed click");
                    Gdx.input.setInputProcessor(cardStage);
                    openCards();
                }
            });

            openCardImages[i].addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    event.stop();
                    //System.out.println("open click");
                    if (clickedCard.contains(true)) {
                        unclickCard(clickedCard.indexOf(true));
                        clickedCard.set(clickedCard.indexOf(true), false);
                    }
                    clickedCard.set(finalI, true);
                    clickCard(finalI);
                }
            });

            closedCardImages[i].setPosition((1650-(i*40)),750); // Top right position
            closedCardImages[i].setScale(0.4f);
            closedCardImages[i].setOrigin(closedCardImages[i].getWidth()/2, closedCardImages[i].getHeight()/2);

            cardTable.add(openCardImages[i]).padRight(40).padLeft(40);

            cardStage.addActor(closedCardImages[i]);

        }
        cardTable.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.isStopped()) {
                    //System.out.println("outside click");
                    updateInputProcessor();
                    if (clickedCard.contains(true)){
                        unclickCard(clickedCard.indexOf(true));
                        clickedCard.set(clickedCard.indexOf(true), false);
                    }
                    closeCards();
                }
            }
        });
    }

    /**
     * Opens the expanded card view
     */
    private void openCards() {
        for (Actor a : cardStage.getActors()){
            a.addAction(Actions.removeActor());
        }

        cardStage.addActor(cardTable);
    }

    /**
     * Closes the expanded card view
     */
    private void closeCards() {
        for (Actor a : cardStage.getActors()){
            a.addAction(Actions.removeActor());
        }

        for (Image i : closedCardImages) {
            if (i != null){
                cardStage.addActor(i);
            }
        }
    }

    /**
     * Sets up the card menu overlay for choosing what player to use the card on and swaps
     * it in for rendering
     */
    public void clickCard(int i) {
        Table tableCardBackground = new Table();
        tableCardBackground.setDebug(false);
        tableCardBackground.setBackground(openCardImages[i].getDrawable());
        tableCardBackground.setSize(openCardImages[0].getImageWidth(), openCardImages[0].getImageHeight());

        Pixmap pixmap=new Pixmap(Math.round(openCardImages[0].getImageWidth()), Math.round(openCardImages[0].getImageHeight()), Pixmap.Format.RGBA8888);
        pixmap.setColor(0f,0.0f,0f,0.8f);
        pixmap.fillRectangle(0,0, pixmap.getWidth(), pixmap.getHeight());
        TextureRegionDrawable overlay = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        Table tableOverlay = new Table();
        tableOverlay.setDebug(false);
        tableOverlay.setBackground(overlay);
        tableOverlay.setTouchable(Touchable.enabled);
        tableOverlay.setSize(openCardImages[0].getImageWidth(), openCardImages[0].getImageHeight());

        tableOverlay.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!event.isStopped()) {
                    event.stop();
                    if (clickedCard.contains(true)){
                        unclickCard(clickedCard.indexOf(true));
                        clickedCard.set(clickedCard.indexOf(true), false);
                    }
                }
            }
        });

        Label.LabelStyle smallStyle = new Label.LabelStyle();
        smallStyle.font = WidgetFactory.getFontSmall();

        ArrayList<Label> playerLabels = new ArrayList<Label>();

        for (final Player player : players.values()) {
            if (!player.equals(getCurrentPlayer())) {
                Label l = new Label(player.getPlayerName(), smallStyle);
                l.setColor(player.getSectorColour());
                final GameScreen gameScreen = this;
                l.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        event.stop();
                        getCurrentPlayer().getCardHand().get(clickedCard.indexOf(true)).act(player, gameScreen);
                        cardDeck.add(getCurrentPlayer().removeCard(clickedCard.indexOf(true)));
                        updateInputProcessor();
                        setupCardUI();
                    }
                });
                playerLabels.add(l);
            }
        }

        tableOverlay.top();
        tableOverlay.add(new Label("Use on Player: ", smallStyle)).padBottom(30);
        for (Label l : playerLabels) {
            tableOverlay.row().center();
            tableOverlay.add(l).padBottom(40);
        }

        clickedCardStack = new Stack();
        clickedCardStack.add(tableCardBackground);
        clickedCardStack.add(tableOverlay);

        //System.out.println("swapping");
        cardTable.getCell(openCardImages[i]).setActor(clickedCardStack);
    }

    /**
     * Removes the card menu and swaps back in the regular card image
     */
    private void unclickCard(int i) {
        cardTable.getCell(clickedCardStack).setActor(openCardImages[i]);
    }

    /**
     * draws a background image behind the map and UI covering the whole visible area of the render window
     */
    private void renderBackground() {
        Vector3 mapDrawPos = gameplayCamera.unproject(new Vector3(0, Gdx.graphics.getHeight(), 0));
        gameplayBatch.draw(mapBackground, mapDrawPos.x, mapDrawPos.y, gameplayCamera.viewportWidth * gameplayCamera.zoom, gameplayCamera.viewportHeight * gameplayCamera.zoom );
    }

    /* Screen implementation */

    /**
     * CHANGED ASSESSMENT 4
     * when this screen is shown updates the input handling so it is from this screen
     */
    @Override
    public void show() {
        if (gamePaused){
            Gdx.input.setInputProcessor(pauseMenuStage);
        }
        else {
            this.updateInputProcessor();
        }
    }

    /**
     * CHANGED ASSESSMENT 4
     * updates the game and renders it to the screen
     *
     * @param delta time elapsed between this and the previous update in seconds
     * @throws RuntimeException when method called before the game is setup
     */
    @Override
    public void render(float delta) {
        if (!gameSetup) throw new RuntimeException("Game must be setup before attempting to play it"); // throw exception if attempt to run game before its setup

        this.controlCamera(); // move camera

        gameplayCamera.update();
        gameplayBatch.setProjectionMatrix(gameplayCamera.combined);

        gameplayBatch.begin(); // begin rendering

        renderBackground(); // draw the background of the game
        map.draw(gameplayBatch); // draw the map

        if (gamePaused) {
            pauseMenuStage.act();
            pauseMenuStage.draw();
        }

        cardStage.act();
        cardStage.draw();

        gameplayBatch.end(); // stop rendering

        if (this.turnTimerEnabled && !this.timerPaused) { // update the timer display, if it is enabled
            this.phases.get(currentPhase).setTimerValue(getTurnTimeRemaining());
        }
        this.phases.get(currentPhase).act(delta); // update the stage of the current phase
        this.phases.get(currentPhase).draw(); // draw the phase UI

        if (this.turnTimerEnabled && (getTurnTimeRemaining() <= 0) && !this.timerPaused) { // goto the next player's turn if the timer is enabled and they have run out of time
            nextPlayer();
        }
    }

    @Override
    public void resize(int width, int height) {
        for (Stage stage : phases.values()) { // update the rendering properties of each stage when the screen is resized
            stage.getViewport().update(width, height);
            stage.getCamera().viewportWidth = width;
            stage.getCamera().viewportHeight = height;
            stage.getCamera().position.x = width/2;
            stage.getCamera().position.y = height/2;
            stage.getCamera().update();
        }

        // update this classes rending properties for the new display size
        this.gameplayViewport.update(width, height);
        this.gameplayCamera.viewportWidth = width;
        this.gameplayCamera.viewportHeight = height;
        this.gameplayCamera.translate(1920/2, 1080/2, 0);
        this.gameplayCamera.update();
    }

    // MODIFIED Assessment 4
    @Override
    public void pause() {
        this.pauseTimer();
        gamePaused = true;
        Gdx.input.setInputProcessor(pauseMenuStage);
        this.displayPauseMenu();
    }

    // MODIFIED Assessment 4
    @Override
    public void resume() {
        if (gamePaused) {
            gamePaused = false;
            this.resumeTimer();
            this.updateInputProcessor();
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        pauseMenuStage.dispose();
    }

    /* Input Processor implementation */

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP) {
            keysDown.put(Input.Keys.UP, true);
        }
        if (keycode == Input.Keys.DOWN) {
            keysDown.put(Input.Keys.DOWN, true);
        }
        if (keycode == Input.Keys.LEFT) {
            keysDown.put(Input.Keys.LEFT, true);
        }
        if (keycode == Input.Keys.RIGHT) {
            keysDown.put(Input.Keys.RIGHT, true);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.UP) {
            keysDown.put(Input.Keys.UP, false);
        }
        if (keycode == Input.Keys.DOWN) {
            keysDown.put(Input.Keys.DOWN, false);
        }
        if (keycode == Input.Keys.LEFT) {
            keysDown.put(Input.Keys.LEFT, false);
        }
        if (keycode == Input.Keys.RIGHT) {
            keysDown.put(Input.Keys.RIGHT, false);
        }
        if (keycode == Input.Keys.ESCAPE) {
            //DialogFactory.leaveGameDialogBox(this, phases.get(currentPhase)); // confirm if the player wants to leave if escape is pressed
            this.pause();
        }
        // TODO Decide if keeping
        if (keycode == Input.Keys.S) {
            this.main.saveGame();
        }
        if (keycode == Input.Keys.L) {
            this.main.loadGame();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector2 worldCoords = screenToWorldCoords(screenX, screenY);

        int hoveredSectorId = map.detectSectorContainsPoint((int)worldCoords.x, (int)worldCoords.y); // get id of sector mouse is currently hovered over
        if (hoveredSectorId == -1) {
            phases.get(currentPhase).setBottomBarText(null); // no sector hovered over: update bottom bar with null sector
        } else {
            phases.get(currentPhase).setBottomBarText(map.getSectorById(hoveredSectorId)); // update the bottom bar of the UI with the details of the sector currently hovered over by the mouse
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if ((gameplayCamera.zoom > 0.5 && amount < 0) || (gameplayCamera.zoom < 1.5 && amount > 0)) { // if the mouse scrolled zoom in/out
            gameplayCamera.zoom += amount * 0.03f;
        }
        return true;
    }

    public TurnPhaseType getCurrentPhase(){
        return this.currentPhase;
    }

    public HashMap<Integer, Player> getPlayers() {
        return players;
    }

    public boolean isTurnTimerEnabled(){
        return this.turnTimerEnabled;
    }

    public int getMaxTurnTime(){
        return this.maxTurnTime;
    }

    public long getTurnTimeStart(){
        return this.turnTimeStart;
    }

    public List<Integer> getTurnOrder(){
        return this.turnOrder;
    }

    public int getCurrentPlayerPointer(){
        return this.currentPlayerPointer;
    }
}