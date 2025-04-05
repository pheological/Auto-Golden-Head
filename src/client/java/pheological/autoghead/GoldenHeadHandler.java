package pheological.autoghead;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class GoldenHeadHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private boolean autoGoldenHeadEnabled = true; // Mod's toggle
    private boolean usedGoldenHead = false;
    private int cooldownTicks = 60;
    private int switchBackDelayTicks = 5;
    private int previousSlot = -1;
    private int healthThreshold = 3;

    public boolean isAutoGoldenHeadEnabled() {
        return autoGoldenHeadEnabled;
    }

    public void toggleAutoGoldenHead() {
        autoGoldenHeadEnabled = !autoGoldenHeadEnabled;
    }

    public void setHealthThreshold(int healthThreshold) {
        this.healthThreshold = healthThreshold;
    }

    public int getHealthThreshold() {
        return healthThreshold;
    }

    public void handleGoldenHeadSwitch(ClientPlayerEntity player) {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        if (player.getHealth() < healthThreshold && !usedGoldenHead) {
            int slot = getGoldenHeadSlot(player);
            if (slot != -1) {
                previousSlot = player.getInventory().selectedSlot;
                player.getInventory().selectedSlot = slot;

                assert client.interactionManager != null;
                client.interactionManager.interactItem(player, Hand.MAIN_HAND);
                usedGoldenHead = true;
                cooldownTicks = 60;
                switchBackDelayTicks = 5;
            }
        }
    }

    public void handleSlotSwitchBack(ClientPlayerEntity player) {
        if (switchBackDelayTicks > 0) {
            switchBackDelayTicks--;
            if (switchBackDelayTicks == 0 && previousSlot != -1) {
                player.getInventory().selectedSlot = previousSlot;
                previousSlot = -1;
                usedGoldenHead = false;
            }
        }
    }

    private int getGoldenHeadSlot(ClientPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack != null && stack.getName().getString().toLowerCase().contains("golden") &&
                    stack.getName().getString().toLowerCase().contains("head")) {
                return i;
            }
        }
        return -1;
    }
}
