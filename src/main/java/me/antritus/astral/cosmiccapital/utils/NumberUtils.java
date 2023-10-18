package me.antritus.astral.cosmiccapital.utils;


import java.text.DecimalFormat;

public class NumberUtils {
	private static final DecimalFormat properDecimalFormat = new DecimalFormat(".00");
	public static String properFormat(double d){
		return properDecimalFormat.format(d);
	}
}
