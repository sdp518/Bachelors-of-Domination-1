package sepr.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import sepr.game.gangmembers.GangMembers;
import sepr.game.gangmembers.Postgraduates;
import sepr.game.gangmembers.Undergraduates;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * stores the game map and the sectors within it
 */
public class Map {
    private HashMap<Integer, Sector> sectors; // mapping of sector ID to the sector object
    private List<UnitChangeParticle> particles; // list of active particle effects displaying the changes to the amount of units on a sector
    private PVC proViceChancellor;

    private BitmapFont font; // font for rendering sector unit data
    private GlyphLayout layout = new GlyphLayout();

    private Texture troopCountOverlay = new Texture("uiComponents/troopCountOverlay.png");
    private Texture pvcOutline = new Texture("uiComponents/pvcOutline.png");

    private int[] unitsToMove; // units to move from an attacking to conquered sector, 3 index array : [0] amount to move; [1] source sector id ; [2] target sector id

    private Random random;

    private GameScreen gameScreen;

    /**
     * Performs the maps initial setup
     * loads the sector data from the sectorProperties.csv file
     * allocates each sector to the players in the passed players hashmap
     *
     * @param players               hashmap of players who are in the game
     * @param allocateNeutralPlayer if true then the neutral player should be allocated the default neutral sectors else they should be allocated no sectors
     */
    public Map(HashMap<Integer, Player> players, boolean allocateNeutralPlayer) {
        random = new Random();

        this.loadSectors();
        font = WidgetFactory.getFontSmall();

        particles = new ArrayList<UnitChangeParticle>();
        this.allocateSectors(players, allocateNeutralPlayer);
    }

    public Map(HashMap<Integer, Player> players, boolean allocateNeutralPlayer, HashMap<Integer, Sector> sectors) {
        this(players, allocateNeutralPlayer);
        this.sectors = sectors;
    }

    public Map(HashMap<Integer, Player> players, boolean allocateNeutralPlayer, PVC proViceChancellor, GameScreen gameScreen) {
        this(players, allocateNeutralPlayer);
        this.proViceChancellor = proViceChancellor;
        this.gameScreen = gameScreen;
    }

    /**
     * converts a space seperated string of integers to an integer array
     *
     * @param stringData space separated integers e.g. '1 2 3 4 5'
     * @return the integers in the data in an array
     */
    private int[] strToIntArray(String stringData) {
        String[] strArray = stringData.split(" ");
        int[] intArray = new int[strArray.length];
        for (int i = 0; i < intArray.length; i++) {
            if (strArray[i].equals("")) {
                continue; // skip if string is empty
            }
            intArray[i] = Integer.parseInt(strArray[i]);
        }
        return intArray;
    }

    /**
     * load the sector properties from the sectorProperties.csv file into the sectors hashmap
     */
    private void loadSectors() {
        this.sectors = new HashMap<Integer, Sector>();

        String csvFile = "mapData/sectorProperties.csv";
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                Sector temp = sectorDataToSector(line.split(","));
                this.sectors.put(temp.getId(), temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // csv file no present
        } catch (IOException e) {
            e.printStackTrace(); // error occurred whilst reading the file
        }
    }

    /**
     * converts a String array of sector data to a sector object
     *
     * @param sectorData sector data taken from the sectorProperties csv file
     * @return a sector with the properties fo the supplied data
     */
    private Sector sectorDataToSector(String[] sectorData) {
        int sectorId = Integer.parseInt(sectorData[0]);
        int ownerId = -1;
        String filename = "mapData/" + sectorData[1];
        String texturePath = "mapData/" + sectorData[1];
        Texture sectorTexture = new Texture(texturePath);
        Pixmap sectorPixmap = new Pixmap(Gdx.files.internal("mapData/" + sectorData[1]));
        String displayName = sectorData[2];
        int numberUndergraduates = 10 + random.nextInt(15);
        int numberPostgraduates = 0;
        ArrayList<GangMembers> unitsInSector = new ArrayList<GangMembers>();
        for (int i = 0; i < numberUndergraduates; i++) {
            Undergraduates u = new Undergraduates();
            unitsInSector.add(u);
        }
        if (numberPostgraduates < 0) {
            Postgraduates g = new Postgraduates();
            unitsInSector.add(g);
        }

        int undergraduatesProvided = Integer.parseInt(sectorData[4]);
        String college = sectorData[5];
        boolean neutral = Boolean.parseBoolean(sectorData[6]);
        int[] adjacentSectors = strToIntArray(sectorData[7]);
        int sectorX = Integer.parseInt(sectorData[8]);
        int sectorY = Integer.parseInt(sectorData[9]);
        boolean decor = Boolean.parseBoolean(sectorData[10]);
        int postgraduatesProvided = Integer.parseInt(sectorData[11]);

        return new Sector(sectorId, ownerId, filename, sectorTexture, texturePath, sectorPixmap, displayName, unitsInSector, undergraduatesProvided, college, neutral, adjacentSectors, sectorX, sectorY, decor, postgraduatesProvided);
    }

