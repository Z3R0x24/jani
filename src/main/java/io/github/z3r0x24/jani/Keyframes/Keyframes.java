package io.github.z3r0x24.jani.Keyframes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class details the keyframes of an animation. Essentially each keyframe consists of a pair containing an instant
 * and a value for said instant. The instant determines when that value should occur during the interpolation.
 */
public class Keyframes {

    /**
     * Defines the instant unit for the key frames:
     * <ul>
     *     <li> PERCENT: Percentage of the animation duration.
     *     <li> SECOND: Absolute value in seconds regardless of duration.
     * </ul>
     */
    public enum Unit {
        PERCENT, SECOND;

        private boolean check(float v) {
            if (this.equals(PERCENT))
                return v >= 0 && v <= 100;

            return v >= 0;
        }

        private String range() {
            if (this.equals(PERCENT))
                return "(0 <= x <= 100)";

            return "(x >= 0)";
        }

        private String stringify(float value) {
            if (this.equals(PERCENT))
                return value + "%";

            return value + "s";
        }
    }

    /**
     * Defines the admitted types of values for the key frames:
     * <ul>
     *     <li> INT: int
     *     <li> DOUBLE: double
     *     <li> POINT: {@code java.awt.Point}
     *     <li> DIM: {@code java.awt.Dimension}
     * </ul>
     */
    public enum Type {
        INT, DOUBLE, POINT, DIM;

        private boolean check(Object value) {
            switch (this) {
                case INT: return value instanceof Integer;
                case DOUBLE: return value instanceof Double;
                case POINT: return value instanceof Point;
                case DIM: return value instanceof Dimension;
            }

            return false;
        }

        @Override
        public String toString() {
            switch (this) {
                case INT: return "int";
                case DOUBLE: return "double";
                case POINT: return "Point";
                case DIM: return "Dimension";
            }

            return null;
        }

        private String stringify(Object value) {
            switch (this) {
                case POINT:
                    Point p = (Point) value;
                    return "point(" + p.x + ", " + p.y + ")";

                case DIM:
                    Dimension dim = (Dimension) value;
                    return "dim(" + dim.width + ", " + dim.height + ")";

                default:
                    return value.toString();
            }
        }
    }

    private final ArrayList<KFrame> frames = new ArrayList<>();
    private Type type = null;
    private Unit unit = null;

    /**
     * Creates a keyframes object with the specified value type and instant unit. These are defined in the {@link Type}
     * and {@link Unit} enums, respectively. Parsing a string with the keyframes info may be preferred.
     * @see Keyframes#parse(String)
     * @param type Value type, it will determine the interpolation boundaries
     * @param unit Instant unit, either percentage or seconds
     */
    public Keyframes(Type type, Unit unit) {
        // When creating the object manually, we need the type and units, as trying to infer type could cause problems
        // when dealing with ints and doubles. And instant units can't be inferred properly.
        this.type = type;
        this.unit = unit;
    }

    // For parsing purposes
    private Keyframes(){}

    /**
     * Adds a keyframe. Keyframes will be auto-sorted in ascending order based on instant.
     * @param instant Keyframe instant
     * @param value Keyframe value
     */
    public void addKeyframe(float instant, Object value) {
        if (!type.check(value))
            throw new KeyframeFormatException("Type mismatch: All keyframes must have a value of type " + type);

        if (!unit.check(instant))
            throw new KeyframeInstantException("Instant value out of range " + unit.range() + ": " + instant);

        frames.add(new KFrame(instant, value));
        Collections.sort(frames);
    }

    // This is for the parsing method.
    private void addKeyframe0(String instant, String value) {
        float atValue;
        Object parsedValue;

        // Infer type and units
        if (type == null || unit == null) {
            unit = ParseUtil.parseUnit(instant);
            type = ParseUtil.parseType(value);
        }

        atValue = ParseUtil.parseUnitValue(instant, unit);

        // Unit check so units won't be mismatched
        if (!unit.check(atValue))
            throw new KeyframeInstantException("Value out of range " + unit.range() + ": " + instant);

        parsedValue = ParseUtil.parseValue(value);

        if (parsedValue instanceof Integer && type.equals(Type.DOUBLE))
            parsedValue = (((Integer) parsedValue).doubleValue());

        // Type check so types won't be mismatched
        if (!type.check(parsedValue)) {
            throw new KeyframeFormatException("Value type mismatch: expected " + type + ", got " +
                    parsedValue.getClass().getSimpleName() + ": " + value);
        }

        frames.add(new KFrame(atValue, parsedValue));
    }

