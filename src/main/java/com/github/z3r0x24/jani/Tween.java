package com.github.z3r0x24.jani;

import java.awt.*;

/**
 * Utility class for value interpolation.
 */
public final class Tween {
    /**
     * Interpolates an integer between {@code from} and {@code to} at the specified {@code fraction} without easing
     * (linear).
     * @param from First value
     * @param to Second value
     * @param fraction Value fraction
     * @return Interpolated integer
     */
    public static int interpolateInt(int from, int to, float fraction) {
        return interpolateInt(from, to, fraction, Easing.Default.LINEAR);
    }

    /**
     * Interpolates an integer between {@code from} and {@code to} at the specified {@code fraction} with the specified
     * easing function.
     * @param from First value
     * @param to Second value
     * @param fraction Value fraction
     * @param ease Easing function
     * @return Interpolated integer
     */
    public static int interpolateInt(int from, int to, float fraction, Easing ease) {
        return Math.round((to - from) * ease.apply(fraction)) + from;
    }

    /**
     * Interpolates a double between {@code from} and {@code to} at the specified {@code fraction} without easing
     * (linear).
     * @param from First value
     * @param to Second value
     * @param fraction Value fraction
     * @return Interpolated double
     */
    public static double interpolateDouble(double from, double to, float fraction) {
        return interpolateDouble(from, to, fraction, Easing.Default.LINEAR);
    }

    /**
     * Interpolates a double between {@code from} and {@code to} at the specified {@code fraction} with the specified
     * easing function.
     * @param from First value
     * @param to Second value
     * @param fraction Value fraction
     * @param ease Easing function
     * @return Interpolated double
     */
    public static double interpolateDouble(double from, double to, float fraction, Easing ease) {
        return (to - from) * ease.apply(fraction) + from;
    }

    /**
     * Interpolates a Point between {@code from} and {@code to} at the specified {@code fraction} without easing
     * (linear).
     * @param from First point
     * @param to Second point
     * @param fraction Value fraction
     * @return Interpolated Point
     */
    public static Point interpolatePoint(Point from, Point to, float fraction) {
        return interpolatePoint(from, to, fraction, Easing.Default.LINEAR, Easing.Default.LINEAR);
    }

    /**
     * Interpolates a Point between {@code from} and {@code to} at the specified {@code fraction} with the specified
     * easing function. The x and y coordinates are interpolated independent of each other, so a different easing
     * function is allowed for each one.
     * @param from First point
     * @param to Second point
     * @param fraction Value fraction
     * @param easeX X coordinate easing function
     * @param easeY Y coordinate easing function
     * @return Interpolated Point
     */
    public static Point interpolatePoint(Point from, Point to, float fraction, Easing easeX, Easing easeY) {
        int x, y;

        x = interpolateInt(from.x, to.x, fraction, easeX);
        y = interpolateInt(from.y, to.y, fraction, easeY);

        return new Point(x, y);
    }

    /**
     * Interpolates a Dimension between {@code from} and {@code to} at the specified {@code fraction} without easing
     * (linear).
     * @param from First dimension
     * @param to Second dimension
     * @param fraction Value fraction
     * @return Interpolated Dimension
     */
    public static Dimension interpolateDim(Dimension from, Dimension to, float fraction) {
        return interpolateDim(from, to, fraction, Easing.Default.LINEAR, Easing.Default.LINEAR);
    }

    /**
     * Interpolates a Dimension between {@code from} and {@code to} at the specified {@code fraction} with the specified
     * easing function. The width and height are interpolated independent of each other, so a different easing function
     * is allowed for each one.
     * @param from First dimension
     * @param to Second dimension
     * @param fraction Value fraction
     * @param easeW Width easing function
     * @param easeH Height easing function
     * @return Interpolated Dimension
     */
    public static Dimension interpolateDim(Dimension from, Dimension to, float fraction, Easing easeW, Easing easeH) {
        int w, h;

        w = interpolateInt(from.width, to.width, fraction, easeW);
        h = interpolateInt(from.height, to.height, fraction, easeH);

        return new Dimension(w, h);
    }
}
