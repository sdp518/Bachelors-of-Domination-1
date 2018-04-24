package sepr.game.saveandload;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import sepr.game.Player;
import sepr.game.Sector;
import sepr.game.utils.TurnPhaseType;
import java.util.Map.Entry;

/**
 * Class to convert the game state to and from a JSON representation
 */
public class JSONifier {

    public GameState state; // The state of the game
    public JSONObject saveState; // The JSON state of the game

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

    /**
     * Get a game state from its JSON representation
     * @return GameState to load
     */
    /*public GameState getStateFromJSON() {
        GameState gameState = new GameState(); // GameState to return
        gameState.currentPhase = this.StringToPhase(this.saveState.get("CurrentPhase").toString()); // Get the current stage

        JSONArray sectors = (JSONArray) this.saveState.get("MapState"); // Get a JSONArray of the map Sectors
        gameState.mapState = gameState.new MapState(); // MapState to return
        gameState.mapState.sectorStates = new GameState.SectorState[sectors.size()]; // Array of SectorStates to load to the map

        int i = 0; // Index of current sector state to load

        for (Object obj : sectors){ // Iterate through JSON sectors
            JSONObject sector = (JSONObject)obj; // Cast to JSONObject

            gameState.mapState.sectorStates[i] = gameState.new SectorState(); // SectorState to load

            gameState.mapState.sectorStates[i].hashMapPosition = (int)(long)(Long)sector.get("HashMapPosition"); // Get Sector's HashMap position
            gameState.mapState.sectorStates[i].id = (int)(long)(Long)sector.get("ID"); // Get Sector's ID
            gameState.mapState.sectorStates[i].ownerId = (int)(long)(Long)sector.get("OwnerID"); // Get Sector's Owner's ID
            gameState.mapState.sectorStates[i].displayName = (String)sector.get("DisplayName"); // Get Sector's display name
            gameState.mapState.sectorStates[i].unitsInSector = (int)(long)(Long)sector.get("UnitsInSector"); // Get the number of units in the Sector
            gameState.mapState.sectorStates[i].reinforcementsProvided = (int)(long)(Long)sector.get("ReinforcementsProvided"); // Get the number of reinforcements provided
            gameState.mapState.sectorStates[i].college = (String)sector.get("College"); // Get the Sector's college
            gameState.mapState.sectorStates[i].texturePath = (String)sector.get("TexturePath"); // Get the Sector's texture filepath
            gameState.mapState.sectorStates[i].neutral = (Boolean)sector.get("Neutral"); // Get whether the Sector is nneutral

            JSONArray adjacentSectors = (JSONArray)sector.get("AdjacentSectorIDs"); // Get the JSONArray of adjacent Sectors
            gameState.mapState.sectorStates[i].adjacentSectorIds = new int[adjacentSectors.size()]; // Create a new array of adjacent Sectors

            int j = 0; // Index of current adjacent sector

            for (Object adj : adjacentSectors){ // Iterate through adjacent sectors and add each to the adjacent sectors
                gameState.mapState.sectorStates[i].adjacentSectorIds[j] = (int)(long)(Long)adj;

                j++;
            }

            gameState.mapState.sectorStates[i].sectorCentreX = (int)(long)(Long)sector.get("SectorCenterX"); // Get the Sector's X coordinate
            gameState.mapState.sectorStates[i].sectorCentreY = (int)(long)(Long)sector.get("SectorCenterY"); // Get the Sector's Y coordinate
            gameState.mapState.sectorStates[i].decor = (Boolean)sector.get("Decor"); // Get whether the Sector is decor
            gameState.mapState.sectorStates[i].fileName = (String)sector.get("FileName"); // Get the file name of the Sector
            gameState.mapState.sectorStates[i].allocated = (Boolean)sector.get("Allocated"); // Get whether the sector has been allocated

            i++;
        }

        JSONArray players = (JSONArray)this.saveState.get("PlayerState"); // Get the JSONArray of player states
        gameState.playerStates = new GameState.PlayerState[players.size()]; // Create an array of Player States

        int k = 0; // Current index of player states

        for (Object pl : players){ // Iterate through players
            JSONObject player = (JSONObject)pl;

            gameState.playerStates[k] = gameState.new PlayerState(); // Create new PlayerState

            gameState.playerStates[k].hashMapPosition = (int)(long)(Long)player.get("HashMapPosition"); // Get Player's HashMap position
            gameState.playerStates[k].id = (int)(long)(Long)player.get("ID"); // Get Player's ID
            gameState.playerStates[k].collegeName = GameSetupScreen.CollegeName.fromString((String)player.get("CollegeName")); // Get Player's college name
            gameState.playerStates[k].playerName = (String)player.get("PlayerName"); // Get Player's name
            gameState.playerStates[k].troopsToAllocate = (int)(long)(Long)player.get("TroopsToAllocate"); // Get the troops that the Player has left to allocate
            JSONObject colour = (JSONObject)player.get("SectorColour"); // Get the Player's Sector colour
            gameState.playerStates[k].sectorColour = new Color((float)(double)(Double)colour.get("R"),(float)(double)(Double)colour.get("G"),(float)(double)(Double)colour.get("B"),(float)(double)(Double)colour.get("A")); // Read Sector colour into a new colour object
            gameState.playerStates[k].playerType = PlayerType.fromString((String)player.get("PlayerType")); // Get the Player's type
            gameState.playerStates[k].ownsPVC = (boolean)(Boolean)player.get("OwnsPVC"); // Get whether the Player owns the PVC

            k++;
        }

        gameState.turnTimerEnabled = (Boolean)this.saveState.get("TurnTimerEnabled"); // Get whether the turn timer is enabled
        gameState.maxTurnTime = (int)(long)(Long)this.saveState.get("MaxTurnTime"); // Get the maximum turn time
        gameState.turnTimeStart = (Long)this.saveState.get("TurnTimeStart"); // Get the start time of the turn

        gameState.turnOrder = new ArrayList<Integer>(); // Turn order
        JSONArray turnOrderJSON = (JSONArray)this.saveState.get("TurnOrder"); // Get the turn order JSONArray

        for (Object obj : turnOrderJSON){ // Iterate through the turn order array and add the order to the game state
            gameState.turnOrder.add((int)(long)(Long)obj);
        }

        gameState.currentPlayerPointer = (int)(long)(Long)this.saveState.get("CurrentPlayerPointer"); // Get the pointer to the current Player

        return gameState;
    }*/

