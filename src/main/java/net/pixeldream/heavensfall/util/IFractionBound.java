package net.pixeldream.heavensfall.util;

import net.pixeldream.heavensfall.attachment.PlayerProgression;

public interface IFractionBound {
    PlayerProgression.Fraction getRequiredFraction();

    default String getFailureMessage() {
        return "§cYou are not worthy of this power!";
    }
}