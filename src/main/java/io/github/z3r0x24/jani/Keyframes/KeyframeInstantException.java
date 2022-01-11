package io.github.z3r0x24.jani.Keyframes;

/**
 * This exception is thrown when a Keyframe instant (e.g: 0%, 25%, 3.5s, etc.) value is invalid or missing. The only
 * cases where this occurs normally are:
 * <ul>
 *     <li>The initial value is missing. That is to say, there's no 0% or 0s instant in the keyframe.</li>
 *     <li>The instant value is out of range (x > 100 or x < 0 for percentage units, x < 0 for second units).</li>
 * </ul>
 * While second units can't check the upper boundary due to the duration of the animation being unknown to the keyframes
 * object, and it is technically possible to set an instant residing outside it (e.g: if the animation duration is 5s
 * and a 7s instant is given), this is discouraged, as it is less predictable in terms of how the animation will look.
 * Try adjusting the animation duration and keyframes instead.
 */
public class KeyframeInstantException extends RuntimeException {
    public KeyframeInstantException(String message) {
        super(message);
    }
}
