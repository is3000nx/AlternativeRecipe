package slashblade.altrecipe;

import java.util.HashMap;
import java.util.Map;
import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import static slashblade.altrecipe.Util.makeWrapBlade;

/**
 * Balkon's WeaponMod 連携 の代わり.
 */
public class AltBalkonMod extends ShapedRecipes
{
	/**
	 * 本来のModのMod ID
	 */
	static private final String ORIGINAL_MOD_ID = "weaponmod";

	/**
	 * 素材になる剣に付けた通し番号.
	 */
	static private Map<String, Integer> IndexSwords = new HashMap<String, Integer>();

	/**
	 * 各素材の露台の登録名.
	 *
	 * IndexSwords で管理している通し番号順
	 */
	static private String[] BladeNames = new String[]
	{
		"wrap.weaponmod.katana.wood",
		"wrap.weaponmod.katana.stone",
		"wrap.weaponmod.katana.iron",
		"wrap.weaponmod.katana.diamond",
		"wrap.weaponmod.katana.gold",
	};
	

	/**
	 * 各素材の露台のテクスチャ名.
	 *
	 * IndexSwords で管理している通し番号順
	 */
	static private String[] TextureNames = new String[]
	{
        "BalkonWood",
        "BalkonStone",
        "BalkonIron",
        "BalkonDiamond",
        "BalkonGold",
	};

	/**
	 * 各素材の露台の攻撃力.
	 *
	 * IndexSwords で管理している通し番号順
	 */
	static private Float[] BaseAttacks = new Float[]
	{
		2.0f,
		4.0f,
		6.0f,
		8.0f,
		2.0f,
	};

	/**
	 * 各種登録処理
	 *
	 * レシピの登録。
	 * 実績画面で表示するためのダミーのレシピも登録。
	 */
	public static void init()
	{
		// 本来のModが入っていたら何もしない。
		if (Loader.isModLoaded(ORIGINAL_MOD_ID))
			return;

		initVariable();
		addRecipeBalkonWeapon();
	}

	/**
	 * 変数初期化
	 */
	private static void initVariable()
	{
		if (IndexSwords.isEmpty()) {
			IndexSwords.put(getKey(Items.WOODEN_SWORD),  0);
			IndexSwords.put(getKey(Items.STONE_SWORD),   1);
			IndexSwords.put(getKey(Items.IRON_SWORD),    2);
			IndexSwords.put(getKey(Items.DIAMOND_SWORD), 3);
			IndexSwords.put(getKey(Items.GOLDEN_SWORD),  4);
		}
	}
	
	/**
	 * 露台のレシピ追加
	 */
	private static void addRecipeBalkonWeapon()
	{
		ForgeRegistries.RECIPES.register(new AltBalkonMod());
	}

	/**
	 * レシピのコンストラクタ
	 */
	private AltBalkonMod()
	{
        super(AltRecipe.RecipeGroup.toString(),
			  3, 3,
			  NonNullList.<Ingredient>from(
				  Ingredient.EMPTY,
				  
				  Ingredient.EMPTY,
				  Ingredient.EMPTY,
				  Ingredient.fromItem(SlashBlade.proudSoul),

				  Ingredient.EMPTY,
				  Ingredient.fromItem(SlashBlade.wrapBlade),
				  Ingredient.EMPTY ,

				  Ingredient.fromItem(Items.WOODEN_SWORD),
				  Ingredient.EMPTY,
				  Ingredient.EMPTY),

			  new ItemStack(SlashBlade.wrapBlade));

		setRegistryName(new ResourceLocation(SlashBlade.modid, "alt.balkon"));
	}

	/**
	 * レシピ判定
	 *
	 * @return true = クラフト可 / false = 不可
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		// 右上は 魂片
		ItemStack ps = inv.getStackInRowAndColumn(2, 0);
		Ingredient ing =recipeItems.get(0*3 + 2);
		if (!ing.apply(ps))
			return false;
		
		// 中央は 空の鞘
		ItemStack sc = inv.getStackInRowAndColumn(1, 1);
		if (sc.isEmpty() ||
			sc.getItem() != SlashBlade.wrapBlade ||
			SlashBlade.wrapBlade.hasWrapedItem(sc)) {

			return false;
		}

		// 左下は 剣
		ItemStack sword = inv.getStackInRowAndColumn(0, 2);
		if (sword.isEmpty() || !IndexSwords.containsKey(getKey(sword)))
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
	 * クラフト結果.
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
		
		int index = IndexSwords.get(getKey(sword));
		return makeWrapBlade(scabbard, sword,
							 BladeNames[index],
							 TextureNames[index],
							 BaseAttacks[index]);
	}

	private static String getKey(Item item)
	{
		return item.getUnlocalizedName();
	}

	private static String getKey(ItemStack stack)
	{
		return getKey(stack.getItem());
	}
}
