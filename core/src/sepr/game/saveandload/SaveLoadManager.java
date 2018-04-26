package sepr.game.saveandload;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sepr.game.GameScreen;
import sepr.game.Main;
import sepr.game.Map;
import sepr.game.Player;
import sepr.game.Sector;
import sepr.game.utils.TurnPhaseType;

import java.io.*;
import java.util.HashMap;

//Todo understand and re-setup saving all data from file and testing
    //Todo Test with JUnit tests, saving looks all good by eye.
//Todo neaten saving into a clean function, commented for understanding.
//Todo reimplement saving and loading into the games code
    //TODO loading seems to be working, need to setup JUnits for loading, all looks good by eye.
//TODO implement saving of PVCTile into save game, make sure all loading procedures work, turn timer currently causes bug after saving.
//TODO Concurrent work on new units by Lewis needs to be implemented into saves, this will require meet-up and manual merge and edits of saving and loading.

/**
 * Class to manage saving and loading from files
 */
public class SaveLoadManager {
    public boolean savesToLoad = false; // Whether there are saves available to load from

    private Main main; // The main class
    private GameScreen gameScreen; // Game screen to read data from

    private static String save_path = ""; // Path to the saves file
    private static int currentSaveID = -1; // ID of the current save
    private static int numberOfSaves = 0; // Current number of saves
    private static GameState loadedState; // The state that has just been loaded

    private static Boolean loadedSave; // Whether a save has been loaded

    public SaveLoadManager(){ }

