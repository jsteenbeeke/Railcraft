/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import mods.railcraft.api.core.INetworkedObject;
import mods.railcraft.api.core.IOwnable;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AdjacentTileCache;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.PacketBuilder;
import mods.railcraft.common.util.network.PacketTileEntity;
import mods.railcraft.common.util.network.RailcraftPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public abstract class RailcraftTileEntity extends TileEntity implements INetworkedObject, IOwnable {

    private GameProfile owner = new GameProfile(null, "[Railcraft]");
    protected int clock = MiscTools.getRand().nextInt();
    private boolean sendClientUpdate = false;
    protected final AdjacentTileCache tileCache = new AdjacentTileCache(this);

    public AdjacentTileCache getTileCache() {
        return tileCache;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        clock++;

        if (sendClientUpdate) {
            sendClientUpdate = false;
            PacketBuilder.instance().sendTileEntityPacket(this);
        }
    }

    @Override
    public FMLProxyPacket getDescriptionPacket() {
//        System.out.println("Sending Tile Packet");
        RailcraftPacket packet = new PacketTileEntity(this);
        return packet.getPacket();
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
//        data.writeUTF(owner);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
//        owner = data.readUTF();
    }

    public void markBlockForUpdate() {
//        System.out.println("updating");
        if (worldObj != null)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void notifyBlocksOfNeighborChange() {
        if (worldObj != null)
            WorldPlugin.notifyBlocksOfNeighborChange(worldObj, xCoord, yCoord, zCoord, getBlockType());
    }

    public void sendUpdateToClient() {
        if (canUpdate())
            sendClientUpdate = true;
        else
            PacketBuilder.instance().sendTileEntityPacket(this);
    }

    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        if (entityliving instanceof EntityPlayer)
            owner = ((EntityPlayer) entityliving).getGameProfile();
    }

    public void onNeighborBlockChange(Block id) {
        tileCache.onNeighborChange();
    }

    @Override
    public void invalidate() {
        tileCache.purge();
        super.invalidate();
    }

    @Override
    public void validate() {
        tileCache.purge();
        super.validate();
    }

    public final int getDimension() {
        if (worldObj == null)
            return 0;
        return worldObj.provider.dimensionId;
    }

    @Override
    public final GameProfile getOwner() {
        return owner;
    }

    public boolean isOwner(GameProfile player) {
        return PlayerPlugin.isSamePlayer(owner, player);
    }

    public abstract String getName();

    public static boolean isUseableByPlayerHelper(TileEntity tile, EntityPlayer player) {
        if (tile.getWorldObj().getTileEntity(tile.xCoord, tile.yCoord, tile.zCoord) != tile)
            return false;
        return player.getDistanceSq(tile.xCoord, tile.yCoord, tile.zCoord) <= 64;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        if (owner.getName() != null)
            data.setString("owner", owner.getName());
        if (owner.getId() != null)
            data.setString("ownerId", owner.getId().toString());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        owner = PlayerPlugin.readOwnerFromNBT(data);
    }

    public final int getX() {
        return xCoord;
    }

    public final int getY() {
        return yCoord;
    }

    public final int getZ() {
        return zCoord;
    }

    @Override
    public final World getWorld() {
        return worldObj;
    }

    public abstract short getId();

}
