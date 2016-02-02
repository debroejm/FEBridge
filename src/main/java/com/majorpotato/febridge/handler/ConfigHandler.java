package com.majorpotato.febridge.handler;


import com.majorpotato.febridge.reference.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler {

    public static Configuration config;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }



    private static void loadConfiguration() {

        //maxAccountsPerPerson = config.getInt("maxAccountsPerPerson", Configuration.CATEGORY_GENERAL, 5, ACCOUNTS_PER_PERSON_MIN, ACCOUNTS_PER_PERSON_MAX, "Determines how many bank accounts each player can make. 0 = Infinite.");

        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if(event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
            loadConfiguration();
        }
    }
}
