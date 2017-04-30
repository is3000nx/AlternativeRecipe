package slashblade.altrecipe;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.named.event.LoadEvent;
import mods.flammpfeil.slashblade.util.SlashBladeAchievementCreateEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * SlashBlade Mod の他Mod連携の刀を
 * バニラ素材でクラフト出来るようにする Mod.
 */
@Mod(name = AltRecipe.modname,
	 modid = AltRecipe.modid,
	 version = AltRecipe.version,
	 dependencies="required-after:flammpfeil.slashblade")
public class AltRecipe
{
	public static final String modname = "AlternativeRecipe";
	public static final String modid = "slashblade.altrecipe";
	public static final String version = "mc1.11.2-r0";
  
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt)
	{
		SlashBlade.InitEventBus.register(this);
	}

    @SubscribeEvent
    public void onPostInit(LoadEvent.PostInitEvent event)
	{
		AltBambooMod.init();
		AltBalkonMod.init();
		AltTwilightForestMod.init();
	}

    @SubscribeEvent
    public void onCreateAchievement(SlashBladeAchievementCreateEvent event)
	{
		AltBambooMod.registAchievement();
		AltBalkonMod.registAchievement();
		AltTwilightForestMod.registAchievement();
	}
}
