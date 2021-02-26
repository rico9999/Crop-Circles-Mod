package cropcircles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;



//The value here should match an entry in the META-INF/mods.toml file
@Mod(CropCirclesMod.MODID)

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the following order:
 * onBlocksRegistration then onItemsRegistration then FMLCommonSetupEvent
 *  See MinecraftByExample class for more information
 *
 *  Just used to register for ServerLifeCycleEvents, which we need for our command registration.
 *  We could have done that in the MinecraftByExample constructor instead, I placed it here to make it more obvious for
 *     the example
 */
public class CropCirclesMod
{
	public static final String MODID = "cropcirclesmod";

	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	// get a reference to the event bus for this mod; Registration events are fired
	// on this bus.
	public static IEventBus MOD_EVENT_BUS;
	

	
	@SuppressWarnings("deprecation")
	public CropCirclesMod() {
		MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
		registerCommonEvents();
		DistExecutor.runWhenOn(Dist.CLIENT, () -> CropCirclesMod::registerClientOnlyEvents);
	}

	
	public static void registerCommonEvents() {
		MOD_EVENT_BUS.register(CropCirclesMod.class);
	}

	public static void registerClientOnlyEvents() {
		MOD_EVENT_BUS.register(CropCirclesMod.class);
	}
	
	
  @SubscribeEvent
  public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
  }

  @SubscribeEvent
  public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
  }

  @SubscribeEvent
  public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
    MinecraftForge.EVENT_BUS.register(EtaRegisterCommandEvent.class);
  }
}
