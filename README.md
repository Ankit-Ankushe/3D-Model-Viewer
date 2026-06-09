# 3D Model Viewer - Android Developer Task

This is a single-activity Android application built using Kotlin and Jetpack Compose that loads and displays multiple 3D models (`.glb` files) concurrently. The app strictly separates container manipulation (moving and resizing) from 3D interaction (rotating and zooming) to ensure an intuitive and highly optimized user experience.

---

## 1. 3D Library Used and Why
**Library:** `io.github.sceneview:arsceneview` (which acts as a Kotlin wrapper for Google's **Filament** engine).

**Why I picked it:**
Filament is a physically-based rendering (PBR) engine explicitly designed for Android by Google. It is written in C++, operates close to the metal using Vulkan/OpenGL ES, and provides world-class rendering with minimal memory overhead. I chose this over Unity (which is far too heavy and requires its own lifecycle) and Sceneform (which has been deprecated for years). SceneView provides a lightweight, Compose-friendly Android wrapper around Filament that allows multiple independent instances to run smoothly side-by-side without crashing the UI thread.

## 2. Performance Optimizations
Treating every feature as a performance decision, I implemented the following to ensure the app runs smoothly on low-end hardware:
* **Hardware Anti-Aliasing Disabled:** I explicitly disabled MSAA (`AntiAliasing.NONE`) inside the Filament rendering pipeline. Anti-aliasing is the #1 cause of GPU thermal throttling on low-end devices when rendering high-poly models.
* **Dynamic Resolution Enabled:** I forced `dynamicResolution` to `true` in the engine. If the device detects frame drops or thermal limits, Filament will automatically lower the internal resolution buffer to maintain 60FPS.
* **Aggressive Garbage Collection:** Tapping the 'Close' button instantly removes the Composable from the UI tree. The `AndroidView` lifecycle actively tears down the Filament engine instance, explicitly freeing up C++ texture memory rather than letting it sit in the background.
* **Manual Clear Options to Prevent Ghosting:** Instead of relying on transparent Compose surface overlapping (which causes frame-buffer accumulation "ghosting" artifacts in Filament), I tied the Compose UI Background Colors directly into the Filament engine's C++ `clearOptions`. The engine explicitly clears its own buffer on every frame redraw, saving compositing passes.

## 3. Trade-Offs Made
* **Discrete Filament Instances vs. Shared Contexts:** To satisfy the requirement of having multiple draggable models that can overlap naturally inside a Compose layout, I spawned discrete `SceneView` instances per container. The trade-off is higher memory consumption compared to building a single massive 3D world with one camera and raycasting the touch events.
* **Lack of Environmental Lighting (IBL):** For maximum performance, I avoided loading an HDR environment map for dynamic lighting. The models are lit using default directional lighting. While HDR maps make models look beautiful and realistic, they cost valuable RAM. 

## 4. What I Would Improve With More Time
* **Model and Material Instancing Cache:** Currently, if a user adds three "Fox" models, the `.glb` file and its textures are loaded into RAM three separate times. With more time, I would build a centralized `ModelCacheManager` that loads the mesh data once and returns lightweight duplicate pointers to new containers.
* **Advanced Collision Detection:** I would add border boundary constraints so users cannot drag the 3D container completely off the screen and lose it.

## 5. Known Bugs or Limitations
* **Android Studio Emulator Glitches:** Filament relies heavily on Vulkan/OpenGL ES 3.0+. When running on an Android Studio emulator with software rendering, the 3D models may render entirely black. This is a known emulator hardware limitation. **Please test the APK on a physical Android device.**
* **Depth Sorting Overlaps:** Because each model exists in its own Jetpack Compose `ElevatedCard` layer rather than sharing the same 3D space, overlapping two containers does not properly depth-sort the 3D geometries against each other.

---

## How to Use the App
1. **Add a Model:** Tap the FAB at the bottom right and select a model from the list.
2. **Move:** Make sure the container has a gray border (Normal Mode). Drag anywhere inside to move it.
3. **Resize:** Grab the explicit **"Drag to resize"** handle at the bottom-right corner of the container and drag to freely scale.
4. **Interact with 3D:** Tap the **"Hand"** icon on the top right. The container border will turn blue. You can now use 1 finger to rotate the 3D model, and 2 fingers to zoom in and out. Tap the icon again to exit.
5. **Change Colors:** Tap the **"Palette"** icon to open a dropdown menu containing 6 premium background color choices.
6. **Close:** Tap the red **"X"** icon to instantly destroy the model and free up RAM.