    /**
     * allocates the default neutral sectors to the neutral player
     *
     * @param players hashmap of players containing the Neutral Player at key value GameScreen.NEUTRAL_PLAYER_ID
     */
    private void allocateNeutralSectors(HashMap<Integer, Player> players) {
        for (Sector sector : sectors.values()) {
            if (sector.isNeutral() && !sector.isDecor()) {
                sector.setOwner(players.get(GameScreen.NEUTRAL_PLAYER_ID));
            }
        }
    }


    /**
     * allocates sectors in the map to the players in a semi-random fashion
     * if there is a neutral player then the default neutral sectors are allocated to them
     *
     * @param players               the players the sectors are to be allocated to
     * @param allocateNeutralPlayer should the neutral player be allocated sectors
     * @throws RuntimeException if the players hashmap is empty
     */
    private void allocateSectors(HashMap<Integer, Player> players, boolean allocateNeutralPlayer) {
        if (players.size() == 0) {
            throw new RuntimeException("Cannot allocate sectors to 0 players");
        }

        // set any default neutral sectors to the neutral player
        if (allocateNeutralPlayer) {
            allocateNeutralSectors(players);
        }

        HashMap<Integer, Integer[]> playerReinforcements = new HashMap<Integer, Integer[]>(); // mapping of player id to amount of reinforcements they will receive currently
        // set all players to currently be receiving 0 reinforcements, ignoring the neutral player
        for (Integer i : players.keySet()) {
            if (i != GameScreen.NEUTRAL_PLAYER_ID) playerReinforcements.put(i, new Integer[]{0,0});
        }


        int lowestReinforcementId = players.keySet().iterator().next(); // id of player currently receiving the least reinforcements, any player id is chosen to start as all have 0 reinforcements
        List<Integer> sectorIdsRandOrder = new ArrayList<Integer>(getSectorIds()); // list of sector ids
        Collections.shuffle(sectorIdsRandOrder); // randomise the order sectors ids are stored so allocation order is randomised

        for (Integer i : sectorIdsRandOrder) {
            if (!sectors.get(i).isAllocated()) { // check sector has not already been allocated, may have been allocated to the neutral player
                if (this.getSectorById(i).isDecor()) {
                    continue; // skip allocating sector if it is a decor sector
                }
                this.getSectorById(i).setOwner(players.get(lowestReinforcementId));

                playerReinforcements.put(lowestReinforcementId, new Integer[]{playerReinforcements.get(lowestReinforcementId)[0] + this.getSectorById(i).getUndergraduatesProvided(), playerReinforcements.get(lowestReinforcementId)[1] + this.getSectorById(i).getPostgraduatesProvided()}); // updates player reinforcements hashmap

                // find the new player with lowest reinforcements

                int minReinforcements = 10000; // get lowest reinforcement amount
                for (Integer[] vals : playerReinforcements.values()) {
                    if (vals[0] < minReinforcements)
                        minReinforcements = vals[0];
                }

                for (Integer j : playerReinforcements.keySet()) { // find id of player which has the lowest reinforcement amount
                    if (playerReinforcements.get(j)[0] == minReinforcements) { // if this player has the reinforcements matching the min amount set them to the new lowest player
                        lowestReinforcementId = j;
                        break;
                    }
                }
            }
        }
    }


    /**
     * spawns the PVC tile and sets the colour to gold and then starts the mini game
     */

    private void spawnPVC(Stage stage, int defendingSectorId) {
        sectors.get(defendingSectorId).setIsPVCTile(true); //set the taken over tile to be the PVC tile
        DialogFactory.takenOverPVCDialogue(proViceChancellor, stage);
        //sectors.get(defendingSectorId).changeSectorColor(com.badlogic.gdx.graphics.Color.GOLD);
        proViceChancellor.setPVCSpawned(true);

    }


