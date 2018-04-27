package sepr.game.saveandload;

import com.badlogic.gdx.graphics.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sepr.game.GameSetupScreen.CollegeName;
import sepr.game.Player;
import sepr.game.Sector;
import sepr.game.utils.PlayerType;
import sepr.game.utils.TurnPhaseType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

/**
 * Class to convert the game state to and from a JSON representation
 */
public class JSONifier {

    private GameState state; // The state of the game
    private JSONObject saveState; // The JSON state of the game

    /**
     * Set the state to represent as JSON
     * @param state The game state
     */
    public void SetState(GameState state){
        this.state = state;
    }

    /**
     * Set the JSON Object to read from
     * @param json JSON Representation of game state to load
     */
    public void SetStateJSON(JSONObject json){
        this.saveState = json;
    }

    public GameState getStateFromJSON() {
        GameState gameState = new GameState();
        gameState.currentPhase = this.StringToPhase(this.saveState.get("CurrentPhase").toString());
        gameState.currentPlayerPointer = Integer.parseInt(this.saveState.get("CurrentPlayerPointer").toString());
        gameState.turnTimeElapsed = Long.parseLong(this.saveState.get("TurnTimeElapsed").toString());
        gameState.maxTurnTime = Integer.parseInt(this.saveState.get("MaxTurnTime").toString());
        gameState.turnTimerEnabled = Boolean.parseBoolean(this.saveState.get("TurnTimerEnabled").toString());

        JSONArray sectors = (JSONArray) this.saveState.get("MapState");
        HashMap<Integer, Sector> tempSectors = new HashMap<Integer, Sector>();
        Sector PVC = null;

        for(Object obj: sectors) {
            Sector temp = new Sector();
            JSONObject sector = (JSONObject) obj;
            temp.setOwnerId(Integer.parseInt(sector.get("OwnerID").toString()));
            temp.setDisplayName(sector.get("DisplayName").toString());
            temp.setUnitsInSector(Integer.parseInt(sector.get("UnitsInSector").toString()));
            temp.setReinforcementsProvided(Integer.parseInt(sector.get("ReinforcementsProvided").toString()));
            temp.setNeutral(Boolean.parseBoolean(sector.get("Neutral").toString()));
            temp.setIsPVCTile(Boolean.parseBoolean(sector.get("PVCTile").toString()));
            temp.setCollege(sector.get("College").toString());
            tempSectors.put(Integer.parseInt(sector.get("HashMapPosition").toString()), temp);
            if (temp.getIsPVCTile()) {
                PVC = temp;
            }
        }
        gameState.sectors = tempSectors;

        JSONArray players = (JSONArray) this.saveState.get("PlayerState");
        HashMap<Integer, Player> tempPlayers = new HashMap<Integer, Player>();

        for(Object obj: players) {
            JSONObject temp = (JSONObject) obj;
            int id = Integer.parseInt(temp.get("ID").toString());
            CollegeName collegeName = CollegeName.fromString(temp.get("CollegeName").toString());
            String playerName = temp.get("PlayerName").toString();
            int troopsToAllocate = Integer.parseInt(temp.get("TroopsToAllocate").toString());
            boolean ownsPVC = Boolean.parseBoolean(temp.get("OwnsPVC").toString());
            PlayerType playerType = PlayerType.fromString(temp.get("PlayerType").toString());
            JSONObject colors = (JSONObject) temp.get("SectorColour");
            Color color = new Color(Float.parseFloat(colors.get("R").toString()), Float.parseFloat(colors.get("G").toString()), Float.parseFloat(colors.get("B").toString()), Float.parseFloat(colors.get("A").toString()));
            Player player;

            if (playerType.equals(PlayerType.HUMAN)) {
                player = Player.createHumanPlayer(id, collegeName, color, playerName);
                player.setTroopsToAllocate(troopsToAllocate);
                player.setOwnsPVC(ownsPVC);
            } else {
                player = Player.createNeutralPlayer(id);
            }

            if ((PVC != null) && (player.getId() == PVC.getOwnerId())) {
                player.setOwnsPVC(true);
            }
            tempPlayers.put(Integer.parseInt(temp.get("HashMapPosition").toString()), player);
        }
        gameState.players = tempPlayers;

        LinkedList<Integer> turnOrder = new LinkedList<Integer>();
        JSONArray turnOrderJSON = (JSONArray) this.saveState.get("TurnOrder");

        for (Object obj : turnOrderJSON){ // Iterate through the turn order array and add the order to the game state
            turnOrder.add(Integer.parseInt(obj.toString()));
        }
        gameState.turnOrder = turnOrder;

        return gameState;
    }

