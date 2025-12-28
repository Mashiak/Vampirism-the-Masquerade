package net.daanlokdrog.vampirismthemasquerade;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.Minecraft;

import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialoguePage;
import net.daanlokdrog.vampirismthemasquerade.DialogueData.DialogueOption;
import net.daanlokdrog.vampirismthemasquerade.network.VampirismTheMasqueradeModVariables; 

import java.util.List;

public class NegotiationScreen extends AbstractContainerScreen<NegotiationMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath("vampirism_the_masquerade", "textures/gui/options_background.png");
    private static final ResourceLocation GOLD_INGOT_TEXTURE = ResourceLocation.parse("minecraft:textures/item/gold_ingot.png");
    private static final ResourceLocation NEGOTIATION_ICON =
    ResourceLocation.parse("vampirism_the_masquerade:textures/gui/negotiation.png");
    
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int HOVER_COLOR = ChatFormatting.YELLOW.getColor() | 0xFF000000;
    private static final int DEFAULT_COLOR = ChatFormatting.LIGHT_PURPLE.getColor() | 0xFF000000;
    private static final int ICON_SIZE = 16; 
    private static final int ICON_MARGIN = 6;
    private static final int ICON_TEXT_SPACING = 4;
    private static final int LEFT_PADDING = 8;

    private String currentPageId; 
    private final int entityId; 
    private Entity targetEntity = null; 

    private static final String TARGET_SERVER_CHECK = "SERVER_CHECK";

    public NegotiationScreen(NegotiationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 250;
        this.imageHeight = 150;
        this.entityId = menu.getVillagerEntityId();
        this.currentPageId = menu.getStartPageId(); 

        if (Minecraft.getInstance().level != null) {
            this.targetEntity = Minecraft.getInstance().level.getEntity(this.entityId);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = 5;
    }

    public void setCurrentPageId(String newPageId) {
        if ("EXIT".equals(newPageId)) {
            Minecraft.getInstance().player.closeContainer();
            return;
        }
            
        if (this.targetEntity != null) { 
            IDialogueProvider provider = DialogueProviderRegistry.getProvider(this.targetEntity);
            if (provider != null && provider.getDialoguePages().containsKey(newPageId)) {
                this.currentPageId = newPageId;
            } else {
                Minecraft.getInstance().player.sendSystemMessage(Component.literal("错误: 服务器尝试切换到一个不存在的页面ID: " + newPageId).withStyle(ChatFormatting.RED));
            }
        }
    }


@Override
protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight) / 2;

    RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
    graphics.blit(BACKGROUND_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    graphics.fill(x + 5, y + 22, x + this.imageWidth - 5, y + 24, 0xFF505050);

    int iconSize = 16;
    int iconX = x + 5;
    int iconY = y + 5;
    graphics.blit(
        NEGOTIATION_ICON,
        iconX,
        iconY,
        0,
        0,
        iconSize,
        iconSize,
        iconSize,
        iconSize
    );

    if (this.minecraft.player != null) {
        double donationValue = this.minecraft.player.getData(VampirismTheMasqueradeModVariables.PLAYER_VARIABLES).donation;
        Component donationText = Component.translatable(
            "gui.masquerade.donation_display",
            Component.literal(String.format("%.1f", donationValue)).withStyle(ChatFormatting.GOLD)
        ).withStyle(ChatFormatting.GRAY);

        int displayX = x + LEFT_PADDING;
        int displayY = y + this.imageHeight + ICON_MARGIN;

        graphics.blit(
            GOLD_INGOT_TEXTURE, 
            displayX, 
            displayY, 
            0, 
            0, 
            ICON_SIZE, 
            ICON_SIZE, 
            ICON_SIZE,
            ICON_SIZE
        );
        
        int textY = displayY + (ICON_SIZE - this.font.lineHeight) / 2;
        graphics.drawString(this.font, donationText, displayX + ICON_SIZE + ICON_TEXT_SPACING, textY, TEXT_COLOR, false);
    }
}


    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, TEXT_COLOR, false);

        DialoguePage page = null;
        if (this.targetEntity != null) { 
            page = DialogueProviderRegistry.getPage(this.targetEntity, currentPageId);
        }

        if (page == null) {
            graphics.drawString(this.font, Component.literal("错误: 无法加载对话页面。"), 8, 30, ChatFormatting.RED.getColor(), false);
            return;
        }

        graphics.drawWordWrap(this.font, page.topText, 8, 30, this.imageWidth - 16, TEXT_COLOR);

        List<DialogueOption> options = page.options;
        int optionY = this.imageHeight - 15 - (options.size() * 10) - 12; 
        for (int i = 0; i < options.size(); i++) {
            DialogueOption option = options.get(i);
            int currentY = optionY + i * 10;
            int x = 8;
            boolean isHovered = isMouseInOption(mouseX, mouseY, x + this.leftPos, currentY + this.topPos, option.optionText);
            int color = isHovered ? HOVER_COLOR : DEFAULT_COLOR;

            graphics.drawString(this.font, option.optionText, x, currentY, color, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.targetEntity != null) { 
            DialoguePage page = DialogueProviderRegistry.getPage(this.targetEntity, currentPageId);
            if (page != null) {
                List<DialogueOption> options = page.options;
                int optionY = this.imageHeight - 15 - (options.size() * 10) - 12; 
                for (int i = 0; i < options.size(); i++) {
                    DialogueOption option = options.get(i);
                    int currentY = optionY + i * 10;
                    int x = 8;
                    if (isMouseInOption(mouseX, mouseY, x + this.leftPos, currentY + this.topPos, option.optionText)) {
                        DialoguePacket.sendToServer(this.entityId, option.packetOptionId, this.currentPageId);
                        if ("EXIT".equals(option.nextState)) {
                            this.minecraft.player.closeContainer();
                        } 
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
private boolean isMouseInOption(double mouseX, double mouseY, int optionX, int optionY, Component optionText) {
int textWidth = this.font.width(optionText);
int textHeight = 9;
return mouseX >= optionX && mouseX < optionX + textWidth &&
mouseY >= optionY && mouseY < optionY + textHeight;
}
}