package com.angelin01.justunicorns;

import com.angelin01.justunicorns.entity.animal.Unicorn;
import com.angelin01.justunicorns.renderer.entity.UnicornRenderer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(JustUnicorns.MOD_ID)
public class JustUnicornsNeoForge {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            BuiltInRegistries.ENTITY_TYPE,
            JustUnicorns.MOD_ID
    );

    public static final DeferredHolder<EntityType<?>, EntityType<Unicorn>> UNICORN = ENTITY_TYPES.register(
            "unicorn",
            () -> EntityType.Builder.of(Unicorn::new, MobCategory.CREATURE)
                    .sized(1.3964844F, 1.6F)
                    .eyeHeight(1.52F)
                    .passengerAttachments(1.44375F)
                    .clientTrackingRange(10)
                    .build("unicorn")
    );

    public JustUnicornsNeoForge(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(this::registerAttributes);
        modBus.addListener(this::registerRenderers);
        JustUnicorns.init();
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(UNICORN.get(), UnicornRenderer::new);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(UNICORN.get(), AbstractHorse.createBaseHorseAttributes().build());
    }
}
