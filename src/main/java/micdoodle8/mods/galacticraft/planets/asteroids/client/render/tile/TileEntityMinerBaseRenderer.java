package micdoodle8.mods.galacticraft.planets.asteroids.client.render.tile;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityMinerBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
public class TileEntityMinerBaseRenderer extends TileEntitySpecialRenderer<TileEntityMinerBase>
{
    public static OBJModel minerBaseModel;
    public static OBJModel.OBJBakedModel minerBaseModelBaked;

    public IBakedModel getBakedModel()
    {
        if (minerBaseModelBaked == null)
        {
            try
            {
                minerBaseModel = (OBJModel) ModelLoaderRegistry.getModel(new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "minerbase0.obj"));
                minerBaseModel = (OBJModel) minerBaseModel.process(ImmutableMap.of("flip-v", "true"));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            Function<ResourceLocation, TextureAtlasSprite> spriteFunction = new Function<ResourceLocation, TextureAtlasSprite>() {
                @Override
                public TextureAtlasSprite apply(ResourceLocation location) {
                    return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                }
            };
            minerBaseModelBaked = (OBJModel.OBJBakedModel) minerBaseModel.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, spriteFunction);
        }
        return minerBaseModelBaked;
    }

    @Override
    public void renderTileEntityAt(TileEntityMinerBase tile, double x, double y, double z, float partialTicks, int destroyStage)
    {
        if (!tile.isMaster)
        {
            return;
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        int i = tile.getWorld().getLightFor(EnumSkyBlock.SKY, tile.getPos().up());
        int j = i % 65536;
        int k = i / 65536;
        float lightMapSaveX = OpenGlHelper.lastBrightnessX;
        float lightMapSaveY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);

        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glTranslatef((float) x + 1F, (float) y + 1F, (float) z + 1F);
        GL11.glScalef(0.05F, 0.05F, 0.05F);

        switch (tile.facing)
        {
            case SOUTH:
                GL11.glRotatef(180F, 0, 1F, 0);
                break;
            case WEST:
                GL11.glRotatef(90F, 0, 1F, 0);
                break;
            case NORTH:
                break;
            case EAST:
                GL11.glRotatef(270F, 0, 1F, 0);
                break;
        }

        RenderHelper.disableStandardItemLighting();
        this.bindTexture(TextureMap.locationBlocksTexture);

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getWorldRenderer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        GlStateManager.translate(-tile.getPos().getX(), -tile.getPos().getY(), -tile.getPos().getZ());
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tile.getWorld(), getBakedModel(), tile.getWorld().getBlockState(tile.getPos()), tile.getPos(), tessellator.getWorldRenderer());
        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
        GL11.glPopMatrix();
    }
}
