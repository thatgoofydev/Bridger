package be.goofydev.bridger.velocity.logging;

import be.goofydev.bridger.common.plugin.logging.PluginLogger;
import org.slf4j.Logger;

public class Slf4jLoggerWrapper implements PluginLogger {

    private final Logger logger;

    public Slf4jLoggerWrapper(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warn(String message) {
        this.logger.warn(message);
    }

    @Override
    public void severe(String message) {
        this.logger.error(message);
    }
}
