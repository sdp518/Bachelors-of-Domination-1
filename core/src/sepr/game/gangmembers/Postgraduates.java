package sepr.game.gangmembers;

public class Postgraduates extends GangMembers {

    private Boolean castUsed;

    public Postgraduates() {
        super("Postgraduate");

        this.castUsed = false;
    }

    public Boolean getCastUsed() {
        return this.castUsed;
    }

    public void setCastStatus(Boolean status) {
        this.castUsed = status;
    }
}
