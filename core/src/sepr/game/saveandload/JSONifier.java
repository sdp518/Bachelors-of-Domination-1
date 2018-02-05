package sepr.game.saveandload;

import com.badlogic.gdx.graphics.Color;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lwjgl.Sys;
import sepr.game.GameSetupScreen;
import sepr.game.Phase;
import sepr.game.utils.PlayerType;
import sepr.game.utils.TurnPhaseType;

import java.util.*;

public class JSONifier {

    public GameState state;
    public JSONObject saveState;

    public JSONifier(){

    }

    public void SetState(GameState state){
        this.state = state;
    }

    public void SetStateJSON(JSONObject json){
        this.saveState = json;
    }

    public GameState getStateFromJSON() {
        GameState gameState = new GameState();
        gameState.currentPhase = this.StringToPhase(this.saveState.get("CurrentPhase").toString());

        JSONArray sectors = (JSONArray) this.saveState.get("MapState");
        gameState.mapState = gameState.new MapState();
        gameState.mapState.sectorStates = new GameState.SectorState[sectors.size()];

        int i = 0;

        for (Object obj : sectors){
            JSONObject sector = (JSONObject)obj;

            gameState.mapState.sectorStates[i] = gameState.new SectorState();

            gameState.mapState.sectorStates[i].hashMapPosition = (int)(long)(Long)sector.get("HashMapPosition");
            gameState.mapState.sectorStates[i].id = (int)(long)(Long)sector.get("ID");
            gameState.mapState.sectorStates[i].ownerId = (int)(long)(Long)sector.get("OwnerID");
            gameState.mapState.sectorStates[i].displayName = (String)sector.get("DisplayName");
            gameState.mapState.sectorStates[i].unitsInSector = (int)(long)(Long)sector.get("UnitsInSector");
            gameState.mapState.sectorStates[i].reinforcementsProvided = (int)(long)(Long)sector.get("ReinforcementsProvided");
            gameState.mapState.sectorStates[i].college = (String)sector.get("College");
            gameState.mapState.sectorStates[i].texturePath = (String)sector.get("TexturePath");
            gameState.mapState.sectorStates[i].neutral = (Boolean)sector.get("Neutral");

            JSONArray adjacentSectors = (JSONArray)sector.get("AdjacentSectorIDs");
            gameState.mapState.sectorStates[i].adjacentSectorIds = new int[adjacentSectors.size()];

            int j = 0;

            for (Object adj : adjacentSectors){
                gameState.mapState.sectorStates[i].adjacentSectorIds[j] = (int)(long)(Long)adj;

                j++;
            }

            gameState.mapState.sectorStates[i].sectorCentreX = (int)(long)(Long)sector.get("SectorCenterX");
            gameState.mapState.sectorStates[i].sectorCentreY = (int)(long)(Long)sector.get("SectorCenterY");
            gameState.mapState.sectorStates[i].decor = (Boolean)sector.get("Decor");
            gameState.mapState.sectorStates[i].fileName = (String)sector.get("FileName");
            gameState.mapState.sectorStates[i].allocated = (Boolean)sector.get("Allocated");

            i++;
        }

        JSONArray players = (JSONArray)this.saveState.get("PlayerState");
        gameState.playerStates = new GameState.PlayerState[players.size()];

        int k = 0;

        for (Object pl : players){
            JSONObject player = (JSONObject)pl;

            gameState.playerStates[k] = gameState.new PlayerState();

            gameState.playerStates[k].hashMapPosition = (int)(long)(Long)player.get("HashMapPosition");
            gameState.playerStates[k].id = (int)(long)(Long)player.get("ID");
            gameState.playerStates[k].collegeName = GameSetupScreen.CollegeName.fromString((String)player.get("CollegeName"));
            gameState.playerStates[k].playerName = (String)player.get("PlayerName");
            gameState.playerStates[k].troopsToAllocate = (int)(long)(Long)player.get("TroopsToAllocate");
            JSONObject colour = (JSONObject)player.get("SectorColour");
            gameState.playerStates[k].sectorColour = new Color((float)(double)(Double)colour.get("R"),(float)(double)(Double)colour.get("G"),(float)(double)(Double)colour.get("B"),(float)(double)(Double)colour.get("A"));
            gameState.playerStates[k].playerType = PlayerType.fromString((String)player.get("PlayerType"));

            k++;
        }

        gameState.turnTimerEnabled = (Boolean)this.saveState.get("TurnTimerEnabled");
        gameState.maxTurnTime = (int)(long)(Long)this.saveState.get("MaxTurnTime");
        gameState.turnTimeStart = (Long)this.saveState.get("TurnTimeStart");

        gameState.turnOrder = new ArrayList<Integer>();
        JSONArray turnOrderJSON = (JSONArray)this.saveState.get("TurnOrder");

        for (Object obj : turnOrderJSON){
            gameState.turnOrder.add((int)(long)(Long)obj);
        }

        gameState.currentPlayerPointer = (int)(long)(Long)this.saveState.get("CurrentPlayerPointer");

        return gameState;
    }

