package sepr.game.saveandload;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.junit.Before;
import org.junit.Test;
import sepr.game.*;
import sepr.game.gangmembers.GangMembers;
import sepr.game.gangmembers.Postgraduates;
import sepr.game.gangmembers.Undergraduates;
import sepr.game.punishmentcards.Card;
import sepr.game.utils.PlayerType;
import sepr.game.utils.TurnPhaseType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class SaveAndLoadTest {
    private SaveLoadManager testSaveLoadManager;
    private HashMap<Integer, Player> players;
    private HashMap<Integer, Sector> sectors;
    private List<Integer> turnOrder;
    private TurnPhaseType currentPhase;
    private int currentPlayerPointer;
    private boolean turnTimerEnabled;
    private int maxTurnTime;
    private long turnTimeElapsed;
    private GameScreen testGameScreen;

    @Before
    public void setUp() {
        Player testPlayer1 = new Player(0, GameSetupScreen.CollegeName.UNI_OF_YORK, Color.WHITE, PlayerType.NEUTRAL_AI, "THE NEUTRAL PLAYER");
        Player testPlayer2 = new Player(1, GameSetupScreen.CollegeName.ALCUIN, Color.RED, PlayerType.HUMAN, "TestPlayer2");
        Player testPlayer3 = new Player(2, GameSetupScreen.CollegeName.WENTWORTH, Color.YELLOW, PlayerType.HUMAN, "TestPlayer3");
        this.players = new HashMap<>();
        this.players.put(0, testPlayer1);
        this.players.put(1, testPlayer2);
        this.players.put(2, testPlayer3);
        Sector sector1 = createSector("Test1", "This is test Sector 1", false, false, 10, 3, 1, false);
        Sector sector2 = createSector("Test2", "This is test Sector 2", false, false, 11, 2, 1, false);
        Sector sector3 = createSector("Test3", "This is test Sector 3", true, false, 12, 1, 1, false);
        Sector sector4 = createSector("Test4", "This is test Sector 4", false, true, 13, 0, 1, false);
        sector1.setOwner(testPlayer2);
        sector2.setOwner(testPlayer2);
        sector3.setOwner(testPlayer3);
        sector4.setOwner(testPlayer1);
        this.sectors = new HashMap<>();
        this.sectors.put(0,sector1);
        this.sectors.put(1,sector2);
        this.sectors.put(2,sector3);
        this.sectors.put(3,sector4);
        this.turnOrder = new LinkedList<>();
        this.turnOrder.add(0);
        this.turnOrder.add(1);
        this.currentPhase = TurnPhaseType.REINFORCEMENT;
        this.currentPlayerPointer = 0;
        this.turnTimerEnabled = true;
        this.maxTurnTime = 450;
        this.turnTimeElapsed = 0;
        //testGameScreen = new GameScreen();
    }

    public Sector createSector(String college, String displayName, boolean isPVCTile, boolean neutral,
                               int reinforcementsProvided, int undergraduates, int postgraduates, boolean postgraduateStatus) {
        Sector testSector = new Sector();
        testSector.setCollege(college);
        testSector.setDisplayName(displayName);
        testSector.setIsPVCTile(isPVCTile);
        testSector.setNeutral(neutral);
        testSector.setReinforcementsProvided(reinforcementsProvided);
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
    public void testSaving() {
        String home = System.getProperty("user.home"); // Get the user's home directory
        String path = home + File.separator + "Bachelors-of-Domination" + File.separator + "saves" + File.separator + "saves.json"; // Generate the path to the saves.json file
        this.testSaveLoadManager.saveByID(1);
        this.testSaveLoadManager.saveToFile();
        assertTrue(new File(path).exists());
    }
}
