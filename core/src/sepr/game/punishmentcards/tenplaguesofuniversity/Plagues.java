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
        int unitsLost = sector.getUnitsInSector() - (int) Math.floor(sector.getUnitsInSector() * negativeMultiplier);
        sector.addUnits(-1 * unitsLost);
    }
}
