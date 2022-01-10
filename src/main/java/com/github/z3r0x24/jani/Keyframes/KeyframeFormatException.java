package com.github.z3r0x24.jani.Keyframes;

/**
 * This exception is thrown when the provided keyframe string is improperly formatted.
 */
public class KeyframeFormatException extends RuntimeException {
    public KeyframeFormatException(String message) {
        super(message);
    }
}
