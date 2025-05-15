package net.pixeldream.heavensfall.blocks.Multiblock;

import net.minecraft.util.StringRepresentable;

public enum MultiblockPart implements StringRepresentable {
    NONE, TOP_LEFT, TOP, TOP_RIGHT,
    LEFT, CENTER, RIGHT,
    BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
