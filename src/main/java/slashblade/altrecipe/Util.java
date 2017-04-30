package slashblade.altrecipe;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * 共通機能
 */
final class Util
{
	private Util() {}


	/**
	 * 名前を指定して、SlashBlade Mod で追加したアイテムを取得する。
	 *
	 * @param name アイテム名
	 * @return アイテム
	 */
	static ItemStack getItemStack(String name)
	{
		return SlashBlade.findItemStack(SlashBlade.modid, name, 1);
	}

	/**
	 * 刀を作成する.
	 *
	 * 鞘に素材の剣を収めて、
	 * 諸々の設定を変更して刀にする。
	 *
	 * @param scabbard 素材の鞘
	 * @param sword 素材の剣
	 * @param name 作成する刀のアイテム名
	 * @param texture 作成する刀のテクスチャ名
	 * @param baseAttack 作成する刀の攻撃力
	 * @return 作成した刀
	 */
	static ItemStack makeWrapBlade(ItemStack scabbard,
								   ItemStack sword,
								   String name,
								   String texture,
								   Float baseAttack)
	{
		scabbard = scabbard.copy();
		sword = sword.copy();

		// 鞘に刀を収める
		SlashBlade.wrapBlade.removeWrapItem(scabbard);
		SlashBlade.wrapBlade.setWrapItem(scabbard, sword);

		// 鞘の設定を変える
		NBTTagCompound tag = scabbard.getTagCompound();
		ItemSlashBladeNamed.CurrentItemName.set(tag, name);
		ItemSlashBladeNamed.TextureName.set(tag, texture);
		ItemSlashBladeNamed.BaseAttackModifier.set(tag, baseAttack);
		
		scabbard.setStackDisplayName(getBladeName(scabbard, sword));

		if (sword.isItemEnchanted())
			tag.setTag("ench", sword.getTagCompound().getTag("ench"));

		return scabbard;
	}

	/**
	 * 刀の表示名を取得する.
	 *
	 * 素材の剣に名前が付いている時は、宝刀。
	 * 素材の剣にエンチャントがあれば、本来の刀の名前。
	 * それ以外は名刀。
	 *
	 * @param scabbard 素材の鞘
	 * @param sword 素材の剣
	 * @return 刀の表示名
	 */
	private static String getBladeName(ItemStack scabbard,
									   ItemStack sword)
	{
		if (sword.hasDisplayName()) {
			// 宝刀
			return String.format(
				I18n.translateToLocal("item.flammpfeil.slashblade.wrapformat").trim(),
				sword.getDisplayName());

		} else if (sword.isItemEnchanted()) {
			return scabbard.getDisplayName();
			// ※ 事前に鞘のCurrentItemNameを変更しておく必要があるのかな？

		} else {
			// 名刀
			return String.format(
				I18n.translateToLocal("item.flammpfeil.slashblade.wrapformat.low").trim(),
				sword.getDisplayName());
		}		
	}

	/**
	 * 実績の登録
	 *
	 * @param key 実績名
	 * @param keyResource 実績名等のリソースキー
	 * @param nameBlade 刀の登録名
	 * @param parent 取得の前提となる実績
	 * @param special スペシャルフラグの有無
	 */
	public static Achievement registCraftAchievement(
		String key,
		String keyResource,
		String nameBlade,
		boolean special,
		Achievement parent)
	{
		// ※
		// ココと「魂晶」の実績登録のどっちが先に実行されるかは不定？
		//
		// 登録済みかどうか確認して、
		// なければ「魂晶」と同様の実績登録をしてから
		// 不適切な情報を置き換える。
		
        Achievement ach = AchievementList.getAchievement(key);
		if (ach == null) {

			ach = AchievementList.registerCraftingAchievement(
				key,
				SlashBlade.getCustomBlade(nameBlade),
				parent);
			AchievementList.setContent(ach, key);

		} else {
			// 「魂晶」の実績として登録済み。
			// 魂晶の実績は前提が「武器の作成」になっているので置き換え
			replaceAchievementParent(ach, parent);
		}

		if (special)
			ach.setSpecial();
		if (keyResource != null)
			replaceResource(ach, keyResource);
		return ach;
	}
	

	
	/**
	 * 「鞘」の作成実績を取得する.
	 * @return 実績
	 */
	static Achievement getScabbardAchievement()
	{
		return AchievementList.getAchievement("saya");
	}

	/**
	 * 実績のリソース名を置き換える.
	 * 
	 * 置き換えるのは実績名と実績の説明のリソースキー
	 *
	 * @param ach 実績
	 * @param keyResource リソースのキー
	 */
	static void replaceResource(Achievement ach, String keyResource)
	{
		// ※
		// 連携Modが未対応なので わざとリソース名を変えているのか
		// 過去のまま修正せずに残ったままなのか
		// わからないが、
		// 不適切なので、無理矢理合わせる。
		// 
		// 新たに適切なリソースを追加してしまうのが楽だし安全だし
		// 日本語環境以外やっぱりリソースが存在しないままだが
		// リソースの内容をまるっとコピーするのもアレなので
		// 変数の中身を変更する方法を行う。
		
		String keyName = "achievement.flammpfeil.slashblade." + keyResource;
		String keyDesc = keyName + ".desc";

		// 実績名
		ReflectionHelper.setPrivateValue(StatBase.class,
										 ach,
										 new TextComponentTranslation(keyName, new Object[0]),
										 "statName", "field_75978_a");

		// 実績の説明
		ReflectionHelper.setPrivateValue(Achievement.class,
										 ach,
										 keyDesc,
										 "achievementDescription", "field_75996_k");

		// AchievementEx.unlocalizedKey は変えない。
		// このキーは
		// 実績アイコンの表示位置のリソースに取得するのに使用しているが、
		// コッチのリソース名は合っている。
	}

	/**
	 * 実績の前提実績を置き換える。
	 *
	 * @param ach 前提を置き換えたい実績
	 * @param parent 前提
	 */
	static void replaceAchievementParent(Achievement ach, Achievement parent)
	{
		ReflectionHelper.setPrivateValue(Achievement.class,
										 ach,
										 parent,
										 "parentAchievement", "field_75992_c");
	}
}
