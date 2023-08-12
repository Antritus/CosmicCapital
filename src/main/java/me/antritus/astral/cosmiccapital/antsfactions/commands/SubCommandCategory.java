package me.antritus.astral.cosmiccapital.antsfactions.commands;

public enum SubCommandCategory {
	ADMIN("admin"),
	SETTINGS("settings"),
	UNKNOWN(),

	PLAYER("player"),
	CONSOLE("console"),
	;
	private final String[] categories;
	SubCommandCategory(String... types) {
		this.categories = types;
	}

	public String[] getCategories() {
		return categories;
	}
}
