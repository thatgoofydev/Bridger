package be.goofydev.bridger.common.redis;

import be.goofydev.bridger.api.model.user.User;
import be.goofydev.bridger.common.plugin.BridgerPlugin;
import net.kyori.text.serializer.gson.GsonComponentSerializer;

import java.util.UUID;

public class RedisBridgerChannelHandler {

    private final BridgerPlugin plugin;

    public RedisBridgerChannelHandler(BridgerPlugin plugin) {
        this.plugin = plugin;
    }

    public void handle(String message) {
        if (message.startsWith(RedisConstants.MESSAGE_PREFIX_BROADCAST)) {
            String broadcastMessage = message.substring(RedisConstants.MESSAGE_PREFIX_BROADCAST.length());
            this.plugin.getCurrentProxy().broadcastMessage(GsonComponentSerializer.INSTANCE.deserialize(broadcastMessage));
        } else if (message.startsWith(RedisConstants.MESSAGE_PREFIX_COMMAND)) {
            String joinedCommand = message.substring(RedisConstants.MESSAGE_PREFIX_COMMAND.length());
            this.plugin.getCurrentProxy().executeCommand(joinedCommand);
        } else if (message.startsWith(RedisConstants.MESSAGE_PREFIX_MSG)) {
            String[] messageParts = message.substring(RedisConstants.MESSAGE_PREFIX_MSG.length()).split(":");
            User user = this.plugin.getCurrentProxy().getUser(UUID.fromString(messageParts[0]));
            if (user != null) user.sendMessage(GsonComponentSerializer.INSTANCE.deserialize(messageParts[1]));
        }
    }

}
