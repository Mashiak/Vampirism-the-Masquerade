package net.daanlokdrog.vampirismthemasquerade;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredBlock; 
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Stream;
import static de.teamlapen.vampirism.core.ModBlocks.*;

public class CoffinPOIs {
    
    public static final DeferredRegister<PoiType> POI_TYPES = 
        DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, VampirismTheMasqueradeMod.MODID);

    public static final DeferredHolder<PoiType, PoiType> ADVISOR_RESTING_SPOT = POI_TYPES.register("advisor_resting_spot", 
        () -> createPoiType(getCoffinStates(), 1, 1)
    );

    private static Set<BlockState> getCoffinStates() {
        Set<BlockState> states = new HashSet<>();
        List<DeferredBlock<CoffinBlock>> Coffins = List.of(
            COFFIN_WHITE, COFFIN_ORANGE, COFFIN_MAGENTA, COFFIN_LIGHT_BLUE, 
            COFFIN_YELLOW, COFFIN_LIME, COFFIN_PINK, COFFIN_GRAY, 
            COFFIN_LIGHT_GRAY, COFFIN_CYAN, COFFIN_PURPLE, COFFIN_BLUE, 
            COFFIN_BROWN, COFFIN_GREEN, COFFIN_RED, COFFIN_BLACK
        );

        for (DeferredBlock<CoffinBlock> coffinHolder : Coffins) {
            CoffinBlock coffin = coffinHolder.get();
            coffin.getStateDefinition().getPossibleStates().forEach(states::add);
        }
        
        return states;
    }

    private static PoiType createPoiType(Set<BlockState> validStates, int ticketCount, int searchDistance) {
        return new PoiType(validStates, ticketCount, searchDistance);
    }

    public static void register(IEventBus modEventBus) {
    POI_TYPES.register(modEventBus);
}
}