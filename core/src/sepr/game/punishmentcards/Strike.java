package sepr.game.punishmentcards;

import sepr.game.DialogFactory;
import sepr.game.GameScreen;
import sepr.game.Player;
import sepr.game.Sector;

public class Strike extends Card {
    public Strike() {
        super(CardType.STRIKE, false, false);
    }

    /**
     * Effect: Steal PVC sector from player that currently holds it
     */
    @Override
    public boolean act(GameScreen gameScreen) {
        System.out.println(type.getCardType());

        // TODO only add cards to deck when PVC spawned?
        if (gameScreen.getMap().getProViceChancellor().isPVCSpawned()) {
            for (Player p : gameScreen.getPlayers().values()) {
                if (p.getOwnsPVC()) {
                    p.setOwnsPVC(false);
                    gameScreen.getCurrentPlayer().setOwnsPVC(true);
                    for (Sector s : gameScreen.getMap().getSectors().values()) {
                        if (s.getIsPVCTile()) {
                            s.setOwner(gameScreen.getCurrentPlayer());
                            break;
                        }
                    }
                    break;
                }
            }
            return true;
        }
        else {
            DialogFactory.basicDialogBox("PVC not found yet!", "You can't use this card just yet as the PVC has not been found. Save it for later though, it may come in useful!", gameScreen.getCardStage());
            return false;
        }

    }
}
