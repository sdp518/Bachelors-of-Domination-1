package sepr.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sun.prism.paint.Color;
import sepr.game.punishmentcards.CardType;
import sepr.game.utils.TurnPhaseType;

/**
 * base class for handling phase specific input
 */
public abstract class Phase extends Stage {
    GameScreen gameScreen;
    Player currentPlayer;
    Player previousPlayer;
    AudioManager Audio = AudioManager.getInstance();


    private Table table;
    private Label bottomBarRightPart;
    private TurnPhaseType turnPhase;

    private Label.LabelStyle playerNameStyle; // store style for updating player name colour with player's colour

    private Label playerNameLabel; // displays the name of the current player in their college's colour colour
    private Label reinforcementLabel; // label showing how many troops the player has to allocate in their next reinforcement phase
    private Label turnTimerLabel; // displays how much time the player has left
    private Image collegeLogo; // ui component for displaying the logo of the current players college

    private static Texture gameHUDBottomBarLeftPartTexture;
    private static Texture gameHUDTopBarTexture; // NEW ASSESSMENT 4

    private Label labelText; // NEW ASSESSMENT 4

    /**
     *
     * @param gameScreen for accessing the map and additional game properties
     * @param turnPhase type of phase this is
     */
    public Phase(GameScreen gameScreen, TurnPhaseType turnPhase) {
        this.setViewport(new ScreenViewport());

        this.gameScreen = gameScreen;

        this.turnPhase = turnPhase;

        this.table = new Table();
        this.table.setFillParent(true); // make ui table fill the entire screen
        this.addActor(table);
        this.table.setDebug(false); // enable table drawing for ui debug

        gameHUDBottomBarLeftPartTexture = new Texture("uiComponents/HUD-Bottom-Bar-Left-Part.png");
        gameHUDTopBarTexture = new Texture("uiComponents/HUD-Top-Bar.png"); // NEW ASSESSMENT 4

        this.setupUi();
    }

