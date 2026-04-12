#include "TileInclude.h"
#include "../Level.h"
#include "../../entity/player/Player.h"
#include "../../entity/item/ItemEntity.h"
#include "../../item/Item.h"
#include "../../item/TileItem.h"

#include "../../../util/Random.h"
//#include "locale/Descriptive.h"
//#include "stats/Stats.h"
#include "../../entity/Entity.h"
#include "../LevelSource.h"
#include "../material/Material.h"
#include "../../phys/AABB.h"
#include "../../phys/HitResult.h"
#include "../../phys/Vec3.h"
#include "../../../locale/I18n.h"
#include "../../item/ClothTileItem.h"

#include "../../item/AuxDataTileItem.h"
#include "../../item/LeafTileItem.h"
#include "../../item/StoneSlabTileItem.h"
#include "../../item/SaplingTileItem.h"
#include "../../item/ItemCategory.h"

const int Tile::RENDERLAYER_OPAQUE          = 0;
const int Tile::RENDERLAYER_ALPHATEST       = 1;
const int Tile::RENDERLAYER_BLEND           = 2;

const std::string Tile::TILE_DESCRIPTION_PREFIX("tile.");

const Tile::SoundType Tile::SOUND_NORMAL("stone", 1, 1);
const Tile::SoundType Tile::SOUND_WOOD("wood", 1, 1);
const Tile::SoundType Tile::SOUND_GRAVEL("gravel", 1, 1);
const Tile::SoundType Tile::SOUND_GRASS("grass", 0.5f, 1);
const Tile::SoundType Tile::SOUND_STONE("stone", 1, 1);
const Tile::SoundType Tile::SOUND_METAL("stone", 1, 1.5f);
const Tile::SoundType Tile::SOUND_GLASS("stone", "random.glass", 1, 1);
const Tile::SoundType Tile::SOUND_CLOTH("cloth", 1, 1);

#ifdef PRE_ANDROID23
	const Tile::SoundType Tile::SOUND_SAND("sand", 0.45f, 1);
#else
	const Tile::SoundType Tile::SOUND_SAND("sand", "step.gravel", 1, 1);
#endif

const Tile::SoundType Tile::SOUND_SILENT("", 0, 0);

Tile* Tile::tiles[] = {NULL};
int Tile::lightBlock[] = {0};
int Tile::lightEmission[] = {0};
bool Tile::solid[] = {false};
bool Tile::isEntityTile[] = {false};
bool Tile::translucent[] = {true, false}; // @trans: translucent, @trans "asbMax", some more like "*conditon"
bool Tile::shouldTick[] = {false};
bool Tile::sendTileData[] = {false};

Tile* Tile::sand        = NULL;
Tile* Tile::sandStone   = NULL;
const int SANDSTONE_TEXTURES[] = { 0 + 16 * 12, 5 + 16 * 14, 6 + 16 * 14 };
const int SANDSTONE_TEXTURE_COUNT = 3;

Tile* Tile::stoneBrick  = NULL;
Tile* Tile::redBrick    = NULL;
Tile* Tile::wood        = NULL;
Tile* Tile::sapling     = NULL;
Tile* Tile::glass       = NULL;
Tile* Tile::web	        = NULL;
Tile* Tile::thinGlass   = NULL;
Tile* Tile::calmWater   = NULL;
Tile* Tile::calmLava    = NULL;
Tile* Tile::gravel      = NULL;
Tile* Tile::rock        = NULL;
Tile* Tile::unbreakable = NULL;
Tile* Tile::dirt        = NULL;
Tile* Tile::grass       = NULL;
Tile* Tile::ice         = NULL;
Tile* Tile::clay        = NULL;
Tile* Tile::farmland    = NULL;
Tile* Tile::stoneSlab   = NULL;
Tile* Tile::stoneSlabHalf=NULL;
Tile* Tile::cloth       = NULL;
Tile* Tile::flower      = NULL;
Tile* Tile::rose        = NULL;
Tile* Tile::mushroom1   = NULL;
Tile* Tile::mushroom2   = NULL;
Tile* Tile::topSnow     = NULL;
Tile* Tile::treeTrunk   = NULL;
Tile* Tile::snow        = NULL;
LeafTile* Tile::leaves  = NULL;
Tile* Tile::emeraldOre  = NULL;
Tile* Tile::redStoneOre = NULL;
Tile* Tile::redStoneOre_lit = NULL;
Tile* Tile::goldOre     = NULL;
Tile* Tile::ironOre     = NULL;
Tile* Tile::coalOre     = NULL;
Tile* Tile::lapisOre    = NULL;
Tile* Tile::lapisBlock	= NULL;
Tile* Tile::reeds       = NULL;
Tile* Tile::ladder      = NULL;
Tile* Tile::obsidian    = NULL;
Tile* Tile::tnt         = NULL;
Tile* Tile::bookshelf	= NULL;
Tile* Tile::sign        = NULL;
Tile* Tile::wallSign    = NULL;
Tile* Tile::mossStone	= NULL;
Tile* Tile::torch       = NULL;
Tile* Tile::water       = NULL;
Tile* Tile::lava        = NULL;
FireTile* Tile::fire    = NULL;
Tile* Tile::invisible_bedrock = NULL;
Tile* Tile::goldBlock   = NULL;
Tile* Tile::ironBlock   = NULL;
Tile* Tile::emeraldBlock= NULL;
Tile* Tile::workBench   = NULL;
Tile* Tile::stonecutterBench = NULL;
Tile* Tile::crops		= NULL;
Tile* Tile::furnace     = NULL;
Tile* Tile::furnace_lit = NULL;
Tile* Tile::chest		= NULL;
Tile* Tile::lightGem    = NULL;
Tile* Tile::stairs_wood = NULL;
Tile* Tile::stairs_stone= NULL;
Tile* Tile::stairs_brick= NULL;
Tile* Tile::door_wood   = NULL;
Tile* Tile::door_iron   = NULL;
Tile* Tile::cactus      = NULL;

Tile* Tile::melon       = NULL;
Tile* Tile::melonStem   = NULL;

Tile* Tile::bed         = NULL;
Tile* Tile::tallgrass   = NULL;
Tile* Tile::trapdoor    = NULL;
Tile* Tile::stoneBrickSmooth = NULL;
const int STONE_BRICK_TEXTURES[] = { 6 + 16 * 3, 4 + 16 * 6, 5 + 16 * 6 };
const int STONE_BRICK_TEXTURE_COUNT = 3;

