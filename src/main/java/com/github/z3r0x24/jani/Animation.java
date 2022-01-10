package com.github.z3r0x24.jani;

import com.github.z3r0x24.jani.Keyframes.Keyframes;

import java.awt.*;

public abstract class Animation {
    private final Keyframes keyframes;
    private final Animator animator;
    private int keyframeIndex;

    public Animation(Keyframes kf, float durationSeconds, float delaySeconds) {
        this(kf, durationSeconds, delaySeconds, false);
    }

    public Animation(Keyframes kf, float durationSeconds, float delaySeconds, boolean loops) {
        this(kf, durationSeconds, delaySeconds, loops, Easing.Default.LINEAR);
    }

    public Animation(Keyframes kf, float durationSeconds, float delaySeconds, boolean loops, Easing easing) {
        this.keyframes = kf;
        this.keyframeIndex = 0;
        this.animator = new Animator(Math.round(durationSeconds * 1000), Math.round(delaySeconds * 1000), loops, easing) {
            @Override
            public void update(float fraction) {
                float previous, next;

                if (keyframes.getUnit().equals(Keyframes.Unit.SECOND)) {
                    if (keyframeIndex + 1 < keyframes.size() &&
                            fraction * durationSeconds > keyframes.getInstantAt(keyframeIndex + 1)) {
                        keyframeIndex++;
                    }

                    previous = keyframes.getInstantAt(keyframeIndex) / durationSeconds;

                    if (keyframeIndex + 1 < keyframes.size())
                        next = keyframes.getInstantAt(keyframeIndex + 1) / durationSeconds;
                    else
                        next = 1;
                } else {
                    if (keyframeIndex + 1 < keyframes.size() &&
                            fraction > keyframes.getInstantAt(keyframeIndex + 1)) {
                        keyframeIndex++;
                    }

                    previous = keyframes.getInstantAt(keyframeIndex);

                    if (keyframeIndex + 1 < keyframes.size())
                        next = keyframes.getInstantAt(keyframeIndex + 1);
                    else
                        next = 1;
                }

                float relativeFraction;

                if (fraction == 0) {
                    relativeFraction = 0;
                    keyframeIndex = 0;
                } else {
                    relativeFraction = (fraction - previous) / (next - previous);
                }

                switch (keyframes.getType()) {
                    case INT:
                        int prevInt, nextInt;

                        prevInt = keyframes.getIntAt(keyframeIndex);

                        if (keyframeIndex + 1 < keyframes.size())
                            nextInt = keyframes.getIntAt(keyframeIndex + 1);
                        else
                            nextInt = keyframes.getIntAt(keyframeIndex);

                        Animation.this.update(Tween.interpolateInt(prevInt, nextInt, relativeFraction));
                        break;
                    case DOUBLE:
                        double prevDouble, nextDouble;

                        prevDouble = keyframes.getDoubleAt(keyframeIndex);

                        if (keyframeIndex + 1 < keyframes.size())
                            nextDouble = keyframes.getDoubleAt(keyframeIndex + 1);
                        else
                            nextDouble = keyframes.getDoubleAt(keyframeIndex);

                        Animation.this.update(Tween.interpolateDouble(prevDouble, nextDouble, relativeFraction));
                        break;
                    case POINT:
                        Point prevPoint, nextPoint;

                        prevPoint = keyframes.getPointAt(keyframeIndex);

                        if (keyframeIndex + 1 < keyframes.size())
                            nextPoint = keyframes.getPointAt(keyframeIndex + 1);
                        else
                            nextPoint = keyframes.getPointAt(keyframeIndex);

                        Animation.this.update(Tween.interpolatePoint(prevPoint, nextPoint, relativeFraction));
                        break;
                    case DIM:
                        Dimension prevDim, nextDim;

                        prevDim = keyframes.getDimAt(keyframeIndex);

                        if (keyframeIndex + 1 < keyframes.size())
                            nextDim = keyframes.getDimAt(keyframeIndex + 1);
                        else
                            nextDim = keyframes.getDimAt(keyframeIndex);

                        Animation.this.update(Tween.interpolateDim(prevDim, nextDim, relativeFraction));
                        break;
                }
            }

            @Override
            protected void onAnimationFinished() {
                Animation.this.onAnimationFinished();
            }
        };
    }

    public void play() {
        animator.play();
    }

    public void play(boolean skipDelay) {
        animator.play(skipDelay);
    }

    public void pause() {
        animator.pause();
    }

    public void stop() {
        animator.stop();
        //keyframeIndex = 0;
    }

    public void cancel() {
        animator.cancel();
        //keyframeIndex = 0;
    }

    public void freeze(boolean b) {
        animator.freeze(b);
    }

    public void onAnimationFinished(){}

    protected void update(Dimension dim) {}
    protected void update(Point p) {}
    protected void update(int x) {}
    protected void update(double x) {}
}
