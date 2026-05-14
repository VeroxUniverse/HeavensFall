package net.pixeldream.heavensfall.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.network.AcceptQuestPayload;
import net.pixeldream.heavensfall.quests.IQuest;

import java.util.List;

public class QuestAcceptScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "textures/gui/quest_accept_ui.png");
    private final IQuest quest;
    private final PlayerProgression.Fraction npcType;

    public QuestAcceptScreen(IQuest quest, PlayerProgression.Fraction npcType) {
        super(Component.literal("Quest Offering"));
        this.quest = quest;
        this.npcType = npcType;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int startY = (this.height - 256) / 2;

        String title = quest.getTargetFraction() == PlayerProgression.Fraction.FALLEN ? "§d[Void] "
                : (npcType == PlayerProgression.Fraction.DEMON ? "§4[Demonic] " : "§b[Celestial] ");
        guiGraphics.drawCenteredString(this.font, title + quest.getTitle(), centerX, startY + 20, 0xFFFFFF);

        int contentY = startY + 45;
        List<FormattedCharSequence> descLines = this.font.split(Component.literal(quest.getDescription()), 280);
        for (FormattedCharSequence line : descLines) {
            guiGraphics.drawCenteredString(this.font, line, centerX, contentY, 0xFFFFFF);
            contentY += 10;
        }

        int boxY = startY + 95;
        guiGraphics.drawString(this.font, "§eRewards:", centerX - 130, boxY + 10, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, "§fAlignment: +" + quest.getAlignmentReward(), centerX - 120, boxY + 25, 0xFFFFFF, false);

        int itemY = boxY + 45;
        guiGraphics.drawString(this.font, "§fItems:", centerX - 120, itemY, 0xFFFFFF, false);
        itemY += 12;
        if (quest.getItemRewards().isEmpty()) {
            guiGraphics.drawString(this.font, "§7None", centerX - 110, itemY, 0xFFFFFF, false);
        } else {
            for (ItemStack stack : quest.getItemRewards()) {
                guiGraphics.drawString(this.font, "§f• " + stack.getCount() + "x " + stack.getHoverName().getString(), centerX - 110, itemY, 0xFFFFFF, false);
                itemY += 11;
            }
        }
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
    protected void init() {
        int centerX = this.width / 2;
        int startY = (this.height - 256) / 2;
        String acceptText = quest.getTargetFraction() == PlayerProgression.Fraction.FALLEN ? "Accept Fate" : (npcType == PlayerProgression.Fraction.DEMON ? "Accept Contract" : "Accept Quest");
        this.addRenderableWidget(Button.builder(Component.literal(acceptText), (btn) -> { PacketDistributor.sendToServer(new AcceptQuestPayload(quest.getId())); this.onClose(); }).bounds(centerX - 110, startY + 220, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("Back"), (btn) -> { Minecraft.getInstance().setScreen(new QuestBoardScreen(this.npcType)); }).bounds(centerX + 10, startY + 220, 100, 20).build());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}