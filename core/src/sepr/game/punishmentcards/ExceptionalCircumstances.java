package sepr.game.punishmentcards;

import sepr.game.GameScreen;
import sepr.game.Player;
import sepr.game.Sector;

import java.util.ArrayList;

public class ExceptionalCircumstances extends Card {
    public ExceptionalCircumstances() {
        super(CardType.EXCEPTIONAL_CIRCUMSTANCES);
    }

    /**
     * Effect: Swap sectors with chosen player
     */
    @Override
    public void act(Player player, GameScreen gameScreen) {
        System.out.println(player.getPlayerName() + ": " + type.getCardType());
        for (Sector sector : gameScreen.getMap().getSectors().values()) {
            ArrayList<Sector> currentPlayerSectors = new ArrayList<Sector>();
            ArrayList<Sector> otherPlayerSectors = new ArrayList<Sector>();
            if (sector.getOwnerId() == player.getId()) {
                otherPlayerSectors.add(sector);
            }
            if (sector.getOwnerId() == gameScreen.getCurrentPlayer().getId()) {
                currentPlayerSectors.add(sector);
            }

            for (Sector s : otherPlayerSectors) {
                sector.setOwner(gameScreen.getCurrentPlayer());
            }
            for (Sector s : currentPlayerSectors) {
                sector.setOwner(player);
            }
        }
    }
}