    /**
     * Initializes the SaveLoadManager
     * @param main Main class
     * @param gameScreen GameScreen to save data from
     */
    public SaveLoadManager(final Main main, GameScreen gameScreen) {
        this.main = main;
        this.gameScreen = gameScreen;

        loadedSave = false;

        String home = System.getProperty("user.home"); // Get the user's home directory

        String path = home + File.separator + "Bachelors-of-Domination" + File.separator + "saves" + File.separator + "saves.json"; // Generate the path to the saves.json file
        boolean directoryExists = new File(path).exists();

        this.save_path = path;

        if(directoryExists) { // Check that the directory exists
            loadFromFile(); // Load the file if it exists
        } else { // Create a blank saves file
            File file = new File(path);
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();

                JSONObject savesTemplate = new JSONObject();
                savesTemplate.put("Saves", this.numberOfSaves);
                savesTemplate.put("CurrentSaveID", this.currentSaveID);

                try {
                    FileWriter fileWriter = new FileWriter(this.save_path);
                    fileWriter.write(savesTemplate.toJSONString());
                    fileWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load GameState JSON from file
     * @return true if loading is successful
     */
    public boolean loadFromFile(){

        JSONParser parser = new JSONParser(); // Create JSON parser
        try {
            Object obj = parser.parse(new FileReader(save_path)); // Read file
            JSONObject loadProperties = (JSONObject)obj;

            this.numberOfSaves = Integer.parseInt(loadProperties.get("Saves").toString()); // Get number of saves

            if(this.numberOfSaves > 0){ // If saves exist, read the first save into the loaded state
                JSONObject gameStateJSON = (JSONObject)loadProperties.get("GameState"); // TODO Allow for more than one save


                JSONifier jifier = new JSONifier();
                jifier.SetStateJSON(gameStateJSON);
                GameState gameState = jifier.getStateFromJSON();

                this.loadedState = gameState;
                this.savesToLoad = true;
            }else{
                this.savesToLoad = false;
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Creates a Map from a given MapState
     * @param mapState
     * @param players
     * @param sectors
     * @return A Map object
     */
    public Map mapFromMapState(GameState.MapState mapState, HashMap<Integer, Player> players, HashMap<Integer, Sector> sectors){
        Map map = new Map(players, false, sectors);

        return map;
    }

    /**
     * Creates a Player from a given PlayerState
     * @param playerStates
     * @return A Player object
     */
    public HashMap<Integer, Player> playersFromPlayerState(GameState.PlayerState[] playerStates){
        HashMap<Integer, Player> players = new HashMap<Integer, Player>();

        for (GameState.PlayerState player : playerStates){
            players.put(player.hashMapPosition, new Player(player.id, player.collegeName, new Color(player.sectorColour.r, player.sectorColour.g, player.sectorColour.b, player.sectorColour.a), player.playerType, player.playerName, player.troopsToAllocate, player.ownsPVC));
        }

        return players;
    }

    /**
     * Creates a HashMap of Sectors from a list of SectorStates
     * @param sectorStates
     * @param players
     * @return A HashMap of Sectors
     */
    public HashMap<Integer, Sector> sectorsFromSectorState(GameState.SectorState[] sectorStates, HashMap<Integer, Player> players, boolean test){
        HashMap<Integer, Sector> sectors = new HashMap<Integer, Sector>();

        for (GameState.SectorState sector : sectorStates) {
            Pixmap map = new Pixmap(Gdx.files.internal(sector.texturePath));

            Color color = new Color(0, 0, 0, 1);

            for (java.util.Map.Entry<Integer, Player> player : players.entrySet()) {
                if (player.getValue().getId() == sector.ownerId) {
                    color = player.getValue().getSectorColour();
                }
            }

            if (test) {
                sectors.put(sector.hashMapPosition, new Sector(sector.id, sector.ownerId, sector.fileName, sector.texturePath, map, sector.displayName, sector.unitsInSector, sector.reinforcementsProvided, sector.college, sector.neutral, sector.adjacentSectorIds, sector.sectorCentreX, sector.sectorCentreY, sector.decor, sector.allocated, color, test));
            }else{
                sectors.put(sector.hashMapPosition, new Sector(sector.id, sector.ownerId, sector.fileName, sector.texturePath, map, sector.displayName, sector.unitsInSector, sector.reinforcementsProvided, sector.college, sector.neutral, sector.adjacentSectorIds, sector.sectorCentreX, sector.sectorCentreY, sector.decor, sector.allocated, color));
            }
        }

        return sectors;
    }

    /**
     * Loads a save file with a given ID
     * @param id
     * @return true if loading is successful
     */
    public boolean loadSaveByID(int id){
        //Map loadedMap = mapFromMapState(loadedState.mapState, players, sectors);

        //this.gameScreen = new GameScreen(this.main, loadedState.currentPhase, loadedMap, players, loadedState.turnTimerEnabled, loadedState.maxTurnTime, loadedState.turnTimeStart, loadedState.turnOrder, loadedState.currentPlayerPointer);
        this.gameScreen.setupGame(this.loadedState.players,
                this.loadedState.turnTimerEnabled,
                this.loadedState.maxTurnTime,
                true);
        this.updateSectors(this.gameScreen.getMap().getSectors());
        this.gameScreen.setTurnOrder(this.loadedState.turnOrder);
        this.gameScreen.setCurrentPlayerPointer(this.loadedState.currentPlayerPointer);
        if (this.loadedState.currentPhase == TurnPhaseType.REINFORCEMENT) {
            gameScreen.getCurrentPlayer().addTroopsToAllocate(-10);
        }
        gameScreen.setCurrentPhase(loadedState.currentPhase);
        for(Player temp: this.loadedState.players.values()) {
            if (temp.getOwnsPVC()) {
                this.gameScreen.getProViceChancellor().setPVCSpawned(true);
                break;
            }
        }
        if(loadedState.turnTimerEnabled) {
            this.gameScreen.setTurnTimeStart(System.currentTimeMillis() - loadedState.turnTimeElapsed);
        }
        this.main.returnGameScreen();
        this.gameScreen.getPhases().get(gameScreen.getCurrentPhase()).enterPhase(gameScreen.getCurrentPlayer());
        this.gameScreen.resetPausedTime();
        //Stage stage = this.main.getSaveScreen.getStage();
        return true;
    }

    public void updateSectors(HashMap<Integer, Sector> fullSectors) {
        Integer[] keys = fullSectors.keySet().toArray(new Integer[fullSectors.size()]);
        Integer[] playerKeys = this.loadedState.players.keySet().toArray(new Integer[this.loadedState.players.size()]);
        for(int i = 0; i < fullSectors.size(); i++) {
            Sector fullSector = fullSectors.get(keys[i]);
            Sector smallSector = this.loadedState.sectors.get(keys[i]);
            fullSector.setOwnerId(smallSector.getOwnerId());
            for(int j = 0; j < playerKeys.length; j++) {
                if (smallSector.getOwnerId() == this.loadedState.players.get(playerKeys[j]).getId()) {
                    fullSector.setOwner(this.loadedState.players.get(playerKeys[j]));
                }
            }
            fullSector.setDisplayName(smallSector.getDisplayName());
            fullSector.setUnitsInSector(smallSector.getUnitsInSector());
            fullSector.setReinforcementsProvided(smallSector.getReinforcementsProvided());
            fullSector.setCollege(smallSector.getCollege());
            fullSector.setNeutral(smallSector.isNeutral());
            fullSector.setIsPVCTile(smallSector.getIsPVCTile());
            if (fullSector.getIsPVCTile()) {
                fullSector.changeSectorColor(com.badlogic.gdx.graphics.Color.GOLD);
            }
        }
    }

    /**
     * Saves to the saves.json file
     * @param newSave
     * @return true if loading is successful
     */
    public boolean saveToFile(JSONObject newSave){
        try {
            FileWriter fileWriter = new FileWriter(this.save_path);
            fileWriter.write(newSave.toJSONString());
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Save to a save with a given ID
     * @param id
     * @return true if saving is successful
     */
    public boolean saveByID(int id) {
        GameState gameState = new GameState();
        gameState.currentPhase = this.gameScreen.getCurrentPhase(); // Store current phase
        gameState.players = this.gameScreen.getPlayers(); // Store players
        gameState.sectors = this.gameScreen.getMap().getSectors();
        gameState.turnTimerEnabled = this.gameScreen.isTurnTimerEnabled(); // Store whether the turn timer is enabled
        gameState.maxTurnTime = this.gameScreen.getMaxTurnTime(); // Store the maximum turn time
        gameState.turnTimeElapsed = this.gameScreen.getTurnTimeElapsed();
        gameState.turnOrder = this.gameScreen.getTurnOrder(); // Store the turn order
        gameState.currentPlayerPointer = this.gameScreen.getCurrentPlayerPointer(); // Store the pointer to the current player

        JSONObject newSave = new JSONObject(); // Create the save object
        newSave.put("Saves", this.numberOfSaves);
        newSave.put("CurrentSaveID", this.currentSaveID);

        JSONifier jifier = new JSONifier(); // Create a JSON representation of the state
        jifier.SetState(gameState);
        newSave.put("GameState", jifier.getJSONGameState());

        saveToFile(newSave); // Save the JSON representation to a file

        return true;
    }

    /**
     * Returns the ID of the currently loaded save, generates a new ID if no file is loaded
     * @return The current save ID
     */
    public int getCurrentSaveID(){
        if (!this.loadedSave){
            return getNextSaveID();
        }else{
            return this.currentSaveID;
        }
    }

    /**
     * Returns the next available save ID
     * @return The nexxt available save ID
     */
    public int getNextSaveID(){
        this.currentSaveID++;
        this.numberOfSaves++;

        return this.currentSaveID;
    }

}
