package mods.blockworks.util;

import java.util.EnumSet;

import mods.blockworks.Blockworks;
import mods.tinker.tconstruct.client.block.BlockSkinRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class BTickHandler implements ITickHandler
{
    public static Minecraft mc;
    MovingObjectPosition mop;
    //private RenderBlocks renderBlocksInstance = new RenderBlocks();

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        if (mc.theWorld != null)
        {
            mop = this.getMovingObjectPositionFromPlayer(mc.theWorld, mc.thePlayer, false);
        }
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.RENDER);
    }

    @Override
    public String getLabel ()
    {
        return "render.ghost";
    }

    public MovingObjectPosition getMovingObjectPositionFromPlayer (World par1World, EntityPlayer par2EntityPlayer, boolean par3)
    {
        float f = 1.0F;
        float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
        float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
        double d0 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * (double) f;
        double d1 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * (double) f + 1.62D - (double) par2EntityPlayer.yOffset;
        double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * (double) f;
        Vec3 vec3 = par1World.getWorldVec3Pool().getVecFromPool(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 500.0D;
        if (par2EntityPlayer instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP) par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return par1World.rayTraceBlocks_do_do(vec3, vec31, par3, !par3);
    }
    
    @ForgeSubscribe
    public void lastRender (RenderWorldLastEvent event)
    {
        //GuiIngameForge.renderCrosshairs = false;
        if (mc.thePlayer != null)
        {
            ItemStack equipstack = mc.thePlayer.getCurrentEquippedItem();
            if (equipstack != null && equipstack.getItem() == Blockworks.cubeWand)
            {
                GuiIngameForge.renderCrosshairs = false;
                if (mop != null)
                {
                    double xPos = mop.blockX;
                    double yPos = mop.blockY;
                    double zPos = mop.blockZ;
                    ForgeDirection sideHit = ForgeDirection.getOrientation(mop.sideHit);
                    switch (sideHit)
                    {
                    case UP:
                    {
                        yPos += 1;
                        break;
                    }
                    case DOWN:
                    {
                        yPos -= 1;
                        break;
                    }
                    case NORTH:
                    {
                        zPos -= 1;
                        break;
                    }
                    case SOUTH:
                    {
                        zPos += 1;
                        break;
                    }
                    case EAST:
                    {
                        xPos += 1;
                        break;
                    }
                    case WEST:
                    {
                        xPos -= 1;
                        break;
                    }
                    default:
                        break;
                    }

                    Tessellator ts = Tessellator.instance;
                    Tessellator.renderingWorldRenderer = false;
                    //event.context.renderEngine.bindTexture("/terrain.png");
                    int texture = event.context.renderEngine.getTexture("/mods/tinker/textures/blocks/compressed_steel.png");
                    //ts.startDrawing();
                    //System.out.println("Rawr!" +xPos);
                    //GL11.glTranslated(xPos, yPos, zPos);
                    //GL11.glScalef(2, 2, 2);
                    //renderBlockBox(ts);

                    double xD = xPos + 0.5F;
                    double yD = yPos + 0.5F;
                    double zD = zPos + 0.5F;
                    double iPX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
                    double iPY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
                    double iPZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;

                    GL11.glDepthMask(false);
                    GL11.glDisable(GL11.GL_CULL_FACE);

                    for (int i = 0; i < 6; i++)
                    {
                        ForgeDirection forgeDir = ForgeDirection.getOrientation(i);
                        int zCorrection = i == 2 ? -1 : 1;
                        GL11.glPushMatrix();
                        GL11.glTranslated(-iPX + xD, -iPY + yD, -iPZ + zD);
                        GL11.glScalef(0.999F, 0.999F, 0.999F);
                        GL11.glRotatef(90, forgeDir.offsetX, forgeDir.offsetY, forgeDir.offsetZ);
                        GL11.glTranslated(0, 0, 0.5f * zCorrection);
                        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                        renderPulsingQuad(texture, 0.75F);
                        GL11.glPopMatrix();
                    }

                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glDepthMask(true);

                    //GL11.glTranslated(iPX, iPY, iPZ);
                    /*event.context.globalRenderBlocks.renderStandardBlock(Block.blockIron, xPos, yPos, zPos);
                    event.context.globalRenderBlocks.renderStandardBlock(Block.blockIron, -64, 91, 192);*/
                    //event.context.globalRenderBlocks.setRenderBounds(-1, -1, -1, 2, 2, 2);
                    //event.context.globalRenderBlocks.renderStandardBlock(Block.blockIron, 0, 0, 0);
                }

            }
            else
            {
                GuiIngameForge.renderCrosshairs = true;
            }
        }
    }

    public static void renderPulsingQuad (int texture, float maxTransparency)
    {

        float pulseTransparency = 2f * maxTransparency / 3f;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        Tessellator tessellator = Tessellator.instance;

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, pulseTransparency);

        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(1, 1, 1, pulseTransparency);

        tessellator.addVertexWithUV(-0.5D, 0.5D, 0F, 0, 1);
        tessellator.addVertexWithUV(0.5D, 0.5D, 0F, 1, 1);
        tessellator.addVertexWithUV(0.5D, -0.5D, 0F, 1, 0);
        tessellator.addVertexWithUV(-0.5D, -0.5D, 0F, 0, 0);

        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
