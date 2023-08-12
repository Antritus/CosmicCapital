package me.antritus.astral.cosmiccapital.antsfactions.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {
	String name();
	String[] aliases() default "";
	String permission() default "";
	String description();
	SenderType sender();
	SubCommandCategory[] category();
}
