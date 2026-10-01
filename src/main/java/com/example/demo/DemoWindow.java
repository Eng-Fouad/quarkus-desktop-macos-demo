package com.example.demo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.UIManager;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkiverse.desktop.awt.DesktopStartupEvent;
import io.quarkiverse.desktop.awt.EdtExecutor;
import io.quarkus.logging.Log;

/**
 * The window of the demo: a banner painted with Java2D, where the application runs, and a few Swing components.
 */
@Singleton
public class DemoWindow extends JFrame {

    @Inject
    EdtExecutor edt;

    /** Saves an image of the window to this file, then closes it: a check that needs no one at the keyboard. */
    @ConfigProperty(name = "demo.quarkus-version")
    String quarkusVersion;

    @ConfigProperty(name = "demo.snapshot")
    Optional<Path> snapshot;

    void open(@Observes DesktopStartupEvent event) {
        setTitle("Quarkus Desktop on macOS");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // the last window closed stops the application

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(new Banner(), BorderLayout.NORTH);
        content.add(new JScrollPane(environment()), BorderLayout.CENTER);
        content.add(controls(), BorderLayout.SOUTH);
        setContentPane(content);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        snapshot.ifPresent(file -> {
            Timer timer = new Timer(1500, e -> saveAndClose(file));
            timer.setRepeats(false);
            timer.start();
        });
    }

    private JTable environment() {
        String nativeImage = System.getProperty("org.graalvm.nativeimage.imagecode");
        String[][] rows = {
                { "Runtime", nativeImage != null ? "native executable (GraalVM " + System.getProperty("java.vm.version") + ")"
                        : "JVM (" + System.getProperty("java.vm.name") + ")" },
                { "Operating system", System.getProperty("os.name") + " " + System.getProperty("os.version") + ", "
                        + System.getProperty("os.arch") },
                { "Java", System.getProperty("java.version") + " (" + System.getProperty("java.vendor.version") + ")" },
                { "Quarkus", quarkusVersion },
                { "Look and feel", UIManager.getLookAndFeel().getName() },
                { "Java2D pipeline", GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        .getDefaultConfiguration().getClass().getSimpleName() },
                { "Event dispatch thread", Thread.currentThread().getName() },
        };
        JTable table = new JTable(rows, new String[] { "", "" });
        table.setTableHeader(null);
        table.setEnabled(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(450);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        return table;
    }

    private JPanel controls() {
        JButton hello = new JButton("Say hello");
        hello.addActionListener(e -> JOptionPane.showMessageDialog(this, "Hello from Quarkus Desktop!"));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.add(hello);
        controls.add(new JComboBox<>(new String[] { "Swing", "AWT", "Java2D" }));
        controls.add(new JCheckBox("Native", true));
        controls.add(new JSlider(0, 100, 60));
        return controls;
    }

    /** Paints the window into an image, writes it off the event dispatch thread, then closes the window. */
    private void saveAndClose(Path file) {
        BufferedImage image = new BufferedImage(getContentPane().getWidth(), getContentPane().getHeight(),
                BufferedImage.TYPE_INT_RGB);
        getContentPane().printAll(image.createGraphics());
        CompletableFuture.runAsync(() -> {
            try {
                ImageIO.write(image, "png", file.toFile());
                Log.infof("Snapshot written to %s", file.toAbsolutePath());
            } catch (Exception e) {
                Log.error("Snapshot not written", e);
            }
        }).thenRunAsync(this::dispose, edt);
    }
}
