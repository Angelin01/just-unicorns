package com.angelin01.justunicorns.entity.animal;

import java.util.UUID;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class Unicorn extends AbstractHorse {
	// TODO copied from Horse. Do we need a new UUID?
	private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");

	public Unicorn(EntityType<? extends Unicorn> entityType, Level level) {
		super(entityType, level);
	}

	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (!this.inventory.getItem(1).isEmpty()) {
			compound.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
		}
	}

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("ArmorItem", 10)) {
			ItemStack itemstack = ItemStack.of(compound.getCompound("ArmorItem"));
			if (!itemstack.isEmpty() && this.isArmor(itemstack)) {
				this.inventory.setItem(1, itemstack);
			}
		}

		this.updateContainerEquipment();
	}

	public ItemStack getArmor() {
		return this.getItemBySlot(EquipmentSlot.CHEST);
	}

	private void setArmor(ItemStack stack) {
		this.setItemSlot(EquipmentSlot.CHEST, stack);
		this.setDropChance(EquipmentSlot.CHEST, 0.0F);
	}

	protected void randomizeAttributes(RandomSource random) {
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(generateUnicornMaxHealth(random::nextInt));
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateUnicornSpeed(random::nextDouble));
		this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateUnicornJumpStrength(random::nextDouble));
	}

	protected void updateContainerEquipment() {
		if (!this.level().isClientSide) {
			super.updateContainerEquipment();
			this.setArmorEquipment(this.inventory.getItem(1));
			this.setDropChance(EquipmentSlot.CHEST, 0.0F);
		}
	}

	private void setArmorEquipment(ItemStack stack) {
		this.setArmor(stack);
		if (!this.level().isClientSide) {
			this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
			if (this.isArmor(stack)) {
				int i = ((HorseArmorItem)stack.getItem()).getProtection();
				if (i != 0) {
					this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)i, AttributeModifier.Operation.ADDITION));
				}
			}
		}
	}

	public void containerChanged(Container invBasic) {
		ItemStack itemstack = this.getArmor();
		super.containerChanged(invBasic);
		ItemStack itemstack1 = this.getArmor();
		if (this.tickCount > 20 && this.isArmor(itemstack1) && itemstack != itemstack1) {
			this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
		}

	}

	protected void playGallopSound(SoundType soundType) {
		super.playGallopSound(soundType);
		if (this.random.nextInt(10) == 0) {
			this.playSound(SoundEvents.HORSE_BREATHE, soundType.getVolume() * 0.6F, soundType.getPitch());
		}

	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.HORSE_AMBIENT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.HORSE_DEATH;
	}

	@Nullable
	protected SoundEvent getEatingSound() {
		return SoundEvents.HORSE_EAT;
	}

	protected SoundEvent getHurtSound(DamageSource _damageSource) {
		return SoundEvents.HORSE_HURT;
	}

	protected SoundEvent getAngrySound() {
		return SoundEvents.HORSE_ANGRY;
	}


	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		boolean flag = !this.isBaby() && this.isTamed() && player.isSecondaryUseActive();
		if (!this.isVehicle() && !flag) {
			ItemStack itemstack = player.getItemInHand(hand);
			if (!itemstack.isEmpty()) {
				if (this.isFood(itemstack)) {
					return this.fedFood(player, itemstack);
				}

				if (!this.isTamed()) {
					this.makeMad();
					return InteractionResult.sidedSuccess(this.level().isClientSide);
				}
			}

			return super.mobInteract(player, hand);
		} else {
			return super.mobInteract(player, hand);
		}
	}

	@Override
	protected boolean handleEating(Player player, ItemStack stack) {
		// TODO: adapt unicorn foods
		return super.handleEating(player, stack);
	}

	@Override
	public boolean canMate(Animal otherAnimal) {
		if (otherAnimal == this) {
			return false;
		}

		if (!(otherAnimal instanceof Unicorn)) {
			return false;
		}

		return this.canParent() && ((Unicorn) otherAnimal).canParent();
	}

	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		Unicorn other = (Unicorn) otherParent;
		return null;
	}

	// TODO: These were copied from AbstractHorse's stats, we should probably buff them!
	protected static double generateUnicornMaxHealth(IntUnaryOperator operator) {
		return 15.0F + (float)operator.applyAsInt(8) + (float)operator.applyAsInt(9);
	}

	// TODO: If we jump too high, do we take fall damage? AbstractHorse has calculateFallDamage and causeFallDamage methods
	protected static double generateUnicornJumpStrength(DoubleSupplier supplier) {
		return (double)0.4F + supplier.getAsDouble() * 0.2D + supplier.getAsDouble() * 0.2D + supplier.getAsDouble() * 0.2D;
	}

	protected static double generateUnicornSpeed(DoubleSupplier supplier) {
		return ((double)0.45F + supplier.getAsDouble() * 0.3D + supplier.getAsDouble() * 0.3D + supplier.getAsDouble() * 0.3D) * 0.25D;
	}
}
