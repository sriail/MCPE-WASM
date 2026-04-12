#pragma once
// C4JSpursJob.h - PS3 SPU job management stub for Emscripten
class C4JSpursJob {
public:
    static void Init() {}
    static void Shutdown() {}
    static void RunJob(void* job, void* params) { (void)job; (void)params; }
};
