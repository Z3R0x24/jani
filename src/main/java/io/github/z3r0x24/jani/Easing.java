package io.github.z3r0x24.jani;

/**
 * This interface defines an easing function for animation. Common easing functions are already pre-defined in the
 * {@code Default} enum, all of these can be found on the
 * <a href="https://easings.net/">Easing Functions Cheat Sheet</a>. Custom easing functions can be defined by
 * implementing the {@code apply()} method.
 */
public interface Easing {
    float apply(float fraction);

    enum Default implements Easing {
        LINEAR(x -> x),
        EASE_IN_SIN(x -> 1 - (float) Math.cos(x * Math.PI / 2)),
        EASE_OUT_SIN(x -> (float) (Math.sin(x * Math.PI / 2))),
        EASE_IN_OUT_SIN(x -> (float) (-Math.cos(x * Math.PI) - 1) / 2),
        EASE_IN_QUAD(x -> x * x),
        EASE_OUT_QUAD(x -> {
            float t = 1 - x;
            return 1 - (t * t);
        }),
        EASE_IN_OUT_QUAD(x -> {
            if (x < 0.5f) {
                return 2 * x * x;
            } else {
                float t = -2 * x + 2;
                return 1 - (t * t)/2;
            }
        }),
        EASE_IN_CUBIC(x -> x * x * x),
        EASE_OUT_CUBIC(x -> {
            float t = 1 - x;
            return 1 - (t * t * t);
        }),
        EASE_IN_OUT_CUBIC(x -> {
            if (x < 0.5f) {
                return 4 * x * x * x;
            } else {
                float t = -2 * x + 2;
                return 1 - (t * t * t)/2;
            }
        }),
        EASE_IN_QUART(x -> x * x * x * x),
        EASE_OUT_QUART(x -> {
            float t = 1 - x;
            return 1 - (t * t * t * t);
        }),
        EASE_IN_OUT_QUART(x -> {
            if (x < 0.5f) {
                return 8 * x * x * x * x;
            } else {
                float t = -2 * x + 2;
                return 1 - (t * t * t * t)/2;
            }
        }),
        EASE_IN_QUINT(x -> x * x * x * x * x),
        EASE_OUT_QUINT(x -> {
            float t = 1 - x;
            return 1 - (t * t * t * t * t);
        }),
        EASE_IN_OUT_QUINT(x -> {
            if (x < 0.5f) {
                return 16 * x * x * x * x * x;
            } else {
                float t = -2 * x + 2;
                return 1 - (t * t * t * t * t)/2;
            }
        }),
        EASE_IN_EXPO(x -> x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10)),
        EASE_OUT_EXPO(x -> x == 1 ? 1 : 1 - (float) Math.pow(2, -10 * x)),
        EASE_IN_OUT_EXPO(x -> {
            if (x == 0 || x == 1)
                return x;
            else if (x < 0.5f){
                return (float) Math.pow(2, 20 * x - 10) / 2;
            } else {
                return (float) (2 - Math.pow(2, -20 * x + 10)) / 2;
            }
        }),
        EASE_IN_CIRC(x -> 1 - (float) Math.sqrt(1 - (x * x))),
        EASE_OUT_CIRC(x -> {
            float t = x - 1;
            return (float) Math.sqrt(1 - (t * t));
        }),
        EASE_IN_OUT_CIRC(x -> {
            if (x < 0.5f) {
                float t = 2 * x;
                return (float) (1 - Math.sqrt(1 - (t * t))) / 2;
            } else {
                float t = -2 * x + 2;
                return (float) (Math.sqrt(1 - (t * t)) + 1) / 2;
            }
        }),
        EASE_IN_BACK(x -> 2.70158f * x * x * x - 1.70158f * x * x),
        EASE_OUT_BACK(x -> {
            float t = x - 1;
            return 1 + 2.70158f * t * t * t + 1.70158f * t * t;
        }),
        EASE_IN_OUT_BACK(x -> {
            float c1 = 1.70158f;
            float c2 = c1 * 1.525f;

            if (x < 0.5f) {
                float t = 2 * x;
                return (t * t * ((c2 + 1) * t - c2)) / 2;
            } else {
                float t = 2 * x - 2;
                return (t * t * ((c2 + 1) * t + c2) + 2) / 2;
            }
        }),
        EASE_IN_ELASTIC(x -> {
            if (x == 0 || x == 1) {
                return x;
            } else {
                return (float) (-Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * ((2 * Math.PI) / 3)));
            }
        }),
        EASE_OUT_ELASTIC(x -> {
            if (x == 0 || x == 1) {
                return x;
            } else {
                return (float) (Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * ((2 * Math.PI) / 3))) + 1;
            }
        }),
        EASE_IN_OUT_ELASTIC(x -> {
            if (x == 0 || x == 1) {
                return x;
            } else {
                double t = Math.sin((20 * x - 11.125) * ((2 * Math.PI) / 4.5));

                if (x < 0.5f) {
                    return -(float) (Math.pow(2, 20 * x - 10) * t) / 2;
                } else {
                    return (float) (Math.pow(2, -20 * x + 10) * t) / 2 + 1;
                }
            }
        }),
        EASE_OUT_BOUNCE(x -> {
            final float n1 = 7.5625f;
            final float d1 = 2.75f;

            if (x < 1 / d1) {
                return n1 * x * x;
            } else if (x < 2 / d1) {
                return n1 * (x -= 1.5f / d1) * x + 0.75f;
            } else if (x < 2.5 / d1) {
                return n1 * (x -= 2.25f / d1) * x + 0.9375f;
            } else {
                return n1 * (x -= 2.625f / d1) * x + 0.984375f;
            }
        }),
        EASE_IN_BOUNCE(x -> 1 - EASE_OUT_BOUNCE.apply(x)),
        EASE_IN_OUT_BOUNCE(x -> x < 0.5
                ? (1 - EASE_OUT_BOUNCE.apply(1 - 2 * x)) / 2
                : (1 + EASE_OUT_BOUNCE.apply(2 * x - 1)) / 2);

        private final Easing delegate;

        Default(final Easing delegate) {
            this.delegate = delegate;
        }

        @Override
        public float apply(float fraction) {
            return delegate.apply(fraction);
        }
    }
}
