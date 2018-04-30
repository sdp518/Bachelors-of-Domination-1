package sepr.game.saveandload;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import org.junit.Before;
import org.junit.Test;
import sepr.game.*;
import sepr.game.gangmembers.GangMembers;
import sepr.game.gangmembers.Undergraduates;
import sepr.game.punishmentcards.Card;
import sepr.game.utils.PlayerType;
import sepr.game.utils.TurnPhaseType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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


    @Before
    public void setUp() {
        /*Sector testSector1 = new Sector(0,0,"Test1",new ArrayList<GangMembers>();
        id;
        ownerId;
        displayName;
        unitsInSector;
        this.reinforcementsProvided = reinforcementsProvided;
        this.college = college;
        this.neutral = neutral;
        this.adjacentSectorIds = adjacentSectorIds;
        this.sectorTexture = new Texture(texturePath);
        this.texturePath = texturePath;
        this.sectorPixmap = sectorPixmap;
        this.sectorCentreX = sectorCentreX;
        this.sectorCentreY = sectorCentreY;
        this.decor = decor;
        this.fileName = fileName;
        this.allocated = allocated;
        Player testPlayer1 = new Player(0, GameSetupScreen.CollegeName.UNI_OF_YORK, Color.WHITE, PlayerType.NEUTRAL_AI, "THE NEUTRAL PLAYER");
        Player testPlayer2 = new Player(1, GameSetupScreen.CollegeName.ALCUIN, Color.RED, PlayerType.HUMAN, "TestPlayer2");
        this.players = new HashMap<>();
        this.players.put(0, testPlayer1);
        this.players.put(1, testPlayer2);
        this.turnOrder = new LinkedList<>();
        this.turnOrder.add(0);
        this.turnOrder.add(1);
        this.currentPhase = TurnPhaseType.REINFORCEMENT;
        this.currentPlayerPointer = 0;
        this.turnTimerEnabled = true;
        this.maxTurnTime = 450;
        this.turnTimeElapsed = 0;*/
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
