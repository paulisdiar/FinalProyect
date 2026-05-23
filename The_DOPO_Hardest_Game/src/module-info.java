module The_DOPO_Hardest_Game {
	requires java.desktop;
	requires junit;
	exports Controller;
	exports View;
	exports model;
	exports editor;
	opens test to junit;
}
