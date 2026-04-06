#include "UnifiedInventoryTabBar.h"
#include "ImageButton.h"
#include "../../Minecraft.h"
#include "../../renderer/Tesselator.h"
#include "../../renderer/Textures.h"
#include "../../../locale/I18n.h"

// Custom tab button that shows selected state with lighter background
class UnifiedInventoryTabBar::TabButton : public ImageButton {
	typedef ImageButton super;
public:
	TabButton(int id, int* selectedIndexPtr, int tabIndex, NinePatchLayer* stateNormal, NinePatchLayer* stateSelected, const std::string& label)
	:	super(id, label),
		selectedIndexPtr(selectedIndexPtr),
		tabIndex(tabIndex),
		stateNormal(stateNormal),
		stateSelected(stateSelected)
	{}

	void renderBg(Minecraft* minecraft, int xm, int ym) override {
		bool isSelected = (*selectedIndexPtr == tabIndex);
		
		if (isSelected)
			stateSelected->draw(Tesselator::instance, (float)x, (float)y);
		else
			stateNormal->draw(Tesselator::instance, (float)x, (float)y);
	}
	
	bool isSecondImage(bool hovered) override { return false; }

private:
	int* selectedIndexPtr;
	int tabIndex;
	NinePatchLayer* stateNormal;
	NinePatchLayer* stateSelected;
};

UnifiedInventoryTabBar::UnifiedInventoryTabBar(Minecraft* minecraft)
:	selectedTabIndex(TAB_ITEMS),
	tabBarHeight(24),
	screenWidth(0),
	screenHeight(0),
	tabNormal(NULL),
	tabSelected(NULL),
	minecraft(minecraft)
{
}

UnifiedInventoryTabBar::~UnifiedInventoryTabBar() {
	for (unsigned int i = 0; i < tabButtons.size(); ++i)
		delete tabButtons[i];
	
	delete tabNormal;
	delete tabSelected;
}

void UnifiedInventoryTabBar::init(Minecraft* minecraft, int screenWidth, int screenHeight) {
	this->minecraft = minecraft;
	this->screenWidth = screenWidth;
	this->screenHeight = screenHeight;
	
	// Create nine-patch layers for tab backgrounds
	NinePatchFactory builder(minecraft->textures, "gui/spritesheet.png");
	
	// Normal tab: darker gray
	tabNormal = builder.createSymmetrical(IntRectangle(8, 32, 8, 8), 2, 2);
	
	// Selected tab: lighter color using inventory palette
	tabSelected = builder.createSymmetrical(IntRectangle(0, 32, 8, 8), 2, 2);
	
	createTabButtons();
}

void UnifiedInventoryTabBar::createTabButtons() {
	// Clear existing buttons
	for (unsigned int i = 0; i < tabButtons.size(); ++i)
		delete tabButtons[i];
	tabButtons.clear();
	
	// Create three tab buttons: Items, Craft, Armor
	const char* tabLabels[] = { "Items", "Craft", "Armor" };
	
	for (int i = 0; i < TAB_COUNT; ++i) {
		TabButton* btn = new TabButton(
			TAB_BUTTON_ID_START + i,
			&selectedTabIndex,
			i,
			tabNormal,
			tabSelected,
			tabLabels[i]
		);
		tabButtons.push_back(btn);
	}
}

void UnifiedInventoryTabBar::setupPositions(int screenWidth, int screenHeight) {
	this->screenWidth = screenWidth;
	this->screenHeight = screenHeight;
	
	// Position tabs horizontally across the top
	// Leave room for close button on the right (19px)
	const int closeButtonWidth = 19;
	const int availableWidth = screenWidth - closeButtonWidth;
	const int tabWidth = availableWidth / TAB_COUNT;
	
	for (int i = 0; i < TAB_COUNT; ++i) {
		Button* btn = tabButtons[i];
		btn->x = i * tabWidth;
		btn->y = 0;
		btn->width = tabWidth;
		btn->height = tabBarHeight;
	}
}

void UnifiedInventoryTabBar::setSelectedTab(int tabIndex) {
	if (tabIndex >= 0 && tabIndex < TAB_COUNT) {
		selectedTabIndex = tabIndex;
	}
}

void UnifiedInventoryTabBar::render(Tesselator& t, Minecraft* minecraft, int xm, int ym) {
	// Buttons will be rendered by the Screen::render() method
	// This method is here for future custom rendering if needed
}

bool UnifiedInventoryTabBar::isTabButton(Button* button) {
	if (!button) return false;
	return (button->id >= TAB_BUTTON_ID_START && button->id < TAB_BUTTON_ID_START + TAB_COUNT);
}

int UnifiedInventoryTabBar::getTabIndexForButton(Button* button) {
	if (!isTabButton(button)) return -1;
	return button->id - TAB_BUTTON_ID_START;
}
