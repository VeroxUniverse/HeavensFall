package net.pixeldream.heavensfall.items;

import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.pixeldream.heavensfall.HeavensFallMod;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class AnimatedWingsItem extends ArmorItem implements ICurioItem {

    private static final Holder<Attribute> FALL_FLY_ATTRIBUTE = CaelusApi.getInstance().getFallFlyingAttribute();
    private static final ResourceLocation ANGEL_WINGS_ID = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "angel_wings_flight");
    private static final ResourceLocation ARMOR_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "curio_armor_bonus");

    private static final double ARMOR_POINTS = 2.0;

    public final WingsItemDispatcher dispatcher;

    public AnimatedWingsItem(Type type, Properties properties) {
        super(ArmorMaterials.DIAMOND, type, properties);
        this.dispatcher = new WingsItemDispatcher();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {

            boolean isEquipped = player.getItemBySlot(EquipmentSlot.CHEST) == stack;

            if (isEquipped) {
                if (player.isFallFlying()) {
                    handleFlightAnimations(player, stack);
                } else {
                    dispatcher.closeWings(player, stack);
                }
            }
        }
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (entity instanceof Player player) {
            player.fallDistance = 0.0f;
            handleFlightAnimations(player, stack);
        }
        return true;
    }

    private void handleFlightAnimations(Player player, ItemStack stack) {
        double speed = player.getDeltaMovement().length();

        if (speed > 0.65 || player.getXRot() > 35.0f) {
            dispatcher.glideWings(player, stack);
        } else {
            dispatcher.flyWings(player, stack);
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player) || player.level().isClientSide) return;

        applyCurioAttributes(player);

        if (player.isFallFlying()) {
            player.fallDistance = 0.0f;
            handleFlightAnimations(player, stack);
        } else {
            dispatcher.closeWings(player, stack);
        }
    }

    private void applyCurioAttributes(Player player) {
        var attrInstance = player.getAttribute(FALL_FLY_ATTRIBUTE);
        if (attrInstance != null && attrInstance.getModifier(ANGEL_WINGS_ID) == null) {
            attrInstance.addTransientModifier(new AttributeModifier(ANGEL_WINGS_ID, 1.0, AttributeModifier.Operation.ADD_VALUE));
        }

        var armorAttr = player.getAttribute(Attributes.ARMOR);
        if (armorAttr != null && armorAttr.getModifier(ARMOR_MODIFIER_ID) == null) {
            armorAttr.addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_ID, ARMOR_POINTS, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack stack, ItemStack newStack) {
        LivingEntity entity = slotContext.entity();
        entity.getAttribute(FALL_FLY_ATTRIBUTE).removeModifier(ANGEL_WINGS_ID);
        entity.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_ID);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_ELYTRA;
    }
}