Tile* Tile::fence       = NULL;
Tile* Tile::fenceGate   = NULL;

Tile* Tile::info_updateGame1 = NULL;
Tile* Tile::info_updateGame2 = NULL;
Tile* Tile::info_reserved6   = NULL;
Tile* Tile::grass_carried    = NULL;
LeafTile* Tile::leaves_carried   = NULL;

Tile* Tile::netherReactor = NULL;
Tile* Tile::glowingObsidian = NULL;

Tile* Tile::stairs_stoneBrickSmooth   = NULL;
Tile* Tile::netherBrick   = NULL;
Tile* Tile::netherrack   = NULL;
Tile* Tile::stairs_netherBricks   = NULL;
Tile* Tile::stairs_sandStone   = NULL;
Tile* Tile::quartzBlock   = NULL;
Tile* Tile::stairs_quartz   = NULL;

Tile* Tile::stoneVariant = NULL;
Tile* Tile::fence_spruce = NULL;
Tile* Tile::fence_birch = NULL;
Tile* Tile::fence_jungle = NULL;
Tile* Tile::fenceGate_spruce = NULL;
Tile* Tile::fenceGate_birch = NULL;
Tile* Tile::fenceGate_jungle = NULL;
Tile* Tile::stairs_spruce = NULL;
Tile* Tile::stairs_birch = NULL;
Tile* Tile::stairs_jungle = NULL;
Tile* Tile::door_spruce = NULL;
Tile* Tile::door_birch = NULL;
Tile* Tile::door_jungle = NULL;
Tile* Tile::trapdoor_spruce = NULL;
Tile* Tile::trapdoor_birch = NULL;
Tile* Tile::trapdoor_jungle = NULL;
Tile* Tile::sign_spruce = NULL;
Tile* Tile::wallSign_spruce = NULL;
Tile* Tile::sign_birch = NULL;
Tile* Tile::wallSign_birch = NULL;
Tile* Tile::sign_jungle = NULL;
Tile* Tile::wallSign_jungle = NULL;
Tile* Tile::stairs_granite = NULL;
Tile* Tile::stairs_polishedGranite = NULL;
Tile* Tile::stairs_diorite = NULL;
Tile* Tile::stairs_polishedDiorite = NULL;
Tile* Tile::stairs_andesite = NULL;
Tile* Tile::stairs_polishedAndesite = NULL;
Tile* Tile::wall_granite = NULL;
Tile* Tile::wall_diorite = NULL;
Tile* Tile::wall_andesite = NULL;
Tile* Tile::wall_sandstone = NULL;
Tile* Tile::wall_netherBrick = NULL;
Tile* Tile::stairs_smoothSandstone = NULL;
Tile* Tile::jungleLog = NULL;
LeafTile* Tile::jungleLeaves = NULL;
Tile* Tile::jungleSapling = NULL;
Tile* Tile::redstoneLamp = NULL;
Tile* Tile::redstoneLamp_lit = NULL;
Tile* Tile::redstoneBlock = NULL;
Tile* Tile::piston = NULL;
Tile* Tile::stickyPiston = NULL;
Tile* Tile::comparator_off = NULL;
Tile* Tile::comparator_on = NULL;
Tile* Tile::greenEmeraldOre = NULL;
Tile* Tile::greenEmeraldBlock = NULL;
Tile* Tile::netherBrickFence = NULL;
Tile* Tile::hellSand = NULL;
Tile* Tile::netherQuartzOre = NULL;
Tile* Tile::smoothStone = NULL;
Tile* Tile::hayBale = NULL;
Tile* Tile::slimeBlock = NULL;
Tile* Tile::coalBlock = NULL;
Tile* Tile::glassPaneTile = NULL;
Tile* Tile::cocoa = NULL;
Tile* Tile::stoneSlab2 = NULL;
Tile* Tile::stoneSlabHalf2 = NULL;
Tile* Tile::diode_off = NULL;
Tile* Tile::diode_on = NULL;

