package sepr.game.gangmembers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GangMemebersTest {
    private GangMembers gangMembers;
    private Undergraduates undergraduates;
    private Postgraduates postgraduates;

    @Before
    public void setUp() {
        this.gangMembers = new GangMembers("test");
        this.undergraduates = new Undergraduates();
        this.postgraduates = new Postgraduates();
    }

    @Test
    public void testGangMemebers() {
        assertEquals("test", this.gangMembers.getName());
        assertEquals(false, this.gangMembers.getCastUsed());
        this.gangMembers.setCastStatus(true);
        assertEquals(true, this.gangMembers.getCastUsed());
    }

    @Test
    public void testUndergraduates() {
        assertEquals("Undergraduate", this.undergraduates.getName());
        assertEquals(false, this.undergraduates.getCastUsed());
        this.undergraduates.setCastStatus(true);
        assertEquals(true, this.undergraduates.getCastUsed());
    }

    @Test
    public void testPostgraduates() {
        assertEquals("Postgraduate", this.postgraduates.getName());
        assertEquals(false, this.postgraduates.getCastUsed());
        this.postgraduates.setCastStatus(true);
        assertEquals(true, this.postgraduates.getCastUsed());
    }
}
