package be.goofydev.bridger.common.plugin.bootstrap;

import be.goofydev.bridger.api.platform.PlatformType;
import be.goofydev.bridger.common.plugin.logging.PluginLogger;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerAdapter;

import java.io.InputStream;
import java.nio.file.Path;

public interface BridgerBootstrap {

    /**
     * Gets a wrapped logger instance for the platform.
     *
     * @return the wrapped plugin's logger
     */
    PluginLogger getPluginLogger();

    /**
     * Gets a wrapped scheduler instance of the platform.
     *
     * @return the wrapped plugin's scheduler
     */
    SchedulerAdapter getSchedulerAdapter();

    /**
     * Gets the platform this instance of Bridger is running on.
     *
     * @return the platform type
     */
    PlatformType getPlatformType();

    /**
     * Gets the default data directory
     *
     * @return the platforms data folder
     */
    Path getDataDirectory();

    /**
     * Gets a bundled resource file from the jar.
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    InputStream getResourceStream(String path);
}
