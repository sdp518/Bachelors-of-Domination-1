package sepr.game.punishmentcards.tenplaguesofuniversity;

import sepr.game.Sector;

public class Plagues {
    private String curse;
    private double negativeMultiplier;

    public Plagues(String curse, double negativeMultiplier) {
        this.curse = curse;
        this.negativeMultiplier = negativeMultiplier;
    }

    public void useCard(Sector sector) {
        //int unitsLost = sector.getUndergraduatesInSector() - (int) Math.floor(sector.getUndergraduatesInSector() * negativeMultiplier);
        //sector.addUndergraduates(-1 * unitsLost);
    }
}
