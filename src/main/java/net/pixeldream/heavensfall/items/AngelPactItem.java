package net.pixeldream.heavensfall.items;

public class AngelPactItem extends AbstractQuestItem {
    public AngelPactItem(Properties props) { super(props); }

    @Override
    protected String getQuestId() {
        return "heavensfall:angel_pact_initiate";
    }

    @Override protected int getAlignmentChange() { return 20; }
    @Override protected String getUnlockMessage() { return "§bCelestial Pact Signed."; }
    @Override protected boolean isPact() { return true; }
}