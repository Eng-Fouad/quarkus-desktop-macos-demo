package com.example.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

/**
 * Java2D: a gradient, shapes with transparency, and antialiased text.
 */
class Banner extends JComponent {

    Banner() {
        setPreferredSize(new Dimension(600, 110));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        g2.setPaint(new GradientPaint(0, 0, new Color(0x4695EB), w, h, new Color(0xFF004A)));
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 24, 24));
        g2.setColor(new Color(255, 255, 255, 60));
        g2.fill(new Ellipse2D.Float(w - 150, -40, 180, 180));
        g2.fill(new Ellipse2D.Float(w - 260, 50, 120, 120));
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        g2.drawString("Quarkus Desktop", 24, 50);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        g2.drawString("Swing and Java2D, native on macOS", 24, 82);
        g2.dispose();
    }
}
