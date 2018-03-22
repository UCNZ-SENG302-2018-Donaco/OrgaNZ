package seng302.Utilities;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.Scanner;

/**
 * A platform-independent Scanner for console input that features a command history that can be cycled through with
 * the UP and DOWN arrow keys. On Windows platforms, the default Scanner is used as the Windows CMD shell already
 * features command history. On all other platforms, a ConsoleReader from the JLine library is used instead.
 */
public class ConsoleScanner {
	private boolean isWindows = false;
	private Scanner scanIn;
	private ConsoleReader consoleIn;

	/**
	 * Creates a new ConsoleScanner by determining whether the platform is Windows-based or not, then instantiating
	 * the appropriate input scanner.
	 */
	public ConsoleScanner() {
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			isWindows = true;
		}

		if (isWindows) {
			scanIn = new Scanner(System.in);
		}
		else {
			try {
				consoleIn = new ConsoleReader();
				consoleIn.setHistoryEnabled(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the next line of user input.
	 * @return the next line of user input.
	 */
	public String readLine() {
		if (isWindows) {
			return scanIn.nextLine();
		} else {
			try {
				return consoleIn.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}
	}

	/**
	 * Closes the console and frees all resources.
	 */
	public void close() {
		if (scanIn != null) {
			scanIn.close();
		}
		if (consoleIn != null) {
			consoleIn.shutdown();
		}
	}
}
