package pheological.autoghead;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class CommandHandler {
    private final GoldenHeadHandler goldenHeadHandler;

    public CommandHandler(GoldenHeadHandler goldenHeadHandler) {
        this.goldenHeadHandler = goldenHeadHandler;
    }

    public void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("AutoGoldenHead").then(literal("health").then(argument("value", IntegerArgumentType.integer(1, 20)).executes(this::setHealthThreshold))).executes(this::toggleAutoGoldenHead) //autistic code for command
        );
    }

    private int toggleAutoGoldenHead(CommandContext<FabricClientCommandSource> context) {
        goldenHeadHandler.toggleAutoGoldenHead();
        boolean enabled = goldenHeadHandler.isAutoGoldenHeadEnabled();

        Text message = enabled
                ? Text.literal("Auto Golden Head has been enabled.").styled(style -> style.withColor(Formatting.GREEN))
                : Text.literal("Auto Golden Head has been disabled.").styled(style -> style.withColor(Formatting.RED));

        context.getSource().getPlayer().sendMessage(message, false);
        return 1;
    }

    private int setHealthThreshold(CommandContext<FabricClientCommandSource> context) {
        int newThreshold = IntegerArgumentType.getInteger(context, "value");
        goldenHeadHandler.setHealthThreshold(newThreshold);

        Text message = Text.literal("Golden Head health threshold set to " + newThreshold + ".")
                .styled(style -> style.withColor(Formatting.YELLOW));

        context.getSource().getPlayer().sendMessage(message, false);
        return 1;
    }
}
