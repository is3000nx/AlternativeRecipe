package slashblade.altrecipe;

import mods.flammpfeil.slashblade.ItemSlashBladeNamed;
import mods.flammpfeil.slashblade.SlashBlade;
import mods.flammpfeil.slashblade.item.ItemSlashBlade;
import mods.flammpfeil.slashblade.named.RecipeAwakeBladeFox;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import static slashblade.altrecipe.Util.getItemStack;
import static slashblade.altrecipe.Util.makeWrapBlade;
import static slashblade.altrecipe.Util.getScabbardAchievement;
import static slashblade.altrecipe.Util.registCraftAchievement;

/**
 * Bamboo Mod 連携の代わり.
 */
public class AltBambooMod extends ShapedOreRecipe
{
	/**
	 * 本来のModのMod ID
	 */
	static private final String ORIGINAL_MOD_ID = "BambooMod";

	/**
	 * 『利刀「無名」紅玉』の登録名.
	 *
	 * レシピや実績等の登録名
	 */
	private static final String KEY_RUBY = "wrap.BambooMod.katana";

	/**
	 * 『利刀「無名」紅玉』の登録名.
	 *
	 * 相手としての刀の登録名.
	 */
	private static final String NAME_RUBY = "wrap.BambooMod.katana.sample";
	
	/**
	 * 『狐月刀「白狐」』の登録名
	 */
	private static final String NAME_FOX_WHITE = "flammpfeil.slashblade.named.fox.white";

	/**
	 * 『狐月刀「黒狐」』の登録名
	 */
	private static final String NAME_FOX_BLACK = "flammpfeil.slashblade.named.fox.black";

	/** 『鞘』の登録名 */
	private static final String NAME_SCABBARD = "slashbladeWrapper";

	/** 紅玉のテクスチャ名 */
	private static final String RUBY_TEXTURE = "BambooKatana";

	/** 紅玉の攻撃力 */
	private static final Float RUBY_BASE_ATTACK = 4.0f;

	
	/**
	 * 各種登録処理
	 *
	 * レシピの登録
	 */
	public static void init()
	{
		// 本来のModが入っていたら何もしない。
		if (Loader.isModLoaded(ORIGINAL_MOD_ID))
			return;
		
		addRecipeBambooKatana();
		addRecipeFox(NAME_FOX_WHITE, Enchantments.LOOTING);
		addRecipeFox(NAME_FOX_BLACK, Enchantments.SMITE);
	}

	/* ============================================================ */

	
	/**
	 * 利刀「無名」紅玉 のレシピ追加
	 */
	private static void addRecipeBambooKatana()
	{
		SlashBlade.addRecipe(KEY_RUBY, new AltBambooMod());

		RecipeSorter.register("flammpfeil.slashblade:alt:bamboomod",
							  AltBambooMod.class,
							  RecipeSorter.Category.SHAPED,
							  "after:forge:shaped");
	}

	/**
	 * 紅玉レシピクラスのコンストラクタ
	 */
	private AltBambooMod()
	{
		super(getItemStack(NAME_RUBY),
			  "  P",
			  " S ",
			  "B  ",
			  'P', getItemStack(SlashBlade.IngotBladeSoulStr),
			  'S', SlashBlade.wrapBlade,
			  'B', Items.IRON_SWORD
			);

		// オリジナルのレシピでは、
		// 右上は「魂片」だが、
		// 『利刀「鉄」露台』とレシピが被るので
		// コッチのレシピを「魂塊」に変更。
	}

	/**
	 * 紅玉のレシピ判定.
	 *
	 * @return true = クラフト可 / false = 不可
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		// 右上は 魂片
		ItemStack ps = inv.getStackInRowAndColumn(2, 0);
		if (ps.isEmpty() || !ps.isItemEqual((ItemStack)input[0*3 + 2]))
			return false;

		// 中央は 空の鞘
		ItemStack sc = inv.getStackInRowAndColumn(1, 1);
		if (sc.isEmpty() ||
			sc.getItem() != SlashBlade.wrapBlade ||
			SlashBlade.wrapBlade.hasWrapedItem(sc)) {

			return false;
		}

		// 左下は 鉄の剣
		ItemStack sword = inv.getStackInRowAndColumn(0, 2);
		if (sword.isEmpty() || !sword.getItem().equals(Items.IRON_SWORD))
			return false;

		// 上記以外は無し。
		// ※ 作業台の大きさは 3x3 を前提としている。
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (x + y != 2 && !inv.getStackInRowAndColumn(x, y).isEmpty())
					return false;
			}
		}

		return true;
	}

	/**
	 * 紅玉のクラフト結果.
	 *
	 * 鞘に刀を収めて紅玉に変化させる。
	 *
	 * @return クラフト結果
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		// 素材の鞘
		ItemStack scabbard = inv.getStackInRowAndColumn(1, 1);
		if (scabbard.isEmpty())
			return ItemStack.EMPTY;

		// 素材の剣
		ItemStack sword = inv.getStackInRowAndColumn(0, 2);
		if (sword.isEmpty())
			return ItemStack.EMPTY;

		return makeWrapBlade(scabbard, sword,
							 KEY_RUBY,
							 RUBY_TEXTURE,
							 RUBY_BASE_ATTACK);
	}

	/* ============================================================ */

