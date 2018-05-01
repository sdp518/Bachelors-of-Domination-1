package sepr.game.punishmentcards;


import sepr.game.GameScreen;
import sepr.game.Player;
import sepr.game.Sector;

public class PlagueOfGeese extends Card {
    public PlagueOfGeese() {
        super(CardType.PLAGUE_OF_GEESE, true, true);
    }

    /**
     * Effect: Remove 3 units from all sectors of affected player
     */
    @Override
    public boolean act(Player player, GameScreen gameScreen) {
        for (Sector sector : gameScreen.getMap().getSectors().values()) {
            if (sector.getOwnerId() == player.getId()) {
                if (sector.getUndergraduatesInSector() > 3){
                    sector.addUndergraduates(-3);
                }
                else {
                    sector.addUndergraduates((sector.getUndergraduatesInSector() * -1));
                    if (sector.getPostgraduatesInSector() > 0) // if a pg exists in a sector when all ugs are lost, remove it
                        sector.addPostgraduate(-1);
                    sector.setOwner(gameScreen.getCurrentPlayer());
                }
            }
        }
        return true;
    }
}
