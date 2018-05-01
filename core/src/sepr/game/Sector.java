package sepr.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import sepr.game.gangmembers.GangMembers;
import sepr.game.gangmembers.Postgraduates;
import sepr.game.gangmembers.Undergraduates;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * class for specifying properties of a sector that is part of a map
 */
public class Sector implements ApplicationListener {
    private int id;
    private int ownerId;
    private String displayName;
    private ArrayList<GangMembers> unitsInSector;
    private int undergraduatesProvided;
    private String college; // name of the college this sector belongs to
    private boolean neutral; // is this sector a default neutral sector
    private int[] adjacentSectorIds; // ids of sectors adjacent to this one
    private Texture sectorTexture;
    private String texturePath;
    private Pixmap sectorPixmap; // the pixel data of this sectors texture
    private int sectorCentreX; // the centre x coordinate of this sector, relative to the sectorTexture
    private int sectorCentreY; //the centre y coordinate of this sector, relative to the sectorTexture
    private boolean decor; // is this sector for visual purposes only, i.e. lakes are decor
    private String fileName;
    private boolean allocated; // becomes true once the sector has been allocated
    private boolean isPVCTile;
    private int postgraduatesProvided;

    public Sector() {

    }

    /** MODIFIED ASSESSMENT 4 - Added support for pgs
     * @param id sector id
     * @param ownerId id of player who owns sector
     * @param displayName sector display name
     * @param unitsInSector number of units in sector
     * @param undergraduatesProvided number of ugs the sector provides
     * @param college unique id of the college this sector belongs to
     * @param adjacentSectorIds ids of adjacent sectors
     * @param sectorTexture sector texture from assets
     * @param sectorPixmap pixmap of sector texture
     * @param fileName sector filename
     * @param sectorCentreX xcoord of sector centre
     * @param sectorCentreY ycoord of sector centre
     * @param decor false if a sector is accessible to a player and true if sector is decorative
     */
    public Sector(int id, int ownerId, String fileName, Texture sectorTexture, String texturePath, Pixmap sectorPixmap, String displayName, ArrayList<GangMembers> unitsInSector, int undergraduatesProvided, String college, boolean neutral, int[] adjacentSectorIds, int sectorCentreX, int sectorCentreY, boolean decor, int postgraduatesProvided) {
        this.id = id;
        this.ownerId = ownerId;
        this.displayName = displayName;
        this.unitsInSector = unitsInSector;
        this.undergraduatesProvided = undergraduatesProvided;
        this.college = college;
        this.neutral = neutral;
        this.adjacentSectorIds = adjacentSectorIds;
        this.sectorTexture = new Texture(texturePath);
        this.texturePath = texturePath;
        this.sectorPixmap = sectorPixmap;
        this.sectorCentreX = sectorCentreX;
        this.sectorCentreY = 1080 - sectorCentreY;
        this.decor = decor;
        this.fileName = fileName;
        this.allocated = false;
        this.postgraduatesProvided = postgraduatesProvided;
    }

    public Sector(int id, int ownerId, String fileName, String texturePath, Pixmap sectorPixmap, String displayName, ArrayList<GangMembers> unitsInSector, int undergraduatesProvided, String college, boolean neutral, int[] adjacentSectorIds, int sectorCentreX, int sectorCentreY, boolean decor, boolean allocated, Color color, int postgraduatesProvided) {
        this(id, ownerId, fileName, new Texture(texturePath), texturePath, sectorPixmap, displayName, unitsInSector, undergraduatesProvided, college, neutral, adjacentSectorIds, sectorCentreX, sectorCentreY, decor, postgraduatesProvided);
        
        this.allocated = allocated;
        this.sectorCentreY = sectorCentreY;

        if(!isDecor()){this.changeSectorColor(color);
            this.changeSectorColor(color);
        }
    }

    public Sector(int id, int ownerId, String fileName, String texturePath, Pixmap sectorPixmap, String displayName, ArrayList<GangMembers> unitsInSector, int undergraduatesProvided, String college, boolean neutral, int[] adjacentSectorIds, int sectorCentreX, int sectorCentreY, boolean decor, boolean allocated, Color color, boolean test, int postgraduatesProvided){
        HeadlessApplicationConfiguration conf = new HeadlessApplicationConfiguration();

        new HeadlessApplication(this, conf);

        this.id = id;
        this.ownerId = ownerId;
        this.displayName = displayName;
        this.unitsInSector = unitsInSector;
        this.undergraduatesProvided = undergraduatesProvided;
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
        this.postgraduatesProvided = postgraduatesProvided; // new assessment 4
    }

