package pheological.autoghead;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AutoGHeadClient implements ClientModInitializer {
	private static final MinecraftClient client = MinecraftClient.getInstance();
	private boolean usedGoldenHead = false; //To reset after every use
	private boolean autoGoldenHeadEnabled = true; // Mod's toggle
	private int cooldownTicks = 0; // Shitcode that stops from the ghead being interacted with multiple times
	private int switchBackDelayTicks = 0; // Timer to delay switching back so it doesn't flag
	private int previousSlot = -1; // Tracks the previous slot so it can switch back after using
	private int healthThreshold = 3; // health before popping

	@Override
	public void onInitializeClient() {
		// Register balls command
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommands(dispatcher));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.world != null && autoGoldenHeadEnabled) {
				handleGoldenHeadSwitch(client.player);
				handleSlotSwitchBack(client.player);
			}
		});
	}

	private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) { //Most autistic code ever written
		dispatcher.register(literal("balls").then(literal("health").then(argument("value", IntegerArgumentType.integer(1, 20)).executes(this::setHealthThreshold))).executes(this::toggleAutoGoldenHead));
	}

	private int getGoldenHeadSlot(ClientPlayerEntity player) {
		for (int i = 0; i < player.getInventory().size(); i++) {
			ItemStack stack = player.getInventory().getStack(i);
			if (stack != null && stack.getName().getString().toLowerCase().contains("golden") && stack.getName().getString().toLowerCase().contains("head")) { // Looks for items which contain the words golden and head.
				return i; //slot w/ ghead
			}
		}
		return -1; //no slot found
	}

	private void handleGoldenHeadSwitch(ClientPlayerEntity player) { //Method is used to fix shitcode that makes it so the Ghead is being interacted (clciked on) multiple times
		if (cooldownTicks > 0) {
			cooldownTicks--; //Reduces every tick
			return;
		}

		if (player.getHealth() < healthThreshold && !usedGoldenHead) {
			int slot = getGoldenHeadSlot(player);
			if (slot != -1) {
				previousSlot = player.getInventory().selectedSlot; // Saves the slot the user is on before the ghead
				player.getInventory().selectedSlot = slot; // Switch to the golden head slot

				assert client.interactionManager != null;

				client.interactionManager.interactItem(player, Hand.MAIN_HAND); // Interacts w/ the item, in this case by right clicking it
				usedGoldenHead = true; //shitcode
				cooldownTicks = 20; // Makes it so the ghead won't be used again for another second; stops double uses


				switchBackDelayTicks = 5; // Needs to be reset after its eaten
			}
		}
	}

	private void handleSlotSwitchBack(ClientPlayerEntity player) {
		if (switchBackDelayTicks > 0) {
			switchBackDelayTicks--; // Count down
			if (switchBackDelayTicks == 0 && previousSlot != -1) {
				player.getInventory().selectedSlot = previousSlot; // Switches back to the previous slot
				previousSlot = -1; // Clears the previous slot variable
				usedGoldenHead = false; //pretty much shitcode
			}
		}
	}

	private int toggleAutoGoldenHead(CommandContext<FabricClientCommandSource> context) { //Method linked to toggle the mod
		autoGoldenHeadEnabled = !autoGoldenHeadEnabled;
		String status = autoGoldenHeadEnabled ? "enabled" : "disabled";


		Text message = autoGoldenHeadEnabled //yeahhh more shitcode; skidded straight from google; literally only for formatting it green & red
				? Text.literal("Auto Golden Head has been enabled.").styled(style -> style.withColor(Formatting.GREEN))
				: Text.literal("Auto Golden Head has been disabled.").styled(style -> style.withColor(Formatting.RED));

		if (client.player != null) {
			client.player.sendMessage(message, false);
		}
		return 1;
	}


	private int setHealthThreshold(CommandContext<FabricClientCommandSource> context) { //Command to set health threshold before eating the ghead
		int newThreshold = IntegerArgumentType.getInteger(context, "value");
		healthThreshold = newThreshold;
		if (client.player != null) {
			client.player.sendMessage(Text.literal("Golden Head health threshold set to " + newThreshold + "."), false);
		}
		return 1;
	}
}