package sepr.game.punishmentcards;

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
    public void act(GameScreen gameScreen) {
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
        }

    }
}
