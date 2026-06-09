# Activity

**Android Developer Task — 3D Model Viewer**

Build a single-activity Android app that can load and show multiple 3D models on the screen at the same time. The user should be able to move, resize, and interact with each model. Read everything below before you start.

# **What You Need to Build**

## **1\. One Activity Only**

The whole app runs inside a single Activity. No fragments. No second screen. Everything happens on one canvas.

## **2\. Load 3D Models (.glb files only)**

Bundle 3 to 5 sample 3D model files (.glb)  inside the app. Add a simple button or menu that lets the user pick a model and add it to the screen.

You can find free sample models on websites like Sketchfab. Download models that are free to use.

## **3\. Multiple Models on Screen at the Same Time**

The user should be able to add many models, one after another. All models stay on the screen together. The app should work smoothly with at least 5 models loaded at once.

## **4\. Each Model is in a Draggable Container**

The user can drag any model anywhere on the screen using one finger. The model should follow the finger smoothly. And the model can be placed anywhere on the full screen.

## **5\. Each Model Container is Resizable**

The user can make any model container bigger or smaller. You can use pinch-to-resize or corner handles, your choice. When the model is resized, the 3D content inside should also scale to fit.

## **6\. Each Model Has Two Buttons**

Attached to every model on the screen, there must be two buttons that are always visible.

**Interact Button.**

When the user taps this button, the model enters interaction mode. In this mode, one finger drag on the model rotates the 3D model, and two-finger pinch zooms the 3D model in and out. The model itself does NOT move on the screen during this mode. Tapping the same button again exits interaction mode and goes back to normal mode.

**Close Button.**

Tapping this removes the model from the screen completely.

## 

## 

## **7\. Two Modes Must Stay Separate**

In normal mode, dragging moves the model on the screen, and pinch resizes the model's container.

In interaction mode, dragging rotates the 3D model, and pinch zooms the 3D content.

The two modes should never mix.

# 

# **Performance is the Most Important Requirement**

This is the single most important part of the task. The app must be heavily performance optimised. It must run smoothly even on very low-end Android devices with limited RAM, weak CPUs, and old GPUs. Treat every feature as a performance decision.

You should profile your app and be ready to explain your performance decisions in the README. We will be running the APK on a low-end device to verify.

# 

# **Technical Details**

* Language: Kotlin

* Minimum SDK: 24

* 3D library: Your choice. You can use Filament, SceneView, or any other 3D library you are comfortable with. Tell us why you picked it and how it helps with performance.

* UI: Views or Jetpack Compose, your choice.

# **What to Submit**

* A GitHub repository link, public or private with access shared to us.

* A signed APK that we can install.

* A video walkthrough of the App and explanation of the code using any video capturing tools like Loom and share the URL

* A short README, one page is enough, that explains which 3D library you used and why, the main performance optimizations you applied, the trade-offs you made, what you would improve with more time, and any known bugs or limitations.

# **Deadline**

Please share your submission within 3 days of receiving the task. If you need more time, let us know early.

If any part of the task is unclear, reach out, and we will help.

# Asessment

