package com.ejemplo.autototem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Cuando el servidor avisa que tu totem se popeo (paquete de estado 35),
 * pone al instante otro totem de la hotbar en la mano secundaria.
 * Solo mira la hotbar (slots 0-8).
 */
public class AutoTotemClient implements ClientModInitializer {

    private static KeyBinding toggleKey;
    private static boolean enabled = true;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autototem.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.autototem"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("AutoTotem: " + (enabled ? "ON" : "OFF")), true);
                }
            }
        });
    }

    /** Lo llama el mixin en el mismo instante en que llega el paquete del pop. */
    public static void onTotemPop(MinecraftClient client) {
        if (!enabled) return;
        ClientPlayerEntity player = client.player;
        if (player == null || client.interactionManager == null) return;

        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                // Hotbar = slots 36-44 del handler del jugador.
                // SWAP con boton 40 = intercambiar con la mano secundaria.
                client.interactionManager.clickSlot(
                        player.playerScreenHandler.syncId,
                        36 + i,
                        40,
                        SlotActionType.SWAP,
                        player
                );
                return;
            }
        }
    }
}