    /**
     * processes an attack from one sector to another
     * triggers specific dialogs dependent on the outcome of the attack
     * controls reassigning owners dependent on the outcome of the attack
     * sets up drawing particle effects showing changes in amount of units in a sector
     * sets up movement of units after conquering a sector
     *
     * @param attackingSectorId id of the sector the attack is coming from
     * @param defendingSectorId id of the defending sector this.sectors = sectors;
     * @param attackersLost     amount of units lost on the attacking sector
     * @param defendersLost     amount of units lost on the defending sector
     * @param attacker          the player who is carrying out the attack
     * @param defender          the player who is being attacked
     * @param stage             the stage to draw any dialogs to
     * @return true if attack successful else false
     * @throws IllegalArgumentException if the amount of attackers lost exceeds the amount of attackers
     * @throws IllegalArgumentException if the amount of defenders lost exceeds the amount of attackers
     */


    public boolean attackSector(int attackingSectorId, int defendingSectorId, int attackersLost, int defendersLost, Player attacker, Player defender, Player netrualPlayer, Stage stage) {
        if (sectors.get(attackingSectorId).getUndergraduatesInSector() < attackersLost) {
            throw new IllegalArgumentException("Cannot loose more attackers than are on the sector: Attackers " + sectors.get(attackingSectorId).getUndergraduatesInSector() + "     Attackers Lost " + attackersLost);
        }
        if (sectors.get(defendingSectorId).getUndergraduatesInSector() < defendersLost) {
            throw new IllegalArgumentException("Cannot loose more defenders than are on the sector: Defenders " + sectors.get(attackingSectorId).getUndergraduatesInSector() + "     Defenders Lost " + attackersLost);
        }

        addUnitsToSectorAnimated(attackingSectorId, -attackersLost, 0); // apply amount of attacking units lost
        addUnitsToSectorAnimated(defendingSectorId, -defendersLost, 0); // apply amount of defending units lost

        /* explain outcome to player using dialog boxes, possible outcomes
         * - All defenders killed, more than one attacker left      -->     successfully conquered sector, player is asked how many units they want to move onto it
         * - All defenders killed, one attacker left                -->     sector attacked becomes neutral as player can't move units onto it
         * - Not all defenders killed, all attackers killed         -->     attacking sector becomes unAssigned
         * - Not all defenders killed, not all attackers killed     -->     both sides loose troops, no dialog to display
         * */
        if (sectors.get(attackingSectorId).getUndergraduatesInSector() == 0) { // attacker lost all troops
            addUnitsToSectorAnimated(attackingSectorId, 0, -1);
            DialogFactory.sectorOwnerChangeDialog(attacker.getPlayerName(), netrualPlayer.getPlayerName(), sectors.get(attackingSectorId).getDisplayName(), stage);
            sectors.get(attackingSectorId).setOwner(netrualPlayer);
            if (sectors.get(defendingSectorId).getUndergraduatesInSector() == 0) { // both players wiped each other out
                addUnitsToSectorAnimated(defendingSectorId, 0, -1);
                DialogFactory.sectorOwnerChangeDialog(defender.getPlayerName(), netrualPlayer.getPlayerName(), sectors.get(attackingSectorId).getDisplayName(), stage);
                sectors.get(defendingSectorId).setOwner(netrualPlayer);
            }

        } else if (sectors.get(defendingSectorId).getUndergraduatesInSector() == 0 && sectors.get(attackingSectorId).getUndergraduatesInSector() > 1) { // territory conquered

            addUnitsToSectorAnimated(defendingSectorId, 0, -1);
            unitsToMove = new int[4];
            unitsToMove[0] = -1;
            unitsToMove[1] = -1;
            unitsToMove[2] = attackingSectorId;
            unitsToMove[3] = defendingSectorId;


            attacker.addUndergraduatesToAllocate(sectors.get(defendingSectorId).getUndergraduatesProvided());
            attacker.addPostGraduatesToAllocate(sectors.get(defendingSectorId).getPostgraduatesProvided());
            sectors.get(defendingSectorId).setOwner(attacker);

            // 0.5 chance of getting card on sector capture
            if (random.nextInt(10) < 5) {
                if ((gameScreen.getCurrentPlayer().getCardHand().size() < 4) && (gameScreen.getCardDeckSize() != 0)){
                    gameScreen.getCurrentPlayer().addCard(gameScreen.getRandomCard());
                    gameScreen.setupCardUI();
                }
            }

            DialogFactory.attackSuccessDialogBox(sectors.get(defendingSectorId).getUndergraduatesProvided(), sectors.get(attackingSectorId).getUndergraduatesInSector(), sectors.get(attackingSectorId).getPostgraduatesInSector(), unitsToMove, defender.getPlayerName(), attacker.getPlayerName(), sectors.get(defendingSectorId).getDisplayName(), defendingSectorId, attacker, defender, this, stage);


        } else if (sectors.get(defendingSectorId).getUndergraduatesInSector() == 0 && sectors.get(attackingSectorId).getUndergraduatesInSector() == 1) { // territory conquered but only one attacker remaining so can't move troops onto it
            addUnitsToSectorAnimated(defendingSectorId, 0, -1);
            DialogFactory.sectorOwnerChangeDialog(defender.getPlayerName(), netrualPlayer.getPlayerName(), sectors.get(defendingSectorId).getDisplayName(), stage);
            sectors.get(defendingSectorId).setOwner(netrualPlayer);
        }
        return true;
    }

