package dev.dubhe.anvilcraft.mixin;

import dev.dubhe.anvilcraft.AnvilCraft;
import dev.dubhe.anvilcraft.item.IHasDefaultEnchantment;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true)
    private static void getItemEnchantmentLevel(
        Holder<Enchantment> enchantment, @NotNull ItemStack stack, CallbackInfoReturnable<Integer> cir
    ) {
        if (!(stack.getItem() instanceof IHasDefaultEnchantment item)) return;
        int level = cir.getReturnValue();
        int defaultLevel = item.getDefaultEnchantmentLevel(enchantment.value());
        cir.setReturnValue(Math.max(level, defaultLevel));
    }
}
