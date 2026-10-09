package niv.burning.x.alloyforgery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import io.wispforest.alloyforgery.block.ForgeControllerBlock;
import io.wispforest.alloyforgery.block.ForgeControllerBlockEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup.BlockApiProvider;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import niv.burning.api.FuelVariant;

@SuppressWarnings("null")
@NullMarked
public class ForgeMaterialProvider implements BlockApiProvider<Storage<FuelVariant>, @Nullable Direction> {

    private static final Map<@NonNull Vec3i, @NonNull Predicate<@NonNull Direction>> SEARCH_OFFSET;

    static {
        var map = new HashMap<@NonNull Vec3i, @NonNull Predicate<@NonNull Direction>>();

        var zero = new Vec3i(0, 0, 0);

        var below = zero.below();
        map.put(below, _ -> true);

        var above = zero.above();
        map.put(above, _ -> true);

        for (var direction : Direction.Plane.HORIZONTAL) {
            if (direction == null)
                continue;

            var clockwise = direction.getClockWise();

            map.put(below.relative(direction, 2),
                    side -> side == direction);
            map.put(below.relative(direction).relative(clockwise),
                    side -> side == direction || side == clockwise);

            map.put(zero.relative(direction, 2),
                    side -> side == direction);
            map.put(zero.relative(direction).relative(clockwise),
                    side -> side == direction || side == clockwise);

            map.put(above.relative(direction),
                    side -> side == direction || side == clockwise || side == clockwise.getOpposite());
            map.put(above.relative(direction).relative(clockwise),
                    side -> side == direction || side == clockwise);
            map.put(above.relative(direction).relative(clockwise, 2),
                    side -> side == clockwise);
            map.put(above.relative(direction, 2),
                    side -> side == direction);
            map.put(above.relative(direction, 2).relative(clockwise),
                    side -> side == direction);
        }

        SEARCH_OFFSET = Collections.unmodifiableMap(map);
    }

    @Override
    public @Nullable InsertionOnlyStorage<FuelVariant> find(
            Level level, BlockPos pos, BlockState state,
            @Nullable BlockEntity blockEntity, @Nullable Direction context) {

        var storages = new ArrayList<ForgeControllerBurningStorage>();

        for (var entry : SEARCH_OFFSET.entrySet()) {

            var offset = pos.offset(entry.getKey());

            var entity = level.getBlockEntity(offset,
                    ForgeControllerBlockEntity.FORGE_CONTROLLER_BLOCK_ENTITY).orElse(null);

            var side = level.getBlockState(offset).getOptionalValue(ForgeControllerBlock.FACING).orElse(null);

            if (entity != null && side != null && entry.getValue().test(side) && entity.verifyMultiblock()) {
                storages.add(new ForgeControllerBurningStorage(entity));
            }
        }

        if (storages.isEmpty()) {
            return null;
        } else if (storages.size() == 1) {
            return storages.get(0);
        } else {
            return storages.get((int) ((level.getGameTime() * pos.hashCode()) % storages.size()));
        }
    }
}
