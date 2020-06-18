package com.opennetworksmc.bridger.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import net.kyori.text.Component;

public class BridgerCommandSource implements CommandSource {

    public static final BridgerCommandSource instance = new BridgerCommandSource();

    private BridgerCommandSource() {
    }

    @Override
    public void sendMessage(Component component) {
    }

    @Override
    public Tristate getPermissionValue(String s) {
        return Tristate.TRUE;
    }
}
