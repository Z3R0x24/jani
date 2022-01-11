# jAni - A Java animation library
jAni is designed to be light and easy to use. Using animations is the best way to make your Swing application more lively!

## Including jAni in your project
jAni is compatible with Java 8 and up. It is available on Maven central:

```xml
<dependency>
    <groupId>io.github.z3r0x24</groupId>
    <artifactId>jAni</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Features
jAni is able to:
* Use key frames to create animations (in a similar fashion to CSS).
* Play, pause, rewind, loop, chain animations easily.
* Use easing functions to make your animation look better with little effort.
* Utilize a user defined framerate.
* Frame skip so you animations won't hurt the user experience as much on slower devices.

## Usage
To get started using jAni simply refer to the [wiki](https://github.com/Z3R0x24/jani/wiki) for usage instructions and detailed information about most classes (currently a WIP).

## Example code
You can find a very simple example of what this library can do in the [Test class](https://github.com/Z3R0x24/jani/blob/main/src/main/java/io/github/z3r0x24/jani/Test.java) inside the library.

## Contribute
If you find any problems or have a suggestion feel free to submit an issue! I'll be happy to look into it when I have the time, same goes for pull requests if you'd like to contribute more directly.
Here is a small list of ideas that might make it into the library later on:
* Parse multiple key frame data from a plain text file and store it by label.
* Make `Animation` capable of interpolate points or dimensions using different easing functions (currently possible with custom logic using `Animator`, but not built-in).
* Create a better demo.

## License
This project is licensed under the [MIT License](https://github.com/Z3R0x24/jani/blob/main/LICENSE).
