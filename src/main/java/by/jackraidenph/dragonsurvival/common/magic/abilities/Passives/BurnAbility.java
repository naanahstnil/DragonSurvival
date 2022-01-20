package by.jackraidenph.dragonsurvival.common.magic.abilities.Passives;

import by.jackraidenph.dragonsurvival.common.magic.common.PassiveDragonAbility;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class BurnAbility extends PassiveDragonAbility
{
	public BurnAbility(DragonType type, String abilityId, String icon, int minLevel, int maxLevel)
	{
		super(type, abilityId, icon, minLevel, maxLevel);
	}
	
	public int getChance(){
		return 15 * getLevel();
	}
	
	@Override
	public BurnAbility createInstance()
	{
		return new BurnAbility(type, id, icon, minLevel, maxLevel);
	}
	
	@Override
	public boolean isDisabled()
	{
		return super.isDisabled() || !ConfigHandler.SERVER.burn.get();
	}
	
	@Override
	public Component getDescription()
	{
		return new TranslatableComponent("ds.skill.description." + getId(), getChance());
	}
	
	@OnlyIn( Dist.CLIENT )
	public ArrayList<Component> getLevelUpInfo(){
		ArrayList<Component> list = super.getLevelUpInfo();
		list.add(new TranslatableComponent("ds.skill.chance", "+15"));
		return list;
	}
}
