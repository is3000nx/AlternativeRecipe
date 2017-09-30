package slashblade.altrecipe;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.event.DropEventHandler;
import net.minecraftforge.fml.common.Loader;

/**
 * Twilight Forest Mod 連携 の代わり.
 */
public class AltTwilightForestMod
{
	/**
	 * 本来のModのMod ID
	 */
	static private final String ORIGINAL_MOD_ID = "TwilightForest";

	/**
	 * 各種登録処理.
	 *
	 * アイテムのドロップ設定
	 */
	public static void init()
	{
		// 本来のModが入っていたら何もしない。
		if (Loader.isModLoaded(ORIGINAL_MOD_ID))
			return;

		// 刀をドロップする敵の選定方針
		// ・特定の場所にしか出現しない敵

        DropEventHandler.registerEntityDrop(
			"EvocationIllager",
			0.3f,
			SlashBlade.getCustomBlade("flammpfeil.slashblade.named.orotiagito.rust"));

        DropEventHandler.registerEntityDrop(
			"CaveSpider",
			0.05f,
			SlashBlade.getCustomBlade("flammpfeil.slashblade.named.agito.rust"));

        DropEventHandler.registerEntityDrop(
			"Stray",
			0.05f,
			SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yasha"));

        DropEventHandler.registerEntityDrop(
			"ElderGuardian",
			0.3f,
			SlashBlade.getCustomBlade("flammpfeil.slashblade.named.yashatrue"));
	}
}
