package me.antritus.astral.cosmiccapital.api;


import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Antritus
 * @since 1.0-SNAPSHOT
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@ApiStatus.Experimental()
public @interface CreatesEntry {
	boolean createsEntry() default false;
}
