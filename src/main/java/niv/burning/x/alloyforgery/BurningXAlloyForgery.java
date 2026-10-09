package niv.burning.x.alloyforgery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class BurningXAlloyForgery implements ModInitializer {

    public static final String MOD_NAME = "Burning x Alloy Forgery";

    public static final String MOD_ID = "burning-x-alloy-forgery";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("({}) Ready", MOD_NAME);
    }
}
