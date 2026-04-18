package net.pixeldream.heavensfall.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.pixeldream.heavensfall.recipes.ritual.ItemMultiSet;

import java.util.List;

public class RitualRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public RitualRecipeSerializer(RitualRecipeFactory<T> factory) {
        this.codec = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("central_item").forGetter(r -> ((RitualRecipeAccessor)r).getCentralItem()),
                BuiltInRegistries.ITEM.byNameCodec().listOf().fieldOf("input_items").forGetter(r -> ((RitualRecipeAccessor)r).getInputList()),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> ((RitualRecipeAccessor)r).getResultStack())
        ).apply(inst, (central, inputs, result) -> factory.create(central, new ItemMultiSet(inputs), result)));

        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.registry(BuiltInRegistries.ITEM.key()), r -> ((RitualRecipeAccessor)r).getCentralItem(),
                ByteBufCodecs.registry(BuiltInRegistries.ITEM.key()).apply(ByteBufCodecs.list()), r -> ((RitualRecipeAccessor)r).getInputList(),
                ItemStack.STREAM_CODEC, r -> ((RitualRecipeAccessor)r).getResultStack(),
                (central, inputs, result) -> factory.create(central, new ItemMultiSet(inputs), result)
        );
    }

    @Override
    public MapCodec<T> codec() { return codec; }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }

    public interface RitualRecipeFactory<T> {
        T create(Item central, ItemMultiSet inputs, ItemStack result);
    }

    public interface RitualRecipeAccessor {
        Item getCentralItem();
        List<Item> getInputList();
        ItemStack getResultStack();
    }
}