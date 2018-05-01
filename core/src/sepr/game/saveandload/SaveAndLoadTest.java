package sepr.game.saveandload;

import com.badlogic.gdx.graphics.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import sepr.game.*;
import sepr.game.gangmembers.GangMembers;
import sepr.game.gangmembers.Postgraduates;
import sepr.game.gangmembers.Undergraduates;
import sepr.game.punishmentcards.*;
import sepr.game.utils.PlayerType;
import sepr.game.utils.TurnPhaseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class SaveAndLoadTest {
    private JSONifier jifier;
    private GameState state;
    private HashMap<Integer, Player> players;
    private HashMap<Integer, Sector> sectors;
    private List<Integer> turnOrder;
    private TurnPhaseType currentPhase;
    private int currentPlayerPointer;
    private boolean turnTimerEnabled;
    private int maxTurnTime;
    private long turnTimeElapsed;
    private ArrayList<Card> cardDeck;

    @Before
    public void setUp() {
        jifier = new JSONifier();
        Player testPlayer1 = new Player(0, GameSetupScreen.CollegeName.UNI_OF_YORK, Color.WHITE, PlayerType.NEUTRAL_AI, "THE NEUTRAL PLAYER");
        Player testPlayer2 = new Player(1, GameSetupScreen.CollegeName.ALCUIN, Color.RED, PlayerType.HUMAN, "TestPlayer2");
        Player testPlayer3 = new Player(2, GameSetupScreen.CollegeName.WENTWORTH, Color.YELLOW, PlayerType.HUMAN, "TestPlayer3");
        this.players = new HashMap<Integer, Player>();
        testPlayer2.addCard(new GoldenGoose());
        testPlayer3.addCard(new FreshersFlu());
        testPlayer3.addCard(new CripplingHangover());
        this.players.put(0, testPlayer1);
        this.players.put(1, testPlayer2);
        this.players.put(2, testPlayer3);
        Sector sector1 = createSector("Test1", "This is test Sector 1", false, false, 10, 3, 1, false);
        Sector sector2 = createSector("Test2", "This is test Sector 2", false, false, 11, 2, 1, false);
        Sector sector3 = createSector("Test3", "This is test Sector 3", true, false, 12, 1, 1, false);
        Sector sector4 = createSector("Test4", "This is test Sector 4", false, true, 13, 0, 1, false);
        sector1.setOwnerId(testPlayer2.getId());
        sector2.setOwnerId(testPlayer2.getId());
        sector3.setOwnerId(testPlayer3.getId());
        sector4.setOwnerId(testPlayer1.getId());
        this.sectors = new HashMap<Integer, Sector>();
        this.sectors.put(0,sector1);
        this.sectors.put(1,sector2);
        this.sectors.put(2,sector3);
        this.sectors.put(3,sector4);
        this.turnOrder = new LinkedList<Integer>();
        this.turnOrder.add(0);
        this.turnOrder.add(1);
        this.currentPhase = TurnPhaseType.REINFORCEMENT;
        this.currentPlayerPointer = 0;
        this.turnTimerEnabled = true;
        this.maxTurnTime = 450;
        this.turnTimeElapsed = 0;
        this.cardDeck = new ArrayList<Card>();
        this.cardDeck.add(new ExceptionalCircumstances());
        this.cardDeck.add(new FreshersFlu());
        this.cardDeck.add(new GoldenGoose());
        this.cardDeck.add(new PlagueOfGeese());
        this.cardDeck.add(new CripplingHangover());
        this.cardDeck.add(new Strike());
    }

    public Sector createSector(String college, String displayName, boolean isPVCTile, boolean neutral,
                               int reinforcementsProvided, int undergraduates, int postgraduates, boolean postgraduateStatus) {
        Sector testSector = new Sector();
        testSector.setCollege(college);
        testSector.setDisplayName(displayName);
        testSector.setIsPVCTile(isPVCTile);
        testSector.setNeutral(neutral);
        testSector.setUndergraduatesProvided(reinforcementsProvided);
        ArrayList<GangMembers> testUnitsInSector = new ArrayList<GangMembers>();
        for (int i = 0; i < undergraduates; i++ ) {
            testUnitsInSector.add(new Undergraduates());
        }
        if (postgraduates > 0) {
            testUnitsInSector.add(new Postgraduates());
        }
        testSector.setUnitsInSector(testUnitsInSector);
        testSector.setPostgraduateStatus(postgraduateStatus);
        return testSector;
    }

    @Test
    public void testTransformToJSON() {
        this.state = new GameState();
        this.state.players = this.players;
        this.state.sectors = this.sectors;
        this.state.currentPhase = this.currentPhase;
        this.state.currentPlayerPointer = this.currentPlayerPointer;
        this.state.maxTurnTime = this.maxTurnTime;
        this.state.turnOrder = this.turnOrder;
        this.state.turnTimeElapsed = this.turnTimeElapsed;
        this.state.turnTimerEnabled = this.turnTimerEnabled;
        this.state.cardDeck = this.cardDeck;
        this.jifier.setState(this.state);
        JSONObject JSONState = this.jifier.getJSONGameState();
        TurnPhaseType turnType = null;
        for (TurnPhaseType type : TurnPhaseType.values()){
            if (type.equalsName(JSONState.get("CurrentPhase").toString())){
                turnType = type;
                break;
            }
        }
        assertEquals(this.currentPhase, turnType);
        assertEquals(this.currentPlayerPointer, JSONState.get("CurrentPlayerPointer"));
        assertEquals(this.maxTurnTime, JSONState.get("MaxTurnTime"));
        assertEquals(this.turnTimeElapsed, JSONState.get("TurnTimeElapsed"));
        assertEquals(this.turnTimerEnabled, JSONState.get("TurnTimerEnabled"));
        JSONArray players = (JSONArray) JSONState.get("PlayerState");
        for(int i = 0; i < players.size(); i++) {
            assertEquals(this.players.get(i).getId(), ((JSONObject) players.get(i)).get("ID"));
        }
        JSONArray sectors = (JSONArray) JSONState.get("MapState");
        for(int i = 0; i < players.size(); i++) {
            assertEquals(this.sectors.get(i).getId(), ((JSONObject) sectors.get(i)).get("ID"));
        }
        JSONArray turnOrder = (JSONArray) JSONState.get("TurnOrder");
        for(int i = 0; i < turnOrder.size(); i++) {
            Integer temp = (Integer) turnOrder.get(i);
            assertEquals(this.turnOrder.get(i), temp);
        }
        JSONArray cards = (JSONArray) JSONState.get("CardDeck");
        for(int i = 0; i < cards.size(); i++) {
            Card temp = Card.initiateCard(CardType.fromString(cards.get(i).toString()));
            assertEquals(this.cardDeck.get(i).getType(), temp.getType());
        }
    }

    @Test
    public void testTransformFromJSON() {
        this.state = new GameState();
        this.state.players = this.players;
        this.state.sectors = this.sectors;
        this.state.currentPhase = this.currentPhase;
        this.state.currentPlayerPointer = this.currentPlayerPointer;
        this.state.maxTurnTime = this.maxTurnTime;
        this.state.turnOrder = this.turnOrder;
        this.state.turnTimeElapsed = this.turnTimeElapsed;
        this.state.turnTimerEnabled = this.turnTimerEnabled;
        this.state.cardDeck = this.cardDeck;
        this.jifier.setState(this.state);
        JSONObject JSONState = this.jifier.getJSONGameState();
        this.jifier.setSaveState(JSONState);
        GameState transformState = this.jifier.getStateFromJSON();
        for (int i = 0; i < this.state.players.size(); i++) {
            assertEquals(this.state.players.get(i).getId(), transformState.players.get(i).getId());
        }
        for (int i = 0; i < this.state.sectors.size(); i++) {
            assertEquals(this.state.sectors.get(i).getId(), transformState.sectors.get(i).getId());
        }
        assertEquals(this.state.currentPhase, transformState.currentPhase);
        assertEquals(this.state.currentPlayerPointer, transformState.currentPlayerPointer);
        assertEquals(this.state.maxTurnTime, transformState.maxTurnTime);
        for (int i = 0; i < this.state.turnOrder.size(); i++) {
            assertEquals(this.state.turnOrder.get(i), transformState.turnOrder.get(i));
        }
        assertEquals(this.state.turnTimeElapsed, transformState.turnTimeElapsed);
        assertEquals(this.state.turnTimerEnabled, transformState.turnTimerEnabled);
        for(int i = 0; i < this.state.cardDeck.size(); i++) {
            assertEquals(this.cardDeck.get(i).getType(), transformState.cardDeck.get(i).getType());
        }
    }
}
