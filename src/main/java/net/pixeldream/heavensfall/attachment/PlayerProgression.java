package net.pixeldream.heavensfall.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.pixeldream.heavensfall.util.HFQuests;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record PlayerProgression(
        Set<String> completedQuests,
        Set<String> activeQuests,
        Map<String, Integer> questStartValues,
        String angelRank,
        String demonRank,
        int alignment
) {

    public enum Fraction { ANGEL, DEMON, FALLEN, UNBOUND }

    public static final Codec<PlayerProgression> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.listOf().xmap(HashSet::new, List::copyOf).fieldOf("completed_quests").forGetter(p -> new HashSet<>(p.completedQuests())),
                    Codec.STRING.listOf().xmap(HashSet::new, List::copyOf).fieldOf("active_quests").forGetter(p -> new HashSet<>(p.activeQuests())),
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("start_values").forGetter(p -> p.questStartValues),
                    Codec.STRING.fieldOf("angel_rank").forGetter(PlayerProgression::angelRank),
                    Codec.STRING.fieldOf("demon_rank").forGetter(PlayerProgression::demonRank),
                    Codec.INT.fieldOf("alignment").forGetter(PlayerProgression::alignment)
            ).apply(instance, (comp, active, starts, angel, demon, align) ->
                    new PlayerProgression(new HashSet<>(comp), new HashSet<>(active), new HashMap<>(starts), angel, demon, align))
    );

    public Fraction getFraction() {
        boolean hasAngelPact = completedQuests.stream().anyMatch(q -> q.contains(HFQuests.TAG_PACT) && q.contains(HFQuests.TAG_ANGEL));
        boolean hasDemonPact = completedQuests.stream().anyMatch(q -> q.contains(HFQuests.TAG_PACT) && q.contains(HFQuests.TAG_DEMON));

        if (Math.abs(alignment) < 20 && hasAngelPact && hasDemonPact) return Fraction.FALLEN;
        if (alignment <= -20) return Fraction.DEMON;
        if (alignment >= 20) return Fraction.ANGEL;
        return Fraction.UNBOUND;
    }

    public PlayerProgression recycleQuest(String questId) {
        Set<String> newCompleted = new HashSet<>(completedQuests());
        newCompleted.remove(questId);
        Set<String> newActive = new HashSet<>(activeQuests());
        newActive.remove(questId);
        return new PlayerProgression(newCompleted, newActive, questStartValues(), angelRank(), demonRank(), alignment());
    }

    public PlayerProgression acceptQuest(String id, int startValue) {
        Set<String> newActive = new HashSet<>(this.activeQuests);
        newActive.add(id);
        Map<String, Integer> newStarts = new HashMap<>(this.questStartValues);
        newStarts.put(id, startValue);
        return new PlayerProgression(this.completedQuests, newActive, newStarts, this.angelRank, this.demonRank, this.alignment);
    }

    public PlayerProgression completeQuest(String id) {
        Set<String> newActive = new HashSet<>(this.activeQuests);
        newActive.remove(id);
        Map<String, Integer> newStarts = new HashMap<>(this.questStartValues);
        newStarts.remove(id);

        Set<String> newComp = new HashSet<>(this.completedQuests);
        newComp.add(id);
        return new PlayerProgression(newComp, newActive, newStarts, this.angelRank, this.demonRank, this.alignment).updateTitles();
    }

    public PlayerProgression addQuest(String id) {
        Set<String> newComp = new HashSet<>(this.completedQuests);
        newComp.add(id);
        return new PlayerProgression(newComp, this.activeQuests, this.questStartValues, this.angelRank, this.demonRank, this.alignment).updateTitles();
    }

    public PlayerProgression updateTitles() {
        String newAngel = "Novice";
        String newDemon = "Initiate";
        if (this.alignment >= 80) newAngel = "Seraphim";
        else if (this.alignment >= 50) newAngel = "Archangel";
        else if (this.alignment >= 20) newAngel = "Guardian";

        if (this.alignment <= -80) newDemon = "Hellgate Lord";
        else if (this.alignment <= -50) newDemon = "Archdemon";
        else if (this.alignment <= -20) newDemon = "Imp";

        return new PlayerProgression(this.completedQuests, this.activeQuests, this.questStartValues, newAngel, newDemon, this.alignment);
    }

    public PlayerProgression withAngelRank(String rank) {
        return new PlayerProgression(this.completedQuests, this.activeQuests, this.questStartValues, rank, this.demonRank, this.alignment);
    }

    public PlayerProgression withDemonRank(String rank) {
        return new PlayerProgression(this.completedQuests, this.activeQuests, this.questStartValues, this.angelRank, rank, this.alignment);
    }

    public PlayerProgression addAlignment(int amount) {
        int newAlign = Math.max(-100, Math.min(100, this.alignment + amount));
        return new PlayerProgression(this.completedQuests, this.activeQuests, this.questStartValues, this.angelRank, this.demonRank, newAlign).updateTitles();
    }

    public boolean isCompleted(String id) { return completedQuests.contains(id); }
    public boolean isActive(String id) { return activeQuests.contains(id); }
    public int getStartValue(String id) { return questStartValues.getOrDefault(id, 0); }
}