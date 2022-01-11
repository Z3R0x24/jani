package io.github.z3r0x24.jani.Keyframes;

import java.awt.*;
import java.util.regex.Pattern;

public class ParseUtil {
    private static final String doubleRegex = "\\d?\\.?\\d+";
    private static final String percentRegex = doubleRegex + "%";
    private static final String secondRegex = doubleRegex + "s?";
    private static final String pointRegex = "point\\(\\d+\\s*,\\s*\\d+\\)\\s*";
    private static final String dimRegex = "dim\\(\\d+\\s*,\\s*\\d+\\)\\s*";

    public static Keyframes.Unit parseUnit(String u) {
        if (Pattern.matches(percentRegex, u)) {
            return Keyframes.Unit.PERCENT;
        } else if (Pattern.matches(secondRegex, u)){
            return Keyframes.Unit.SECOND;
        } else
            throw new KeyframeFormatException("Invalid unit format: " + u);
    }

    public static float parseUnitValue(String u, Keyframes.Unit expected) {
        if (Pattern.matches(percentRegex, u)) {
            if (expected.equals(Keyframes.Unit.SECOND))
                throw new KeyframeFormatException("Unit mismatch: expected seconds, got percent: " + u);
            u = u.substring(0, u.length() - 1);
        } else if (Pattern.matches(secondRegex, u)) {
            if (expected.equals(Keyframes.Unit.PERCENT))
                throw new KeyframeFormatException("Unit mismatch: expected percent, got seconds: " + u);

            if (u.endsWith("s"))
                u = u.substring(0, u.length() - 1);
        }

        return Float.parseFloat(u);
    }

    public static Keyframes.Type parseType(String t) {
        if (Pattern.matches(pointRegex, t)) {
            return Keyframes.Type.POINT;
        } else if (Pattern.matches(dimRegex, t)) {
            return Keyframes.Type.DIM;
        } else if (t.contains(".")) {
            return Keyframes.Type.DOUBLE;
        } else {
            try {
                Integer.parseInt(t);
            } catch (NumberFormatException e) {
                throw new KeyframeFormatException("Invalid type format: " + t);
            }

            return Keyframes.Type.INT;
        }
    }

    public static Object parseValue(String v) {
        if (v.contains("(")) {
            String substring = v.substring(v.indexOf("(") + 1, v.indexOf(")"));

            if (Pattern.matches(pointRegex, v)) {
                String[] pointValues = substring.split("\\s*,\\s*");
                return new Point(Integer.parseInt(pointValues[0]), Integer.parseInt(pointValues[1]));
            } else if (Pattern.matches(dimRegex, v)) {
                String[] dimValues = substring.split("\\s*,\\s*");
                return new Dimension(Integer.parseInt(dimValues[0]), Integer.parseInt(dimValues[1]));
            }

            throw new KeyframeFormatException("Invalid value format: " + v);
        } else {
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                try {
                    return Double.parseDouble(v);
                } catch (NumberFormatException e1) {
                    throw new KeyframeFormatException("Invalid value format: " + v);
                }
            }
        }
    }
}
