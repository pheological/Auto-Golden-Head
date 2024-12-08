package pheological.autoghead;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class AutoGHeadClient implements ClientModInitializer {
	private final GoldenHeadHandler goldenHeadHandler = new GoldenHeadHandler();
	private final CommandHandler commandHandler = new CommandHandler(goldenHeadHandler);

	@Override
	public void onInitializeClient() {
		// Register commands
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				commandHandler.registerCommands(dispatcher)
		);

		// Register tick event
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.world != null && goldenHeadHandler.isAutoGoldenHeadEnabled()) {
				goldenHeadHandler.handleGoldenHeadSwitch(client.player);
				goldenHeadHandler.handleSlotSwitchBack(client.player);
			}
		});
	}
}
