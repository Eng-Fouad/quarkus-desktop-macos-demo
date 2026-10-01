package com.example.demo;

import java.awt.desktop.AboutEvent;

import javax.swing.JOptionPane;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkiverse.desktop.awt.QuitRequest;
import io.quarkus.logging.Log;

/**
 * The macOS application menu: quarkus-desktop fires its items as CDI events, on the event dispatch thread.
 */
@Singleton
public class ApplicationMenu {

    @ConfigProperty(name = "demo.quarkus-version")
    String quarkusVersion;

    void about(@Observes AboutEvent event) {
        JOptionPane.showMessageDialog(null, "Quarkus Desktop demo\nA Swing application built with Quarkus " + quarkusVersion,
                "About", JOptionPane.INFORMATION_MESSAGE);
    }

    void quit(@Observes QuitRequest request) {
        // request.cancel() would keep the application running (for instance with unsaved changes)
        Log.info("Cmd-Q: the application stops");
    }
}