    /**
     *
     * @return this sectors unique id
     */
    public int getId() { return id; }

    /**
     *
     * @return the id of the player that owns this sector
     */
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * sets the owner id and colour of this sector
     * @param player the player object that owns this sector
     */
    public void setOwner(Player player) {


        this.ownerId = player.getId();
        if(!this.isPVCTile){
            this.changeSectorColor(player.getSectorColour());
        }
        this.allocated = true;
    }


    /**
     *
     * @return if sector is PVC sector
     */
    public boolean getIsPVCTile() { return isPVCTile; }

    /**
     *
     * set's true if sector is PVC tile, false if not
     */
    public void setIsPVCTile(boolean value) { this.isPVCTile = value; }


    /**
     *
     * @return the name of the sector that is to be shown in the GUI
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     *
     * @return number of troops rewarded for conquering this territory
     */
    public int getUndergraduatesProvided() {
        return undergraduatesProvided;
    }

    /**
     * NEW ASSESSMENT 4
     * @return boolean for whether the sector gives ugs as reinforcements (false indicates pgs instead)
     */
    public boolean givesUndergraduates() {
        if (undergraduatesProvided == 0)
            return false;
        else
            return true;
    }

    /**
     * MODIFIED ASSESSMENT 4 - Renamed to provide support for pgs
     * @param undergraduatesProvided
     */
    public void setUndergraduatesProvided(int undergraduatesProvided) {
        this.undergraduatesProvided = undergraduatesProvided;
    }

    /**
     * NEW ASSESSMENT 4 - Gets the number of pgs in a sector
     * @return the number of pgs given to a player on sector capture
     */
    public int getPostgraduatesProvided() {
       return postgraduatesProvided;
    }

