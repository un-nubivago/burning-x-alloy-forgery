package niv.burning.x.alloyforgery;

import static niv.burning.x.alloyforgery.BurningAlloyForgery.LOGGER;

import java.lang.reflect.Field;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import io.wispforest.alloyforgery.block.ForgeControllerBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Mth;
import niv.burning.api.FuelVariant;

@SuppressWarnings("java:S3011")
@NullMarked
public class ForgeControllerBurningStorage
        implements InsertionOnlyStorage<FuelVariant> {

    private final ForgeControllerBlockEntity entity;

    private static @Nullable Field nullableFuelField = null;

    static {
        try {
            nullableFuelField = ForgeControllerBlockEntity.class.getDeclaredField("fuel");
            if (nullableFuelField != null)
                nullableFuelField.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            LOGGER.warn("Failed to get 'fuel' private field from ForgeControllerBlockEntity", ex);
        }
    }

    public ForgeControllerBurningStorage(ForgeControllerBlockEntity entity) {
        this.entity = entity;
    }

    @Override
    public long insert(@Nullable FuelVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource == null)
            return 0L;

        var fuel = getFuel(this.entity);

        if (fuel >= 0) {
            var amount = (int) Math.min(
                    maxAmount,
                    0L + this.entity.forgeTier().fuelCapacity() - Mth.ceil(fuel));
            this.entity.addFuel(amount);
            return amount;
        } else if (this.entity.canAddFuel((int) maxAmount)) {
            this.entity.addFuel((int) maxAmount);
            return (int) maxAmount;
        } else {
            return 0L;
        }
    }

    @Override
    public long extract(FuelVariant resource, long maxAmount, TransactionContext transaction) {
        return 0L;
    }

    private final float getFuel(ForgeControllerBlockEntity entity) {
        var fuel = nullableFuelField;
        if (fuel == null)
            return -1F;
        try {
            return (float) fuel.get(entity);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            LOGGER.warn("Failed to read private 'fuel' field's value", ex);
            return -1F;
        }
    }
}
