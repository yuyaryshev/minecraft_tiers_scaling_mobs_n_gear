package com.lomods.tierscalingmod.mixin;

import com.lomods.tierscalingmod.ServerPlayerAcessor;
import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.lomods.tierscalingmod.Constants.MOD_ID;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements ServerPlayerAcessor {
    @Unique
    private boolean angelic_mode = false;

    @Unique
    public boolean isAngelicMode() {
        return this.angelic_mode;
    }

    @Unique
    public void setAngelicMode(boolean angelicMode) {
        this.angelic_mode = angelicMode;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void hook_addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        CompoundTag modTag = new CompoundTag();
        modTag.putBoolean("angelic_mode", this.angelic_mode);
        nbt.put(MOD_ID, modTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void hook_readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains(MOD_ID)) {
            CompoundTag modTag = nbt.getCompound(MOD_ID);
            this.angelic_mode = modTag.getBoolean("angelic_mode");
        }
    }

}