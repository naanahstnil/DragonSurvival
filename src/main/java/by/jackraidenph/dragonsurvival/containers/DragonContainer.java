package by.jackraidenph.dragonsurvival.containers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DragonContainer extends RecipeBookContainer<CraftingInventory> {
    private final CraftingInventory craftMatrix;
    private final CraftResultInventory craftResult = new CraftResultInventory();
    public final boolean isLocalWorld;
    private final PlayerEntity player;
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
    public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation("item/empty_armor_slot_chestplate");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation("item/empty_armor_slot_leggings");
    public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};

    protected DragonContainer(@Nullable ContainerType<?> type, int id, PlayerInventory playerInventory, boolean localWorld) {
        super(type, id);
        this.isLocalWorld = localWorld;
        this.player = playerInventory.player;
        craftMatrix = new CraftingInventory(this, 3, 3);
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 178, 33));

        int slotIndex = 0;
        for (int i = 0; i < craftMatrix.getWidth(); ++i) {
            for (int j = 0; j < craftMatrix.getHeight(); ++j) {
                this.addSlot(new Slot(this.craftMatrix, slotIndex++, 111 + j * 18, 15 + i * 18));
            }
        }

        for (int k = 0; k < 4; ++k) {
            final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(playerInventory, 39 - k, 8, 8 + k * 18) {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
                 * the case of armor slots)
                 */
                public int getSlotStackLimit() {
                    return 1;
                }

                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
                 */
                public boolean isItemValid(ItemStack stack) {
                    return stack.canEquip(equipmentslottype, player);
                }

                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                public boolean canTakeStack(PlayerEntity playerIn) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }

                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
                    return Pair.of(LOCATION_BLOCKS_TEXTURE, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }
            });
        }

        //main inventory
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
            }
        }
        //hotbar
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
        }
    }

    @Override
    public void fillStackedContents(RecipeItemHelper itemHelperIn) {
        craftMatrix.fillStackedContents(itemHelperIn);
    }

    @Override
    public void clear() {
        craftMatrix.clear();
        craftResult.clear();
    }

    @Override
    public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
        return recipeIn.matches(this.craftMatrix, this.player.world);
    }

    @Override
    public int getOutputSlot() {
        return 0;
    }

    @Override
    public int getWidth() {
        return craftMatrix.getWidth();
    }

    @Override
    public int getHeight() {
        return craftMatrix.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public boolean canMergeSlot(@Nonnull ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        //TODO
        return super.transferStackInSlot(playerIn, index);
    }
}