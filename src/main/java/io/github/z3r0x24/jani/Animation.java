package io.github.z3r0x24.jani;

import io.github.z3r0x24.jani.Keyframes.Keyframes;

import java.awt.*;

/**
 * The animation class is an abstraction of the Animator class that is able to use key frames in order to interpolate
 * the necessary values for an animation.
 */
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

    /**
     * Plays the animation in whichever direction it was set before calling this method.
     */
    public void play() {
        animator.play();
    }

    /**
     * Plays the animation in whichever direction it was set before calling this method.
     * @param skipDelay If true, will skip the initial delay.
     */
    public void play(boolean skipDelay) {
        animator.play(skipDelay);
    }

    /**
     * Plays the animation ensuring it will go forward.
     */
    public void forward() {
        animator.forward();
    }

    /**
     * Plays the animation ensuring it will go forward.
     * @param skipDelay If true, will skip the initial delay.
     */
    public void forward(boolean skipDelay) {
        animator.forward(skipDelay);
    }

    /**
     * Plays the animation ensuring it will go backward.
     */
    public void backward() {
        animator.backward();
    }

    /**
     * Plays the animation ensuring it will go backward.
     * @param skipDelay If true, will skip the initial delay.
     */
    public void backward(boolean skipDelay) {
        animator.backward(skipDelay);
    }

    /**
     * Pauses the animation. Can be resumed.
     */
    public void pause() {
        animator.pause();
    }

    /**
     * Stops the animation setting it back to the beginning and calls {@code onAnimationFinished()}.
     * @see Animation#cancel()
     */
    public void stop() {
        animator.stop();
    }

    /**
     * Stops the animation without calling {@code onAnimationFinished()}.
     * @see Animation#stop()
     */
    public void cancel() {
        animator.cancel();
    }

    /**
     * Sets the initial delay before the animation plays.
     * @param delay Delay time in milliseconds
     */
    public void setDelay(float delay) {
        animator.setDelay(Math.round(delay * 1000));
    }

    /**
     * Sets the easing function to use. Default functions are given in the {@code Easing.Default} enum.
     * @param easing Easing interface defining an easing function
     * @see Animator#setEasing(Easing) More information on easing functions
     * @see Easing.Default Default easing functions
     */
    public void setEasing(Easing easing) {
        animator.setEasing(easing);
    }

    /**
     * Returns the duration of this animation.
     * @return Duration in seconds
     */
    public float getDuration() {
        return animator.getDuration()/1000f;
    }

    /**
     * Defines whether the animation should loop.
     * @param loop If true, the animation will loop
     * @see Animator#loop(boolean) More information on looping
     */
    public void loop(boolean loop) {
        animator.loop(loop);
    }

    /**
     * @return true if the animation is set to run backward
     */
    public boolean isGoingBackward() {
        return animator.isGoingBackward();
    }

    /**
     * @return true if the animation is set to run forward
     */
    public boolean isGoingForward() {
        return animator.isGoingForward();
    }

    /**
     * Reverts the current direction while keeping the running state. That is, it won't start the animation it was
     * paused/stopped, and it won't stop it if it was running.
     */
    public void revert() {
        animator.revert();
    }

    /**
     * Setting this value to true on a looping animation will indicate the animation that it should run back only to the
     * start instead of looping backwards the same amount of times it looped forward.
     * @param backToStart if true, animation will only revert to starting point
     */
    public void setBackToStart(boolean backToStart) {
        animator.setBackToStart(backToStart);
    }

    /**
     * Determines whether the animation will return to the starting point on finishing or it will retain that state.
     * This only has effect on non-repeating animations.
     * @param b If true, animation will retain the ending state (fraction at 1)
     */
    public void freeze(boolean b) {
        animator.freeze(b);
    }

    /**
     * @return true if the animation is currently running
     */
    public boolean isRunning() {
        return animator.isRunning();
    }

    /**
     * This method is called upon the animation finishing or the {@code stop()} method being called. Useful for chaining
     * animations.
     */
    public void onAnimationFinished(){}

    protected void update(Dimension dim) {}
    protected void update(Point p) {}
    protected void update(int x) {}
    protected void update(double x) {}
}