    /**
     * Parses a string into a keyframes object. The string format for each keyframe is: <br><br>
     * {@code [instant][unit]: [value];} (e.g: 15%: 5) <br><br>
     * where instant is a double value, unit is a character (% for percentage, s for seconds), and value is any of
     * these: int (e.g: 5), double (e.g: 2.17), Point (e.g: point(120, 170)) or Dimension (e.g: dim(600, 400)).
     * The instant unit is required on the first one, optional for the rest (same units will be assigned as they can't
     * be mismatched). Initial instant is also required (either 0% or 0s). <br>
     * Full string example:<br>
     * {<br>
     *     0%: point(100, 50); <br>
     *     25%: point(150, 50); <br>
     *     50%: point(150, 100); <br>
     *     100%: point(200, 100); <br>
     * }
     * @param kFrames String to be parsed
     * @return Keyframes object
     */
    public static Keyframes parse(String kFrames) {
        if (kFrames == null || kFrames.trim().isEmpty()) {
            throw new IllegalArgumentException("String cannot be null or blank");
        }

        String temp = kFrames.replaceAll("\n+", "");
        int start, end;

        start = temp.indexOf('{');
        end = temp.indexOf('}');

        if (start != -1) {
            if (end == -1)
                throw new KeyframeFormatException("Unclosed braces");

            temp = temp.substring(start + 1, end);
        } else {
            if (end != -1)
                throw new KeyframeFormatException("Unopened braces");
        }

        String[] frames = temp.split("\\s*;\\s*");
        Keyframes keyframes = new Keyframes();

        for (String frame: frames) {
            if (frame.trim().isEmpty())
                continue;

            String[] values = frame.split("\\s*:\\s*");

            if (values.length != 2)
                throw new KeyframeFormatException("Invalid format: " + frame);

            keyframes.addKeyframe0(values[0], values[1]);
        }

        Collections.sort(keyframes.frames);

        if (keyframes.frames.get(0).instant != 0)
            throw new KeyframeInstantException("Missing initial instant value (add a value for 0% or 0s).");

        return keyframes;
    }

    @Override
    public String toString() {
        StringBuilder frameString = new StringBuilder();

        for (KFrame frame: frames) {
            frameString.append("\t").append(unit.stringify(frame.instant)).append(": ");
            frameString.append(type.stringify(frame.value)).append(";\n");
        }

        return "Keyframes{\n" +
                frameString +
                "}";
    }

    private static class KFrame implements Comparable<KFrame> {
        float instant;
        Object value;

        KFrame(float instant, Object value) {
            this.instant = instant;
            this.value = value;
        }

        @Override
        public int compareTo(KFrame o) {
            return Float.compare(instant, o.instant);
        }
    }

    /**
     * @return The type admitted by this instance
     * @see Keyframes.Type
     */
    public Type getType() {
        return type;
    }

    /**
     * @return The unit used in this instance
     * @see Keyframes.Unit
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Returns the int value at the key frame specified by {@code kfIndex}. Calling this method on an instance with a
     * type different from {@code Keyframes.Type.INT} will result in an exception.
     * @param kfIndex index of the key frame holding the value
     * @return integer value
     * @see Keyframes.Type
     */
    public int getIntAt(int kfIndex) {
        if (!type.equals(Type.INT))
            throw new IllegalStateException("Incorrect solicited type (Type: " + type + ", solicited: int");

        return (int) frames.get(kfIndex).value;
    }

    /**
     * Returns the double value at the key frame specified by {@code kfIndex}. Calling this method on an instance with a
     * type different from {@code Keyframes.Type.DOUBLE} will result in an exception.
     * @param kfIndex index of the key frame holding the value
     * @return double value
     * @see Keyframes.Type
     */
    public double getDoubleAt(int kfIndex) {
        if (!type.equals(Type.DOUBLE))
            throw new IllegalStateException("Incorrect solicited type (Type: " + type + ", solicited: double");

        return (double) frames.get(kfIndex).value;
    }

    /**
     * Returns the Point value at the key frame specified by {@code kfIndex}. Calling this method on an instance with a
     * type different from {@code Keyframes.Type.POINT} will result in an exception.
     * @param kfIndex index of the key frame holding the value
     * @return Point value
     * @see Keyframes.Type
     */
    public Point getPointAt(int kfIndex) {
        if (!type.equals(Type.POINT))
            throw new IllegalStateException("Incorrect solicited type (Type: " + type + ", solicited: Point");

        return (Point) frames.get(kfIndex).value;
    }

    /**
     * Returns the Dimension value at the key frame specified by {@code kfIndex}. Calling this method on an instance
     * with a type different from {@code Keyframes.Type.DIM} will result in an exception.
     * @param kfIndex index of the key frame holding the value
     * @return Dimension value
     * @see Keyframes.Type
     */
    public Dimension getDimAt(int kfIndex) {
        if (!type.equals(Type.DIM))
            throw new IllegalStateException("Incorrect solicited type (Type: " + type + ", solicited: Dimension");

        return (Dimension) frames.get(kfIndex).value;
    }

    /**
     * Returns the instant of the key frame specified by {@code kfIndex}.
     * @param kfIndex index of the key frame holding the instant
     * @return float value representing the instant
     */
    public float getInstantAt(int kfIndex) {
        if (unit.equals(Unit.SECOND))
            return frames.get(kfIndex).instant;
        else
            return frames.get(kfIndex).instant / 100;
    }

    /**
     * @return Amount of key frames within this instance
     */
    public int size() {
        return frames.size();
    }
}
