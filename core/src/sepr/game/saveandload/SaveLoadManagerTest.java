package sepr.game.saveandload;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sepr.game.GameSetupScreen;
import sepr.game.Player;
import sepr.game.utils.PlayerType;
import sepr.game.utils.TurnPhaseType;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class SaveLoadManagerTest {
    public SaveLoadManager saveLoadManager;
    public GameState gameState;

    @Before
    public void setUp() throws Exception {
        this.saveLoadManager = new SaveLoadManager();

        this.gameState = new GameState();
        gameState.mapState = gameState.new MapState();


        gameState.mapState.sectorStates = new GameState.SectorState[5];

        for (int i = 0; i < 4; i++){
            GameState.SectorState sectorState = gameState.new SectorState();

            sectorState.hashMapPosition = i;
            sectorState.id = i;
            sectorState.ownerId = i;
            sectorState.displayName = Integer.toString(i);
            sectorState.unitsInSector = i;
            sectorState.reinforcementsProvided = i;
            sectorState.college = "DERWENT";
            sectorState.texturePath = "uiComponents/menuBackground.png";
            sectorState.neutral = true;
            sectorState.adjacentSectorIds = new int[3];
            sectorState.adjacentSectorIds[0] = 1;
            sectorState.adjacentSectorIds[1] = 2;
            sectorState.adjacentSectorIds[2] = 3;
            sectorState.sectorCentreX = i;
            sectorState.sectorCentreY = i;
            sectorState.decor = false;
            sectorState.fileName = "uiComponents/menuBackground.png";
            sectorState.allocated = false;

            gameState.mapState.sectorStates[i] = sectorState;
        }

        gameState.playerStates = new GameState.PlayerState[4];

        for (int i = 0; i < 4; i++){
            GameState.PlayerState playerState = gameState.new PlayerState();

            playerState.hashMapPosition = i;
            playerState.id = i;
            playerState.collegeName = GameSetupScreen.CollegeName.DERWENT;
            playerState.playerName = Integer.toString(i);
            playerState.troopsToAllocate = i;
            playerState.sectorColour = new Color(0, 0, 0, 0);
            playerState.playerType = PlayerType.HUMAN;
            playerState.ownsPVC = false;

            gameState.playerStates[i] = playerState;
        }

        gameState.currentPhase = TurnPhaseType.MOVEMENT;
        gameState.turnTimerEnabled = true;
        gameState.maxTurnTime = 10;
        gameState.turnTimeStart = 1;

        gameState.turnOrder = new ArrayList<Integer>();
        gameState.turnOrder.add(0);
        gameState.turnOrder.add(1);
        gameState.turnOrder.add(2);
        gameState.turnOrder.add(3);

        gameState.currentPlayerPointer = 2;
    }

    @After
    public void tearDown() throws Exception {
        saveLoadManager = null;
        gameState = null;
    }

    @Test
    public void mapFromMapState() {
    }

    @Test
    public void playersFromPlayerState() {
        HashMap<Integer, Player> playerHashMap = saveLoadManager.PlayersFromPlayerState(gameState.playerStates);

        assertTrue("Player HashMap Size", playerHashMap.size() == 4);

        int index = 0;

        for (java.util.Map.Entry<Integer, Player> playerEntry : playerHashMap.entrySet()){
            Integer key = playerEntry.getKey();
            Player value = playerEntry.getValue();

            assertTrue("Player ID", value.getId() == index);
            assertTrue("Player College", value.getCollegeName() == GameSetupScreen.CollegeName.DERWENT);
            assertTrue("Player Name", value.getPlayerName().equalsIgnoreCase(Integer.toString(index)));
            assertTrue("Player Troops To Allocate", value.getTroopsToAllocate() == index);
            assertTrue("Player Sector Colour", value.getSectorColour().equals(new Color(0, 0, 0, 0)));
            assertTrue("Player Type",value.getPlayerType() == PlayerType.HUMAN);
            assertTrue("Player Owns PVC",value.getOwnsPVC() == false);

            index++;
        }
    }

    @Test
    public void sectorsFromSectorState() {
    }

    @Test
    public void getCurrentSaveID() {
    }

    @Test
    public void getNextSaveID() {
    }
}