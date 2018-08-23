package panda.corn;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeFireworks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import panda.corn.events.GenericBreedHandler;
import panda.corn.events.GenericFollowHandler;
import panda.corn.events.RemapHandler;
import panda.corn.events.ToolTipHandler;
import panda.corn.other.Compatability;
import panda.corn.other.Config;
import panda.corn.other.MyRecipeFireworks;
import panda.corn.registry.MasterRegistrar;
import panda.corn.registry.ObjectList;

@Mod(modid = Corn.MODID, name = Corn.NAME, version = Corn.VERSION)

public class Corn {	
	
	public static final String MODID = "simplecorn";
	public static final String NAME = "Simple Corn";
	public static final String VERSION = "2.2.0";
	
	public static boolean isIEInstalled = Loader.isModLoaded("immersiveengineering");
	
	public static Logger log;
	
	
	@Instance(MODID)
	public static Corn instance;
		
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		log = LogManager.getLogger(Corn.NAME);
		Config.preInit(event);
		MasterRegistrar.callRegistry(event);
		ReplaceFireworkRecipe();
		    	
	}
	
	@EventHandler
	public void Init(FMLInitializationEvent event) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		MinecraftForge.EVENT_BUS.register(new GenericFollowHandler(EntityPig.class, ObjectList.COB));
		MinecraftForge.EVENT_BUS.register(new GenericBreedHandler(EntityPig.class,ObjectList.COB));
		
		MinecraftForge.EVENT_BUS.register(new GenericFollowHandler(EntityChicken.class,ObjectList.KERNELS));
		MinecraftForge.EVENT_BUS.register(new GenericBreedHandler(EntityChicken.class,ObjectList.KERNELS));
		MinecraftForge.EVENT_BUS.register(new ToolTipHandler());
		MinecraftForge.EVENT_BUS.register(new RemapHandler());
		
		// MinecraftForge.EVENT_BUS.register(this);
		
		if(isIEInstalled){
			Compatability.IE2();
		}
	}
	
	// @SubscribeEvent
	// public static void ReplaceFireworkRecipe(RegistryEvent.Register<IRecipe> event){
		
		// ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>)event.getRegistry();
		
		// IRecipe fireworkRecipe = null;
		// for (IRecipe recipe : recipeRegistry.getValuesCollection()){
			// if (recipe.getClass().equals(RecipeFireworks.class)){
				// fireworkRecipe = recipe;
				// break;
			// }
		// }
		
        // if (fireworkRecipe != null){
			// recipeRegistry.remove(fireworkRecipe.getRegistryName());
			// recipeRegistry.register(new MyRecipeFireworks());
        // } else {
            // log.error("Something in Recipes Borked.");
        // }
		
		// ResourceLocation torch = new ResourceLocation("minecraft:torch");
		// IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
		// modRegistry.remove(torch);
	// }
	
	private static void ReplaceFireworkRecipe(){
		ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>)ForgeRegistries.RECIPES;
		
		IRecipe fireworkRecipe = null;
		for (IRecipe recipe : recipeRegistry.getValuesCollection()){
			if (recipe.getClass().equals(RecipeFireworks.class)){
				fireworkRecipe = recipe;
				break;
			}
		}
		
        if (fireworkRecipe != null){
			recipeRegistry.remove(fireworkRecipe.getRegistryName());
			MyRecipeFireworks newRecipe = new MyRecipeFireworks();
			newRecipe.setRegistryName("corn:fireworks");
			recipeRegistry.register(newRecipe);
        } else {
            log.error("Something in Recipes Borked.");
        }
	}
}
