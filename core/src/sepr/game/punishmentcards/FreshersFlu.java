package sepr.game.punishmentcards;

import sepr.game.DialogFactory;
import sepr.game.GameScreen;
import sepr.game.Player;
import sepr.game.Sector;

import java.util.HashMap;

public class FreshersFlu extends Card {

    private HashMap<Integer, Integer> previousUnits;

    public FreshersFlu() {
        super(CardType.FRESHERS_FLU, true, true);
    }

    /**
     * Effect: Debuffs affected player (half strength) for remainder of turn
     */
    @Override
    public boolean act(Player player, GameScreen gameScreen) {
        if (player.hasFreshersFlu()) {
            DialogFactory.basicDialogBox("You can't catch it twice!", "This player already has freshers flu!", gameScreen.getCardStage());
            return false;
        }
        else {
            player.switchFreshersFlu();
            previousUnits = new HashMap<Integer, Integer>();
            for (Sector sector : gameScreen.getMap().getSectors().values()) {
                if (sector.getOwnerId() == player.getId()) {
                    previousUnits.put(sector.getId(), sector.getUndergraduatesInSector());
                    sector.addUndergraduates((Math.round(sector.getUndergraduatesInSector()/2) * -1));
                }
            }
            player.setFreshersFluPrevUnits(previousUnits);
            return true;
        }

    }
}
