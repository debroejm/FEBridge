package com.majorpotato.febridge.commands;


import com.forgeessentials.api.APIRegistry;
import com.majorpotato.febridge.FEBridge;
import com.majorpotato.febridge.init.ModPermissions;
import com.majorpotato.febridge.tileentity.ICurrencyService;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CommandServiceOld implements ICommand {

    private List aliases;
    public CommandServiceOld() {
        this.aliases = new ArrayList();
        this.aliases.add("service");
        this.aliases.add("currencyservice");
        this.aliases.add("currencyService");
        this.aliases.add("cs");
    }

    @Override
    public String getCommandName() { return "currencyService"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/service <x> <y> <z> <action> <[variable]> ... (use action 'help' for more)"; }

    protected void printFullHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("/service <x> <y> <z> <action> <[variable]> ..."));
        sender.addChatMessage(new ChatComponentText(" Use one of the following actions with this command. Actions can be chained in a single command."));
        sender.addChatMessage(new ChatComponentText("  help: Shows this wall of text."));
        sender.addChatMessage(new ChatComponentText("  gui: Opens the corresponding gui to the specified service."));
        sender.addChatMessage(new ChatComponentText("  buy <value>: Sets the buy price to indicated value. 0 or below disables."));
        sender.addChatMessage(new ChatComponentText("  sell <value>: Sets the sell price to the indicated value. 0 or below disables."));
    }

    protected boolean canUseAdminService(EntityPlayer player) {
        return player.canCommandSenderUseCommand(MinecraftServer.getServer().getOpPermissionLevel(), player.getCommandSenderName())
                || APIRegistry.perms.checkPermission(player, ModPermissions.PERM_SERVICES_ADMIN);
    }

    @Override
    public List getCommandAliases() { return this.aliases; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if(sender.getEntityWorld().isRemote) return;

        if(sender instanceof EntityPlayer) {
            if(args.length < 4) {
                printFullHelp(sender);
                return;
            }
            for(String arg : args) {
                if(arg.equalsIgnoreCase("help")) {
                    printFullHelp(sender);
                    return;
                }
            }

            int x, y, z;
            try {
                x = Integer.parseInt(args[0]);
                y = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            } catch(Exception ex) {
                printFullHelp(sender);
                return;
            }

            World world = sender.getEntityWorld();

            TileEntity tEnt = world.getTileEntity(x, y, z);
            if(!(tEnt instanceof ICurrencyService)) {
                sender.addChatMessage(new ChatComponentText("§eCurrency Service does not exist at Specified Location"));
                return;
            }

            ICurrencyService service = (ICurrencyService)tEnt;
            boolean owner = (service.getOwner() != null && service.getOwner().equals(((EntityPlayer)sender).getPersistentID())) || canUseAdminService((EntityPlayer)sender);

            for(int i = 3;i < args.length; i++) {
                if(args[i].equalsIgnoreCase("gui")) {
                    if(owner) {
                        FMLNetworkHandler.openGui((EntityPlayer)sender, FEBridge.instance, 1, world, x, y, z);
                        //PacketBuilder.instance().askClientToOpenShopGui((EntityPlayer)sender, shop);
                    } else {
                        sender.addChatMessage(new ChatComponentText("§eYou do not own this Currency Service"));
                        return;
                    }
                } else if(args[i].equalsIgnoreCase("buy")) {
                    if(owner) {
                        if(i+1 < args.length) {
                            try {
                                int amount = Integer.parseInt(args[i+1]);
                                service.setBuyPrice(amount);
                                service.markDirty();
                                sender.addChatMessage(new ChatComponentText("§eService Buy Price set to: "+amount+" "+ APIRegistry.economy.currency(amount)));
                            } catch(Exception ex) {
                                sender.addChatMessage(new ChatComponentText("§eUnknown Argument '"+args[i+1]+"' for Action 'buy'"));
                            }
                            i++;
                        } else {
                            sender.addChatMessage(new ChatComponentText("§eMissing Argument for Action 'buy'"));
                            return;
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText("§eYou do not own this Currency Service"));
                    }
                } else if(args[i].equalsIgnoreCase("sell")) {
                    if(owner) {
                        if(i+1 < args.length) {
                            try {
                                int amount = Integer.parseInt(args[i+1]);
                                service.setSellPrice(amount);
                                service.markDirty();
                                sender.addChatMessage(new ChatComponentText("§eService Sell Price set to: "+amount+" "+ APIRegistry.economy.currency(amount)));
                            } catch(Exception ex) {
                                sender.addChatMessage(new ChatComponentText("§eUnknown Argument '"+args[i+1]+"' for Action 'sell'"));
                            }
                            i++;
                        } else {
                            sender.addChatMessage(new ChatComponentText("§eMissing Argument for Action 'sell'"));
                            return;
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText("§eYou do not own this Currency Service"));
                    }
                }
            }

        } else {
            sender.addChatMessage(new ChatComponentText("This Command Must Be Executed Client-Side"));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) { return true; }

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

    @Override
    public boolean isUsernameIndex(String[] args, int i) { return false; }

    @Override
    public int compareTo(Object o) {
        if(o instanceof ICommand) {
            return getCommandName().compareTo(((ICommand)o).getCommandName());
        } else return 0;
    }
}
