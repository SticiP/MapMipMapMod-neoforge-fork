# MapMipMapMod (NeoForge 1.21.1)

A NeoForge 1.21.1 port and enhancement of the original [MapMipMapMod](https://github.com/PimentelGamer/MapMipMapMod) by PimentelGamer.

When large amounts of maps are placed in item frames and viewed from a distance, Minecraft's default rendering causes severe visual artifacts (Moiré patterns) and Z-fighting. This happens because mipmaps for dynamic textures are disabled by default. This mod aims to solve these niche rendering and performance problems, making it a must-have for Map Art builders.

## 🖼️ Visual Comparison

The image on the left shows default vanilla rendering (Level 0 Mipmaps) with heavy noise and artifacts. The image on the right shows the mod in action (Level 4 Mipmaps), resulting in a smooth and clean image.

*(Original Mod Example - Floyd-Steinberg dithered image at 10 blocks away)*
![Original Comparison](https://github.com/user-attachments/assets/d76ed9df-9245-4fb9-8a61-b77c2d4d38a3)

### Siemens Star & Noise Map Art Test
*(Replace these placeholder texts by dragging and dropping your own screenshots here)*
![Vanilla - No Mipmap](https://github.com/user-attachments/assets/f64a9d64-9823-4bcc-a66b-8e7d5e00ad0b)
![Modded - Level 4 Mipmap](https://github.com/user-attachments/assets/dfa80f3f-d450-464e-a47b-803a92359d34)

---

## ✨ Features & Enhancements in this Port

This version was built from the ground up for NeoForge 1.21.1, bringing modern compatibility and new features:

* **GPU-Accelerated Mipmapping:** Generates mipmaps directly on the GPU instead of the CPU. Smooths out map textures from a distance without compromising performance.
* **Smart Map Updates (Lag Fix):** Vanilla Minecraft constantly tries to update map data. This mod stops texture updates for **Locked Maps** (maps locked via a Cartography Table), completely removing CPU lag spikes when looking at massive map walls.
* **Depth Bias (Z-Fighting Fix):** Adds a configurable depth bias to slightly push maps forward on the Z-axis, stopping the map and the item frame from flickering into each other.
* **Seamless UI Integration:** Full configuration menu support directly in the vanilla NeoForge Mods screen, as well as native integration with the **Sodium** options menu.

---

## 📊 Performance Benchmarks

Generating MipMaps on the GPU has **practically zero impact on performance**. Whether you play on Vanilla (Level 0) or maximum filtering (Level 8), your FPS will remain stable.

All tests were performed in a completely static Flatworld environment to ensure identical rendering loads. The test scene consisted of two massive map arts (a noise map and a Siemens star), totaling **776 maps** rendered simultaneously on screen.

**Hardware Setup:**
* **GPU:** NVIDIA RTX 3070 Ti (Laptop)
* **CPU:** Intel Core i9-12900H
* **RAM:** 32GB

*Average results across all MipMap levels (0 through 8):*

| Setup / Mods | Average FPS | 1% Lows | 0.1% Lows |
| :--- | :---: | :---: | :---: |
| **Mod Only** | ~128 | 84 | 73 |
| **Mod + Sodium** | ~222 | 150 | 117 |
| **Mod + Sodium + Iris + FastItemFrames*** | ~63 | 49 | 40 |

*\*Tested with Complementary Reimagined Shaders on High Settings.*

---

## 🤝 Credits
* Massive thanks to the original author, **[Jalvaviel](https://github.com/Jalvaviel)**, for the original concept, OpenGL implementation, and Forge codebase.
* Thanks to **EarilGarion** for his OpenGL explanations in the original repository.
