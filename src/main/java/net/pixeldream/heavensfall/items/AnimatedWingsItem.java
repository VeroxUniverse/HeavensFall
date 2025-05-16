package net.pixeldream.heavensfall.items;

import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pixeldream.heavensfall.HeavensFallMod;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import static mod.azure.azurelib.sblforked.util.RandomUtil.RANDOM;

public class AnimatedWingsItem extends ArmorItem implements ICurioItem {

    private static final Holder<Attribute> FALL_FLY_ATTRIBUTE = CaelusApi.getInstance().getFallFlyingAttribute();
    private static final ResourceLocation ANGEL_WINGS_ID = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "angel_wings_flight");
    private static final ResourceLocation ARMOR_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "curio_armor_bonus");

    private static final int MIN_GLIDE_TICKS  = 5 * 20;
    private static final int MAX_GLIDE_TICKS = 10 * 20;
    private static final int FLY_DURATION_TICKS  = (4 * 20) + 4;

    private static final double ARMOR_POINTS = 2.0;

    public final WingsItemDispatcher dispatcher;

    private int GLIDE_ANIMATION = 0; // 0 = Fly, 1 = Glide
    private int currentTick = 0;
    private int maxTick = FLY_DURATION_TICKS;

    public AnimatedWingsItem(Type type, Properties properties) {
        super(ArmorMaterials.DIAMOND, type, properties);
        this.dispatcher = new WingsItemDispatcher();
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack stack, ItemStack newStack) {
        LivingEntity entity = slotContext.entity();
        var attrInstance = entity.getAttribute(FALL_FLY_ATTRIBUTE);
        if (attrInstance != null && attrInstance.getModifier(ANGEL_WINGS_ID) != null) {
            attrInstance.removeModifier(ANGEL_WINGS_ID);
        }

        var armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null && armorAttr.getModifier(ARMOR_MODIFIER_ID) != null) {
            armorAttr.removeModifier(ARMOR_MODIFIER_ID);
        }
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
            dispatcher.idleWings(player, itemStack);
        }

        return result;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_ELYTRA;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (entity instanceof Player player) {
            Vec3 lookVec = player.getLookAngle().normalize();

            double speed = 1.2;
            double maxSinkSpeed = -0.5;
            double maxRiseSpeed = 0.3;

            double motionY = player.getDeltaMovement().y;

            if (motionY < maxSinkSpeed) {
                motionY = maxSinkSpeed;
            } else if (motionY > maxRiseSpeed) {
                motionY = maxRiseSpeed;
            }

            player.setDeltaMovement(lookVec.x * speed, motionY, lookVec.z * speed);

            player.fallDistance = 0.0f;
        }
        return true;
    }



    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.PHANTOM_MEMBRANE);
    }

    private void handleFlightState(Player player, ItemStack stack) {
        if (GLIDE_ANIMATION == 0) {
            dispatcher.flyWings(player, stack);
        } else {
            dispatcher.glideWings(player, stack);
        }

        currentTick++;

        if (currentTick >= maxTick) {
            currentTick = 0;

            if (GLIDE_ANIMATION == 0) {
                GLIDE_ANIMATION = 1;
                maxTick = MIN_GLIDE_TICKS + RANDOM.nextInt(MAX_GLIDE_TICKS - MIN_GLIDE_TICKS + 1);
            } else {
                GLIDE_ANIMATION = 0;
                maxTick = FLY_DURATION_TICKS;
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (player.isFallFlying()) {
                handleFlightState(player, stack);
                player.fallDistance = 0.0f;
            } else {
                dispatcher.closeWings(player, stack);
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (!(entity instanceof Player player) || player.level().isClientSide) return;

        var attrInstance = player.getAttribute(FALL_FLY_ATTRIBUTE);
        if (attrInstance != null && attrInstance.getModifier(ANGEL_WINGS_ID) == null) {
            AttributeModifier modifier = new AttributeModifier(
                    ANGEL_WINGS_ID,
                    1.0,
                    AttributeModifier.Operation.ADD_VALUE
            );
            attrInstance.addTransientModifier(modifier);
        }

        if (player.isFallFlying()) {
            handleFlightState(player, stack);

            elytraFlightTick(stack, player, 0);

            player.fallDistance = 0.0f;
        } else {
            dispatcher.closeWings(player, stack);
        }

        var armorAttr = player.getAttribute(Attributes.ARMOR);
        if (armorAttr != null && armorAttr.getModifier(ARMOR_MODIFIER_ID) == null) {
            AttributeModifier armorBonus = new AttributeModifier(
                    ARMOR_MODIFIER_ID,
                    ARMOR_POINTS,
                    AttributeModifier.Operation.ADD_VALUE
            );
            armorAttr.addTransientModifier(armorBonus);
        }
    }


}
