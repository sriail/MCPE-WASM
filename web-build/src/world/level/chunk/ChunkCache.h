#ifndef NET_MINECRAFT_WORLD_LEVEL_CHUNK__ChunkCache_H__
#define NET_MINECRAFT_WORLD_LEVEL_CHUNK__ChunkCache_H__

//package net.minecraft.world.level.chunk;

#include <unordered_map>
#include <vector>
#include <algorithm>
#include <cstdint>
#include <cstdio>

#include "ChunkSource.h"
#include "storage/ChunkStorage.h"
#include "EmptyLevelChunk.h"
#include "../Level.h"
#include "../LevelConstants.h"

// Hash for packed (x<<32|z) chunk coordinate keys
struct ChunkKeyHash {
    std::size_t operator()(int64_t key) const {
        uint64_t k = (uint64_t)key;
        k ^= k >> 33;
        k *= 0xff51afd7ed558ccdULL;
        k ^= k >> 33;
        k *= 0xc4ceb9fe1a85ec53ULL;
        k ^= k >> 33;
        return (std::size_t)k;
    }
};

class ChunkCache: public ChunkSource {
    static const int MAX_SAVES = 2;

    // Maximum chunks kept in memory; beyond this distant chunks are evicted.
    static const int MAX_LOADED_CHUNKS = 512;

    // Chunks within this radius (in chunk coords) of the player are never evicted.
    static const int UNLOAD_KEEP_RADIUS = 8;

    static inline int64_t chunkKey(int x, int z) {
        return ((int64_t)x << 32) | ((int64_t)z & 0xFFFFFFFFLL);
    }

public:
    ChunkCache(Level* level_, ChunkStorage* storage_, ChunkSource* source_)
	:	xLast(-999999999),
		zLast(-999999999),
		last(NULL),
		level(level_),
		storage(storage_),
		source(source_)
	{
		isChunkCache = true;
		emptyChunk = new EmptyLevelChunk(level_, NULL, 0, 0);
    }

	~ChunkCache() {
		delete source;
		delete emptyChunk;

		for (auto& pair : chunks) {
			if (pair.second && pair.second != emptyChunk) {
				pair.second->deleteBlockData();
				delete pair.second;
			}
		}
		chunks.clear();
	}

    // The world is now infinite — every coordinate is valid.
    bool fits(int x, int z) {
        (void)x; (void)z;
        return true;
    }

    bool hasChunk(int x, int z) {
        if (x == xLast && z == zLast && last != NULL) {
            return true;
        }
        int64_t key = chunkKey(x, z);
        auto it = chunks.find(key);
        return it != chunks.end() && it->second != NULL &&
               (it->second == emptyChunk || it->second->isAt(x, z));
    }

    LevelChunk* create(int x, int z) {
        return getChunk(x, z);
    }

    LevelChunk* getChunk(int x, int z) {
		if (x == xLast && z == zLast && last != NULL) {
            return last;
        }

        int64_t key = chunkKey(x, z);
        auto it = chunks.find(key);

        if (it != chunks.end() && it->second != NULL && it->second->isAt(x, z)) {
            xLast = x;
            zLast = z;
            last = it->second;
            return it->second;
        }

        // Evict distant chunks before allocating a new one
        evictDistantChunks(x, z);

        // If a stale entry exists at this key, save and remove it
        it = chunks.find(key);
        if (it != chunks.end() && it->second != NULL && it->second != emptyChunk) {
            it->second->unload();
            save(it->second);
            saveEntities(it->second);
            it->second->deleteBlockData();
            delete it->second;
            chunks.erase(it);
        }

        // Load from storage first; fall back to world generator
        LevelChunk* newChunk = load(x, z);
		bool updateLights = false;
        if (newChunk == NULL) {
            if (source == NULL) {
                newChunk = emptyChunk;
            } else {
                newChunk = source->getChunk(x, z);
            }
        } else {
			updateLights = true;
        }

        chunks[key] = newChunk;
        newChunk->lightLava();

		if (updateLights) {
			for (int cx = 0; cx < 16; cx++) {
				for (int cz = 0; cz < 16; cz++) {
					int height = level->getHeightmap(cx + x * 16, cz + z * 16);
					for (int cy = height; cy >= 0; cy--) {
						level->updateLight(LightLayer::Sky, cx + x * 16, cy, cz + z * 16, cx + x * 16, cy, cz + z * 16);
						level->updateLight(LightLayer::Block, cx + x * 16 - 1, cy, cz + z * 16 - 1, cx + x * 16 + 1, cy, cz + z * 16 + 1);
					}
				}
			}
		}

        if (newChunk != NULL) {
            newChunk->load();
        }

        // Post-process (ores, trees, etc.) once all four required neighbours exist
        if (newChunk != emptyChunk && !newChunk->terrainPopulated &&
            hasChunk(x + 1, z + 1) && hasChunk(x, z + 1) && hasChunk(x + 1, z))
            postProcess(this, x, z);

        LevelChunk* nb;
        nb = getChunkIfLoaded(x - 1, z);
        if (nb != emptyChunk && !nb->terrainPopulated &&
            hasChunk(x - 1, z + 1) && hasChunk(x, z + 1))
            postProcess(this, x - 1, z);

        nb = getChunkIfLoaded(x, z - 1);
        if (nb != emptyChunk && !nb->terrainPopulated &&
            hasChunk(x + 1, z - 1) && hasChunk(x + 1, z))
            postProcess(this, x, z - 1);

        nb = getChunkIfLoaded(x - 1, z - 1);
        if (nb != emptyChunk && !nb->terrainPopulated &&
            hasChunk(x, z - 1) && hasChunk(x - 1, z))
            postProcess(this, x - 1, z - 1);

        xLast = x;
        zLast = z;
        last = newChunk;

        return newChunk;
    }