	/**
	 * 狐月刀のレシピ追加
	 *
	 * @param bladeName 作成する刀の登録名
	 * @param reqEnch 要求エンチャント
	 */
	private static void addRecipeFox(String bladeName, Enchantment reqEnch)
	{
		ItemStack blade = SlashBlade.getCustomBlade(bladeName);

		ItemStack reqiredBlade = getItemStack(NAME_SCABBARD);
		SlashBlade.wrapBlade.setWrapItem(
			reqiredBlade,
			SlashBlade.findItemStack("minecraft", "wooden_sword", 1));
		// ※
		// 素材となる紅玉のItemStackは
		// 何でもいいから鞘に突っ込んで （ここでは木の剣）、
		// タグにそれっぽい値を設定する
		// ってことで良いのかな？
		// 
		// SlashBlade.getCustomeBlade()で取ってくるものは
		// LOOTING が付いているから不適切？

		reqiredBlade.addEnchantment(reqEnch, 1);
		NBTTagCompound tag = reqiredBlade.getTagCompound();
		ItemSlashBladeNamed.CurrentItemName.set(tag, KEY_RUBY);
		ItemSlashBladeNamed.TextureName.set(tag, RUBY_TEXTURE);
		ItemSlashBladeNamed.BaseAttackModifier.set(tag, RUBY_BASE_ATTACK);
		ItemSlashBlade.KillCount.set(tag, 199);
		ItemSlashBlade.ProudSoul.set(tag, 1000);
		ItemSlashBlade.RepairCount.set(tag, 1);

		reqiredBlade.setStackDisplayName(reqiredBlade.getDisplayName());;

		String reqiredStr = bladeName + ".reqired";
		SlashBlade.registerCustomItemStack(reqiredStr, reqiredBlade);
		ItemSlashBladeNamed.NamedBlades.add(SlashBlade.modid + ":" + reqiredStr);

		reqiredBlade = reqiredBlade.copy();
		reqiredBlade.setItemDamage(OreDictionary.WILDCARD_VALUE);

		SlashBlade.addRecipe(
			bladeName,
			new RecipeAwakeBladeFox(
				blade,
				reqiredBlade,
				"FPF",
				"EXE",
				"FIF",
				'X', reqiredBlade,
				'F', Blocks.LAPIS_BLOCK,
				'E', Blocks.LIT_PUMPKIN,
				'I', Items.WHEAT,
				'P', getItemStack(SlashBlade.ProudSoulStr))
			);

		// ※
		// 狐月刀の素材となる刀は、
		// ちゃんと『利刀「無名」紅玉』になっていないと
		// ダメだと聞いた気がするが、
		// 『名刀「鉄の剣」』でも
		// （後からエンチャントすれば）クラフト出来てしまう。
		
		// ※
		// RecipeAwakeBladeFoxのgetCraftingResult(),getRecipeOutput()は
		// Bamboo Modの刀を鞘に納め直しているけど、
		// 同じように素材の剣を入れた方が良いかな。
	}

	/* ============================================================ */

	/**
	 * 実績の登録処理.
	 */
	public static void registAchievement()
	{
		// 本来のModが入っていたら何もしない。
		if (Loader.isModLoaded(ORIGINAL_MOD_ID))
			return;

		Achievement ruby = registCraftAchievement(
			KEY_RUBY,
			"bamboo",
			NAME_RUBY,
			false,
			getScabbardAchievement());


		registCraftAchievement(NAME_FOX_WHITE,
							   "foxwhite",
							   NAME_FOX_WHITE,
							   true,
							   ruby);

		registCraftAchievement(NAME_FOX_BLACK,
							   "foxblack",
							   NAME_FOX_BLACK,
							   true,
							   ruby);
	}
}