/*static*/
void Tile::initTiles() {
	rock        = (new StoneTile(1, 1))->init()->setDestroyTime(1.5f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stone");
	grass       = (GrassTile*) (new GrassTile(2))->init()->setDestroyTime(0.6f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("grass");
	dirt        = (new DirtTile(3, 2))->init()->setDestroyTime(0.5f)->setSoundType(SOUND_GRAVEL)->setCategory(ItemCategory::Structures)->setDescriptionId("dirt");
	stoneBrick  = (new Tile(4, 16, Material::stone))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stonebrick");
	wood        = (new WoodPlanks(5))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("wood");
	sapling     = (new Sapling(6, 15))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("sapling");//->sendTileData();
	unbreakable = (new Tile(7, 17, Material::stone))->init()->setDestroyTime(-1)->setExplodeable(6000000)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("bedrock");
	water       = (new LiquidTileDynamic(8, Material::water))->init()->setDestroyTime(100.0f)->setLightBlock(3)->setCategory(ItemCategory::Structures)->setDescriptionId("water");
	calmWater   = (new LiquidTileStatic(9, Material::water))->init()->setDestroyTime(100.0f)->setLightBlock(3)->setCategory(ItemCategory::Structures)->setDescriptionId("water");
	lava        = (new LiquidTileDynamic(10, Material::lava))->init()->setDestroyTime(00.0f)->setLightEmission(1.0f)->setLightBlock(255)->setCategory(ItemCategory::Structures)->setDescriptionId("lava"); // 00.0?
	calmLava    = (new LiquidTileStatic(11, Material::lava))->init()->setDestroyTime(100.0f)->setLightEmission(1.0f)->setLightBlock(255)->setCategory(ItemCategory::Structures)->setDescriptionId("lava");
	sand        = (new HeavyTile(12, 18))->init()->setDestroyTime(0.5f)->setSoundType(SOUND_SAND)->setCategory(ItemCategory::Structures)->setDescriptionId("sand");
	gravel      = (new GravelTile(13, 19))->init()->setDestroyTime(0.6f)->setSoundType(SOUND_GRAVEL)->setCategory(ItemCategory::Structures)->setDescriptionId("gravel");
	goldOre     = (new OreTile(14, 32))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Decorations)->setDescriptionId("oreGold");
	ironOre     = (new OreTile(15, 33))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Decorations)->setDescriptionId("oreIron");
	coalOre     = (new OreTile(16, 34))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("oreCoal");
	treeTrunk   = (new TreeTile(17))->init()->setDestroyTime(2.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("log");
	leaves      = (LeafTile*) (new LeafTile(18, 4 + 3 * 16))->init()->setDestroyTime(0.2f)->setLightBlock(1)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("leaves");

	glass       = (new GlassTile(20, 49, Material::glass, false))->init()->setDestroyTime(0.3f)->setSoundType(SOUND_GLASS)->setCategory(ItemCategory::Structures)->setDescriptionId("glass");
	lapisOre    = (new OreTile(21, 10 * 16))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("oreLapis");
	lapisBlock	= (new Tile(22, 9 * 16, Material::stone))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Decorations)->setDescriptionId("blockLapis");
	sandStone   = (new SandStoneTile(24, (const int*)&SANDSTONE_TEXTURES, SANDSTONE_TEXTURE_COUNT))->init()->setSoundType(SOUND_STONE)->setDestroyTime(0.8f)->setCategory(ItemCategory::Structures)->setDescriptionId("sandStone");
	bed         = (new BedTile(26))->init()->setDestroyTime(0.2f)->setCategory(ItemCategory::Structures)->setDescriptionId("bed");
	web	        = (new WebTile(30, 11))->init()->setLightBlock(1)->setDestroyTime(4.0f)->setCategory(ItemCategory::Decorations)->setDescriptionId("web");
	tallgrass   = (new TallGrass(31, 2 * 16 + 7))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Decorations)->setDescriptionId("tallgrass");
	cloth       = (new ClothTile(35))->init()->setDestroyTime(0.8f)->setSoundType(SOUND_CLOTH)->setCategory(ItemCategory::Structures)->setDescriptionId("cloth");

	flower      = (new Bush(37, 13))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Decorations)->setDescriptionId("flower");
	rose        = (new Bush(38, 12))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Decorations)->setDescriptionId("rose");
	mushroom1   = (new Mushroom(39, 13 + 16))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setLightEmission(2 / 16.0f)->setCategory(ItemCategory::Decorations)->setDescriptionId("mushroom");
	mushroom2   = (new Mushroom(40, 12 + 16))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Decorations)->setDescriptionId("mushroom");
	goldBlock   = (new MetalTile(41, 39 - 16))->init()->setDestroyTime(3.0f)->setExplodeable(10)->setSoundType(SOUND_METAL)->setCategory(ItemCategory::Decorations)->setDescriptionId("blockGold");
	ironBlock   = (new MetalTile(42, 38 - 16))->init()->setDestroyTime(5.0f)->setExplodeable(10)->setSoundType(SOUND_METAL)->setCategory(ItemCategory::Decorations)->setDescriptionId("blockIron");
	stoneSlab   = (new StoneSlabTile(43, true))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stoneSlab");
	stoneSlabHalf=(new StoneSlabTile(44, false))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stoneSlab");
	redBrick    = (new Tile(45, 7, Material::stone))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("brick");
	tnt         = (new TntTile(46, 8))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Tools)->setDescriptionId("tnt");
	bookshelf	= (new BookshelfTile(47, 35))->init()->setDestroyTime(1.5f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("bookshelf");
	mossStone	= (new Tile(48, 36, Material::stone))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stoneMoss");
	obsidian    = (new ObsidianTile(49, 37, false))->init()->setDestroyTime(10.0f)->setExplodeable(2000)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("obsidian");
	torch       = (new TorchTile(50, 5 * 16))->init()->setDestroyTime(0.0f)->setLightEmission(15 / 16.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Tools)->setDescriptionId("torch");

	stairs_wood = (new StairTile(53, wood))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsWood");
	chest		= (new ChestTile(54))->init()->setCategory(ItemCategory::Structures)->setDestroyTime(2.5f)->setSoundType(SOUND_WOOD)->setDescriptionId("chest");//->sendTileData();

	emeraldOre  = (new OreTile(56, 16 * 3 + 2))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Decorations)->setDescriptionId("oreDiamond");
	emeraldBlock= (new MetalTile(57, 40 - 16))->init()->setDestroyTime(5.0f)->setExplodeable(10)->setSoundType(SOUND_METAL)->setCategory(ItemCategory::Decorations)->setDescriptionId("blockDiamond");
	workBench   = (new WorkbenchTile(58))->init()->setDestroyTime(2.5f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("workbench");
	crops		= (new CropTile(59, 8 + 5 * 16))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("crops");//->sendTileData();
	farmland    = (new FarmTile(60))->init()->setDestroyTime(0.6f)->setSoundType(SOUND_GRAVEL)->setCategory(ItemCategory::Structures)->setDescriptionId("farmland");
	furnace		= (new FurnaceTile(61, false))->init()->setDestroyTime(3.5f)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("furnace");//.sendTileData();
	furnace_lit = (new FurnaceTile(62, true))->init()->setDestroyTime(3.5f)->setSoundType(SOUND_STONE)->setLightEmission(14 / 16.0f)->setCategory(ItemCategory::Structures)->setDescriptionId("furnace");//.sendTileData();
    sign        = (new SignTile(63, TileEntityType::Sign, true))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("sign");//->sendTileData();
	door_wood   = (new DoorTile(64, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("doorWood");
	ladder      = (new LadderTile(65, 3 + 5 * 16))->init()->setDestroyTime(0.4f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("ladder");

	stairs_stone= (new StairTile(67, stoneBrick))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsStone");
    wallSign    = (new SignTile(68, TileEntityType::Sign, false))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("sign");//->sendTileData();

	door_iron   = (new DoorTile(71, Material::metal))->init()->setDestroyTime(5.0f)->setSoundType(SOUND_METAL)->setCategory(ItemCategory::Structures)->setDescriptionId("doorIron");

	redStoneOre = (new RedStoneOreTile(73, 16 * 3 + 3, false))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("oreRedstone");
	redStoneOre_lit = (new RedStoneOreTile(74, 16 * 3 + 3, true))->init()->setDestroyTime(3.0f)->setLightEmission(10 / 16.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("oreRedstone");

	topSnow     = (new TopSnowTile(78, 16 * 4 + 2))->init()->setDestroyTime(0.1f)->setSoundType(SOUND_CLOTH)->setCategory(ItemCategory::Structures)->setDescriptionId("snow");
	ice         = (new IceTile(79, 16 * 4 + 3))->init()->setDestroyTime(0.5f)->setLightBlock(3)->setSoundType(SOUND_GLASS)->setCategory(ItemCategory::Structures)->setDescriptionId("ice");
	snow		= (new SnowTile(80, 16 * 4 + 2))->init()->setDestroyTime(0.2f)->setSoundType(SOUND_CLOTH)->setCategory(ItemCategory::Structures)->setDescriptionId("snow");
    cactus      = (new CactusTile(81, 16 * 4 + 6))->init()->setDestroyTime(0.4f)->setSoundType(SOUND_CLOTH)->setCategory(ItemCategory::Structures)->setDescriptionId("cactus");
	clay        = (new ClayTile(82, 16 * 4 + 8))->init()->setDestroyTime(0.6f)->setSoundType(SOUND_GRAVEL)->setCategory(ItemCategory::Structures)->setDescriptionId("clay");
	reeds       = (new ReedTile(83, 16 * 4 + 9))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("reeds");

	fence		= (new FenceTile(85, 4))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("fence");

	netherrack = (new Tile(87, 7 + 6 * 16, Material::stone))->init()->setDestroyTime(0.4f)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("hellrock");
	//hellSand = (new HellSandTile(88, 8 + 6 * 16, Material::sand))->init()->setDestroyTime(0.5f)->setSoundType(SOUND_SAND)->setDescriptionId("hellsand");

	lightGem	= (new LightGemTile(89, 9 + 16 * 6, Material::glass))->init()->setDestroyTime(0.3f)->setCategory(ItemCategory::Structures)->setSoundType(SOUND_GLASS)->setLightEmission(1.0f)->setDescriptionId("lightgem");
	invisible_bedrock = (new InvisibleTile(95, 0, Material::stone))->init()->setDestroyTime(-1)->setExplodeable(6000000)->setCategory(ItemCategory::Structures);//->setSoundType(SOUND_SILENT);
	trapdoor = (new TrapDoorTile(96, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("trapdoor");//->sendTileData();

	stoneBrickSmooth = (new MultiTextureTile(98, (const int*)&STONE_BRICK_TEXTURES, STONE_BRICK_TEXTURE_COUNT, Material::stone))->init()->setDestroyTime(1.5f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stonebricksmooth");

	thinGlass = (new ThinFenceTile(102, 1 + 3 * 16, 4 + 9 * 16, Material::glass, false))->init()->setDestroyTime(0.3f)->setSoundType(SOUND_GLASS)->setCategory(ItemCategory::Structures)->setDescriptionId("thinGlass");
	melon = (new MelonTile(103))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::FoodArmor)->setDescriptionId("melon");
	melonStem = (new StemTile(105, Tile::melon))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::FoodArmor)->setDescriptionId("pumpkinStem");//->sendTileData();
	fenceGate = (new FenceGateTile(107, 4))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("fenceGate");//->sendTileData();
	stairs_brick = (new StairTile(108, Tile::redBrick))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsBrick");//->sendTileData();

	stairs_stoneBrickSmooth = (new StairTile(109, Tile::stoneBrickSmooth))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsStoneBrickSmooth");//->sendTileData();
	netherBrick    = (new Tile(112, 0 + 14 * 16, Material::stone))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("netherBrick");
	stairs_netherBricks = (new StairTile(114, Tile::netherBrick))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsNetherBrick");//->sendTileData();
	stairs_sandStone = (new StairTile(128, Tile::sandStone))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsSandStone");//->sendTileData();

	quartzBlock   = (new QuartzBlockTile(155))->init()->setSoundType(SOUND_STONE)->setDestroyTime(0.8f)->setCategory(ItemCategory::Structures)->setDescriptionId("quartzBlock");
	stairs_quartz = (new StairTile(156, Tile::quartzBlock))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsQuartz");//->sendTileData();

	// Stone Variants (ID 19) - granite/polished granite/diorite/polished diorite/andesite/polished andesite
	stoneVariant = (new StoneVariantTile(19))->init()->setDestroyTime(1.5f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stoneVariant");

	// Smooth Stone (ID 23)
	smoothStone = (new Tile(23, 542, Material::stone))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("smoothStone");

	// Redstone Block (ID 25)
	redstoneBlock = (new Tile(25, 527, Material::metal))->init()->setDestroyTime(5.0f)->setExplodeable(10)->setSoundType(SOUND_METAL)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("blockRedstone");

	// Redstone Lamp Off (ID 27)
	redstoneLamp = (new RedstoneLampTile(27, 493, false))->init()->setDestroyTime(0.3f)->setSoundType(SOUND_GLASS)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("redstoneLamp");

	// Redstone Lamp On (ID 28)
	redstoneLamp_lit = (new RedstoneLampTile(28, 510, true))->init()->setDestroyTime(0.3f)->setLightEmission(1.0f)->setSoundType(SOUND_GLASS)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("redstoneLamp");

	// Piston (ID 29)
	piston = (new PistonTile(29, false))->init()->setDestroyTime(0.5f)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("piston");

	// Sticky Piston (ID 32)
	stickyPiston = (new PistonTile(32, true))->init()->setDestroyTime(0.5f)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("stickyPiston");

	// Comparator Off (ID 33)
	comparator_off = (new Tile(33, 531, Material::decoration))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("comparator");

	// Comparator On (ID 34)
	comparator_on = (new Tile(34, 531, Material::decoration))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("comparator");

	// Diode/Repeater Off (ID 136)
	diode_off = (new Tile(136, 131, Material::decoration))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("diode");

	// Diode/Repeater On (ID 137)
	diode_on = (new Tile(137, 147, Material::decoration))->init()->setDestroyTime(0.0f)->setLightEmission(0.625f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("diode");

	// Green Emerald Ore (ID 36) - not to be confused with 'emeraldOre' which is diamond
	greenEmeraldOre = (new OreTile(36, 532))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Decorations)->setDescriptionId("oreEmerald");

	// Coal Block (ID 52)
	coalBlock = (new Tile(52, 537, Material::stone))->init()->setDestroyTime(5.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("blockCoal");

	// Slime Block (ID 55)
	slimeBlock = (new SlimeBlockTile(55, 536))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Mechanisms)->setDescriptionId("slimeBlock");

	// Hay Bale (ID 66)
	hayBale = (new HayBaleTile(66))->init()->setDestroyTime(0.5f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("hayBale");

	// Nether Quartz Ore (ID 69)
	netherQuartzOre = (new OreTile(69, 543))->init()->setDestroyTime(3.0f)->setExplodeable(5)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("oreQuartz");

	// Soul Sand / Hell Sand (ID 70)
	hellSand = (new SoulSandTile(70, 541))->init()->setDestroyTime(0.5f)->setSoundType(SOUND_SAND)->setCategory(ItemCategory::Structures)->setDescriptionId("hellsand");

	// Cocoa (ID 72)
	cocoa = (new CocoaTile(72))->init()->setDestroyTime(0.2f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("cocoa");

	// Jungle Log (ID 75)
	jungleLog = (new TreeTile(75))->init()->setDestroyTime(2.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("jungleLog");

	// Jungle Leaves (ID 76)
	jungleLeaves = (LeafTile*)(new LeafTile(76, 459))->init()->setDestroyTime(0.2f)->setLightBlock(1)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("jungleLeaves");

	// Jungle Sapling (ID 77)
	jungleSapling = (new Sapling(77, 476))->init()->setDestroyTime(0.0f)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("jungleSapling");

	// Nether Brick Fence (ID 84)
	netherBrickFence = (new FenceTile(84, 224, Material::stone))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("netherBrickFence");

	// Green Emerald Block (ID 86)
	greenEmeraldBlock = (new MetalTile(86, 533))->init()->setDestroyTime(5.0f)->setExplodeable(10)->setSoundType(SOUND_METAL)->setCategory(ItemCategory::Decorations)->setDescriptionId("blockEmerald");

	// Glass Pane alias
	glassPaneTile = thinGlass;

	// Spruce Fence (ID 88)
	fence_spruce = (new FenceTile(88, 198))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("spruceFence");

	// Birch Fence (ID 90)
	fence_birch = (new FenceTile(90, 199))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("birchFence");

	// Jungle Fence (ID 91)
	fence_jungle = (new FenceTile(91, 200))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("jungleFence");

	// Spruce Fence Gate (ID 92)
	fenceGate_spruce = (new FenceGateTile(92, 198))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("spruceFenceGate");

	// Birch Fence Gate (ID 93)
	fenceGate_birch = (new FenceGateTile(93, 199))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("birchFenceGate");

	// Jungle Fence Gate (ID 94)
	fenceGate_jungle = (new FenceGateTile(94, 200))->init()->setDestroyTime(2.0f)->setExplodeable(5)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("jungleFenceGate");

	// Stairs: Granite (ID 97)
	stairs_granite = (new StairTile(97, stoneVariant, StoneVariantTile::GRANITE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsGranite");

	// Stairs: Polished Granite (ID 99)
	stairs_polishedGranite = (new StairTile(99, stoneVariant, StoneVariantTile::POLISHED_GRANITE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsPolishedGranite");

	// Stairs: Diorite (ID 100)
	stairs_diorite = (new StairTile(100, stoneVariant, StoneVariantTile::DIORITE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsDiorite");

	// Stairs: Polished Diorite (ID 101)
	stairs_polishedDiorite = (new StairTile(101, stoneVariant, StoneVariantTile::POLISHED_DIORITE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsPolishedDiorite");

	// Stairs: Andesite (ID 104)
	stairs_andesite = (new StairTile(104, stoneVariant, StoneVariantTile::ANDESITE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsAndesite");

	// Stairs: Polished Andesite (ID 106)
	stairs_polishedAndesite = (new StairTile(106, stoneVariant, StoneVariantTile::POLISHED_ANDESITE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsPolishedAndesite");

	// Walls
	wall_granite = (new WallTile(110, 130))->init()->setDestroyTime(1.5f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("wallGranite");
	wall_diorite = (new WallTile(111, 132))->init()->setDestroyTime(1.5f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("wallDiorite");
	wall_andesite = (new WallTile(113, 134))->init()->setDestroyTime(1.5f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("wallAndesite");
	wall_sandstone = (new WallTile(115, 12 * 16))->init()->setDestroyTime(0.8f)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("wallSandstone");
	wall_netherBrick = (new WallTile(116, 224))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("wallNetherBrick");

	// Stairs: Smooth Sandstone (ID 117)
	stairs_smoothSandstone = (new StairTile(117, sandStone, SandStoneTile::TYPE_SMOOTHSIDE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsSmoothSandstone");

	// Doors (spruce, birch, jungle)
	door_spruce = (new DoorTile(118, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("doorSpruce");
	((DoorTile*)door_spruce)->setTextures(306, 306); // solid dark spruce (no windows)
	door_birch = (new DoorTile(119, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("doorBirch");
	((DoorTile*)door_birch)->setTextures(289, 289); // solid light birch (no windows)
	door_jungle = (new DoorTile(120, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("doorJungle");
	((DoorTile*)door_jungle)->setTextures(561, 323); // jungle lower solid, upper diamond windows

	// Trapdoors (spruce, birch, jungle)
	trapdoor_spruce = (new TrapDoorTile(121, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("trapdoorSpruce");
	trapdoor_spruce->tex = 578; // dark spruce trapdoor
	trapdoor_birch = (new TrapDoorTile(122, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("trapdoorBirch");
	trapdoor_birch->tex = 272; // light birch trapdoor
	trapdoor_jungle = (new TrapDoorTile(123, Material::wood))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("trapdoorJungle");
	trapdoor_jungle->tex = 595; // jungle trapdoor

	// Signs (spruce)
	sign_spruce = (new SignTile(124, TileEntityType::Sign, true))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("signSpruce");
	wallSign_spruce = (new SignTile(127, TileEntityType::Sign, false))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("signSpruce");

	// Signs (birch)
	sign_birch = (new SignTile(125, TileEntityType::Sign, true))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("signBirch");
	wallSign_birch = (new SignTile(129, TileEntityType::Sign, false))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("signBirch");

	// Signs (jungle)
	sign_jungle = (new SignTile(126, TileEntityType::Sign, true))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("signJungle");
	wallSign_jungle = (new SignTile(130, TileEntityType::Sign, false))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Decorations)->setDescriptionId("signJungle");

	// Stairs: Wood variants
	stairs_spruce = (new StairTile(131, wood, WoodPlanks::SPRUCE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsSpruce");
	stairs_birch = (new StairTile(132, wood, WoodPlanks::BIRCH))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsBirch");
	stairs_jungle = (new StairTile(133, wood, WoodPlanks::JUNGLE))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("stairsJungle");

	// Second slab pair (ID 134/135) for new slab types
	stoneSlab2 = (new StoneSlabTile(134, true, 1))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stoneSlab2");
	stoneSlabHalf2 = (new StoneSlabTile(135, false, 1))->init()->setDestroyTime(2.0f)->setExplodeable(10)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stoneSlab2");

	//
	// Special tiles for Pocket Edition is placed at high IDs
	//
	stonecutterBench= (new StonecutterTile(245))->init()->setDestroyTime(2.5f)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("stonecutter");
	glowingObsidian = (new ObsidianTile(246, 10 + 16 * 13, true))->init()->setDestroyTime(10.0f)->setLightEmission(14 / 16.0f)->setExplodeable(2000)->setSoundType(SOUND_STONE)->setCategory(ItemCategory::Structures)->setDescriptionId("glowingobsidian");
	netherReactor	= (new NetherReactor(247, 10  + 14 * 16, Material::metal))->init()->setDestroyTime(3.0f)->setSoundType(SOUND_METAL)->setCategory(ItemCategory::Structures)->setDescriptionId("netherreactor");
	info_updateGame1= (new Tile(248, 252, Material::dirt))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_GRAVEL)->setCategory(ItemCategory::Structures)->setDescriptionId("info_update");
	info_updateGame2= (new Tile(249, 253, Material::dirt))->init()->setDestroyTime(1.0f)->setSoundType(SOUND_GRAVEL)->setCategory(ItemCategory::Structures)->setDescriptionId("info_update");
	grass_carried   = (new CarriedTile(253, 3, 12*16 + 12))->init()->setCategory(ItemCategory::Structures)->setDescriptionId("grass");
	leaves_carried  = (LeafTile*) (new LeafTile(254, 11 + 14 * 16))->init()->setDestroyTime(0.2f)->setLightBlock(1)->setSoundType(SOUND_GRASS)->setCategory(ItemCategory::Structures)->setDescriptionId("leaves");
	info_reserved6  = (new Tile(255, Material::dirt))->init()->setCategory(ItemCategory::Structures);

    //
    // Stuff that need to be inited in a specific order (i.e. after the other tiles have been created)
    //
    fire     = (FireTile*) (new FireTile(51, 1 * 16 + 15))->init()->setDestroyTime(0.0f)->setLightEmission(1.0f)->setSoundType(SOUND_WOOD)->setCategory(ItemCategory::Structures)->setDescriptionId("fire");

	//
    // Special case for certain items since they can have different icons
	// @note: Make sure those different items are handled in ItemInHandRenderer::renderItem
	//
	Item::items[cloth->id] = (new ClothTileItem(cloth->id - 256))->setCategory(ItemCategory::Structures)->setDescriptionId("cloth");
	Item::items[treeTrunk->id] = (new AuxDataTileItem(treeTrunk->id - 256, treeTrunk))->setCategory(ItemCategory::Structures)->setDescriptionId("log");
	Item::items[stoneBrickSmooth->id] = (new AuxDataTileItem(stoneBrickSmooth->id - 256, stoneBrickSmooth))->setCategory(ItemCategory::Structures)->setDescriptionId("stonebricksmooth");
	Item::items[stoneSlabHalf->id] = (new StoneSlabTileItem(stoneSlabHalf->id - 256))->setCategory(ItemCategory::Structures)->setDescriptionId("stoneSlab");
	Item::items[sapling->id] = (new SaplingTileItem(sapling->id - 256))->setCategory(ItemCategory::Structures)->setDescriptionId("sapling");
	Item::items[leaves->id] = (new LeafTileItem(leaves->id - 256))->setCategory(ItemCategory::Decorations)->setDescriptionId("leaves");
	Item::items[sandStone->id] = (new AuxDataTileItem(sandStone->id - 256, sandStone))->setCategory(ItemCategory::Structures)->setDescriptionId("sandStone");

	Item::items[quartzBlock->id] = (new AuxDataTileItem(quartzBlock->id - 256, quartzBlock))->setCategory(ItemCategory::Structures)->setDescriptionId("quartzBlock");

	Item::items[stoneVariant->id] = (new AuxDataTileItem(stoneVariant->id - 256, stoneVariant))->setCategory(ItemCategory::Structures)->setDescriptionId("stoneVariant");
	Item::items[wood->id] = (new AuxDataTileItem(wood->id - 256, wood))->setCategory(ItemCategory::Structures)->setDescriptionId("wood");
	Item::items[stoneSlabHalf2->id] = (new StoneSlabTileItem(stoneSlabHalf2->id - 256))->setCategory(ItemCategory::Structures)->setDescriptionId("stoneSlab2");

    for (int i = 0; i < 256; i++) {
        if (Tile::tiles[i] != NULL) {
			if (Item::items[i] == NULL) {
				Item::items[i] = new TileItem(i - 256);
				Item::items[i]->category = Tile::tiles[i]->category;
			}
			// Check for missing category
			if (Item::items[i]->category == -1)
				LOGE("Error: Missing category for tile %d: %s\n", tiles[i]->id, tiles[i]->getDescriptionId().c_str());
        }
    }
}

/*static*/
void Tile::teardownTiles() {
	for (int i = 0; i < 256; ++i)
		if (Tile::tiles[i]) {
			delete Tile::tiles[i];
			Tile::tiles[i] = NULL;
		}
}

int Tile::transformToValidBlockId( int blockId ) {
	return transformToValidBlockId(blockId, 0, 0, 0);
}

int Tile::transformToValidBlockId( int blockId, int x, int y, int z ) {
	if (blockId != 0 && Tile::tiles[blockId] == NULL)
		return (((x + y + z)&1) == 1)? Tile::info_updateGame1->id : Tile::info_updateGame2->id;
	return blockId;
}


Tile::Tile(int id, const Material* material)
:	id(id),
	material(material),
	tex(1),
	category(-1),
	gravity(1.0f),
	friction(0.6f),
	soundType(&Tile::SOUND_NORMAL),
	tmpBB(0,0,0,1,1,1),
	xx0(0),yy0(0),zz0(0),
	xx1(1),yy1(1),zz1(1)
{
	if (Tile::tiles[id]) {
		printf("Slot %d is already occupied by %p when adding %p\n", id, &Tile::tiles[id], this);
	}
}

Tile::Tile( int id, int tex, const Material* material )
:	id(id),
	tex(tex),
	material(material),
	category(-1),
	gravity(1.0f),
	friction(0.6f),
	soundType(&Tile::SOUND_NORMAL),
	tmpBB(0,0,0,1,1,1),
	xx0(0),yy0(0),zz0(0),
	xx1(1),yy1(1),zz1(1)
{
	if (Tile::tiles[id]) {
		printf("Slot %d is already occupied by %p when adding %p\n", id, &Tile::tiles[id], this);
	}
}

//Tile* sendTileData() {
//    Tile::sendTileData[id] = true;
//    return this;
//}

/*protected*/
Tile* Tile::setLightEmission(float f) {
    Tile::lightEmission[id] = (int) (Level::MAX_BRIGHTNESS * f);
    return this;
}

/*public static*/
bool Tile::isFaceVisible(Level* level, int x, int y, int z, int f) {
	switch (f) {
		case Facing::DOWN : y--; break;
		case Facing::UP   : y++; break;
		case Facing::NORTH: z--; break;
		case Facing::SOUTH: z++; break;
		case Facing::WEST : x--; break;
		case Facing::EAST : x++; break;
	}
	return !level->isSolidRenderTile(x, y, z);
}

/* private */
Tile* Tile::init() {
    Tile::tiles[id] = this;
	setShape(xx0, yy0, zz0, xx1, yy1, zz1); // @attn
	solid[id] = isSolidRender();
	lightBlock[id] = isSolidRender() ? 255 : 0;
	translucent[id] = !material->blocksLight();
	return this;
}

/*public virtual*/
float Tile::getDestroyProgress(Player* player) {
    if (destroySpeed < 0) return 0;
    if (!player->canDestroy(this)) return 1 / destroySpeed / 100.0f;
    return (player->getDestroySpeed(this) / destroySpeed) / 30.0f;
}

/*public virtual*/
HitResult Tile::clip(Level* level, int xt, int yt, int zt, const Vec3& A, const Vec3& B) {
    updateShape(level, xt, yt, zt);

	//Stopwatch sw;
	//sw.start();

	Vec3 sub((float)xt, (float)yt, (float)zt);
	Vec3 a = A - sub;//a.add((float)-xt, (float)-yt, (float)-zt);
    Vec3 b = B - sub;//b.add((float)-xt, (float)-yt, (float)-zt);

    Vec3 xh0, xh1, yh0, yh1, zh0, zh1;

	bool bxh0 = a.clipX(b, xx0, xh0);
    bool bxh1 = a.clipX(b, xx1, xh1);

    bool byh0 = a.clipY(b, yy0, yh0);
    bool byh1 = a.clipY(b, yy1, yh1);

    bool bzh0 = a.clipZ(b, zz0, zh0);
    bool bzh1 = a.clipZ(b, zz1, zh1);

    //if (!containsX(xh0)) xh0 = NULL;
    if (!bxh0 || !containsX(xh0)) bxh0 = false;
    if (!bxh1 || !containsX(xh1)) bxh1 = false;
    if (!byh0 || !containsY(yh0)) byh0 = false;
    if (!byh1 || !containsY(yh1)) byh1 = false;
    if (!bzh0 || !containsZ(zh0)) bzh0 = false;
    if (!bzh1 || !containsZ(zh1)) bzh1 = false;
    Vec3* closest = NULL;

    //if (xh0 != NULL && (closest == NULL || a.distanceToSqr(xh0) < a.distanceToSqr(closest))) closest = xh0;
    if (bxh0 && (closest == NULL || a.distanceToSqr(xh0) < a.distanceToSqr(*closest))) closest = &xh0;
    if (bxh1 && (closest == NULL || a.distanceToSqr(xh1) < a.distanceToSqr(*closest))) closest = &xh1;
    if (byh0 && (closest == NULL || a.distanceToSqr(yh0) < a.distanceToSqr(*closest))) closest = &yh0;
    if (byh1 && (closest == NULL || a.distanceToSqr(yh1) < a.distanceToSqr(*closest))) closest = &yh1;
    if (bzh0 && (closest == NULL || a.distanceToSqr(zh0) < a.distanceToSqr(*closest))) closest = &zh0;
    if (bzh1 && (closest == NULL || a.distanceToSqr(zh1) < a.distanceToSqr(*closest))) closest = &zh1;

    if (closest == NULL)
		return HitResult();

    int face = -1;

    if (closest == &xh0) face = 4;
    if (closest == &xh1) face = 5;
    if (closest == &yh0) face = 0;
    if (closest == &yh1) face = 1;
    if (closest == &zh0) face = 2;
    if (closest == &zh1) face = 3;

	//sw.stop();
	//sw.printEvery(5, ">>> ");

    return HitResult(xt, yt, zt, face, closest->add((float)xt, (float)yt, (float)zt));
}

/*virtual*/
void Tile::spawnResources(Level* level, int x, int y, int z, int data, float odds) {
    if (level->isClientSide) return;

    int count = getResourceCount(&level->random);
    for (int i = 0; i < count; i++) {
        if (level->random.nextFloat() > odds) continue;
        int type = getResource(data, &level->random);
        if (type <= 0) continue;
        const float s = 0.7f;
        float xo = level->random.nextFloat() * s + (1 - s) * 0.5f;
        float yo = level->random.nextFloat() * s + (1 - s) * 0.5f;
        float zo = level->random.nextFloat() * s + (1 - s) * 0.5f;
		ItemEntity* item = new ItemEntity(level, x + xo, y + yo, z + zo, ItemInstance(type, 1, getSpawnResourcesAuxValue(data)));
        item->throwTime = 10;
        level->addEntity(item);
    }
}

void Tile::spawnResources( Level* level, int x, int y, int z, int data )
{
	spawnResources(level, x, y, z, data, 1);
}

void Tile::popResource(Level* level, int x, int y, int z, const ItemInstance& itemInstance) {
	if (level->isClientSide || level->getLevelData()->getGameType() == GameType::Creative) return;

	float s = 0.7f;
	float xo = level->random.nextFloat() * s + (1 - s) * 0.5f;
	float yo = level->random.nextFloat() * s + (1 - s) * 0.5f;
	float zo = level->random.nextFloat() * s + (1 - s) * 0.5f;

	ItemEntity* item = new ItemEntity(level, x + xo, y + yo, z + zo, itemInstance);
	item->throwTime = 10;
	level->addEntity(item);
}


void Tile::destroy( Level* level, int x, int y, int z, int data )
{
}

bool Tile::isCubeShaped()
{
	return true;
}

int Tile::getRenderShape()
{
	return SHAPE_BLOCK;
}

float Tile::getBrightness( LevelSource* level, int x, int y, int z )
{
	return level->getBrightness(x, y, z);
}

bool Tile::shouldRenderFace( LevelSource* level, int x, int y, int z, int face )
{
	if (face == 0 && y == -1) return false;
	// Common
	if (face == 0 && yy0 > 0) return true;
	if (face == 1 && yy1 < 1) return true;
	if (face == 2 && zz0 > 0) return true;
	if (face == 3 && zz1 < 1) return true;
	if (face == 4 && xx0 > 0) return true;
	if (face == 5 && xx1 < 1) return true;
	Tile* t = Tile::tiles[level->getTile(x, y, z)];
	if (!t) return true;
	if (face == 1 && t->id == topSnow->id) return false;
	return !t->isSolidRender();
	//return (!level->isSolidRenderTile(x, y, z));
}

int Tile::getTexture( LevelSource* level, int x, int y, int z, int face )
{
	return getTexture(face, level->getData(x, y, z));
}

int Tile::getTexture( int face, int data )
{
	return getTexture(face);
}

int Tile::getTexture( int face )
{
	return tex;
}

void Tile::addAABBs( Level* level, int x, int y, int z, const AABB* box, std::vector<AABB>& boxes )
{
	AABB* aabb = getAABB(level, x, y, z);
	if (aabb != NULL && box->intersects(*aabb)) {
		boxes.push_back(*aabb);
	}
}

AABB* Tile::getAABB( Level* level, int x, int y, int z )
{
	tmpBB.x0 = x + xx0;
	tmpBB.y0 = y + yy0;
	tmpBB.z0 = z + zz0;
	tmpBB.x1 = x + xx1;
	tmpBB.y1 = y + yy1;
	tmpBB.z1 = z + zz1;
	return &tmpBB;
}

bool Tile::isSolidRender()
{
	return true;
}

bool Tile::mayPick( int data, bool liquid )
{
	return mayPick();
}

bool Tile::mayPick()
{
	return true;
}

int Tile::getTickDelay()
{
	return 10;
}

int Tile::getResourceCount( Random* random )
{
	return 1;
}

int Tile::getResource( int data, Random* random )
{
	return id;
}

float Tile::getExplosionResistance( Entity* source )
{
	return explosionResistance / 5.0f;
}

int Tile::getRenderLayer()
{
	return Tile::RENDERLAYER_OPAQUE;
}

bool Tile::use( Level* level, int x, int y, int z, Player* player )
{
	return false;
}

bool Tile::spawnBurnResources( Level* level, float x, float y, float z )
{
	return false;
}

int Tile::getColor( LevelSource* level, int x, int y, int z )
{
	return 0xffffff;
}

bool Tile::getSignal( LevelSource* level, int x, int y, int z )
{
	return false;
}

bool Tile::getSignal( LevelSource* level, int x, int y, int z, int dir )
{
	return false;
}

bool Tile::isSignalSource()
{
	return false;
}

bool Tile::getDirectSignal( Level* level, int x, int y, int z, int dir )
{
	return false;
}

void Tile::playerDestroy( Level* level, Player* player, int x, int y, int z, int data )
{
	//player.awardStat(Stats.blockMined[id], 1);
	spawnResources(level, x, y, z, data);
}

bool Tile::canSurvive( Level* level, int x, int y, int z )
{
	return true;
}

Tile* Tile::setDescriptionId( const std::string& id )
{
	descriptionId = TILE_DESCRIPTION_PREFIX + id;
	return this;
}

std::string Tile::getName() const
{
	return I18n::get(getDescriptionId() + ".name");
}

std::string Tile::getDescriptionId() const
{
	return descriptionId;
}

Tile* Tile::setSoundType( const SoundType& soundType )
{
	this->soundType = &soundType;
	return this;
}

Tile* Tile::setLightBlock( int i )
{
	lightBlock[id] = i;
	return this;
}

Tile* Tile::setExplodeable( float explosionResistance )
{
	this->explosionResistance = explosionResistance * 3;
	return this;
}

Tile* Tile::setDestroyTime( float destroySpeed )
{
	this->destroySpeed = destroySpeed;
	if (explosionResistance < destroySpeed * 5) explosionResistance = destroySpeed * 5;
	return this;
}

void Tile::setTicking( bool tick )
{
	shouldTick[id] = tick;
}

int Tile::getSpawnResourcesAuxValue( int data )
{
	return 0;
}

bool Tile::containsX( const Vec3& v )
{
	return v.y >= yy0 && v.y <= yy1 && v.z >= zz0 && v.z <= zz1;
}

bool Tile::containsY( const Vec3& v )
{
	return v.x >= xx0 && v.x <= xx1 && v.z >= zz0 && v.z <= zz1;
}

bool Tile::containsZ( const Vec3& v )
{
	return v.x >= xx0 && v.x <= xx1 && v.y >= yy0 && v.y <= yy1;
}

/*public*/
AABB Tile::getTileAABB(Level* level, int x, int y, int z) {
	return AABB(x + xx0, y + yy0, z + zz0, x + xx1, y + yy1, z + zz1);
}

/*public*/
void Tile::setShape(float x0, float y0, float z0, float x1, float y1, float z1) {
	this->xx0 = x0;
	this->yy0 = y0;
	this->zz0 = z0;
	this->xx1 = x1;
	this->yy1 = y1;
	this->zz1 = z1;
}

/*public*/
bool Tile::mayPlace(Level* level, int x, int y, int z, unsigned char face) {
	return mayPlace(level, x, y, z);
}

bool Tile::mayPlace( Level* level, int x, int y, int z ) {
	int t = level->getTile(x, y, z);
	return t == 0 || Tile::tiles[t]->material->isReplaceable();
}

Tile* Tile::setCategory(int category) {
	this->category = category;
	return this;
}