    /**
     * NEW ASSESSMENT 4
     * handles PVC capture
     *
     * @param defendingSectorId id of the sector receiving troops
     * @param attacker          the player who is carrying out the attack
     * @param defender          the player who is being attacked
     * @param stage             the stage to draw any dialogs to
     **/
    public void handlePVC(int defendingSectorId, Player attacker, Player defender, Stage stage) {
        if (sectors.get(defendingSectorId).getIsPVCTile()) //if the player takes over PVC tile add PVC bonus
        {
            defender.setOwnsPVC(false);
            attacker.setOwnsPVC(true);
            // TODO Fix this
            sectors.get(defendingSectorId).setOwner(attacker);
            //sectors.get(defendingSectorId).changeSectorColor(com.badlogic.gdx.graphics.Color.GOLD);
            proViceChancellor.startMiniGame();

        }
        if (proViceChancellor.PVCSpawn() && !proViceChancellor.isPVCSpawned()) {
            spawnPVC(stage, defendingSectorId);
            attacker.setOwnsPVC(true);
        }
    }


    /**
     * processes a movement from one sector to another
     * sets up drawing particle effects showing changes in amount of units in a sector
     * sets up movement of units after conquering a sector
     *
     * @param startingSectorID id of the sector the troops are moving from
     * @param destinationSectorID id of the sector receiving troops
     * @param startUndergraduates     amount of units lost on the sector sending sector
     * @param endUndergraduates     amount of units gained on the sector receiving troops
     * @return true if movement successful else false
     **/
    public Boolean moveTroops(int startingSectorID, int destinationSectorID, int startUndergraduates, int endUndergraduates, int startPostgraduates, int endPostgraduates) {
        if (getSectorById(destinationSectorID).getPostgraduatesInSector() > 0 && startPostgraduates > 0) {
            return false;
        }
        else {
            addUnitsToSectorAnimated(startingSectorID, -startUndergraduates, -startPostgraduates); // apply amount of units lost from start sector
            addUnitsToSectorAnimated(destinationSectorID, endUndergraduates, endPostgraduates); // apply amount of units gained in end sector
            return true;
        }
    }


    /** MODIFIED ASSESSMENT 4 - Now takes an argument for the number of pgs
     * adds the specified number of units to this sector and sets up drawing a particle effect showing the addition
     *
     * @param sectorId id of sector to add the units to
     * @param amountOfUndergraduates   to add
     */
    public void addUnitsToSectorAnimated(int sectorId, int amountOfUndergraduates, int amountOfPostgraduates) {
        this.sectors.get(sectorId).addUndergraduates(amountOfUndergraduates);
        this.sectors.get(sectorId).addPostgraduate(amountOfPostgraduates);
        this.particles.add(new UnitChangeParticle(amountOfUndergraduates + "/" + amountOfPostgraduates, new Vector2(sectors.get(sectorId).getSectorCentreX(), sectors.get(sectorId).getSectorCentreY())));
    }

    /**
     * gets the sector that has the corresponding sector id in the sectors hashmap
     *
     * @param sectorId id of the desired sector
     * @return Sector object with the corresponding id in hashmap sectors
     * @throws NullPointerException if the key sectorId does not exist in the sectors hashmap
     */
    public Sector getSectorById(int sectorId) {
        if (sectors.containsKey(sectorId)) {
            return sectors.get(sectorId);
        } else {
            throw new NullPointerException("Cannot get sector as sector id " + sectorId + " does not exist in the sectors hashmap");
        }
    }

    /**
     * @return Set of all SectorIds
     */
    public Set<Integer> getSectorIds() {
        return sectors.keySet();
    }

