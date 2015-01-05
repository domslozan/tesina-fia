/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author ricc
 */
public class MapLegend extends JPanel {

    public MapLegend() {
        super();
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

    }

    public void addElement(PathfinderWrapper pf, MouseListener mouseHandler) {
        LegendElement le = new LegendElement(pf.getName(), pf.getPathColor(), pf.getPathStroke());
        le.addMouseListener(mouseHandler);
        add(le);
        revalidate();
    }

    public void clearElements() {
        removeAll();
        revalidate();
    }
}

class LegendElement extends JPanel {

    private final String text;
    private final Color color;
    private final Stroke stroke;
    private final JLabel label;
    private final JPanel line;



    public LegendElement(String text, Color color, Stroke stroke) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.color = color;
        this.text = text;
        this.stroke = stroke;

        final Border selectedBorder = BorderFactory.createLineBorder(Color.BLACK);
        final Border unselectedBorder = BorderFactory.createLineBorder(new Color(0,0,0,0));
        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                setBorder(selectedBorder);
            }

            @Override
            public void mouseExited(MouseEvent me) {
                setBorder(unselectedBorder);
            }
        });

        setBorder(unselectedBorder);
        

        this.line = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke(LegendElement.this.stroke);
                g2d.setColor(LegendElement.this.color);
                g2d.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
            }
        };
        line.setPreferredSize(new Dimension(15, 10));
        this.label = new JLabel(text);
        this.add(line);
        this.add(Box.createHorizontalStrut(5));
        this.add(label);
    }
}