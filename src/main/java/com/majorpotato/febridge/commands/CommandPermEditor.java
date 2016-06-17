package com.majorpotato.febridge.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.handler.GuiHandler;
import com.majorpotato.febridge.init.ModPermissions;
import com.majorpotato.febridge.network.PacketBuilder;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import com.majorpotato.febridge.util.FormatHelper;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permission.PermissionLevel;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.SERVER)
public class CommandPermEditor extends FEcmdModuleCommands {

    @Override
    public boolean canConsoleUseCommand() { return false; }

    @Override
    public PermissionLevel getPermissionLevel() { return PermissionLevel.TRUE; }

    @Override
    public String getCommandName() { return "permgui"; }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/pg";
    }

    @Override
    public String[] getDefaultAliases() {
        return new String[] { "permgui", "pgui", "pg" };
    }

    @Override
    public String getPermissionNode() { return ModuleEconomy.PERM_COMMAND + "." + getCommandName(); }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args) {
        PacketBuilder.instance().commandClientToOpenGui(player, GuiHandler.GUI_PERM_EDITOR);
    }
}
