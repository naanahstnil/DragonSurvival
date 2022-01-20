package by.jackraidenph.dragonsurvival.api;

import by.jackraidenph.dragonsurvival.common.capability.provider.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.handlers.DragonFoodHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;

public class DragonFood {

	public static boolean isEdible(Item item, Entity entity) {
		if (entity != null && DragonStateProvider.isDragon(entity))
			return DragonFoodHandler.isDragonEdible(item, DragonStateProvider.getCap(entity).orElseGet(null).getType());
		return item.isEdible();
	}
	
	@Nullable
	public static FoodProperties getEffectiveFoodProperties(Item item, Entity entity) {
		if (entity != null && DragonStateProvider.isDragon(entity))
			return DragonFoodHandler.getDragonFoodProperties(item, DragonStateProvider.getCap(entity).orElseGet(null).getType());
		return item.getFoodProperties();
	}
	
	public static DragonType getDragonType(Entity entity) {
		if (entity != null && DragonStateProvider.isDragon(entity))
			return DragonStateProvider.getCap(entity).orElseGet(null).getType();
		return DragonType.NONE;
	}
	
	public static boolean isDrawingDragonFood() {
		return DragonFoodHandler.isDrawingOverlay;
	}
}