    // Return an already-loaded chunk; never triggers generation.
    LevelChunk* getChunkIfLoaded(int x, int z) {
        if (x == xLast && z == zLast && last != NULL) return last;
        int64_t key = chunkKey(x, z);
        auto it = chunks.find(key);
        if (it != chunks.end() && it->second != NULL && it->second->isAt(x, z))
            return it->second;
        return emptyChunk;
    }

	Biome::MobList getMobsAt(const MobCategory& mobCategory, int x, int y, int z) {
		return source->getMobsAt(mobCategory, x, y, z);
	}

    void postProcess(ChunkSource* parent, int x, int z) {
        LevelChunk* chunk = getChunkIfLoaded(x, z);
        if (chunk == emptyChunk) return;
        if (!chunk->terrainPopulated) {
            chunk->terrainPopulated = true;
            if (source != NULL) {
                source->postProcess(parent, x, z);
				chunk->clearUpdateMap();
            }
        }
    }

    bool tick() {
        if (storage != NULL) storage->tick();
        return source->tick();
    }

    bool shouldSave() {
        return true;
    }

    std::string gatherStats() {
        char buf[64];
        snprintf(buf, sizeof(buf), "ChunkCache: %d loaded", (int)chunks.size());
        return std::string(buf);
    }
	
	void saveAll(bool onlyUnsaved) {
		if (storage != NULL) {
			std::vector<LevelChunk*> toSave;
			for (auto& pair : chunks) {
				LevelChunk* chunk = pair.second;
				if (chunk && chunk != emptyChunk) {
					if (!onlyUnsaved || chunk->shouldSave(false))
						toSave.push_back(chunk);
				}
			}
			storage->saveAll(level, toSave);
		}
	}

private:
    LevelChunk* load(int x, int z) {
        if (storage == NULL) return NULL;
        LevelChunk* levelChunk = storage->load(level, x, z);
        if (levelChunk != NULL) {
            levelChunk->lastSaveTime = level->getTime();
        }
        return levelChunk;
    }

    void saveEntities(LevelChunk* levelChunk) {
        if (storage == NULL) return;
        storage->saveEntities(level, levelChunk);
    }

    void save(LevelChunk* levelChunk) {
        if (storage == NULL) return;
        levelChunk->lastSaveTime = level->getTime();
        storage->save(level, levelChunk);
    }

    // Evict chunks furthest from (cx, cz) when too many are in memory.
    void evictDistantChunks(int cx, int cz) {
        if ((int)chunks.size() < MAX_LOADED_CHUNKS) return;

        std::vector<std::pair<int64_t, int>> dists;
        dists.reserve(chunks.size());
        for (auto& pair : chunks) {
            if (pair.second == NULL || pair.second == emptyChunk) continue;
            int px = pair.second->x;
            int pz = pair.second->z;
            int dist = (px - cx) * (px - cx) + (pz - cz) * (pz - cz);
            dists.push_back({pair.first, dist});
        }

        // Sort furthest-first
        std::sort(dists.begin(), dists.end(),
            [](const std::pair<int64_t, int>& a, const std::pair<int64_t, int>& b) {
                return a.second > b.second;
            });

        // Evict until we're at 75% capacity; never evict chunks within UNLOAD_KEEP_RADIUS
        const int keepRadiusSq = UNLOAD_KEEP_RADIUS * UNLOAD_KEEP_RADIUS;
        int toEvict = (int)chunks.size() - (MAX_LOADED_CHUNKS * 3 / 4);
        for (int i = 0; i < (int)dists.size() && toEvict > 0; ++i) {
            if (dists[i].second <= keepRadiusSq) break; // remaining chunks are close enough to keep
            int64_t key = dists[i].first;
            auto it = chunks.find(key);
            if (it == chunks.end()) continue;
            LevelChunk* c = it->second;
            if (c == NULL || c == emptyChunk) continue;
            if (c == last) { last = NULL; xLast = -999999999; zLast = -999999999; }
            c->unload();
            save(c);
            saveEntities(c);
            c->deleteBlockData();
            delete c;
            chunks.erase(it);
            --toEvict;
        }
    }

public:
	int xLast;
    int zLast;
private:
    LevelChunk* emptyChunk;
    ChunkSource* source;
    ChunkStorage* storage;
    std::unordered_map<int64_t, LevelChunk*, ChunkKeyHash> chunks;
    Level* level;

    LevelChunk* last;

};

#endif /*NET_MINECRAFT_WORLD_LEVEL_CHUNK__ChunkCache_H__*/
