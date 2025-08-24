package com.angelin01.justunicorns.renderer.entity;

import com.angelin01.justunicorns.JustUnicorns;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public class UnicornRenderer extends AbstractHorseRenderer<AbstractHorse, HorseModel<AbstractHorse>> {
	private static final ResourceLocation UNICORN_TEXTURE =
			ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_skeleton.png");

	public UnicornRenderer(EntityRendererProvider.Context context) {
		super(context, new HorseModel<>(context.bakeLayer(ModelLayers.SKELETON_HORSE)), 1.0F);
	}

	public ResourceLocation getTextureLocation(AbstractHorse entity) {
		return UNICORN_TEXTURE;
	}
}