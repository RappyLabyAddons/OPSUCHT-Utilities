package com.rappytv.opsucht.v1_21_5.mixins;

import com.rappytv.opsucht.api.inventory.ContainerOpenEvent;
import net.labymod.api.Laby;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen extends Screen {

    protected MixinAbstractContainerScreen(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void onInit(CallbackInfo ci) {
        AbstractContainerScreen<?> abstractScreen = (AbstractContainerScreen<?>) (Object) this;
        if(!(abstractScreen instanceof ContainerScreen)) {
            return;
        }
        Laby.fireEvent(new ContainerOpenEvent(
            Laby.references()
                .componentMapper()
                .fromMinecraftComponent(this.getTitle())
        ));
    }

}
