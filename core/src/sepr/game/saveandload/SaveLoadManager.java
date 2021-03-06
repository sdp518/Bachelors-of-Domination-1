package sepr.game.saveandload;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sepr.game.GameScreen;
import sepr.game.Main;
import sepr.game.Player;
import sepr.game.Sector;
import sepr.game.utils.TurnPhaseType;

import java.io.*;
import java.util.HashMap;

//Todo Test with JUnit tests
//TODO Implement new elements of the game into saving and loading.

/**
 * Class to manage saving and loading from files
 */
public class SaveLoadManager {
    private GameScreen gameScreen; // Game screen to read data from

    private String savePath = ""; // Path to the saves file
    private GameState[] loadedStates = new GameState[4]; // The state that has just been loaded

    private boolean directoryExists;

    public SaveLoadManager(){ }

    /**
     * MODIFIED - ASSESSMENT 4
     * Initializes the SaveLoadManager
     * @param gameScreen GameScreen to save data from
     */
    public SaveLoadManager( GameScreen gameScreen) {
        this.gameScreen = gameScreen;

        String home = System.getProperty("user.home"); // Get the user's home directory

        String path = home + File.separator + "Bachelors-of-Domination" + File.separator + "saves" + File.separator + "saves.json"; // Generate the path to the saves.json file
        this.directoryExists = new File(path).exists();

        this.savePath = path;

        if(directoryExists) { // Check that the directory exists
            loadFromFile(); // Load the file if it exists
        }
    }

    /**
     * MODIFIED - ASSESSMENT 4
     * Load all saved GameStates JSON from file
     */
    public void loadFromFile(){

        JSONParser parser = new JSONParser(); // Create JSON parser
        try {
            Object fullFile = parser.parse(new FileReader(savePath)); // Read file
            JSONArray allSaves = (JSONArray) fullFile;

            for(Object obj: allSaves) {
                JSONObject jObj = (JSONObject) obj;
                int save = Integer.parseInt(jObj.get("CurrentSaveID").toString());
                JSONObject gameStateJSON = (JSONObject) jObj.get("GameState");

                JSONifier jifier = new JSONifier();
                jifier.SetStateJSON(gameStateJSON);
                GameState gameState = jifier.getStateFromJSON();

                this.loadedStates[save] = gameState;
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    /**
     * Loads a save game state with a given ID
     * @param id The ID of the save file to access the correct one.
     */
    public void loadSaveByID(int id){
        this.gameScreen.setupGame(this.loadedStates[id].players,
                this.loadedStates[id].turnTimerEnabled,
                this.loadedStates[id].maxTurnTime,
                true);
        this.updateSectors(this.gameScreen.getMap().getSectors(), id);
        this.gameScreen.setTurnOrder(this.loadedStates[id].turnOrder);
        this.gameScreen.setCurrentPlayerPointer(this.loadedStates[id].currentPlayerPointer);
        this.gameScreen.setCardDeck(this.loadedStates[id].cardDeck);

        if (this.loadedStates[id].currentPhase == TurnPhaseType.REINFORCEMENT) {
            gameScreen.getCurrentPlayer().addPostGraduatesToAllocate(-1);
            gameScreen.getCurrentPlayer().addUndergraduatesToAllocate(-5);
        }
        gameScreen.setCurrentPhase(loadedStates[id].currentPhase);

        for(Player temp: this.loadedStates[id].players.values()) {
            if (temp.getOwnsPVC()) {
                this.gameScreen.getProViceChancellor().setPVCSpawned(true);
                break;
            }
        }

        if(loadedStates[id].turnTimerEnabled) {
            this.gameScreen.setTurnTimeStart(System.currentTimeMillis() - loadedStates[id].turnTimeElapsed);
        }
        this.gameScreen.getPhases().get(gameScreen.getCurrentPhase()).enterPhase(gameScreen.getCurrentPlayer());
        this.gameScreen.resetPausedTime();
    }

    /**
     * NEW - ASSESSMENT 4
     * Transfers all of the data stored into the game state into the correct place in each sector.
     * @param fullSectors the list of sectors created by the game.
     * @param id the id of the save being loaded.
     */
    private void updateSectors(HashMap<Integer, Sector> fullSectors, int id) {
        Integer[] keys = fullSectors.keySet().toArray(new Integer[fullSectors.size()]);
        Integer[] playerKeys = this.loadedStates[id].players.keySet().toArray(new Integer[this.loadedStates[id].players.size()]);

        for(int i = 0; i < fullSectors.size(); i++) {
            Sector fullSector = fullSectors.get(keys[i]);
            Sector smallSector = this.loadedStates[id].sectors.get(keys[i]);
            fullSector.setOwnerId(smallSector.getOwnerId());

            for(Integer key: playerKeys) {
                if (smallSector.getOwnerId() == this.loadedStates[id].players.get(key).getId()) {
                    fullSector.setOwner(this.loadedStates[id].players.get(key));
                }
            }
            fullSector.setDisplayName(smallSector.getDisplayName());
            fullSector.addPostgraduate(-1 * fullSector.getPostgraduatesInSector());
            fullSector.addUndergraduates(-1 * fullSector.getUndergraduatesInSector());
            fullSector.addPostgraduate(smallSector.getPostgraduatesInSector());
            fullSector.addUndergraduates(smallSector.getUndergraduatesInSector());
            fullSector.setUndergraduatesProvided(smallSector.getUndergraduatesProvided());
            fullSector.setCollege(smallSector.getCollege());
            fullSector.setNeutral(smallSector.isNeutral());
            fullSector.setIsPVCTile(smallSector.getIsPVCTile());

            if (fullSector.getIsPVCTile()) {
                fullSector.changeSectorColor(com.badlogic.gdx.graphics.Color.GOLD);
            }
        }
    }

    /**
     * MODIFIED - ASSESSMENT 4
     * Saves all current game states to the saves.json file as a JSON string.
     */
    public void saveToFile() {
        JSONArray allSaves = new JSONArray();
        JSONifier jifier = new JSONifier();

        for(int i = 0; i < 4; i++) {
            if (loadedStates[i] != null) {
                JSONObject save = new JSONObject();
                save.put("CurrentSaveID", i);
                jifier.SetState(loadedStates[i]);
                save.put("GameState", jifier.getJSONGameState());
                allSaves.add(save);
            }
        }

        try {
            if (!directoryExists) {
                File file = new File(this.savePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
                this.directoryExists = true;
            }
            FileWriter fileWriter = new FileWriter(this.savePath);
            fileWriter.write(allSaves.toJSONString());
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * MODIFIED - ASSESSMENT 4
     * Save to a save slot with a given ID
     * @param id The id given to the game state in the process of saving the game.
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
        gameState.cardDeck = this.gameScreen.getCardDeck();

        this.loadedStates[id] = gameState;

        return true;
    }

    /**
     * NEW - ASSESSMENT 4
     * Gives the current game states stored in the program.
     * @return A GameState array of all current save games.
     */
    public GameState[] getLoadedStates() {
        return this.loadedStates;
    }

}
