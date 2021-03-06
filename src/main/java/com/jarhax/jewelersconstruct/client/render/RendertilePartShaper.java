package com.jarhax.jewelersconstruct.client.render;

import org.lwjgl.opengl.GL11;

import com.jarhax.jewelersconstruct.tileentities.TileEntityPartShaper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class RendertilePartShaper extends TileEntitySpecialRenderer<TileEntityPartShaper> {
    
    @Override
    public void render (TileEntityPartShaper te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        
        if (te.getFuel() > 0 && te.isProcessing()) {
            GlStateManager.disableLighting();
            GlStateManager.translate(this.scale(5), 0.5, this.scale(5));
            GlStateManager.scale(1f / 4 + this.scale(2), 1.4f / 4, 1f / 4 + this.scale(2));
            renderBlockModel(te.getWorld(), te.getPos(), Blocks.FIRE.getDefaultState(), true);
            GlStateManager.enableLighting();
        }
        GlStateManager.translate(-x, -y, -z);
        GlStateManager.popMatrix();
    }
    
    public float scale (float px) {
        
        return px * 0.0625f;
    }
    
    public static void renderBlockModel (World world, BlockPos pos, IBlockState state, boolean translateToOrigin) {
        
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        final BufferBuilder wr = Tessellator.getInstance().getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        if (translateToOrigin) {
            wr.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }
        final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        final BlockModelShapes modelShapes = blockrendererdispatcher.getBlockModelShapes();
        final IBakedModel ibakedmodel = modelShapes.getModelForState(state);
        final IBlockAccess worldWrapper = world;
        for (final BlockRenderLayer layer : BlockRenderLayer.values()) {
            if (state.getBlock().canRenderInLayer(state, layer)) {
                ForgeHooksClient.setRenderLayer(layer);
                blockrendererdispatcher.getBlockModelRenderer().renderModel(worldWrapper, ibakedmodel, state, pos, wr, false);
            }
        }
        ForgeHooksClient.setRenderLayer(null);
        if (translateToOrigin) {
            wr.setTranslation(0, 0, 0);
        }
        Tessellator.getInstance().draw();
    }
}
