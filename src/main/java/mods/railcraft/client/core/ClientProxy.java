/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.client.core;

import mods.railcraft.common.blocks.aesthetics.lantern.BlockLantern;
import org.apache.logging.log4j.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.VillagerRegistry;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;

import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import mods.railcraft.client.render.*;
import mods.railcraft.client.render.carts.LocomotiveRendererDefault;
import mods.railcraft.client.render.carts.LocomotiveRendererElectric;
import mods.railcraft.client.render.carts.RenderCart;
import mods.railcraft.client.render.carts.RenderItemLocomotive;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamMagic;
import mods.railcraft.client.render.models.locomotives.ModelLocomotiveSteamSolid;
import mods.railcraft.client.sounds.RCSoundHandler;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import mods.railcraft.common.blocks.aesthetics.post.TilePostEmblem;
import mods.railcraft.common.blocks.aesthetics.wall.BlockRailcraftWall;
import mods.railcraft.common.blocks.machine.alpha.TileSteamTurbine;
import mods.railcraft.common.blocks.machine.beta.TileChestMetals;
import mods.railcraft.common.blocks.machine.beta.TileChestVoid;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHigh;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamHobby;
import mods.railcraft.common.blocks.machine.beta.TileEngineSteamLow;
import mods.railcraft.common.blocks.machine.beta.TileTankIronGauge;
import mods.railcraft.common.blocks.machine.beta.TileTankIronValve;
import mods.railcraft.common.blocks.machine.beta.TileTankIronWall;
import mods.railcraft.common.blocks.machine.beta.TileTankSteelGauge;
import mods.railcraft.common.blocks.machine.beta.TileTankSteelValve;
import mods.railcraft.common.blocks.machine.beta.TileTankSteelWall;
import mods.railcraft.common.blocks.machine.delta.TileCage;
import mods.railcraft.common.blocks.machine.gamma.TileLiquidLoader;
import mods.railcraft.common.blocks.machine.gamma.TileLiquidUnloader;
import mods.railcraft.common.blocks.tracks.TileTrackTESR;
import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityTunnelBore;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.core.CommonProxy;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.items.ItemGoggles;
import mods.railcraft.common.items.firestone.TileFirestoneRecharge;
import mods.railcraft.common.modules.ModuleWorld;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundRegistry;
import net.minecraft.item.Item;

public class ClientProxy extends CommonProxy {

    @Override
    public World getClientWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public String getItemDisplayName(ItemStack stack) {
        return stack.getItem().getItemStackDisplayName(stack);
    }

    @Override
    public String getCurrentLanguage() {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
    }

    @Override
    public int getRenderId() {
        return RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void preInitClient() {
        MinecraftForge.EVENT_BUS.register(RCSoundHandler.INSTANCE);
    }

    @Override
    public void initClient() {
        FMLCommonHandler.instance().bus().register(new LatestVersionMessage());

        SoundRegistry.setupBlockSounds();

        FMLCommonHandler.instance().bus().register(LocomotiveKeyHandler.INSTANCE);

        if (!ItemGoggles.areEnabled())
            FMLCommonHandler.instance().bus().register(AuraKeyHandler.INSTANCE);

        Game.log(Level.TRACE, "Init Start: Renderer");

        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:default", "locomotive.model.steam.solid.default", new ModelLocomotiveSteamSolid()));
//        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:magic", "locomotive.model.steam.magic.default", new ModelLocomotiveSteamMagic()));
//        LocomotiveRenderType.STEAM_SOLID.registerRenderer(new LocomotiveRendererDefault("railcraft:electric", "locomotive.model.electric.default", new ModelLocomotiveElectric()));
        LocomotiveRenderType.STEAM_MAGIC.registerRenderer(new LocomotiveRendererDefault("railcraft:default", "locomotive.model.steam.magic.default", new ModelLocomotiveSteamMagic()));
        LocomotiveRenderType.ELECTRIC.registerRenderer(new LocomotiveRendererElectric());

        ItemStack stack = LocomotiveRenderType.STEAM_SOLID.getItemWithRenderer("railcraft:default");
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.STEAM_SOLID, (EntityLocomotive) EnumCart.LOCO_STEAM_SOLID.makeCart(stack, null, 0, 0, 0)));

