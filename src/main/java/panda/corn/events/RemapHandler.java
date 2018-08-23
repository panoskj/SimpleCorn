package panda.corn.events;

import java.util.List;

import panda.corn.registry.ObjectList;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RemapHandler {

    @SubscribeEvent
	public static void processMissingBlockMappings(MissingMappings<Block> event)
    {
        for (MissingMappings.Mapping<Block> entry : event.getAllMappings())
        {
        	if(entry.key.toString().equals("corn:corn"))
        		entry.remap(ObjectList.CORN);
        }
    }
    
    @SubscribeEvent
	public static void processMissingItemMappings(MissingMappings<Item> event)
    {
        for (MissingMappings.Mapping<Item> entry : event.getAllMappings())
        {
            switch(entry.key.toString())
            {
                case "corn:corncob":
                    entry.remap(ObjectList.COB);
                    break;
                case "corn:kernels":
                    entry.remap(ObjectList.KERNELS);
                    break;
                case "corn:poppedcorn":
                    entry.remap(ObjectList.POPCORN);
                    break;
                case "corn:roastedcorn":
                    entry.remap(ObjectList.ROASTED_CORN);
                    break;
                case "corn:popfirework":
                    entry.remap(ObjectList.POP_FIREWORK);
                    break;
                case "corn:chickencornchowder":
                    entry.remap(ObjectList.CHICKEN_CHOWDER);
                    break;
                case "corn:cornchowder":
                    entry.remap(ObjectList.CHOWDER);
                    break;
                    
                default: break;
            }
        }
    }
}
