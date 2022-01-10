package com.github.z3r0x24.jani;

import com.github.z3r0x24.jani.Keyframes.Keyframes;

import javax.swing.*;
import java.awt.*;

// Very simple example code. Might change later.
public class Test {
    private static int arc = 0;
    private static int opacity = 0;
    public static void main(String[] args) {
        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLUE);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.fillRoundRect(150, 150, 200, 200, arc, arc);
                g2d.setColor(new Color(255, 255, 255, opacity));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(500, 500));

        String keyframes =
                "{0%: 0; 25%: 50; 50%: 100; 75%: 150; 100%: 200;}";
        String keyframes2 =
                "{0%: 0; 50%: 255; 100%: 0}";

        Keyframes kframes = Keyframes.parse(keyframes);
        Keyframes kframes2 = Keyframes.parse(keyframes2);

        Animation animation2 = new Animation(kframes2, 0.5f, 0) {
            @Override
            protected void update(int x) {
                opacity = x;
                panel.repaint();
            }
        };

        Animation animation = new Animation(kframes, 1, 0, false, Easing.Default.EASE_OUT_BOUNCE) {
            @Override
            protected void update(int x) {
                arc = x;
                panel.repaint();
            }

            @Override
            public void onAnimationFinished() {
                animation2.play();
            }
        };

        animation.freeze(true);

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel buttonContainer = new JPanel(new GridLayout(0, 2));

        JButton play = new JButton("Play");
        JButton pause = new JButton("Pause");

        play.addActionListener(e -> animation.play());
        pause.addActionListener(e -> animation.pause());

        buttonContainer.add(play);
        buttonContainer.add(pause);

        container.add(panel);
        container.add(buttonContainer);

        frame.add(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
