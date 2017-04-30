package slashblade.altrecipe;

import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.event.DropEventHandler;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.common.Loader;
import static slashblade.altrecipe.Util.replaceResource;

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

	/**
	 * 実績の登録処理.
	 *
	 * 実績そのものは登録されているが、
	 * 一部の刀でリソース名が合っていないのがあるので
	 * それらを変更する。
	 */
	public static void registAchievement()
	{
		// 本来のModが入っていたら何もしない。
		// ※ リソース間違いが直るかどうかは不明だけど
		//    一応何もしないことにする。
		if (Loader.isModLoaded(ORIGINAL_MOD_ID))
			return;

		replaceAchievementResource("agito.rust", "agitoRust");
		replaceAchievementResource("orotiagito.rust", "orotiagitoRust");
		replaceAchievementResource("orotiagito.sealed", "orotiagitoSealed");
		replaceAchievementResource("yashatrue", "yashaTrue");

		// ※
		// 実績画面で表示されるレシピで、素材の刀の名前が正しく表示されない。
		// が、
		// これを治すには、リソース名そのものを変えないと
		// どうにもならないんじゃないかな？
	}

	/**
	 * 実績のリソース名を置き換える.
	 *
	 * @param key 実績のキー
	 * @param keyResource 置き換えるリソースのキー
	 */
	private static void replaceAchievementResource(String key, String keyResource)
	{
        Achievement ach = AchievementList.getAchievement(key);
		if (ach == null) {
			// ※
			// 実績は既に登録されているはずだから
			// ここに来ることは無いはずだが
			return;
		}

		replaceResource(ach, keyResource);
	}
}
