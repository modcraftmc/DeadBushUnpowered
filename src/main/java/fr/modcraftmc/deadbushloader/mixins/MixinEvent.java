package fr.modcraftmc.deadbushloader.mixins;

import fr.modcraftmc.deadbushloader.events.GenericEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Event.class)

public class MixinEvent implements GenericEvent {


    @Override
    public void onEvent() {

    }
}