    /**
     * returns the id of the sector that contains the specified point
     * ignores decor sectors
     *
     * @param worldX world x coord
     * @param worldY world y coord
     * @return id of sector that contains point or -1 if no sector contains the point or sector is decor only
     */
    public int detectSectorContainsPoint(int worldX, int worldY) {
        int worldYInverted = 1080 - 1 - worldY; // invert y coordinate for pixmap coordinate system
        for (Sector sector : sectors.values()) {
            if (worldX < 0 || worldYInverted < 0 || worldX > sector.getSectorTexture().getWidth() || worldYInverted > sector.getSectorTexture().getHeight()) {
                return -1; // return no sector contains the point if it outside of the map bounds
            }
            int pixelValue = sector.getSectorPixmap().getPixel(worldX, worldYInverted); // get pixel value of the point in sector image the mouse is over
            if (pixelValue != -256) { // if pixel is not transparent then it is over the sector
                if (sector.isDecor()) {
                    continue; // sector is decor so continue checking to see if a non-decor sector contains point
                } else {
                    return sector.getId(); // return id of sector which is hovered over
                }
            }
        }
        return -1;
    }

    /**
     * carries out the unit movement specified by unitsToMove array
     * - unitsToMove[0] : number of ugs to move
     * - unitsToMove[1] : number of pgs to move
     * - unitsToMove[2] : source sector id
     * - unitsToMove[3] : target sector id
     * changes in units on sectors are shown on scren using the UnitChangeParticle
     *
     * @throws IllegalArgumentException if the sector are not both owned by the same player
     * @throws IllegalArgumentException if the amount exceeds the (number of units - 1) on the source sector
     * @throws IllegalArgumentException if the sectors are not connected
     */
    private void moveUnits() throws IllegalArgumentException {
        if (sectors.get(unitsToMove[2]).getOwnerId() != sectors.get(unitsToMove[3]).getOwnerId()) {
            throw new IllegalArgumentException("Source and target sectors must have the same owners");
        }
        if (sectors.get(unitsToMove[2]).getUndergraduatesInSector() <= unitsToMove[0]) {
            throw new IllegalArgumentException("Must leave at least one unit on source sector and can't move more units than are on source sector");
        }
        if (!sectors.get(unitsToMove[2]).isAdjacentTo(sectors.get(unitsToMove[3]))) {
            throw new IllegalArgumentException("Sectors must be adjacent in order to move units");
        }
        addUnitsToSectorAnimated(unitsToMove[2], -unitsToMove[0], -unitsToMove[1]); // remove units from source
        addUnitsToSectorAnimated(unitsToMove[3], unitsToMove[0], unitsToMove[1]); // add units to target
    }

    /**
     * once unitsToMove has had the amount of units to move and the ids of the source and target sector set, perform the move
     */
    private void detectUnitsMove() {
        if (unitsToMove != null) {
            if (unitsToMove[0] != -1 || unitsToMove[1] != -1) {
                moveUnits();
                unitsToMove = null;
            }
        }
    }

    /**
     * draws the map and the number of units in each sector and the units change particle effect
     *
     * @param batch the batch used for drawing
     */
    public void draw(SpriteBatch batch) {
        detectUnitsMove(); // check if units need to be moved, and carry the movement out if required

        for (Sector sector : sectors.values()) {
            String text = sector.getUndergraduatesInSector() + "/" + sector.getPostgraduatesInSector() + "";
            batch.draw(sector.getSectorTexture(), 0, 0);
            if (!sector.isDecor()) { // don't need to draw the amount of units on a decor sector
                layout.setText(font, text);

                if (sector.getIsPVCTile()){
                    float pvcOverlaySize = 50.0f;
                    batch.draw(pvcOutline, sector.getSectorCentreX() - pvcOverlaySize / 2, sector.getSectorCentreY() - pvcOverlaySize / 2, pvcOverlaySize, pvcOverlaySize);
                }
                float overlaySize = 40.0f;
                batch.draw(troopCountOverlay, sector.getSectorCentreX() - overlaySize / 2, sector.getSectorCentreY() - overlaySize / 2, overlaySize, overlaySize);
                font.draw(batch, layout, sector.getSectorCentreX() - layout.width / 2, sector.getSectorCentreY() + layout.height / 2);
            }
        }

        // render particles
        List<UnitChangeParticle> toDelete = new ArrayList<UnitChangeParticle>();
        for (UnitChangeParticle particle : particles) {
            particle.draw(batch);
            if (particle.toDelete()) {
                toDelete.add(particle);
            }
        }
        particles.removeAll(toDelete);
    }

    /**
     * @return mapping of sector ID's to sector objects
     */
    public HashMap<Integer, Sector> getSectors() {
        return sectors;
    }

    /**
     * @return PVC
     */
    public PVC getProViceChancellor() {
        return proViceChancellor;
    }

}
