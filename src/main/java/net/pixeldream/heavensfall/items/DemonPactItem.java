package net.pixeldream.heavensfall.items;

public class DemonPactItem extends AbstractQuestItem {
    public DemonPactItem(Properties props) { super(props); }

    @Override
    protected String getQuestId() {
        return "heavensfall:demon_pact_initiate";
    }

    @Override protected int getAlignmentChange() { return -20; }
    @Override protected String getUnlockMessage() { return "§4The abyss gazes back..."; }

    @Override
    protected boolean isPact() {
        return true;
    }
}