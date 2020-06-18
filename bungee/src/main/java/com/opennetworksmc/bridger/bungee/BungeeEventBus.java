package com.opennetworksmc.bridger.bungee;

import com.opennetworksmc.bridger.common.event.AbstractEventBus;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BungeeEventBus extends AbstractEventBus<Plugin> implements Listener {

    public BungeeEventBus(BridgerPlugin plugin) {
        super(plugin);
    }

    @Override
    public Plugin checkPlugin(Object plugin) throws IllegalArgumentException {
        if (plugin instanceof Plugin) {
            Plugin bungeePlugin = (Plugin) plugin;

            // add a custom log handler to effectively listen for the plugin being disabled.
            // BungeeCord doesn't really support enabling/disabling plugins at runtime, and as
            // such doesn't have a PluginDisableEvent. However, some plugins do exist to reload
            // plugins at runtime. We rely on these plugins following the BungeeCord behaviour,
            // and #close ing the plugins logger, so we can unregister the listeners. :)
            Handler[] handlers = bungeePlugin.getLogger().getHandlers();
            for (Handler handler : handlers) {
                if (handler instanceof UnloadHookLoggerHandler) {
                    return bungeePlugin;
                }
            }

            bungeePlugin.getLogger().addHandler(new UnloadHookLoggerHandler(bungeePlugin));
            return bungeePlugin;
        }

        throw new IllegalArgumentException("Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin.");
    }

    @Override
    public void close() {
        super.close();
    }

    private final class UnloadHookLoggerHandler extends Handler {
        private final Plugin plugin;

        private UnloadHookLoggerHandler(Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void close() {
            unregisterHandlers(this.plugin);
        }

        @Override public void publish(LogRecord record) {}
        @Override public void flush() {}
    }
}
