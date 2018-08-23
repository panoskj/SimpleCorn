package panda.corn.other;

import panda.corn.registry.ObjectList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Recipes {
	public static void register() {
		// GameRegistry.addShapelessRecipe(new ItemStack(ObjectList.CHOWDER),
				// new Object[] {
			// new ItemStack(Items.BOWL, 1),
			// new ItemStack(Items.MILK_BUCKET, 1),
			// new ItemStack(ObjectList.KERNELS, 1),
			// new ItemStack(ObjectList.KERNELS, 1)
		// });

		// GameRegistry.addShapelessRecipe(new ItemStack(ObjectList.CHICKEN_CHOWDER),
				// new Object[] {
			// new ItemStack(Items.BOWL, 1),
			// new ItemStack(Items.MILK_BUCKET, 1),
			// new ItemStack(Items.COOKED_CHICKEN, 1),
			// new ItemStack(ObjectList.KERNELS, 1),
			// new ItemStack(ObjectList.KERNELS, 1)
		// });

		// GameRegistry.addShapelessRecipe(new ItemStack(ObjectList.CHICKEN_CHOWDER),
				// new Object[] {
			// new ItemStack(ObjectList.CHOWDER, 1),
			// new ItemStack(Items.COOKED_CHICKEN, 1)
		// });

		// GameRegistry.addShapelessRecipe(new ItemStack(ObjectList.KERNELS,Config.numkernels),
				// new Object[] {
			// new ItemStack(ObjectList.COB)
		// });

		GameRegistry.addSmelting(ObjectList.COB, new ItemStack(ObjectList.ROASTED_CORN), .2F);
		GameRegistry.addSmelting(ObjectList.KERNELS, new ItemStack(ObjectList.POPCORN,2), .01F);
		MinecraftForge.addGrassSeed(new ItemStack(ObjectList.KERNELS), Config.kernelWeight);
	}
	
	public static class KernelsRecipeFactory implements IRecipeFactory
	{
    	public IRecipe parse(JsonContext context, JsonObject json)
		{
			String group = JsonUtils.getString(json, "group", "");

            NonNullList<Ingredient> ings = NonNullList.create();
            for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
                ings.add(CraftingHelper.getIngredient(ele, context));

            if (ings.isEmpty())
                throw new JsonParseException("No ingredients for shapeless recipe");
            if (ings.size() > 9)
                throw new JsonParseException("Too many ingredients for shapeless recipe");

            ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
			//itemstack.setCount(JsonUtils.getInt(json, "count", Config.numkernels));
            return new ShapelessRecipes(group, itemstack, ings);
		}
	}
}