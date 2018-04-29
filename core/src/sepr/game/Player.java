package sepr.game;

import com.badlogic.gdx.graphics.Color;
import sepr.game.punishmentcards.Card;
import sepr.game.utils.PlayerType;

import java.util.ArrayList;

/**
 * base class for storing Neutral and Human player data
 */
public class Player {
    private int id; // player's unique id
    private GameSetupScreen.CollegeName collegeName; // college this player chose
    private String playerName;
    private int[] troopsToAllocate; // how many troops the player has to allocate at the start of their next reinforcement phase, 0 - ug, 1 - pg
    private Color sectorColour; // what colour to shade sectors owned by the player
    private PlayerType playerType; // Human or Neutral player
    private boolean ownsPVC;

    private boolean cripplingHangover;
    private boolean goldenGoose;
    private ArrayList<Card> cardHand;

    /**
     * creates a player object with the specified properties
     *
     * @param id player's unique identifier
     * @param collegeName display name for this player
     * @param sectorColour colour that the sectors owned by this player are coloured
     * @param playerType is this player a Human, AI or Neutral AI
     * @param playerName player's name to be displayed
     */
    public Player(int id, GameSetupScreen.CollegeName collegeName, Color sectorColour, PlayerType playerType, String playerName) {
        this.id = id;
        this.collegeName = collegeName;
        this.troopsToAllocate = new int[2];
        this.troopsToAllocate[0] = 0;
        this.troopsToAllocate[1] = 0;
        this.sectorColour = sectorColour;
        this.playerType = playerType;
        this.playerName = playerName;
        this.ownsPVC = false;
        this.cripplingHangover = false;
        this.goldenGoose = false;
        this.cardHand = new ArrayList<Card>();
    }


    public Player(int id, GameSetupScreen.CollegeName collegeName, Color sectorColour, PlayerType playerType, String playerName, int undergraduatesToAllocate, int postgraduatesToAllocate, boolean ownsPVC){
        this(id, collegeName, sectorColour, playerType, playerName);

        this.troopsToAllocate[0] = undergraduatesToAllocate;
        this.troopsToAllocate[1] = postgraduatesToAllocate;
        this.setOwnsPVC(ownsPVC);
        //this never actually gets used
    }

    /**
     * @param id player's unique identifier
     * @param collegeName display name for this player
     * @param sectorColour colour that the sectors owned by this player are coloured
     * @param playerName player's name to be displayed
     */
    public static Player createHumanPlayer(int id, GameSetupScreen.CollegeName collegeName, Color sectorColour, String playerName) {
        return new Player(id, collegeName, sectorColour, PlayerType.HUMAN, playerName);
    }

    /**
     * @param id player's unique identifier
     */
    public static Player createNeutralPlayer(int id) {
        return new Player(id, GameSetupScreen.CollegeName.UNI_OF_YORK, Color.WHITE, PlayerType.NEUTRAL_AI, "THE NEUTRAL PLAYER");
    }


    /**
     * @return  if the player owns the PVC tile
     */
    public Boolean getOwnsPVC() { return ownsPVC; }

    /**
     * @param  ownsPVC boolean if the player owns the PVC
     */
    public void setOwnsPVC(Boolean ownsPVC) { this.ownsPVC = ownsPVC; }



    /**
     *
     * @return the player's id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return the name of the player's college
     */
    public GameSetupScreen.CollegeName getCollegeName() {
        return collegeName;
    }

    /**
     *
     * @return the colour associated with this player
     */
    public Color getSectorColour() {
        return sectorColour;
    }

    /**
     *
     * @return the name of the player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     *
     * @return the player's type
     */
    public PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * fetches number of troops this player can allocate in their next turn
     * @return amount troops to allocate
     */
    public int[] getTroopsToAllocate() {
        return troopsToAllocate;
    }

    /**
     * sets the number of troops this player has to allocate to this value
     *
     * @param troopsToAllocate number of troops to allocate
     */
    public void setTroopsToAllocate(int troopsToAllocate) {
        this.troopsToAllocate[0] = troopsToAllocate;
    }

    /**
     * increases the number of troops to allocate by the the given amount
     * @param troopsToAllocate amount to increase allocation by
     */
    public void addUndergraduatesToAllocate(int troopsToAllocate) {
        this.troopsToAllocate[0] += troopsToAllocate;
    }

    public void addPostGraduatesToAllocate(int troopsToAllocate) {
        this.troopsToAllocate[1] += troopsToAllocate;
    }

    /**
     * adds the given card to the players card hand
     * @param card the card to be added
     */
    public void addCard(Card card) {
        cardHand.add(card);
    }

    /**
     * removes the card at index i from the card hand
     * @param i the cardHand index of the card to be removed
     */
    public Card removeCard(int i) {
        return cardHand.remove(i);
    }

    /**
     * @return the players card hand as an ArrayList of Cards
     */
    public ArrayList<Card> getCardHand() {
        return cardHand;
    }

    /**
     * @return true if player has hangover, false otherwise
     */
    public boolean hasCripplingHangover() {
        return cripplingHangover;
    }

    /**
     * switches boolean value of cripplingHangover
     */
    public void switchCripplingHangover() {
        cripplingHangover = !cripplingHangover;
    }

    /**
     * @return true if player has golden goose, false otherwise
     */
    public boolean hasGoldenGoose() {
        return goldenGoose;
    }

    /**
     * switches boolean value of goldenGoose
     */
    public void switchGoldenGoose() {
        goldenGoose = !goldenGoose;
    }
}
