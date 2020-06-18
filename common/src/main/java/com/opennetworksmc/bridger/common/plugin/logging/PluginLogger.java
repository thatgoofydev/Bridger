package com.opennetworksmc.bridger.common.plugin.logging;

public interface PluginLogger {
    /**
     * Log information
     * @param message to log
     */
    void info(String message);

    /**
     * Log warning
     * @param message to log
     */
    void warn(String message);

    /**
     * Log severe error
     * @param message to log
     */
    void severe(String message);
}
