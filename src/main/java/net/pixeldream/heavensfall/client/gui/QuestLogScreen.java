package net.pixeldream.heavensfall.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.network.ClientPayloadHandler;
import net.pixeldream.heavensfall.network.TurnInQuestPayload;
import net.pixeldream.heavensfall.quests.IQuest;
import net.pixeldream.heavensfall.quests.QuestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuestLogScreen extends Screen {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "textures/gui/quest_ui.png");
    private int activeTab = 1;
    private int currentPage = 0;

    public QuestLogScreen() {
        super(Component.literal("Celestial Log"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int startY = (this.height - 256) / 2;
        if (this.minecraft.player == null) return;
        PlayerProgression data = this.minecraft.player.getData(HFAttachments.PROGRESSION);

        String currentRank = (data.alignment() < 0) ? "§4" + data.demonRank().toUpperCase()
                : (data.alignment() > 0 ? "§b" + data.angelRank().toUpperCase() : "§7[ UNBOUND ]");
        guiGraphics.drawCenteredString(this.font, currentRank, centerX, startY + 15, 0xFFFFFF);
        renderAlignmentBar(guiGraphics, centerX, startY + 35, data.alignment());

        int contentY = startY + 95;
        if (activeTab == 0) {
            List<String> pacts = data.completedQuests().stream().filter(id -> id.contains("pact")).toList();
            if (pacts.isEmpty()) {
                guiGraphics.drawCenteredString(this.font, "§7No pacts made yet.", centerX, contentY, 0xFFFFFF);
            } else {
                renderPacts(guiGraphics, centerX, contentY, data.completedQuests());
            }
        } else {
            if (data.activeQuests().isEmpty()) {
                guiGraphics.drawCenteredString(this.font, "§7No active quests.", centerX, contentY, 0xFFFFFF);
            } else {
                renderQuests(guiGraphics, centerX, contentY, data);
            }
        }

        guiGraphics.drawCenteredString(this.font, "Page: " + (currentPage + 1), centerX, startY + 230, 0xFFFFFF);
    }

    private void renderAlignmentBar(GuiGraphics guiGraphics, int centerX, int y, int alignment) {
        int fillWidth = (int) (alignment * 0.8f);
        int color = alignment > 0 ? 0xFF55FFFF : (alignment < 0 ? 0xFFFF5555 : 0xFF888888);
        if (fillWidth != 0) {
            int xPos = alignment > 0 ? centerX : centerX + fillWidth;
            guiGraphics.fill(xPos, y + 1, xPos + Math.abs(fillWidth), y + 9, color);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.drawCenteredString(this.font, "§l" + alignment, centerX, y + 1, 0xFFFFFF);
    }

    private void renderPacts(GuiGraphics guiGraphics, int centerX, int y, Set<String> completed) {
        List<String> pacts = completed.stream().filter(id -> id.contains("pact")).toList();
        int startIdx = currentPage * 8;
        int currentY = y;
        for (int i = startIdx; i < Math.min(startIdx + 8, pacts.size()); i++) {
            String name = pacts.get(i).contains(":") ? pacts.get(i).split(":")[1].replace("_", " ") : pacts.get(i);
            guiGraphics.drawString(this.font, "§6📜 §f" + name.toUpperCase(), centerX - 140, currentY, 0xFFFFFF, false);
            currentY += 15;
        }
    }

    private int getQuestBlockHeight() {
        return 12 + 14 + 8;
    }

    private void renderQuests(GuiGraphics guiGraphics, int centerX, int y, PlayerProgression data) {
        List<String> activeIds = new ArrayList<>(data.activeQuests());
        int startIdx = currentPage * 4;
        int currentY = y;
        for (int i = startIdx; i < Math.min(startIdx + 4, activeIds.size()); i++) {
            IQuest quest = QuestManager.getQuests().get(activeIds.get(i));
            if (quest == null) continue;

            int progress = ClientPayloadHandler.clientQuestProgress.getOrDefault(activeIds.get(i), 0);
            boolean completed = quest.isCountable()
                    ? progress >= quest.getRequiredAmount()
                    : isQuestCompletedClient(quest);

            String progressText = quest.isCountable()
                    ? "[" + progress + "/" + quest.getRequiredAmount() + "]"
                    : (completed ? "[ready]" : "[active]");
            int progressColor = completed ? 0x55FF55 : 0xAAAAAA;

            guiGraphics.drawString(this.font, "§e▶ " + quest.getTitle(), centerX - 140, currentY, 0xFFFFFF, false);
            guiGraphics.drawString(this.font, progressText, centerX + 60, currentY, progressColor, false);
            currentY += 12;

            if (!completed) {
                List<FormattedCharSequence> lines = this.font.split(Component.literal(quest.getDescription()), 250);
                for (FormattedCharSequence line : lines) {
                    guiGraphics.drawString(this.font, line, centerX - 130, currentY, 0x555555, false);
                    currentY += 10;
                }
            } else {
                currentY += 14;
            }
            currentY += 8;
        }
    }

    private boolean isQuestCompletedClient(IQuest quest) {
        if (this.minecraft.player == null) return false;
        return quest.isCompleted(this.minecraft.player);
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

    public void rebuildAllWidgets() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int startY = (this.height - 256) / 2;

        // Tab Buttons
        this.addRenderableWidget(Button.builder(Component.literal("PACTS"),
                        (btn) -> { activeTab = 0; currentPage = 0; rebuildAllWidgets(); })
                .bounds(centerX - 110, startY + 65, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("QUESTS"),
                        (btn) -> { activeTab = 1; currentPage = 0; rebuildAllWidgets(); })
                .bounds(centerX + 10, startY + 65, 100, 20).build());

        // Page Buttons
        this.addRenderableWidget(Button.builder(Component.literal("<"), (btn) -> {
            if (currentPage > 0) { currentPage--; rebuildAllWidgets(); }
        }).bounds(centerX - 145, startY + 225, 20, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(">"), (btn) -> {
            if (this.minecraft.player == null) return;
            PlayerProgression data = this.minecraft.player.getData(HFAttachments.PROGRESSION);
            int totalItems = activeTab == 0
                    ? (int) data.completedQuests().stream().filter(id -> id.contains("pact")).count()
                    : data.activeQuests().size();
            int itemsPerPage = activeTab == 0 ? 8 : 4;
            if ((currentPage + 1) * itemsPerPage < totalItems) { currentPage++; rebuildAllWidgets(); }
        }).bounds(centerX + 125, startY + 225, 20, 20).build());

        if (activeTab == 1 && this.minecraft.player != null) {
            PlayerProgression data = this.minecraft.player.getData(HFAttachments.PROGRESSION);
            List<String> activeIds = new ArrayList<>(data.activeQuests());
            int startIdx = currentPage * 4;
            int questY = startY + 95;

            for (int i = startIdx; i < Math.min(startIdx + 4, activeIds.size()); i++) {
                IQuest quest = QuestManager.getQuests().get(activeIds.get(i));
                if (quest == null) continue;

                int progress = ClientPayloadHandler.clientQuestProgress.getOrDefault(activeIds.get(i), 0);
                boolean completed = quest.isCountable()
                        ? progress >= quest.getRequiredAmount()
                        : isQuestCompletedClient(quest);

                if (completed) {
                    final String questId = activeIds.get(i);
                    this.addRenderableWidget(Button.builder(
                            Component.literal("✔ Hand In"),
                            (btn) -> {
                                PacketDistributor.sendToServer(new TurnInQuestPayload(questId));
                                this.onClose();
                            }
                    ).bounds(centerX - 50, questY + 12, 100, 14).build());
                }

                questY += getQuestBlockHeight();
            }
        }
    }

    @Override
    protected void init() {
        rebuildAllWidgets();
    }

    @Override
    public boolean isPauseScreen() { return false; }
}