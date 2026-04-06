// Minecraft Web - Platform JS Library
// Handles world creation modal dialog and save syncing

mergeInto(LibraryManager.library, {
    mcShowCreateWorldDialog: function () {
        // Create modal overlay
        var overlay = document.createElement('div');
        overlay.id = 'mc-create-world';
        overlay.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;' +
            'background:rgba(0,0,0,0.8);display:flex;align-items:center;' +
            'justify-content:center;z-index:9999;font-family:monospace';

        var box = document.createElement('div');
        box.style.cssText = 'background:#3c3c3c;border:2px solid #888;padding:28px;' +
            'min-width:320px;color:#eee;text-align:center';

        box.innerHTML =
            '<h2 style="margin:0 0 18px;color:#fff;font-size:20px;letter-spacing:1px">Create New World</h2>' +
            '<div style="text-align:left;margin-bottom:14px">' +
            '<label style="font-size:13px;color:#aaa">World Name</label><br>' +
            '<input id="mc-worldname" type="text" value="My World" ' +
            'style="width:100%;box-sizing:border-box;padding:8px;margin-top:5px;' +
            'background:#222;color:#fff;border:1px solid #666;font-size:14px;font-family:monospace">' +
            '</div>' +
            '<div style="text-align:left;margin-bottom:20px">' +
            '<label style="font-size:13px;color:#aaa">Game Mode</label><br>' +
            '<select id="mc-gamemode" style="width:100%;box-sizing:border-box;padding:8px;' +
            'margin-top:5px;background:#222;color:#fff;border:1px solid #666;font-size:14px;font-family:monospace">' +
            '<option value="creative">Creative</option>' +
            '<option value="survival">Survival</option>' +
            '</select>' +
            '</div>' +
            '<div style="display:flex;gap:10px;justify-content:center">' +
            '<button id="mc-create-btn" style="padding:10px 24px;background:#4a8a4a;color:#fff;' +
            'border:none;cursor:pointer;font-size:15px;font-family:monospace;letter-spacing:1px">Create</button>' +
            '<button id="mc-cancel-btn" style="padding:10px 24px;background:#555;color:#fff;' +
            'border:none;cursor:pointer;font-size:15px;font-family:monospace;letter-spacing:1px">Cancel</button>' +
            '</div>';

        overlay.appendChild(box);
        document.body.appendChild(overlay);

        // Focus the name field
        setTimeout(function () {
            var input = document.getElementById('mc-worldname');
            if (input) { input.focus(); input.select(); }
        }, 50);

        // Reset state
        window._mcWorldStatus = -1;
        window._mcWorldResult = null;

        document.getElementById('mc-create-btn').onclick = function () {
            var nm = (document.getElementById('mc-worldname').value || 'My World').trim();
            var mode = document.getElementById('mc-gamemode').value;
            window._mcWorldStatus = 1;
            window._mcWorldResult = nm + '|' + mode;
            document.body.removeChild(overlay);
        };

        document.getElementById('mc-cancel-btn').onclick = function () {
            window._mcWorldStatus = 0;
            window._mcWorldResult = null;
            document.body.removeChild(overlay);
        };

        // Handle Enter key in name field
        document.getElementById('mc-worldname').onkeydown = function (e) {
            if (e.key === 'Enter') document.getElementById('mc-create-btn').click();
            if (e.key === 'Escape') document.getElementById('mc-cancel-btn').click();
        };
    },

    mcGetWorldStatus: function () {
        if (window._mcWorldStatus === undefined) return -1;
        return window._mcWorldStatus;
    },

    mcGetWorldResult: function () {
        var r = window._mcWorldResult;
        if (!r) return 0;
        window._mcWorldResult = null;
        window._mcWorldStatus = -1;
        var len = lengthBytesUTF8(r) + 1;
        var buf = _malloc(len);
        stringToUTF8(r, buf, len);
        return buf;
    },

    mcSyncSaves: function () {
        if (typeof FS !== 'undefined') {
            FS.syncfs(false, function (err) {
                if (err) console.error('FS sync error:', err);
            });
        }
    },

    // ─── Ambient Music System ────────────────────────────────────────────────
    mcMusicInit: function () {
        if (window._mcMusic) return;
        window._mcMusic = {
            audio: null,
            enabled: false,
            volume: 0,
            targetVolume: 0.5,
            fadeSpeed: 0.01,
            trackIndex: -1,
            tracks: [
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/calm1.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/calm2.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/calm3.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/hal1.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/hal2.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/hal3.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/hal4.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/nuance1.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/nuance2.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/piano1.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/piano2.ogg',
                'https://minecraft-music.fra1.cdn.digitaloceanspaces.com/piano3.ogg'
            ],
            fadeState: 'none', // 'fadein', 'fadeout', 'none'
            pendingPlay: false,
            fadeInterval: null
        };
        // Set up fade interval
        window._mcMusic.fadeInterval = setInterval(function () {
            var m = window._mcMusic;
            if (!m || !m.audio) return;
            if (m.fadeState === 'fadein') {
                m.volume = Math.min(m.volume + m.fadeSpeed, m.targetVolume);
                m.audio.volume = m.volume;
                if (m.volume >= m.targetVolume) m.fadeState = 'none';
            } else if (m.fadeState === 'fadeout') {
                m.volume = Math.max(m.volume - m.fadeSpeed, 0);
                m.audio.volume = m.volume;
                if (m.volume <= 0) {
                    m.audio.pause();
                    m.fadeState = 'none';
                    if (m.pendingPlay) {
                        m.pendingPlay = false;
                        if (m.enabled) {
                            // Pick next track and start playing
                            var idx = Math.floor(Math.random() * m.tracks.length);
                            m.trackIndex = idx;
                            m.audio.src = m.tracks[idx];
                            m.audio.play().catch(function () {});
                            m.fadeState = 'fadein';
                        }
                    }
                }
            }
        }, 50);
    },

    mcMusicSetEnabled: function (enabled) {
        if (!window._mcMusic) return;
        var m = window._mcMusic;
        m.enabled = !!enabled;
        if (m.enabled) {
            if (!m.audio) {
                m.audio = new Audio();
                m.audio.loop = false;
                m.audio.volume = 0;
                m.audio.addEventListener('ended', function () {
                    // When track ends, wait a moment then play next
                    setTimeout(function () {
                        if (m.enabled) {
                            var idx = Math.floor(Math.random() * m.tracks.length);
                            m.trackIndex = idx;
                            m.audio.src = m.tracks[idx];
                            m.volume = 0;
                            m.audio.volume = 0;
                            m.audio.play().catch(function () {});
                            m.fadeState = 'fadein';
                        }
                    }, 3000 + Math.random() * 10000);
                });
            }
            if (!m.audio.src || m.audio.paused) {
                var idx = Math.floor(Math.random() * m.tracks.length);
                m.trackIndex = idx;
                m.audio.src = m.tracks[idx];
                m.volume = 0;
                m.audio.volume = 0;
                m.audio.play().catch(function () {});
                m.fadeState = 'fadein';
            }
        } else {
            if (m.audio && !m.audio.paused) {
                m.fadeState = 'fadeout';
                m.pendingPlay = false;
            }
        }
    },

    mcMusicFadeOutAndSwitch: function () {
        if (!window._mcMusic) return;
        var m = window._mcMusic;
        if (!m.enabled || !m.audio) return;
        if (!m.audio.paused) {
            m.fadeState = 'fadeout';
            m.pendingPlay = true;
        } else {
            // Not currently playing, just start a new track
            var idx = Math.floor(Math.random() * m.tracks.length);
            m.trackIndex = idx;
            m.audio.src = m.tracks[idx];
            m.volume = 0;
            m.audio.volume = 0;
            m.audio.play().catch(function () {});
            m.fadeState = 'fadein';
        }
    }
});
