package sepr.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;

public class MiniGameScreen implements Screen {

    private static final int MAX_CARDS = 16; // maximum number of cards, must be divisible by 4
    private static final int NUM_PAIRS = MAX_CARDS/2;
    private static final int COLS = MAX_CARDS/4;
    private static final int ROWS = MAX_CARDS/COLS;
    private static final float DELAY_TIME = 5; // time in seconds before cards are hidden

    private Main main;
    private Stage stage;
    private GameScreen gameScreen;
    private Table table; // table for inserting ui widgets into
    private Player player; // player to allocate gang members to at the end of the minigame

    private int[] locations = new int[MAX_CARDS]; // array to contain random locations of values
    private TextButton[] textButtons = new TextButton[MAX_CARDS]; // array to contain all buttons

    private int score; // score equates to additional gang members at the end of the minigame
    private int currentValue = -1; // -1 is designated as invalid

    MiniGameScreen(final Main main, final GameScreen gameScreen) {
        this.main = main;
        this.gameScreen = gameScreen;
        this.stage = new Stage() {
            @Override
            public boolean keyUp(int keyCode) {
                if (keyCode == Input.Keys.ESCAPE) { // ask player if they would like to exit the game if they press escape
                    DialogFactory.exitMinigame(stage,gameScreen,main);

                }
                return super.keyUp(keyCode);
            }
        };
        this.stage.setViewport(new ScreenViewport());

        this.table = new Table();
        this.table.setFillParent(true); // make ui table fill the entire screen
        this.stage.addActor(table);
        this.table.setDebug(false); // enable table drawing for ui debug
    }

    private Table setupMenuTable() {
        /* Listener for the buttons, passes the value of the clicked button to the buttonClicked method */
        InputListener listener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                TextButton buttonUsed = (TextButton)event.getListenerActor();
                String location = buttonUsed.getName(); // name of button equal to its location in textButtons
                buttonClicked(location);
                return true;
            }
        };

        /* Procedurally generate buttons */
        for (int i = 0; i < MAX_CARDS; i++) {
            textButtons[i] = WidgetFactory.genBasicButton(Integer.toString(locations[i]));
            textButtons[i].setName("-1"); // -1 is invalid, disables buttons at the start
            textButtons[i].addListener(listener);
        }

        /* Create sub-table for all the menu buttons */
        Table btnTable = new Table();
        btnTable.setDebug(false);

        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                btnTable.add(textButtons[i+(j*COLS)]).height(100).width(100).pad(30);
                btnTable.right();
            }
            btnTable.row();
        }

        /* Sub-table complete */
        return btnTable;
    }

    private void setupUi() {
        table.background(new TextureRegionDrawable(new TextureRegion(new Texture("uiComponents/menuBackground.png"))));

        table.center();
        table.add(WidgetFactory.genMenusTopBar("MINIGAME - MATCH THE PAIRS")).colspan(2);

        table.row();
        table.left();
        table.add(setupMenuTable()).expand();

        table.row();
        table.center();
        table.add(WidgetFactory.genBottomBar("QUIT", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DialogFactory.exitProgramDialogBox(stage);}

        })).colspan(2);


        for (int i = 0; i < MAX_CARDS; i++) {
            textButtons[i].setTouchable(Touchable.disabled);
            textButtons[i].setName(Integer.toString(i)); // activates buttons
        }
    }

    public void setupGame(Player player) {
        this.player = player;

        Arrays.fill(locations, 0, MAX_CARDS, -1);
        RandomXS128 rand = new RandomXS128();

        /* Randomly assigns values to an array to be used as locations for the values in a table */
        for (int i = 0; i < NUM_PAIRS; i++) {
            for (int j = 0; j < 2; j++) {
                while (true) {
                    int randInt = rand.nextInt(MAX_CARDS);
                    if (locations[randInt] == -1) {
                        locations[randInt] = i;
                        break;
                    }
                }
            }
        }
        setupUi();
    }

    /**
     * Starts the game by showing all the values for a set amount of time and then hiding them.
     * Activates buttons once hidden
     */
    public void startGame() {

        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                for (int i = 0; i < MAX_CARDS; i++) {
                    textButtons[i].setTouchable(Touchable.enabled);
                    textButtons[i].setText(""); // hides button values
                }
            }

        }, DELAY_TIME);

    }

    /**
     * Helpful and clean way of translating button location into button value
     *
     * @param location index in textButtons of the target button
     * @return the value of the target button, or -1 if button is invalid (if location is -1)
     */
    private int getValueAtLocation(String location) {
        if (location.equals("-1")) {
            return -1;
        }
        else {
            return locations[Integer.parseInt(location)];
        }
    }

    /**
     * Handles the possible outcomes of a button being pressed
     *
     * @param location index in textButtons of the pressed button
     */
    private void buttonClicked(String location) {
        /* Gets the value of the clicked button */
        int value = getValueAtLocation(location);

        /* If at start of choosing a pair, nothing currently selected */
        if (currentValue == -1) {
            currentValue = value;
            textButtons[Integer.parseInt(location)].setName("-1"); // first button becomes invalid
            textButtons[Integer.parseInt(location)].setText(Integer.toString(getValueAtLocation(location)));
        }

        /* If correct */
        else if (value == currentValue) {
            score += 1;
            currentValue = -1;
            textButtons[Integer.parseInt(location)].setName("-1");
            textButtons[Integer.parseInt(location)].setText(Integer.toString(getValueAtLocation(location)));
            pairFound();
        }

        /* If incorrect */
        else {
            score = 0;
            DialogFactory.basicDialogBox("Game Over!", "You receive 0 additional troops!", stage);
            endMiniGame();

        }
    }

    /**
     * Removes a pair of values and asks the user if they would like to continue playing
     */
    private void pairFound() {
        DialogFactory.leaveMiniGameDialog(this, stage);
    }

    /**
     * Correctly ends the minigame by giving the appropriate number of troops to the player
     * and switching back to the main game
     */
    public void endMiniGame() {
        player.addTroopsToAllocate(score);
        DialogFactory.miniGameOverDialog(main, stage, gameScreen, score);
        this.dispose();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.stage.act(Gdx.graphics.getDeltaTime());
        this.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
