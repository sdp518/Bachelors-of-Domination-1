package sepr.game.saveandload;

import sepr.game.Player;
import sepr.game.Sector;
import sepr.game.punishmentcards.Card;
import sepr.game.utils.TurnPhaseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class to store data relating to the current state of the game in preparation for saving
 */
public class GameState {
    public TurnPhaseType currentPhase; // Current phase of the game
    public HashMap<Integer, Sector> sectors;
    public HashMap<Integer, Player> players; // HashMap of players
    public boolean turnTimerEnabled; // Whether the turn timer is enabled
    public int maxTurnTime; // Maximum time that the player can take on a turn
    public long turnTimeElapsed;
    public List<Integer> turnOrder; // The order in which players take their turn
    public int currentPlayerPointer; // The player currently taking their turn
    public ArrayList<Card> cardDeck;
}
