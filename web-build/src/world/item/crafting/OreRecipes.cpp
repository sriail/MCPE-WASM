#include "OreRecipes.h"
#include "Recipes.h"
#include "../../level/tile/Tile.h"
#include "../DyePowderItem.h"
#include <utility>

void OreRecipes::addRecipes(Recipes* r)
{
	typedef std::pair<Tile*, ItemInstance> Pair;
	Pair map[] = {
		std::make_pair(Tile::goldBlock,    ItemInstance(Item::goldIngot, 9)),
		std::make_pair(Tile::ironBlock,    ItemInstance(Item::ironIngot, 9)),
		std::make_pair(Tile::emeraldBlock, ItemInstance(Item::emerald, 9)),
		std::make_pair(Tile::lapisBlock,   ItemInstance(Item::dye_powder, 9, DyePowderItem::BLUE)),
		std::make_pair(Tile::greenEmeraldBlock, ItemInstance(Item::greenEmerald, 9)),
		std::make_pair(Tile::coalBlock,        ItemInstance(Item::coal, 9)),
		std::make_pair(Tile::redstoneBlock,    ItemInstance(Item::redStone, 9))
	};
	const int NumItems = sizeof(map) / sizeof(Pair);

	for (int i = 0; i < NumItems; i++) {
		Tile* from = map[i].first;
		ItemInstance to = map[i].second;

		r->addShapedRecipe(ItemInstance(from), //
			"###", //
			"###", //
			"###", //

			definition('#', to));

		r->addShapedRecipe(to, //
			"#", //

			definition('#', from));
	}

	// Slime block
	r->addShapedRecipe(ItemInstance(Tile::slimeBlock), //
		"###", //
		"###", //
		"###", //

		definition('#', Item::slimeBall));

	r->addShapedRecipe(ItemInstance(Item::slimeBall, 9), //
		"#", //

		definition('#', Tile::slimeBlock));

	// Hay bale
	r->addShapedRecipe(ItemInstance(Tile::hayBale), //
		"###", //
		"###", //
		"###", //

		definition('#', Item::wheat));

	//r->addShapedRecipe(ItemInstance(Item::goldIngot), //
	//	"###", //
	//	"###", //
	//	"###", //

	//	definition('#', Item::goldNugget));
	//r->addShapedRecipe(ItemInstance(Item::goldNugget, 9), //
	//    "#", //

	//  definition('#', Item::goldIngot));
}
