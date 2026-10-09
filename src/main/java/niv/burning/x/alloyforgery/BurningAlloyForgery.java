package niv.burning.x.alloyforgery;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wispforest.alloyforgery.block.ForgeControllerBlockEntity;
import io.wispforest.alloyforgery.forges.ForgeRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.Block;
import niv.burning.api.BurningStorage;

public class BurningAlloyForgery implements ModInitializer {

    public static final String MOD_NAME = "Burning ⨯ Alloy Forgery";

    public static final String MOD_ID = "burning-x-alloy-forgery";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {

        BurningStorage.SIDED.registerForBlockEntity(
                (entity, side) -> entity != null && entity.verifyMultiblock()
                        ? new ForgeControllerBurningStorage(entity)
                        : null,
                requireNonNull(ForgeControllerBlockEntity.FORGE_CONTROLLER_BLOCK_ENTITY));

        @SuppressWarnings("null")
        Block @NonNull [] fallbackBlocks = ForgeRegistry
                .getForgeEntries().stream()
                .map(Map.Entry::getValue)
                .flatMap(definition -> Stream.concat(
                        Stream.of(definition.material()),
                        definition.additionalMaterials().stream()))
                .distinct()
                .filter(Objects::nonNull)
                .toArray(Block[]::new);

        BurningStorage.SIDED.registerForBlocks(new ForgeMaterialProvider(), fallbackBlocks);

        LOGGER.info("({}) Ready", MOD_NAME);
    }
}
