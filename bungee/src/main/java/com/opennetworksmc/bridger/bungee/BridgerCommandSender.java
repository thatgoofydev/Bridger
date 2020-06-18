package com.opennetworksmc.bridger.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.Collection;
import java.util.Collections;

public class BridgerCommandSender implements CommandSender {

    public static final CommandSender instance = new BridgerCommandSender();

    private BridgerCommandSender() {
    }

    @Override
    public String getName() {
        return "Bridger";
    }

    @Override
    @Deprecated
    public void sendMessage(String s) {
    }

    @Override
    @Deprecated
    public void sendMessages(String... strings) {
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
    }

    @Override
    public Collection<String> getGroups() {
        return Collections.emptySet();
    }

    @Override
    public void addGroups(String... strings) {
    }

    @Override
    public void removeGroups(String... strings) {
    }

    @Override
    public boolean hasPermission(String s) {
        return true;
    }

    @Override
    public void setPermission(String s, boolean b) {
    }

    @Override
    public Collection<String> getPermissions() {
        return Collections.emptySet();
    }
}
