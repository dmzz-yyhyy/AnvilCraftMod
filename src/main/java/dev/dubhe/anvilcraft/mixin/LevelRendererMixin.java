package dev.dubhe.anvilcraft.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import dev.dubhe.anvilcraft.client.init.ModRenderTargets;
import dev.dubhe.anvilcraft.client.init.ModRenderTypes;
import dev.dubhe.anvilcraft.client.init.ModShaders;
import dev.dubhe.anvilcraft.client.renderer.laser.LaserRenderState;
import dev.dubhe.anvilcraft.util.RenderHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow
    protected abstract void renderSectionLayer(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix);

    @Shadow
    @Nullable
    private RenderTarget translucentTarget;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            ordinal = 5,
            shift = At.Shift.AFTER
        )
    )
    void renderLayer(
        DeltaTracker deltaTracker,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {
        anvilcraft$renderLaser(deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture, frustumMatrix, projectionMatrix);
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            ordinal = 3,
            shift = At.Shift.AFTER
        )
    )
    void renderLayer1(
        DeltaTracker deltaTracker,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {
        anvilcraft$renderLaser(deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture, frustumMatrix, projectionMatrix);
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V"
        )
    )
    void laserPostProcess(
        DeltaTracker deltaTracker,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix,
        Matrix4f projectionMatrix,
        CallbackInfo ci
    ) {
        if (!LaserRenderState.isEnhancedRenderingAvailable())return;
        RenderTarget mcInput = ModShaders.getLaserBloomChain().getTempTarget("mcinput");
        mcInput.setClearColor(
            FogRenderer.fogRed,
            FogRenderer.fogGreen,
            FogRenderer.fogBlue,
            0f
        );
        mcInput.clear(Minecraft.ON_OSX);
        ModShaders.getLaserBloomChain().process(RenderHelper.getPartialTick());
        this.minecraft.getMainRenderTarget().bindWrite(false);
    }

    @Unique
    private void anvilcraft$renderLaser(
        DeltaTracker deltaTracker,
        boolean renderBlockOutline,
        Camera camera,
        GameRenderer gameRenderer,
        LightTexture lightTexture,
        Matrix4f frustumMatrix,
        Matrix4f projectionMatrix
    ) {
        if (!LaserRenderState.isEnhancedRenderingAvailable())return;
        Vec3 vec3 = camera.getPosition();
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        if (this.translucentTarget != null) {
            this.translucentTarget.clear(Minecraft.ON_OSX);
            this.translucentTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
        }
        if (ModRenderTargets.getLaserTarget() != null) {
            ModRenderTargets.getLaserTarget().setClearColor(0, 0, 0, 0);
            ModRenderTargets.getLaserTarget().clear(Minecraft.ON_OSX);
            ModRenderTargets.getLaserTarget().copyDepthFrom(this.minecraft.getMainRenderTarget());
        }

        LaserRenderState.levelStage();
        this.renderSectionLayer(ModRenderTypes.LASER, d0, d1, d2, frustumMatrix, projectionMatrix);
        LaserRenderState.bloomStage();
        this.renderSectionLayer(ModRenderTypes.LASER, d0, d1, d2, frustumMatrix, projectionMatrix);
    }


}