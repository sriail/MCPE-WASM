#ifndef NET_MINECRAFT_CLIENT_GUI_COMPONENTS__UnifiedInventoryTabBar_H__
#define NET_MINECRAFT_CLIENT_GUI_COMPONENTS__UnifiedInventoryTabBar_H__

#include <vector>
#include <string>
#include "Button.h"
#include "NinePatch.h"

class Minecraft;
class Tesselator;

// Tab definitions for the unified inventory tab bar
enum InventoryTab {
	TAB_ITEMS = 0,
	TAB_CRAFT = 1,
	TAB_ARMOR = 2,
	TAB_COUNT = 3
};

class UnifiedInventoryTabBar {
public:
	UnifiedInventoryTabBar(Minecraft* minecraft);
	~UnifiedInventoryTabBar();

	void init(Minecraft* minecraft, int screenWidth, int screenHeight);
	void setupPositions(int screenWidth, int screenHeight);
	
	void setSelectedTab(int tabIndex);
	int getSelectedTab() const { return selectedTabIndex; }
	
	void render(Tesselator& t, Minecraft* minecraft, int xm, int ym);
	
	// Get buttons to add to screen's button vector
	std::vector<Button*>& getButtons() { return tabButtons; }
	
	// Check if a button is a tab button and handle selection
	bool isTabButton(Button* button);
	int getTabIndexForButton(Button* button);
	
	// Get the height of the tab bar for layout purposes
	int getTabBarHeight() const { return tabBarHeight; }
	
	static const int TAB_BUTTON_ID_START = 200;  // IDs 200-202
	static const int DEFAULT_TAB_BAR_HEIGHT = 24;  // Default height in pixels

private:
	void createTabButtons();
	
	std::vector<Button*> tabButtons;
	int selectedTabIndex;
	int tabBarHeight;
	int screenWidth;
	int screenHeight;
	
	// Nine-patch layers for tab rendering
	NinePatchLayer* tabNormal;
	NinePatchLayer* tabSelected;
	
	Minecraft* minecraft;
	
	class TabButton;
};

#endif /*NET_MINECRAFT_CLIENT_GUI_COMPONENTS__UnifiedInventoryTabBar_H__*/