        stack = LocomotiveRenderType.STEAM_MAGIC.getItemWithRenderer("railcraft:default");
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.STEAM_MAGIC, (EntityLocomotive) EnumCart.LOCO_STEAM_MAGIC.makeCart(stack, null, 0, 0, 0)));

        stack = LocomotiveRenderType.ELECTRIC.getItemWithRenderer("railcraft:default");
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderItemLocomotive(LocomotiveRenderType.ELECTRIC, (EntityLocomotive) EnumCart.LOCO_ELECTRIC.makeCart(stack, null, 0, 0, 0)));

        RenderLiquidLoader liquidLoaderRenderer = new RenderLiquidLoader();
        ClientRegistry.bindTileEntitySpecialRenderer(TileLiquidLoader.class, liquidLoaderRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer(TileLiquidUnloader.class, liquidLoaderRenderer);

        ClientRegistry.bindTileEntitySpecialRenderer(TileTankIronGauge.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankIronWall.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankIronValve.class, new RenderIronTank());

        ClientRegistry.bindTileEntitySpecialRenderer(TileTankSteelGauge.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankSteelWall.class, new RenderIronTank());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTankSteelValve.class, new RenderIronTank());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineSteamHobby.class, RenderPneumaticEngine.renderHobby);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineSteamLow.class, RenderPneumaticEngine.renderLow);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineSteamHigh.class, RenderPneumaticEngine.renderHigh);

        ClientRegistry.bindTileEntitySpecialRenderer(TileCage.class, new RenderCagedEntity());

        ClientRegistry.bindTileEntitySpecialRenderer(TileTrackTESR.class, new RenderTrackBuffer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileChestVoid.class, new RenderChest(RailcraftConstants.TESR_TEXTURE_FOLDER + "chest_void.png", new TileChestVoid()));
        ClientRegistry.bindTileEntitySpecialRenderer(TileChestMetals.class, new RenderChest(RailcraftConstants.TESR_TEXTURE_FOLDER + "chest_metals.png", new TileChestMetals()));

        ClientRegistry.bindTileEntitySpecialRenderer(TilePostEmblem.class, new RenderBlockPost.EmblemPostTESR());

        ClientRegistry.bindTileEntitySpecialRenderer(TileFirestoneRecharge.class, new RenderTESRFirestone());

        ClientRegistry.bindTileEntitySpecialRenderer(TileSteamTurbine.class, new RenderTurbineGauge());

        if (RailcraftBlocks.getBlockTrack() != null)
            RenderingRegistry.registerBlockHandler(new RenderTrack());

        if (RailcraftBlocks.getBlockElevator() != null)
            RenderingRegistry.registerBlockHandler(new RenderElevator());

        registerBlockRenderer(new RenderBlockMachineBeta());
        registerBlockRenderer(new RenderBlockMachineDelta());
        registerBlockRenderer(new RenderBlockSignal());
        registerBlockRenderer(RenderBlockPost.make());
        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.post));
        registerBlockRenderer(RenderBlockPostMetal.make(BlockPostMetal.platform));
        registerBlockRenderer(new RenderBlockOre());
        registerBlockRenderer(new RenderBlockFrame());
        registerBlockRenderer(new RenderBlockStrengthGlass());
        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockStone()));
        registerBlockRenderer(new RenderBlockLamp(BlockLantern.getBlockMetal()));
        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockAlpha()));
        registerBlockRenderer(new RenderWall(BlockRailcraftWall.getBlockBeta()));
        registerBlockRenderer(new RenderStair());
        registerBlockRenderer(new RenderSlab());

        RenderingRegistry.registerEntityRenderingHandler(EntityTunnelBore.class, new RenderTunnelBore());
        RenderingRegistry.registerEntityRenderingHandler(EntityMinecart.class, new RenderCart());

        stack = EnumCart.TANK.getCartItem();
        if (stack != null)
            MinecraftForgeClient.registerItemRenderer(stack.getItem(), new RenderTankCartItem());

        Minecraft.getMinecraft().entityRenderer.debugViewDirection = 0;
        FMLCommonHandler.instance().bus().register(new DebugViewTicker());

        if (RailcraftConfig.isWorldGenEnabled("workshop")) {
            int id = RailcraftConfig.villagerID();
            VillagerRegistry.instance().registerVillagerSkin(id, ModuleWorld.VILLAGER_TEXTURE);
        }

        Game.log(Level.TRACE, "Init Complete: Renderer");
    }

    private void registerBlockRenderer(BlockRenderer renderer) {
        if (renderer.getBlock() != null) {
            RenderingRegistry.registerBlockHandler(renderer);
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.getBlock()), renderer.getItemRenderer());
        }
    }

}
