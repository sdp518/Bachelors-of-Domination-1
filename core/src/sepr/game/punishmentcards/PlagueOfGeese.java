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
                    // TODO Work out this case
                    sector.addUndergraduates((sector.getUndergraduatesInSector() * -1));
                    sector.setOwner(gameScreen.getPlayers().get(gameScreen.NEUTRAL_PLAYER_ID));
                }
            }
        }
        return true;
    }
}
