package net.pixeldream.heavensfall.items;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Random;

public class AnimatedWingsItem extends ArmorItem implements ICurioItem {

    public final WingsItemDispatcher dispatcher;

    public AnimatedWingsItem(Type type) {
        super(ArmorMaterials.NETHERITE, type,
                new Properties()
                        .stacksTo(1)
                        .durability(1024));
        this.dispatcher = new WingsItemDispatcher();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> swapWithEquipmentSlot(
            @NotNull Item item,
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        InteractionResultHolder<ItemStack> result = super.swapWithEquipmentSlot(item, level, player, hand);

        if (!level.isClientSide) {
            EquipmentSlot slot = getEquipmentSlot();
            ItemStack itemStack = player.getItemBySlot(slot);
            dispatcher.closeWings(player, itemStack);
        }

        return result;
    }

    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_ELYTRA;
    }


    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.PHANTOM_MEMBRANE);
    }


    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide && flightTicks % 10 == 0) {
            stack.hurtAndBreak(1, entity, null);
        }
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            player.getArmorSlots().forEach(wornArmor -> {
                if (wornArmor != null && wornArmor.is(HFItems.ANGEL_WINGS)) {
                    if (player.isFallFlying()) {
                        if (player.getRandom().nextBoolean()) {
                            dispatcher.glideWings(player, stack);
                        } else {
                            dispatcher.flyWings(player, stack);
                        }
                    } else if (player.onGround()) {
                        dispatcher.closeWings(entity, stack);
                    } else {
                        dispatcher.openWings(entity, stack);
                    }
                }
            });
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity instanceof Player player && !player.level().isClientSide) {
            if (stack.is(HFItems.ANGEL_WINGS)) {
                if (player.isFallFlying()) {
                    if (player.getRandom().nextBoolean()) {
                        dispatcher.glideWings(player, stack);
                    } else {
                        dispatcher.flyWings(player, stack);
                    }
                } else if (player.onGround()) {
                    dispatcher.closeWings(player, stack);
                } else {
                    dispatcher.openWings(player, stack);
                }
            }
        }
    }
}
