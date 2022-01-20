package io.github.z3r0x24.jani;

import io.github.z3r0x24.jani.Keyframes.Keyframes;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Demo {
    private static int arc = 0;
    private static int opacity = 0;
    private static int flashOpacity = 0;
    private static int angle = 0;
    private static boolean color = false;

    private static Animation currentAnimation;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException ignored) {}

        int rectX = 150;
        int rectY = 150;
        int rectW = 200;
        int rectH = 200;

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                AffineTransform transform = new AffineTransform();
                transform.rotate(Math.toRadians(angle), rectX + rectW / 2f, rectY + rectH / 2f);

                float colorHue = 0;

                if (color)
                    colorHue = angle/360f;


                g2d.setColor(Color.getHSBColor(colorHue, 1, 1));

                AffineTransform old = g2d.getTransform();
                g2d.transform(transform);
                Composite comp = g2d.getComposite();

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity/255f));
                g2d.fillRoundRect(rectX, rectY, rectW, rectH, arc, arc);

                g2d.setComposite(comp);
                g2d.setTransform(old);

                g2d.setColor(new Color(255, 255, 255, flashOpacity));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(500, 500));

        String opacity_string =
                "{0%: 0; 100%: 255}";
        String arc_string =
                "{0%: 0; 25%: 25; 50%: 50; 75%: 75; 100%: 100}";
        String flash_string =
                "{0%: 0; 50%: 255; 100%: 0}";
        String angle_string =
                "{0%: 0; 100%: 360}";

        Keyframes opacity_kframes = Keyframes.parse(opacity_string);
        Keyframes arc_kframes = Keyframes.parse(arc_string);
        Keyframes flash_kframes = Keyframes.parse(flash_string);
        Keyframes angle_kframes = Keyframes.parse(angle_string);

        Animation rotate2 = new Animation(angle_kframes, 2, 0, true) {
            @Override
            protected void update(int x) {
                angle = x;
                panel.repaint();
            }
        };

        Animation flashAnimation = new Animation(flash_kframes, 0.5f) {
            @Override
            protected void update(int x) {
                flashOpacity = x;
                panel.repaint();
            }
        };

        Animation rotate1 = new Animation(angle_kframes, 2, 0, false, Easing.Default.EASE_IN_QUART) {
            @Override
            protected void update(int x) {
                angle = x;
                panel.repaint();
            }

            @Override
            public void onAnimationFinished() {
                currentAnimation = rotate2;
                flashAnimation.play();
                rotate2.play();
                color = true;
            }
        };

        Animation arcAnimation = new Animation(arc_kframes, 1, 0, false, Easing.Default.EASE_OUT_BOUNCE) {
            @Override
            protected void update(int x) {
                arc = x;
                panel.repaint();
            }

            @Override
            public void onAnimationFinished() {
                currentAnimation = rotate1;
                rotate1.play();
            }
        };

        Animation opacityAnimation = new Animation(opacity_kframes, 1.5f) {
            @Override
            protected void update(int x) {
                opacity = x;
                panel.repaint();
            }

            @Override
            public void onAnimationFinished() {
                currentAnimation = arcAnimation;
                arcAnimation.play();
            }
        };

        currentAnimation = opacityAnimation;

        opacityAnimation.freeze(true);
        arcAnimation.freeze(true);

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel buttonContainer = new JPanel(new GridLayout(0, 3));

        JButton play = new JButton("Play");
        JButton pause = new JButton("Pause");
        JButton stop = new JButton("Stop");

        play.addActionListener(e -> currentAnimation.play());
        pause.addActionListener(e -> currentAnimation.pause());
        stop.addActionListener(e -> {
            opacityAnimation.stop();
            arcAnimation.stop();
            rotate1.stop();
            flashAnimation.stop();
            rotate2.stop();
            color = false;
            currentAnimation = opacityAnimation;
        });

        buttonContainer.add(play);
        buttonContainer.add(pause);
        buttonContainer.add(stop);

        container.add(panel);
        container.add(buttonContainer);

        frame.add(container);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
