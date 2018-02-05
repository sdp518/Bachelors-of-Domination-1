package sepr.game.saveandload;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.lwjgl.Sys;
import sepr.game.GameScreen;
import sepr.game.Main;
import sepr.game.Map;
import sepr.game.Player;
import sepr.game.Sector;

import java.io.*;
import java.util.HashMap;
import java.util.*;

public class SaveLoadManager {
    private Main main;
    private GameScreen gameScreen;

    private static String SAVE_FILE_PATH = "";
    private static int currentSaveID = -1;
    private static int numberOfSaves = 0;
    private static GameState loadedState;

    private static Boolean loadedSave;

    public SaveLoadManager(final Main main, GameScreen gameScreen) {
        this.main = main;
        this.gameScreen = gameScreen;

        loadedSave = false;

        String home = System.getProperty("user.home");

        String path = home + File.separator + "Bachelors-of-Domination" + File.separator + "saves" + File.separator + "saves.json";
        boolean directoryExists = new File(path).exists();

        this.SAVE_FILE_PATH = path;

        if(directoryExists) {
            LoadFromFile();
        } else {
            File file = new File(path);
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();

                JSONObject savesTemplate = new JSONObject();
                savesTemplate.put("Saves", this.numberOfSaves);
                savesTemplate.put("CurrentSaveID", this.currentSaveID);

                try {
                    FileWriter fileWriter = new FileWriter(this.SAVE_FILE_PATH);
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

    public boolean LoadFromFile(){

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(SAVE_FILE_PATH));
            JSONObject loadProperties = (JSONObject)obj;

            this.numberOfSaves = Integer.parseInt(loadProperties.get("Saves").toString());

            JSONObject gameStateJSON = (JSONObject)loadProperties.get("GameState"); // TODO Allow for more than one save


            JSONifier jifier = new JSONifier();
            jifier.SetStateJSON(gameStateJSON);
            GameState gameState = jifier.getStateFromJSON();

            this.loadedState = gameState;

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }

        return true;
    }

    public Map MapFromMapState(GameState.MapState mapState, HashMap<Integer, Player> players, HashMap<Integer, Sector> sectors){
        Map map = new Map(players, false, sectors);

        return map;
    }

    public HashMap<Integer, Player> PlayersFromPlayerState(GameState.PlayerState[] playerStates){
        HashMap<Integer, Player> players = new HashMap<Integer, Player>();

        for (GameState.PlayerState player : playerStates){
            players.put(player.hashMapPosition, new Player(player.id, player.collegeName, new Color(player.sectorColour.r, player.sectorColour.g, player.sectorColour.b, player.sectorColour.a), player.playerType, player.playerName, player.troopsToAllocate));
        }

        return players;
    }

    public HashMap<Integer, Sector> SectorsFromSectorState(GameState.SectorState[] sectorStates, HashMap<Integer, Player> players){
        HashMap<Integer, Sector> sectors = new HashMap<Integer, Sector>();

        for (GameState.SectorState sector : sectorStates){
            Pixmap map = new Pixmap(Gdx.files.internal(sector.texturePath));

            Color color = new Color(0, 0, 0, 1);

            for (java.util.Map.Entry<Integer, Player> player : players.entrySet()){
                if (player.getValue().getId() == sector.ownerId){
                    color = player.getValue().getSectorColour();
                }
            }

            sectors.put(sector.hashMapPosition, new Sector(sector.id, sector.ownerId, sector.fileName, sector.texturePath, map, sector.displayName, sector.unitsInSector, sector.reinforcementsProvided, sector.college, sector.neutral, sector.adjacentSectorIds, sector.sectorCentreX, sector.sectorCentreY, sector.decor, sector.allocated, color));
        }

        return sectors;
    }

    public boolean LoadSaveByID(int id){
        HashMap<Integer, Player> players = PlayersFromPlayerState(loadedState.playerStates);
        HashMap<Integer, Sector> sectors = SectorsFromSectorState(loadedState.mapState.sectorStates, players);

        Map loadedMap = MapFromMapState(loadedState.mapState, players, sectors);

        GameScreen gameScreen = new GameScreen(this.main, loadedState.currentPhase, loadedMap, players, loadedState.turnTimerEnabled, loadedState.maxTurnTime, loadedState.turnTimeStart, loadedState.turnOrder, loadedState.currentPlayerPointer);

        this.main.setGameScreenFromLoad(gameScreen);

        return true;
    }

    public boolean SaveToFile(JSONObject newSave){
        try {
            FileWriter fileWriter = new FileWriter(this.SAVE_FILE_PATH);
            fileWriter.write(newSave.toJSONString());
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean SaveByID(int id){
        GameState gameState = new GameState();
        gameState.currentPhase = this.gameScreen.getCurrentPhase();
        gameState.map = this.gameScreen.getMap();
        gameState.players = this.gameScreen.getPlayers();
        gameState.turnTimerEnabled = this.gameScreen.isTurnTimerEnabled();
        gameState.maxTurnTime = this.gameScreen.getMaxTurnTime();
        gameState.turnTimeStart = this.gameScreen.getTurnTimeStart();
        gameState.turnOrder = this.gameScreen.getTurnOrder();
        gameState.currentPlayerPointer = this.gameScreen.getCurrentPlayerPointer();

        GameState.MapState mapState = gameState.new MapState();

        mapState.sectors = gameState.map.getSectors();
        mapState.sectorStates = new GameState.SectorState[mapState.sectors.size()];

        int i = 0;

        for (java.util.Map.Entry<Integer, Sector> sector : mapState.sectors.entrySet()){
            Integer key = sector.getKey();
            Sector value = sector.getValue();

            GameState.SectorState sectorState = gameState.new SectorState();
            sectorState.hashMapPosition = key;
            sectorState.id = value.getId();
            sectorState.ownerId = value.getOwnerId();
            sectorState.displayName = value.getDisplayName();
            sectorState.unitsInSector = value.getUnitsInSector();
            sectorState.reinforcementsProvided = value.getReinforcementsProvided();
            sectorState.college = value.getCollege();
            sectorState.texturePath = value.getTexturePath();
            sectorState.neutral = value.isNeutral();
            sectorState.adjacentSectorIds = value.getAdjacentSectorIds();
            sectorState.sectorCentreX = value.getSectorCentreX();
            sectorState.sectorCentreY = value.getSectorCentreY();
            sectorState.decor = value.isDecor();
            sectorState.fileName = value.getFileName();
            sectorState.allocated = value.isAllocated();

            mapState.sectorStates[i] = sectorState;

            i++;
        }

        mapState.sectors = null;
        gameState.map = null;
        gameState.mapState = mapState;

        gameState.playerStates = new GameState.PlayerState[gameState.players.size()];

        i = 0;

        for (java.util.Map.Entry<Integer, Player> player : gameState.players.entrySet()) {
            Integer key = player.getKey();
            Player value = player.getValue();

            GameState.PlayerState playerState = gameState.new PlayerState();
            playerState.hashMapPosition = key;
            playerState.id = value.getId();
            playerState.collegeName = value.getCollegeName();
            playerState.playerName = value.getPlayerName();
            playerState.troopsToAllocate = value.getTroopsToAllocate();
            playerState.sectorColour = value.getSectorColour();
            playerState.playerType = value.getPlayerType();

            gameState.playerStates[i] = playerState;
            i++;
        }

        gameState.players = null;

        JSONObject newSave = new JSONObject();
        newSave.put("Saves", this.numberOfSaves);
        newSave.put("CurrentSaveID", this.currentSaveID);

        JSONifier jifier = new JSONifier();
        jifier.SetState(gameState);
        newSave.put("GameState", jifier.getJSONGameState());

        SaveToFile(newSave);

        return true;
    }

    /**
     * Returns the ID of the currently loaded save, generates a new ID if no file is loaded
     * @return The current save ID
     */
    public int GetCurrentSaveID(){
        if (!this.loadedSave){
            return GetNextSaveID();
        }else{
            return this.currentSaveID;
        }
    }

    /**
     * Returns the next available save ID
     * @return The nexxt available save ID
     */
    public int GetNextSaveID(){
        this.currentSaveID++;

        return this.currentSaveID;
    }

}