    /**
     * Creates JSON representation of GameState
     * @return JSON representation of GameState
     */
    public JSONObject getJSONGameState(){
        JSONObject gameStateObject = new JSONObject(); // Create JSON Object to store state

        gameStateObject.put("CurrentPhase", this.state.currentPhase.toString()); // Store the current phase
        gameStateObject.put("TurnTimerEnabled", this.state.turnTimerEnabled); // Store whether the turn timer is enabled
        gameStateObject.put("MaxTurnTime", this.state.maxTurnTime); // Store the max turn time
        gameStateObject.put("TurnTimeElapsed", this.state.turnTimeElapsed);
        gameStateObject.put("CurrentPlayerPointer", this.state.currentPlayerPointer); // Store the pointer to the current player

        JSONArray sectorStates = new JSONArray(); // JSONArray of sector states

        for(Entry<Integer, Sector> entry: this.state.sectors.entrySet()) {
            JSONObject sectorState = new JSONObject(); // Create a JSON object for each state
            Sector sector = entry.getValue();

            sectorState.put("HashMapPosition", entry.getKey()); // Store the Sector's position in the HashMap
            sectorState.put("ID", sector.getId()); // Store the Sector's ID
            sectorState.put("OwnerID", sector.getOwnerId()); // Store the Sector's Owner's ID
            sectorState.put("DisplayName", sector.getDisplayName()); // Store the Sector's display name
            sectorState.put("UnitsInSector", sector.getUnitsInSector()); // Store the number of units in the Sector
            sectorState.put("ReinforcementsProvided", sector.getReinforcementsProvided()); // Store the number of reinforcements provided to the sector
            sectorState.put("College", sector.getCollege()); // Store the college that the Sector belongs to
            sectorState.put("Neutral", sector.isNeutral()); // Store whether the Sector is neutral
            sectorState.put("PVCTile", sector.getIsPVCTile());

            sectorStates.add(sectorState);
        }

        gameStateObject.put("MapState", sectorStates); // Store the map state

        JSONArray playerStates = new JSONArray();

        for(Entry<Integer, Player> entry: this.state.players.entrySet()) {
            JSONObject playerState = new JSONObject();
            Player player = entry.getValue();

            playerState.put("HashMapPosition", entry.getKey());
            playerState.put("ID", player.getId());
            playerState.put("CollegeName", player.getCollegeName().getCollegeName());
            playerState.put("PlayerName", player.getPlayerName());
            playerState.put("TroopsToAllocate", player.getTroopsToAllocate());
            playerState.put("OwnsPVC", player.getOwnsPVC());
            playerState.put("PlayerType", player.getPlayerType().toString()); // Store the Player's type

            JSONObject colour = new JSONObject(); // Store the Player's colour
            colour.put("R", player.getSectorColour().r);
            colour.put("G", player.getSectorColour().g);
            colour.put("B", player.getSectorColour().b);
            colour.put("A", player.getSectorColour().a);
            playerState.put("SectorColour", colour);

            playerStates.add(playerState);
        }

        gameStateObject.put("PlayerState", playerStates); // Store the Player's state

        JSONArray turnOrder = new JSONArray(); // Store the order of player turns
        turnOrder.addAll(this.state.turnOrder);

        gameStateObject.put("TurnOrder", turnOrder);

        return gameStateObject;
    }

    /**
     * Converts a string to the corresponding phase
     * @param phase string
     * @return Phase object
     */
    private TurnPhaseType StringToPhase(String phase) {
        for (TurnPhaseType type : TurnPhaseType.values()){
            if (type.equalsName(phase)){
                return type;
            }
        }

        return TurnPhaseType.INVALID;
    }

}