    /**
     * Creates JSON representation of GameState
     * @return JSON representation of GameState
     */
    public JSONObject getJSONGameState(){
        JSONObject gameStateObject = new JSONObject(); // Create JSON Object to store state
        gameStateObject.put("CurrentPhase", this.state.currentPhase.toString()); // Store the current phase

        JSONObject mapState = new JSONObject(); // Create JSON Object to store map state

        JSONArray sectorStates = new JSONArray(); // JSONArray of sector states

        //for (int i = 0; i < this.state.mapState.sectorStates.length; i++){ // Iterate through sectors in the map
        for(Entry<Integer, Sector> entry: this.state.sectors.entrySet()) {
            JSONObject sectorState = new JSONObject(); // Create a JSON object for each state
            Sector sector = entry.getValue();
            //GameState.SectorState sector = this.state.mapState.sectorStates[i];

            //sectorState.put("HashMapPosition", sector.hashMapPosition); // Store the Sector's position in the HashMap
            sectorState.put("HashMapPosition", entry.getKey()); // Store the Sector's position in the HashMap
            //sectorState.put("ID", sector.id); // Store the Sector's ID
            sectorState.put("ID", sector.getId()); // Store the Sector's ID
            //sectorState.put("OwnerID", sector.ownerId); // Store the Sector's Owner's ID
            sectorState.put("OwnerID", sector.getOwnerId()); // Store the Sector's Owner's ID
            //sectorState.put("DisplayName", sector.displayName); // Store the Sector's display name
            sectorState.put("DisplayName", sector.getDisplayName()); // Store the Sector's display name
            //sectorState.put("UnitsInSector", sector.unitsInSector); // Store the number of units in the Sector
            sectorState.put("UnitsInSector", sector.getUnitsInSector()); // Store the number of units in the Sector
            //sectorState.put("ReinforcementsProvided", sector.reinforcementsProvided); // Store the number of reinforcements provided to the sector
            sectorState.put("ReinforcementsProvided", sector.getReinforcementsProvided()); // Store the number of reinforcements provided to the sector
            //sectorState.put("College", sector.college); // Store the college that the Sector belongs to
            sectorState.put("College", sector.getCollege()); // Store the college that the Sector belongs to
            //sectorState.put("TexturePath", sector.texturePath); // Store the path to the Sector's texture
            //sectorState.put("TexturePath", sector.getTexturePath()); // Store the path to the Sector's texture
            //sectorState.put("Neutral", sector.neutral); // Store whether the Sector is neutral
            sectorState.put("Neutral", sector.isNeutral()); // Store whether the Sector is neutral
            sectorState.put("PVCTile", sector.getIsPVCTile());

            JSONArray adjSectors = new JSONArray(); // JSONArray of adjacent sectors

            /*for (int j = 0; j < sector.adjacentSectorIds.length; j++){ // Store adjacent sectors
                adjSectors.add(sector.adjacentSectorIds[j]);
            }

            sectorState.put("AdjacentSectorIDs", adjSectors);

            sectorState.put("SectorCenterX", sector.sectorCentreX); // Store sector center
            sectorState.put("SectorCenterY", sector.sectorCentreY);
            sectorState.put("Decor", sector.decor); // Store whether the sector is decor
            sectorState.put("FileName", sector.fileName); // Store sector texture filename
            sectorState.put("Allocated", sector.allocated); // Store whether the sector has been allocated*/

            sectorStates.add(sectorState);
        }

        gameStateObject.put("MapState", sectorStates); // Store the map state

        JSONArray playerStates = new JSONArray();

        //for (int k = 0; k < this.state.players.size(); k++){
        for(Entry<Integer, Player> entry: this.state.players.entrySet()) {
            JSONObject playerState = new JSONObject();
            Player player = entry.getValue();
            //GameState.PlayerState player = this.state.playerStates[k];

            //playerState.put("HashMapPosition", player.hashMapPosition); // Store Player HashMap position
            playerState.put("HashMapPosition", entry.getKey());
            //playerState.put("ID", player.id); // Store Player ID
            playerState.put("ID", player.getId());
            //playerState.put("CollegeName", player.collegeName.getCollegeName()); // Store Player college name
            playerState.put("CollegeName", player.getCollegeName());
            //playerState.put("PlayerName", player.playerName); // Store Player name
            playerState.put("PlayerName", player.getPlayerName());
            //playerState.put("TroopsToAllocate", player.troopsToAllocate); // Store the number of troops left to allocate
            playerState.put("TroopsToAllocate", player.getTroopsToAllocate());
            //playerState.put("OwnsPVC", player.ownsPVC); // Store whether the Player owns the OVC
            playerState.put("OwnsPVC", player.getOwnsPVC());

            JSONObject colour = new JSONObject(); // Store the Player's colour
            colour.put("R", player.getSectorColour().r);
            colour.put("G", player.getSectorColour().g);
            colour.put("B", player.getSectorColour().b);
            colour.put("A", player.getSectorColour().a);
            playerState.put("SectorColour", colour);

            playerState.put("PlayerType", player.getPlayerType().toString()); // Store the Player's type

            playerStates.add(playerState);
        }

        gameStateObject.put("PlayerState", playerStates); // Store the Player's state

        gameStateObject.put("TurnTimerEnabled", this.state.turnTimerEnabled); // Store whether the turn timer is enabled
        gameStateObject.put("MaxTurnTime", this.state.maxTurnTime); // Store the max turn time
        //gameStateObject.put("TurnTimeStart", this.state.turnTimeStart); // Store the start time of the turn
        gameStateObject.put("TurnTimeElapsed", this.state.turnTimeElapsed);

        JSONArray turnOrder = new JSONArray(); // Store the order of player turns
        for (int i = 0; i < this.state.turnOrder.size(); i++){
            turnOrder.add(this.state.turnOrder.get(i));
        }

        gameStateObject.put("TurnOrder", turnOrder);

        gameStateObject.put("CurrentPlayerPointer", this.state.currentPlayerPointer); // Store the pointer to the current player

        return gameStateObject;
    }

    /**
     * Converts a string to the corresponding phase
     * @param phase string
     * @return Phase object
     */
    public TurnPhaseType StringToPhase(String phase) {
        for (TurnPhaseType type : TurnPhaseType.values()){
            if (type.equalsName(phase)){
                return type;
            }
        }

        return TurnPhaseType.INVALID;
    }

}