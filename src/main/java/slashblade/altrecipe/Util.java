package slashblade.altrecipe;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.stats.AchievementList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraft.util.text.translation.I18n;

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
			return I18n.translateToLocalFormatted(
				"item.flammpfeil.slashblade.wrapformat",
				sword.getDisplayName()).trim();

		} else if (sword.isItemEnchanted()) {
			return scabbard.getDisplayName();
			// ※ 事前に鞘のCurrentItemNameを変更しておく必要があるのかな？

		} else {
			// 名刀
			return I18n.translateToLocalFormatted(
				"item.flammpfeil.slashblade.wrapformat.low",
				sword.getDisplayName()).trim();
		}		
	}
}
