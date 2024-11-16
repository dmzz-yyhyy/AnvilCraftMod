package dev.dubhe.anvilcraft.client.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.dubhe.anvilcraft.AnvilCraft;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public class ModShaders {
    public static final ResourceLocation LASER_BLOOM_LOCATION = ResourceLocation.fromNamespaceAndPath(
        "anvilcraft",
        "shaders/post/bloom.json"
    );

    @Getter
    private static PostChain bloomChain;
    static final Minecraft MINECRAFT = Minecraft.getInstance();

    static ShaderInstance renderTypeLaserShader;


    public static void register(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(
                    event.getResourceProvider(),
                    AnvilCraft.of("rendertype_laser"),
                    DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL
                ),
                it -> renderTypeLaserShader = it
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resize(int width, int height) {
        if (bloomChain != null) {
            bloomChain.resize(width, height);
        }
    }

    public static void loadBloomEffect(ResourceProvider resourceProvider) throws IOException {
        bloomChain = new PostChain(
            MINECRAFT.getTextureManager(),
            resourceProvider,
            Minecraft.getInstance().getMainRenderTarget(),
            LASER_BLOOM_LOCATION
        );
        bloomChain.resize(
            Minecraft.getInstance().getWindow().getWidth(),
            Minecraft.getInstance().getWindow().getHeight()
        );
        ModRenderTargets.renderTargetLoaded(
            bloomChain.getTempTarget("input")
        );
    }
}
