package io.github.mireole.mgtceua;

import io.github.mireole.mgtceua.common.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]")
public class MGTCEuA {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);
    @SidedProxy(modId = Tags.MODID, clientSide = "io.github.mireole.mgtceua.client.ClientProxy", serverSide = "io.github.mireole.mgtceua.common.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("MGTCEuA Pre-Initialization");
        proxy.preInit();
    }

}
