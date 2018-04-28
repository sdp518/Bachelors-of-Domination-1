package sepr.game.punishmentcards;


import sepr.game.GameScreen;
import sepr.game.Player;
import sepr.game.Sector;

public class PlagueOfGeese extends Card {
    public PlagueOfGeese() {
        super(CardType.PLAGUE_OF_GEESE, true);
    }

    /**
     * Effect: Remove 3 units from all sectors of affected player
     */
    @Override
    public void act(Player player, GameScreen gameScreen) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
        for (Sector sector : gameScreen.getMap().getSectors().values()) {
            if (sector.getOwnerId() == player.getId()) {
                if (sector.getUnitsInSector() > 3){
                    sector.addUnits(-3);
                }
                else {
                    // TODO Work out this case
                    sector.addUnits((sector.getUnitsInSector() * -1));
                    sector.setOwner(gameScreen.getCurrentPlayer());
                }
            }
        }
    }
}
