package com.opennetworksmc.bridger.common.plugin.logging;

import java.util.logging.Logger;

public class JavaLoggerWrapper implements PluginLogger {
    private Logger logger;

    public JavaLoggerWrapper(Logger logger) {
        this.logger = logger;
    }

    public void info(String message) {
        this.logger.info(message);
    }

    public void warn(String message) {
        this.logger.warning(message);
    }
    
    public void severe(String message) {
        this.logger.severe(message);
    }
}
