package org.example.foodanddrinkproject.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;


import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


@Configuration
@Profile("local") // CRITICAL: This bean only loads if the 'local' profile is active
public class BrowserLauncherConfig {


    private static final Logger logger = LoggerFactory.getLogger(BrowserLauncherConfig.class);

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserOnStartup() {
        String url = "http://localhost:8080/swagger-ui.html";
        logger.info("Application ready. Opening browser at: {}", url);
        String oauth2Url = " http://localhost:8080/oauth2/authorization/google";
        logger.info("Trying Google login at: {}", oauth2Url);


        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                logger.error("Failed to open browser", e);
            }
        } else {
            logger.warn("Desktop is not supported. Cannot open browser automatically.");
        }
    }
}