    public JSONObject getJSONGameState(){
        JSONObject gameStateObject = new JSONObject();
        gameStateObject.put("CurrentPhase", this.state.currentPhase.toString());

        JSONObject mapState = new JSONObject();

        JSONArray sectorStates = new JSONArray();

        for (int i = 0; i < this.state.mapState.sectorStates.length; i++){
            JSONObject sectorState = new JSONObject();
            GameState.SectorState sector = this.state.mapState.sectorStates[i];

            sectorState.put("HashMapPosition", sector.hashMapPosition);
            sectorState.put("ID", sector.id);
            sectorState.put("OwnerID", sector.ownerId);
            sectorState.put("DisplayName", sector.displayName);
            sectorState.put("UnitsInSector", sector.unitsInSector);
            sectorState.put("ReinforcementsProvided", sector.reinforcementsProvided);
            sectorState.put("College", sector.college);
            sectorState.put("TexturePath", sector.texturePath);
            sectorState.put("Neutral", sector.neutral);

            JSONArray adjSectors = new JSONArray();

            for (int j = 0; j < sector.adjacentSectorIds.length; j++){
                adjSectors.add(sector.adjacentSectorIds[j]);
            }

            sectorState.put("AdjacentSectorIDs", adjSectors);

            sectorState.put("SectorCenterX", sector.sectorCentreX);
            sectorState.put("SectorCenterY", sector.sectorCentreY);
            sectorState.put("Decor", sector.decor);
            sectorState.put("FileName", sector.fileName);
            sectorState.put("Allocated", sector.allocated);

            sectorStates.add(sectorState);
        }

        gameStateObject.put("MapState", sectorStates);

        JSONArray playerStates = new JSONArray();

        for (int k = 0; k < this.state.playerStates.length; k++){
            JSONObject playerState = new JSONObject();
            GameState.PlayerState player = this.state.playerStates[k];

            playerState.put("HashMapPosition", player.hashMapPosition);
            playerState.put("ID", player.id);
            playerState.put("CollegeName", player.collegeName.getCollegeName());
            playerState.put("PlayerName", player.playerName);
            playerState.put("TroopsToAllocate", player.troopsToAllocate);

            JSONObject colour = new JSONObject();
            colour.put("R", player.sectorColour.r);
            colour.put("G", player.sectorColour.g);
            colour.put("B", player.sectorColour.b);
            colour.put("A", player.sectorColour.a);
            playerState.put("SectorColour", colour);

            playerState.put("PlayerType", player.playerType.toString());

            playerStates.add(playerState);
        }

        gameStateObject.put("PlayerState", playerStates);

        gameStateObject.put("TurnTimerEnabled", this.state.turnTimerEnabled);
        gameStateObject.put("MaxTurnTime", this.state.maxTurnTime);
        gameStateObject.put("TurnTimeStart", this.state.turnTimeStart);

        JSONArray turnOrder = new JSONArray();
        for (int i = 0; i < this.state.turnOrder.size(); i++){
            turnOrder.add(this.state.turnOrder.get(i));
        }

        gameStateObject.put("TurnOrder", turnOrder);

        gameStateObject.put("CurrentPlayerPointer", this.state.currentPlayerPointer);

        return gameStateObject;
    }

    public TurnPhaseType StringToPhase(String phase) {
        for (TurnPhaseType type : TurnPhaseType.values()){
            if (type.equalsName(phase)){
                return type;
            }
        }

        return TurnPhaseType.INVALID;
    }

}