    /**
     * setup UI that is consistent across all game phases
     */
    private void setupUi() {
        TextButton endPhaseButton = WidgetFactory.genEndPhaseButton();
        endPhaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.nextPhase();
                Audio.get("sound/Other/click.mp3", Sound.class).play(AudioManager.GlobalFXvolume); //plays the music

            }
        });
        bottomBarRightPart = WidgetFactory.genGameHUDBottomBarRightPart("INIT");
        Table bottomBarLeftPart = genGameHUDBottomBarLeftPart();

        table.top().center();
        table.add(this.genGameHUDTopBar()).colspan(2).expandX().height(60).width(910).top().padLeft(200); //MODIFIED ASSESSMENT 4

        table.row();
        table.add(new Table()).expand();

        Table subTable = new Table();

        subTable.bottom();
        subTable.add(bottomBarLeftPart).height(190).width(250);
        subTable.add(bottomBarRightPart).bottom().expandX().fillX().height(60);

        table.row();
        table.add(subTable).expandX().fill().colspan(1);
        table.bottom().right();
        table.add(endPhaseButton).fillX().height(60).width(180).bottom().right();

        setBottomBarText(null);
    }

    /**
     * generates the UI widget to be displayed at the bottom left of the HUD
     * @return table containing the information to display in the HUD
     */
    private Table genGameHUDBottomBarLeftPart(){
        Label.LabelStyle style = new Label.LabelStyle();
        playerNameStyle = new Label.LabelStyle();

        // load fonts
        style.font = WidgetFactory.getFontSmall();

        playerNameStyle.font = WidgetFactory.getFontSmall();

        playerNameLabel = new Label("", playerNameStyle);
        reinforcementLabel = new Label("", style);
        turnTimerLabel = new Label("Timer: DISABLED", style);
        collegeLogo = new Image(WidgetFactory.genCollegeLogoDrawable(GameSetupScreen.CollegeName.UNI_OF_YORK));

        Table table = new Table();
        table.background(new TextureRegionDrawable(new TextureRegion(gameHUDBottomBarLeftPartTexture)));

        Table subTable = new Table();
        subTable.setDebug(false);
        subTable.left().add(collegeLogo).height(80).width(100).pad(0);
        subTable.right().add(playerNameLabel).pad(0);
        subTable.row();
        subTable.add(reinforcementLabel).colspan(2);
        subTable.row();
        subTable.add(turnTimerLabel).colspan(2);

        table.add(subTable);

        return table;
    }

    // TODO Set owning college colour
    /**
     * sets the bar at the bottom of the HUD to the details of the sector currently hovered over
     * If no sector is being hovered then displays "Mouse over a sector to see further details"
     * @param sector the sector of details to be displayed
     */
    public void setBottomBarText(Sector sector) {
        if (sector == null) {
            this.bottomBarRightPart.setText("Mouse over a sector to see further details");
        } else {
            String ownerColour = gameScreen.getPlayerById(sector.getOwnerId()).getSectorColour().toString();
            if (ownerColour.equals("7f7f7fff")) {
                ownerColour = "000000ff"; // Neutral player displays as black for readability
            }
            this.bottomBarRightPart.setText("College: " + sector.getCollege() + " - " + sector.getDisplayName() + " - " + "Owned By: [#" + ownerColour + "]" + gameScreen.getPlayerById(sector.getOwnerId()).getPlayerName() + "[] - " + "Grants +" + sector.getReinforcementsProvided() + " Troops");
        }
    }

    /**
     * sets up phase when a new player enters it
     *
     * @param player the new player that is entering the phase
     */
    void enterPhase(Player player) {
        this.currentPlayer = player;


        playerNameStyle.fontColor = GameSetupScreen.getCollegeColor(currentPlayer.getCollegeName()); // update colour of player name

        playerNameLabel.setText(new StringBuilder((CharSequence) currentPlayer.getPlayerName())); // change the bottom bar label to the players name
        collegeLogo.setDrawable(WidgetFactory.genCollegeLogoDrawable(player.getCollegeName()));
        updateTroopReinforcementLabel();
        this.updatePhaseLabelColour();
        gameScreen.setupCardUI();
    }

    /**MOVED FROM WIDGET FACTORY ASSESSMENT 4
     * creates a table containing the components to make up the top bar of the HUD
     *
     * @return the top bar of the HUD for the specified phase
     */
    public Table genGameHUDTopBar() {
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = WidgetFactory.getFontSmall();
        TextButton exitButton = new TextButton("PAUSE", btnStyle);

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.pause();
            }
        });

        // TODO Remove temp code before submission
        /*********TEMP**********/
        TextButton addCardButton = new TextButton("CARD", btnStyle);

        addCardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ((currentPlayer.getCardHand().size() < 4) && (gameScreen.getCardDeckSize() != 0)){
                    currentPlayer.addCard(gameScreen.getRandomCard());
                    gameScreen.setupCardUI();
                }
            }
        });
        /*********END TEMP**********/

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = WidgetFactory.getFontSmall();

        String text = "";
        String pre = "";
        String post = "";
        switch (turnPhase) {
            case REINFORCEMENT:
                text = "REINFORCEMENT";
                post = "  -  Attack  -  Movement";
                break;
            case ATTACK:
                pre = "Reinforcement  -  ";
                text = "ATTACK";
                post = "  -  Movement";
                break;
            case MOVEMENT:
                pre = "Reinforcement  -  Attack  -  ";
                text = "MOVEMENT";
                break;
        }

        Label labelPre = new Label(pre, style);
        labelPre.setAlignment(Align.center);

        labelText = new Label(text, style);
        labelText.setAlignment(Align.center);
        labelText.setColor(gameScreen.getCurrentPlayer().getSectorColour());

        Label labelPost = new Label(post, style);
        labelPost.setAlignment(Align.center);

        Table table = new Table();
        table.background(new TextureRegionDrawable(new TextureRegion(gameHUDTopBarTexture)));
        table.left().add(exitButton).padRight(190).padLeft(20);
        table.add(labelPre).height(60);
        table.add(labelText).height(60);
        table.add(labelPost).height(60);
        table.right().add(addCardButton).padLeft(190).padRight(20);

        return table;
    }

    /**
     * NEW ASSESSMENT 4
     * updates the colour of the phase indicator to be the same as the current player's colour
     */
    private void updatePhaseLabelColour() {
        labelText.setColor(gameScreen.getCurrentPlayer().getSectorColour());
    }

    // TODO Decide on timer low indicator
    /**
     * updates the text of the turn timer label
     *
     * @param timeRemaining time remaining of turn in seconds
     */
    void setTimerValue(int timeRemaining) {
        if (timeRemaining <= 10) {
            turnTimerLabel.setColor(com.badlogic.gdx.graphics.Color.RED);
        }
        else {
            turnTimerLabel.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        }
        turnTimerLabel.setText(new StringBuilder("Turn Timer: " + timeRemaining));
    }

    /**
     * updates the display of the number of troops the current player will have in their next reinforcement phase
     */
    void updateTroopReinforcementLabel() {
        this.reinforcementLabel.setText("Troop Allocation: " + currentPlayer.getTroopsToAllocate());
    }

    /**
     * method for tidying up phase for next player to use
     */
    public void endPhase () {
        this.currentPlayer = null;
    }

    @Override
    public void act() {
        super.act();
    }

    @Override
    public void draw() {
        phaseAct();

        gameScreen.getGameplayBatch().begin();
        visualisePhase(gameScreen.getGameplayBatch());
        gameScreen.getGameplayBatch().end();

        super.draw();
    }

    public abstract void phaseAct();

    /**
     * abstract method for writing phase specific rendering
     * @param batch
     */
    protected abstract void visualisePhase(SpriteBatch batch);

    @Override
    public String toString() {
        switch(this.turnPhase){
            case ATTACK:
                return "PHASE_ATTACK";
            case MOVEMENT:
                return "PHASE_MOVEMENT";
            case REINFORCEMENT:
                return "PHASE_REINFORCEMENT";
            default:
                return "PHASE_BLANK";
        }
    }
}
