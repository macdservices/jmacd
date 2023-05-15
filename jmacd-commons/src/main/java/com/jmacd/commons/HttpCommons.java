package com.jmacd.commons;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface HttpCommons extends JMacDCommonsErrors {

	default void openBrowser(String url) {
		openUrl(url);
	}

	default void openUrl(String url) {

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();

			try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				throw new JMacDCommonsRuntimeException(JMacDCommonsError_ERROR_1, //
						"Failed to open browser", e); //$NON-NLS-1$
			}
		} else {
			Runtime runtime = Runtime.getRuntime();

			try {
				runtime.exec("xdg-open " + url); //$NON-NLS-1$
			} catch (IOException e) {
				throw new JMacDCommonsRuntimeException(JMacDCommonsError_ERROR_2, //
						"Failed to open browser", e); //$NON-NLS-1$
			}
		}
	}

}
