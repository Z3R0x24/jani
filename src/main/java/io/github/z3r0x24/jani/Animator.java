package io.github.z3r0x24.jani;

import javax.swing.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The animator class provides the ticking function to perform updates and takes care of interpolating the fraction
 * value using the easing function provided to generate the in-between values.
 */
public abstract class Animator {
    private static final ScheduledExecutorService scheduler = createScheduler();
    private static Thread timerAccuracyThread = null;

    private static int FPSTarget = 60;
    private static boolean frameSkip = true;

    private int duration;
    private boolean loops;
    private int delay;
    private volatile Easing easing;
    private volatile boolean reverse = false;

    protected ScheduledFuture<?> ticker;

    protected volatile float fraction;
    protected float fractionDelta;
    protected int expectedDelay;
    protected long lastUpdateTime;

    private volatile boolean running = false;
    private volatile boolean backToStart = false;
    private volatile boolean freeze = false;

    public Animator(int durationMillis, int delayMillis) {
        this(durationMillis, delayMillis, false);
    }

    public Animator(int durationMillis, int delayMillis, boolean loops) {
        this(durationMillis, delayMillis, loops, Easing.Default.LINEAR);
    }

    public Animator(int durationMillis, int delayMillis, boolean loops, Easing easing) {
        this.duration = durationMillis;
        this.delay = delayMillis;
        this.loops = loops;
        this.easing = easing;
    }

    // For some ungodly reason, delay accuracy in Windows is awful (off by about 10 - 15 ms) unless I do this
    private static void initAccuracyThread() {
        if (!System.getProperty("os.name").startsWith("Win") || timerAccuracyThread != null)
            return;

        timerAccuracyThread = new Thread(() -> {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (Exception ignored) {}
        });

        timerAccuracyThread.setName("Timer accuracy thread");
        timerAccuracyThread.setDaemon(true);
        timerAccuracyThread.start();
    }

    /**
     * Plays the animation in whichever direction it was set before calling this method.
     */
    public void play() {
        play(true);
    }

