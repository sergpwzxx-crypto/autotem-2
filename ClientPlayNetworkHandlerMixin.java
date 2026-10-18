package com.ejemplo.autototem.mixin;

import com.ejemplo.autototem.AutoTotemClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    // TAIL: asi solo corre en el hilo principal (el metodo se reintenta ahi tras cambiar de hilo).
    @Inject(method = "onEntityStatus", at = @At("TAIL"))
    private void autototem$onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;
        if (packet.getStatus() != 35) return; // 35 = totem de la inmortalidad

        Entity entity = packet.getEntity(client.world);
        if (entity == client.player) {
            AutoTotemClient.onTotemPop(client);
        }
    }
}
