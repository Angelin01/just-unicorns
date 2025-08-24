package com.angelin01.justunicorns.entity.animal;

import java.util.UUID;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public class Unicorn extends AbstractHorse {
	public Unicorn(EntityType<? extends Unicorn> entityType, Level level) {
		super(entityType, level);
	}

	protected void randomizeAttributes(RandomSource random) {
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(generateUnicornMaxHealth(random::nextInt));
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateUnicornSpeed(random::nextDouble));
		this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateUnicornJumpStrength(random::nextDouble));
	}

	@Override
	public void containerChanged(Container container) {
		ItemStack itemStack = this.getBodyArmorItem();
		super.containerChanged(container);
		ItemStack itemStack2 = this.getBodyArmorItem();
		if (this.tickCount > 20 && this.isBodyArmorItem(itemStack2) && itemStack != itemStack2) {
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

	protected SoundEvent getEatingSound() {
		return SoundEvents.HORSE_EAT;
	}

	protected SoundEvent getHurtSound(DamageSource _damageSource) {
		return SoundEvents.HORSE_HURT;
	}

	protected SoundEvent getAngrySound() {
		return SoundEvents.HORSE_ANGRY;
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		boolean bl = !this.isBaby() && this.isTamed() && player.isSecondaryUseActive();
		if (!this.isVehicle() && !bl) {
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
		}

		return super.mobInteract(player, hand);
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