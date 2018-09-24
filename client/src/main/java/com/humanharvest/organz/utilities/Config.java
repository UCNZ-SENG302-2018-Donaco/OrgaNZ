package com.humanharvest.organz.utilities;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;

/**
 * Utility class to retrieve user configuration directories in a cross platform manner.
 */
public final class Config {

    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    private static final String APP_NAME = "OrgaNZ";

    private static final String XDG_CACHE_HOME = "XDG_CACHE_HOME";

    private static String cacheDirectory;

    private Config() {
    }

    /**
     * Returns a windows system folder.
     *
     * @param folder A CSIDL that refers to a folder.
     */
    private static String resolveFolder(int folder) {
        char[] pszPath = new char[WinDef.MAX_PATH];
        WinNT.HRESULT result = Shell32.INSTANCE.SHGetFolderPath(null, folder, null, null, pszPath);
        if (WinError.S_OK.equals(result)) {
            return Native.toString(pszPath);
        }

        throw new UnsupportedOperationException("SHGetFolderPath returns an error: " + result.intValue());
    }

    /**
     * This returns a platform dependant directory to store cache data and other temporary data.
     * Windows: %USER_DIR%\AppData\Local\%APP_NAME%
     * Mac OS: /Users/%USER%/Library/Caches/%APP_NAME%
     * Linux: %HOME%/.cache/%APP_NAME%
     */
    public static String getCacheDirectory() {
        if (cacheDirectory == null) {
            String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

            if (os.contains("windows")) {
                // This OS is windows
                cacheDirectory = Paths.get(resolveFolder(ShlObj.CSIDL_LOCAL_APPDATA), APP_NAME).toString();
            } else if (os.contains("mac os")) {
                // This OS is mac
                cacheDirectory = Paths.get(System.getProperty("user.home"), "Library/Caches", APP_NAME).toString();
            } else {
                // This OS is likely unix
                Path userCache = Paths.get(System.getProperty("user.home"), ".cache");
                String dir = System.getProperty(XDG_CACHE_HOME, userCache.toString());
                cacheDirectory = Paths.get(dir, APP_NAME).toString();
            }

            File configFile = new File(cacheDirectory);
            boolean failed = !configFile.mkdirs();
            if (failed && !configFile.exists()) {
                LOGGER.log(Level.WARNING, "Cache directory could not be created");
            }
        }

        return cacheDirectory;
    }
}
