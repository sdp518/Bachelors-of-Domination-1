package sepr.game.punishmentcards;

import sepr.game.GameScreen;
import sepr.game.Player;
import sepr.game.Sector;

public class FreshersFlu extends Card {
    public FreshersFlu() {
        super(CardType.FRESHERS_FLU, true, true);
    }

    /**
     * Effect: Debuffs affected player (half strength) for remainder of turn
     */
    @Override
    public boolean act(Player player, GameScreen gameScreen) {
        for (Sector sector : gameScreen.getMap().getSectors().values()) {
            if (sector.getOwnerId() == player.getId()) {

            }
        }
        return true;
    }
}
