package com.majorpotato.febridge.commands;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.economy.ModuleEconomy;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.init.ModPermissions;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import com.majorpotato.febridge.util.FormatHelper;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.permission.PermissionLevel;

import java.util.ArrayList;
import java.util.List;

public class CommandService extends FEcmdModuleCommands {

    @Override
    public boolean canConsoleUseCommand() { return false; }

    @Override
    public PermissionLevel getPermissionLevel() { return PermissionLevel.TRUE; }

    @Override
    public String getCommandName() { return "economyservice"; }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/service <x> <y> <z> <action> <[variable]> ... (use '/service help' for more)";
    }

    @Override
    public String[] getDefaultAliases() {
        return new String[] { "service", "economyService", "es" };
    }

    @Override
    public String getPermissionNode() { return ModuleEconomy.PERM_COMMAND + "." + getCommandName(); }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args) {

        // Has to have a location (3 coords)
        if(args.length < 4) {
            printFullHelp(player); return;
        }

        // If this is a help action, just print help and ignore all other factors
        for(String arg : args) {
            if(arg.equalsIgnoreCase("help")) {
                printFullHelp(player); return;
            }
        }

        // Get the position we want to check
        int[] coords = new int[3];
        for(int i = 0; i < 3; i++) {
            boolean relative = false;
            String arg = args[i];
            if(arg == null || arg.length() < 1) {
                printFullHelp(player); return;
            }

            if(arg.charAt(0) == '~') {
                relative = true;
                arg = arg.substring(1, arg.length());
            }

            int value;
            try { value = Integer.parseInt(arg); }
            catch(Exception ex) {
                if(relative) value = 0;
                else { printFullHelp(player); return; }
            }

            if(relative) coords[i] = ((int)player.posX) + value;
            else coords[i] = value;
        }

        World world = player.getEntityWorld();

        // Is there a Service at the position we are using?
        TileEntity tent = world.getTileEntity(coords[0], coords[1], coords[2]);
        if(!(tent instanceof ICurrencyService)) {
            ChatOutputHandler.chatError(player, "Economy Service does not exist at "+ FormatHelper.nicePos3i(coords[0], coords[1], coords[2]));
            return;
        }
        ICurrencyService service = (ICurrencyService)tent;

        // Is the player allowed to access said service?
        boolean hasAccessPermission = (service.getOwner() != null && service.getOwner().equals(player.getPersistentID()))
                || player.canCommandSenderUseCommand(MinecraftServer.getServer().getOpPermissionLevel(), player.getCommandSenderName())
                || APIRegistry.perms.checkPermission(player, ModPermissions.PERM_SERVICES_ADMIN);
        if(!hasAccessPermission) {
            ChatOutputHandler.chatError(player, "You do not have permission to access this Economy Service");
            return;
        }

        // Default action = 'gui'
        if(args.length == 4) {
            FMLNetworkHandler.openGui(player, FEBridge.instance, 1, world, coords[0], coords[1], coords[2]);
        }

        // Parse for actions
        for(int i = 3; i < args.length; i++) {
            if(args[i].equalsIgnoreCase("gui")) {
                FMLNetworkHandler.openGui(player, FEBridge.instance, 1, world, coords[0], coords[1], coords[2]);
            } else if(args[i].equalsIgnoreCase("buy")) {
                if(i+1 < args.length) {
                    try {
                        int amount = Integer.parseInt(args[i+1]);
                        service.setBuyPrice(amount);
                        service.markDirty();
                        ChatOutputHandler.chatNotification(player, "Service Buy Price set to: "+amount+" "+ APIRegistry.economy.currency(amount));
                    } catch(Exception ex) {
                        ChatOutputHandler.chatError(player, "Unknown Argument '"+args[i+1]+"' for Action 'buy'");
                    }
                    i++;
                } else {
                    ChatOutputHandler.chatError(player, "Missing Argument for Action 'buy'");
                    return;
                }
            } else if(args[i].equalsIgnoreCase("sell")) {
                if(i+1 < args.length) {
                    try {
                        int amount = Integer.parseInt(args[i+1]);
                        service.setSellPrice(amount);
                        service.markDirty();
                        ChatOutputHandler.chatNotification(player, "Service Sell Price set to: "+amount+" "+ APIRegistry.economy.currency(amount));
                    } catch(Exception ex) {
                        ChatOutputHandler.chatError(player, "Unknown Argument '"+args[i+1]+"' for Action 'sell'");
                    }
                    i++;
                } else {
                    ChatOutputHandler.chatError(player, "Missing Argument for Action 'sell'");
                    return;
                }
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        String last = "";
        if(args.length > 0) last = args[args.length-1];
        if(last.equalsIgnoreCase("buy") || last.equalsIgnoreCase("sell")) return null;
        else {
            List options = new ArrayList();
            options.add("help");
            options.add("gui");
            options.add("buy");
            options.add("sell");
            return options;
        }
    }

    protected void printFullHelp(EntityPlayerMP player) {
        player.addChatMessage(new ChatComponentText("/service <x> <y> <z> <action> <[variable]> ..."));
        player.addChatMessage(new ChatComponentText(" Use one of the following actions with this command. Actions can be chained in a single command."));
        player.addChatMessage(new ChatComponentText("  help: Shows this wall of text."));
        player.addChatMessage(new ChatComponentText("  gui: Opens the corresponding gui to the specified service."));
        player.addChatMessage(new ChatComponentText("  buy <value>: Sets the buy price to indicated value. 0 or below disables."));
        player.addChatMessage(new ChatComponentText("  sell <value>: Sets the sell price to the indicated value. 0 or below disables."));
    }
}
