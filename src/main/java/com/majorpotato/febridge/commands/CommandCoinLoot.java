package com.majorpotato.febridge.commands;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.majorpotato.febridge.handler.CoinLootHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.permission.PermissionLevel;

@SideOnly(Side.SERVER)
public class CommandCoinLoot extends FEcmdModuleCommands {

    @Override
    public boolean canConsoleUseCommand() { return true; }

    @Override
    public PermissionLevel getPermissionLevel() { return PermissionLevel.OP; }

    @Override
    public String getCommandName() { return "coinloot"; }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/loot <amount> <x> <y> <z> [dim]";
    }

    @Override
    public String[] getDefaultAliases() {
        return new String[] { "loot", "cl", "coins" };
    }

    @Override
    public String getPermissionNode() { return ModuleEconomy.PERM_COMMAND + "." + getCommandName(); }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length < 4) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        int amount;
        double x, y, z;
        int dim;

        try {
            amount = Integer.parseInt(args[0]);
            x = Double.parseDouble(args[1]);
            y = Double.parseDouble(args[2]);
            z = Double.parseDouble(args[3]);
            if(args.length == 5) dim = Integer.parseInt(args[4]);
            else dim = sender.getEntityWorld().provider.dimensionId;
        } catch(Exception ex) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        CoinLootHandler.spawnCoinLootInWorld(MinecraftServer.getServer().worldServerForDimension(dim), x, y, z, amount);
    }
}
