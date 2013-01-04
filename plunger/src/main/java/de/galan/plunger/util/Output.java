package de.galan.plunger.util;

import static org.fusesource.jansi.Ansi.*;

import org.fusesource.jansi.Ansi.Color;


/**
 * Prints output to the stdout/err, colors can be used via AnsiConsole
 * 
 * @author daniel
 */
public class Output {

	private static boolean colors = true;


	public static void setColor(boolean colors) {
		Output.colors = colors;
	}


	public static void print(Color color, String text) {
		System./**/out.print(colors ? ansi().fg(color).a(text).reset() : text);
	}


	public static void println(Color color, String text) {
		System./**/out.println(colors ? ansi().fg(color).a(text).reset() : text);
	}


	public static void print(String line) {
		System/**/.out.print(line);
	}


	public static void println(String line) {
		System/**/.out.println(line);
	}


	public static void error(String text) {
		System./**/err.println(colors ? ansi().fg(Color.RED).a(text).reset() : text);
	}

}