    /**
     * MODIFED ASSESSMENT 4
     * @return number of undergraduates present in this sector
     */
    public int getUndergraduatesInSector() {
        Iterator<GangMembers> iterator = this.unitsInSector.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            GangMembers g = iterator.next();
            if (g.getName().equals("Undergraduate")) {
                count++;
            }
        }
        return count;
    }

    /**
     * NEW ASSESSMENT 4
     * @return the number of pgs in a sector
     */
    public int getPostgraduatesInSector() {
        Iterator<GangMembers> iterator = this.unitsInSector.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            GangMembers g = iterator.next();
            if (g.getName().equals("Postgraduate")) {
                count++;
            }
        }
        return count;
    }

    /**
     * NEW ASSESSMENT 4
     * Gets the status (whether it has attacked this turn) of a pg
     * @return0 if set to false, 1 if set to true, -1 if no pg exists in the sector
     */
    public int getPostgraduateStatus() {
        Iterator<GangMembers> iterator = this.unitsInSector.iterator();
        while (iterator.hasNext()) {
            GangMembers g = iterator.next();
            if (g.getName().equals("Postgraduate")) {
                Postgraduates h = (Postgraduates)g;
                if (h.getAttacked() == false) {
                    return 0;
                }
                else {
                    return 1;
                }
            }
        }
        return -1; // error if there is no postgraduate in sector
    }

    /**
     * NEW ASSESSMENT 4
     * Sets the status (whether it has attacked this turn) of a pg
     * @param status the value to set the status to
     */
    public void setPostgraduateStatus(Boolean status) {
        System.out.println("Called");
        Iterator<GangMembers> iterator = this.unitsInSector.iterator();
        while (iterator.hasNext()) {
            GangMembers g = iterator.next();
            if (g.getName().equals("Postgraduate")) {
                Postgraduates h = (Postgraduates)g;
                System.out.println("Updated");
                h.setAttacked(status);
                break;
            }
        }
    }

    /**
     * MODIFIED ASSESSMENT 4 - Changed to add support for pgs
     * @param unitsInSector
     */
    public void setUnitsInSector(ArrayList<GangMembers> unitsInSector) {
        this.unitsInSector = unitsInSector;
    }

    /**
     *
     * @return the texture used for drawing the sectorNeutral
     */
    public Texture getSectorTexture() {
        return sectorTexture;
    }

    /**
     * Sets the new texture for a sector
     * @param newPixmap the memory representation of the textures pixels
     */
    private void setNewSectorTexture(Pixmap newPixmap) {
        this.sectorTexture.dispose();
        this.sectorTexture = new Texture(newPixmap);
    }

    /**
     *
     * @return the pixel data of this sectors texture
     */
    public Pixmap getSectorPixmap() {
        return sectorPixmap;
    }

    /**
     *
     * @return centre x coordinate of this sector
     */
    public int getSectorCentreX() {
        return sectorCentreX;
    }

    /**
     *
     * @return centre y coordinate of this sector
     */
    public int getSectorCentreY() {
        return sectorCentreY;
    }

    /**
     * @return boolean value to check whether sector is decorative
     */
    public boolean isDecor() {
        return decor;
    }

    /**
     *
     * @return true if this sector is a default neutral sector, else false
     */
    public boolean isNeutral() { return neutral; }

    public void setNeutral(boolean neutral) {
        this.neutral = neutral;
    }

    /**
     *
     * @return true if this sector has been allocated to a player, else false
     */
    public boolean isAllocated() {
        return allocated;
    }

    /**
     *
     * @return the name of the college this sector belongs to
     */
    public String getCollege() { return college; }

    public void setCollege(String college) {
        this.college = college;
    }

    /**
     *
     * @return the filename of the sector image
     */
    private String getFileName() {
        return fileName;
    }

    /**
     * Function to check if a given sector is adjacent
     * @param toCheck The sector object to check
     * @return True/False
     */
    public boolean isAdjacentTo(Sector toCheck) {
        for (int adjacent : this.adjacentSectorIds) {
            if (adjacent == toCheck.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * MODIFIED ASSESSMENT 4 - Changed to add support for pgs
     * Changes the number of units in this sector
     * If there are 0 units in sector then ownerId should be -1 (neutral)
     * @param amount number of units to change by, (can be negative to subtract units)
     * @throws IllegalArgumentException if units in sector is below 0
     */
    public void addUndergraduates(int amount) throws IllegalArgumentException {

        if (amount > 0) {
            for (int i = 0; i < amount; i++) {
                Undergraduates undergraduate = new Undergraduates();
                this.unitsInSector.add(undergraduate);
            }
        }
        else {
            int count = -amount;
            for (Iterator<GangMembers> iterator = this.unitsInSector.iterator(); iterator.hasNext();) {
                GangMembers g = iterator.next();
                if (g.getName().equals("Undergraduate") && count > 0) {
                    iterator.remove();
                    count--;
                }
            }
            unitsInSector.trimToSize();
        }

        if (this.unitsInSector.size() < 0) {
            this.unitsInSector = new ArrayList<GangMembers>();
            throw new IllegalArgumentException("Cannot have less than 0 units on a sector");
        }
    }

    /**
     * ADDED ASSESSMENT 4
     * Adds a postgrad to the sector
     */
    public void addPostgraduate(int amount) {
        //System.out.println("Entered");
        if (amount > 0) {
            //System.out.println("Added");
            Postgraduates postgraduate = new Postgraduates();
            this.unitsInSector.add(postgraduate);
        }
        else if (amount < 0) {
            for (Iterator<GangMembers> iterator = this.unitsInSector.iterator(); iterator.hasNext();) {
                GangMembers g = iterator.next();
                if (g.getName().equals("Postgraduate")) {
                    //System.out.println("Removed");
                    iterator.remove();
                    break;
                }
            }
            unitsInSector.trimToSize();
        }
    }

    /**
     * The method takes a sectorId and recolors it to the specified color
     * @param newColor what color the sector be changed to
     * @throws RuntimeException if attempt to recolor a decor sector
     */
    public void changeSectorColor(Color newColor){
        if (this.isDecor()) {
            throw new RuntimeException("Should not recolour decor sector");
        }

        Pixmap newPix = new Pixmap(Gdx.files.internal(this.getFileName())); // pixmap for drawing updated sector texture to
        for (int x = 0; x < this.getSectorPixmap().getWidth(); x++){
            for (int y = 0; y < this.getSectorPixmap().getHeight(); y++){
                if(newPix.getPixel(x, y) != -256){
                    Color tempColor = new Color(0,0,0,0);
                    Color.rgba8888ToColor(tempColor, newPix.getPixel(x, y)); // get the pixels current color
                    tempColor.sub(new Color(Color.WHITE).sub(newColor)); // calculate the new color of the pixel
                    newPix.drawPixel(x, y, Color.rgba8888(tempColor));  // draw the modified pixel value to the new pixmap
                }
            }
        }
        this.setNewSectorTexture(newPix); // draw the generated pixmap to the new texture
        newPix.dispose();
    }

    public int[] getAdjacentSectorIds() {
        return this.adjacentSectorIds;
    }

    public String getTexturePath() {
        return texturePath;
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void render() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