    /**
     * Plays the animation in whichever direction it was set before calling this method.
     * @param skipDelay If true, will skip the initial delay.
     */
    public void play(boolean skipDelay) {
        if (ticker == null) {
            if (freeze && fraction == 1)
                cancel();

            initAccuracyThread();

            running = true;

            expectedDelay = 1000 / FPSTarget;
            int delayTime = skipDelay ? 0 : delay;

            fractionDelta = 1f/(FPSTarget * duration /1000f);

            ticker = scheduler.scheduleWithFixedDelay(new Runnable() {
                final AtomicBoolean updateScheduled = new AtomicBoolean(false);

                @Override
                public void run() {
                    if (!updateScheduled.get()) {
                        tick();

                        final float frameFraction = fraction;

                        SwingUtilities.invokeLater(()-> {
                            updateScheduled.set(true);
                            update(easing.apply(frameFraction));
                            updateScheduled.set(false);
                        });

                        if (running)
                            lastUpdateTime = System.currentTimeMillis();
                    }
                }
            }, delayTime, expectedDelay, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Plays the animation ensuring it will go forward.
     */
    public void forward() {
        forward(true);
    }

    /**
     * Plays the animation ensuring it will go forward.
     * @param skipDelay If true, will skip the initial delay.
     */
    public void forward(boolean skipDelay) {
        if (reverse || !running) {
            pause();
            reverse = false;
            play(skipDelay);
        }
    }

    /**
     * Plays the animation ensuring it will go backward.
     */
    public void backward() {
        backward(true);
    }

    /**
     * Plays the animation ensuring it will go backward.
     * @param skipDelay If true, will skip the initial delay.
     */
    public void backward(boolean skipDelay) {
        if (!reverse || !running) {
            pause();
            reverse = true;
            play(skipDelay);
        }
    }

    /**
     * Pauses the animation. Can be resumed.
     */
    public void pause() {
        if (ticker != null) {
            ticker.cancel(false);
            ticker = null;
            running = false;
            lastUpdateTime = 0;
        }
    }

    /**
     * Stops the animation setting it back to the beginning and calls {@code onAnimationFinished()}.
     * @see Animator#cancel()
     */
    public void stop() {
        cancel();
        onAnimationFinished();
    }

    /**
     * Stops the animation without calling {@code onAnimationFinished()}.
     * @see Animator#stop()
     */
    public void cancel() {
        pause();
        fraction = 0;
        SwingUtilities.invokeLater(() -> update(0));
    }

    /**
     * Ticker function. Updates the fraction by adding the appropriate delta value to it. Frame skip is done by dividing
     * the actual delay (since last frame) by the expected delay, and multiplying the fraction delta by this value.
     */
    protected void tick() {
        double multiplier;

        if (lastUpdateTime == 0 || !frameSkip)
            multiplier = 1;
        else {
            long time = System.currentTimeMillis();
            multiplier = (double) (time - lastUpdateTime) / expectedDelay;
        }

        final float oldFraction = fraction;

        if (reverse)
            fraction = oldFraction - fractionDelta * (float) multiplier;
        else
            fraction = oldFraction + fractionDelta * (float) multiplier;

        if (loops) {
            if (backToStart && fraction <= 0) {
                stop();
            }
            loop0();
        } else if (fraction > 1) {
            fraction = 1;

            if (freeze) {
                pause();
                onAnimationFinished();
            } else
                stop();
        } else if (fraction < 0) {
            stop();
        }
    }

    // Loops the fraction
    private void loop0() {
        final float fractionMod = fraction % 1;

        fraction = fractionMod;

        if (fraction < 0)
            fraction = 1 - fractionMod;
    }

    /**
     * Override this method to update (repaint) the component you want to animate. The easing function is pre-applied on
     * the fraction.
     * @param fraction A value between 0 and 1 that determines the current progress of the animation
     */
    public abstract void update(float fraction);

    /**
     * This method is called upon the animation finishing or the {@code stop()} method being called. Useful for chaining
     * animations.
     */
    protected void onAnimationFinished(){}

    /**
     * Sets the initial delay before the animation plays.
     * @param delay Delay time in milliseconds
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Returns this animation's initial delay.
     * @return Delay in milliseconds
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the easing function to use. Default functions are given in the {@code Easing.Default} enum. All easing
     * functions should have a min value of 0 and a max value of 1. More specifically: <br>
     * <br>
     * <b>f(x)</b> =
     * <table summary="">
     *     <tr>
     *         <td>0 ;</td>
     *         <td>x = 0</td>
     *     </tr>
     *     <tr>
     *         <td>f(x) ;</td>
     *         <td>0 &lt; x &lt; 1</td>
     *     </tr>
     *     <tr>
     *         <td>1 ;</td>
     *         <td>x = 1</td>
     *     </tr>
     * </table>
     * @param easing Easing interface defining an easing function.
     * @see Easing.Default Default easing functions
     */
    public void setEasing(Easing easing) {
        this.easing = easing;
    }

    /**
     * Sets the duration of the animation. Effective after pausing/stopping and playing again.
     * @param duration Animation duration in milliseconds
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Returns the duration of this animation.
     * @return Duration in milliseconds
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Defines whether the animation should loop. Note that looping is different to restarting on finish: looping
     * implies the animation doesn't reach and endpoint. For starters, {@code onAnimationFinished()} won't be called
     * until {@code stop()} is called manually. Looping also supports frame skip while restart does not; if an animation
     * is set to restart itself after finishing (via {@code onAnimationFinished(){this.play()}}), it will frame skip to
     * the end at max, and start again after it is able to perform the last tick.
     * @param loop If true, the animation will loop
     */
    public void loop(boolean loop) {
        this.loops = loop;
    }

    /**
     * @return true if the animation is set to loop
     */
    public boolean isLoop() {
        return loops;
    }

    /**
     * Gets the global FPS target for the animations.
     * @return Global FPS target
     */
    public static int getGlobalFPSTarget() {
        return FPSTarget;
    }

    /**
     * Sets the global FPS target for the animations. Since the animator runs on millisecond resolution, there won't be
     * much granularity (e.g: At 1ms delay, we theoretically get 1000 FPS; at 2 ms delay, we get 500; there's no way to
     * get, say, 750 FPS). While using nanosecond delay would solve this issue, it seems unnecessary given the scope of
     * this library. Takes effect on all animations that start after this function is called; animations currently
     * running need to be restarted (call {@code pause()} then {@code play()}) for this to take effect.
     * @param FPSTarget FPS target for all animations
     */
    public static void setGlobalFPSTarget(int FPSTarget) {
        Animator.FPSTarget = FPSTarget;
    }

    /**
     * Enables or disables frame skipping. Animations may take longer to complete than anticipated if frame skip is
     * disabled, overall on older devices that cannot keep up with the rendering. Takes effect immediately after being
     * called.
     * @param fs New value for frame skip for all animations
     */
    public static void setFrameSkip(boolean fs) {
        frameSkip = fs;
    }

    /**
     * Indicates if frame skipping is enabled.
     * @return Current frame skip value
     */
    public static boolean isFrameSkipEnabled() {
        return frameSkip;
    }

    /**
     * @return true if the animation is set to run backward
     */
    public boolean isGoingBackward() {
        return reverse;
    }

    /**
     * @return true if the animation is set to run forward
     */
    public boolean isGoingForward() {
        return !reverse;
    }

    /**
     * Reverts the current direction while keeping the running state. That is, it won't start the animation it was
     * paused/stopped, and it won't stop it if it was running.
     */
    public void revert() {
        if (running) {
            if (reverse)
                forward();
            else
                backward();
        } else {
            // Just so IntelliJ won't yell at me for performing a non-atomic operation on a volatile variable.
            boolean temp = reverse;
            this.reverse = !temp;
        }
    }

    /**
     * Setting this value to true on a looping animation will indicate the animation that it should run back only to the
     * start instead of looping backwards the same amount of times it looped forward.
     * @param backToStart if true, animation will only revert to starting point
     */
    public void setBackToStart(boolean backToStart) {
        this.backToStart = backToStart;
    }

    /**
     * @return true if the animation is set to run back to start only
     */
    public boolean isBackToStart() {
        return backToStart;
    }

    /**
     * Determines whether the animation will return to the starting point on finishing or it will retain that state.
     * This only has effect on non-repeating animations.
     * @param b If true, animation will retain the ending state (fraction at 1)
     */
    public void freeze(boolean b) {
        this.freeze = b;
    }

    /**
     * @return true if this animation is set to freeze
     */
    public boolean isFrozen() {
        return this.freeze;
    }

    /**
     * @return true if the animation is currently running
     */
    public boolean isRunning() {
        return running;
    }

    private static ScheduledExecutorService createScheduler() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, r -> {
            final Thread thread = new Thread(r, "Animator Thread");
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            return thread;
        });
        executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        return executor;
    }
}
