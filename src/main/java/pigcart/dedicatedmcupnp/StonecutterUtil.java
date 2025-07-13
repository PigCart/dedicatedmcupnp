package pigcart.dedicatedmcupnp;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class StonecutterUtil {
    public static void sendMsg(CommandContext<CommandSourceStack> ctx, String msg) {
        ctx.getSource().sendSuccess(
        //? if >=1.20.1 {
        () ->
        //?}
        Component.literal(msg), false);
    }
}
