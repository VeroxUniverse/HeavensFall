package net.pixeldream.heavensfall.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.quests.IQuest;
import net.pixeldream.heavensfall.quests.QuestManager;

import java.util.ArrayList;
import java.util.List;

public class QuestBoardScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "textures/gui/quest_board_ui.png");
    private final PlayerProgression.Fraction fraction;
    private int currentPage = 0;
    private List<IQuest> displayedQuests = new ArrayList<>();

    public QuestBoardScreen(PlayerProgression.Fraction fraction) {
        super(Component.literal("Quest Board"));
        this.fraction = fraction;
    }

    @Override
    protected void init() {
        if (displayedQuests.isEmpty()) {
            if (this.minecraft.player == null) return;
            var playerData = this.minecraft.player.getData(HFAttachments.PROGRESSION);

            List<IQuest> allQuests = QuestManager.getQuests().values().stream()
                    .filter(q -> q.getTargetFraction() == this.fraction || q.getTargetFraction() == PlayerProgression.Fraction.FALLEN)
                    .filter(q -> !playerData.activeQuests().contains(q.getId()))
                    .filter(q -> !playerData.completedQuests().contains(q.getId()))
                    .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

            java.util.Collections.shuffle(allQuests);
            displayedQuests = allQuests.stream().limit(16).toList();

            HeavensFallMod.LOGGER.info("QuestManager hat {} quests", QuestManager.getQuests().size());
            HeavensFallMod.LOGGER.info("Player activeQuests: {}", playerData.activeQuests());
            HeavensFallMod.LOGGER.info("Player completedQuests: {}", playerData.completedQuests());
        }
        refreshButtons();

    }

    private void refreshButtons() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int startY = (this.height - 256) / 2;
        int buttonY = startY + 45;

        int startIdx = currentPage * 8;
        for (int i = startIdx; i < Math.min(startIdx + 8, displayedQuests.size()); i++) {
            IQuest quest = displayedQuests.get(i);
            String color = (quest.getTargetFraction() == PlayerProgression.Fraction.FALLEN) ? "§d"
                    : (fraction == PlayerProgression.Fraction.DEMON ? "§c" : "§b");

            this.addRenderableWidget(Button.builder(Component.literal(color + quest.getTitle()), (btn) -> {
                Minecraft.getInstance().setScreen(new QuestAcceptScreen(quest, this.fraction));
            }).bounds(centerX - 100, buttonY, 200, 18).build());
            buttonY += 21;
        }

        this.addRenderableWidget(Button.builder(Component.literal("<"), (btn) -> {
            if (currentPage > 0) { currentPage--; refreshButtons(); }
        }).bounds(centerX - 85, startY + 225, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Close"), (btn) -> this.onClose())
                .bounds(centerX - 55, startY + 225, 110, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), (btn) -> {
            if ((currentPage + 1) * 8 < displayedQuests.size()) { currentPage++; refreshButtons(); }
        }).bounds(centerX + 65, startY + 225, 20, 20).build());
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x77000000);
        int centerX = this.width / 2;
        int startY = (this.height - 256) / 2;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(TEXTURE, centerX - 160, startY, 0, 0, 320, 256, 320, 256);
        RenderSystem.disableBlend();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int centerX = this.width / 2;
        int startY = (this.height - 256) / 2;
        guiGraphics.drawCenteredString(this.font, "§lAVAILABLE TASKS", centerX, startY + 18, 0xFFFFFF);
    }
}