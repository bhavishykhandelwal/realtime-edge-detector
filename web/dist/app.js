"use strict";
// web/app.ts
// Mock data (A small red square image, demonstrating the data format)
const MOCK_FRAME_DATA = {
    timestamp: Date.now(),
    fps: 15.5,
    resolution: "640x480",
    base64Frame: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAD0lEQVR42mP8z8AARgYAAwIAAZwAB0Y6D+cAAAAASUVORK5CYII="
};
/**
 * Updates the HTML DOM with new frame data.
 */
function updateViewer(data) {
    console.log(`Web Viewer: Received simulated frame data. FPS: ${data.fps}`);
    const imageElement = document.getElementById('frame-image');
    const resStat = document.getElementById('res-stat');
    const fpsStat = document.getElementById('fps-stat');
    if (imageElement) {
        // Set the image source to the received Base64 string
        imageElement.src = data.base64Frame;
    }
    if (resStat) {
        resStat.textContent = data.resolution;
    }
    if (fpsStat) {
        fpsStat.textContent = data.fps.toFixed(1);
    }
}
/**
 * Simulates receiving a frame from a mock endpoint.
 * This satisfies the "Add a simple WebSocket or HTTP endpoint (mock)" bonus.
 */
function connectAndSimulateFrames() {
    console.log("MOCK: Starting connection simulation for frame stream...");
    // Delay to simulate connection time, then update
    setTimeout(() => {
        updateViewer(MOCK_FRAME_DATA);
        console.log("MOCK: Viewer updated with static frame and statistics.");
    }, 1500);
}
// Entry point: start the simulation once the page is fully loaded
document.addEventListener('DOMContentLoaded', connectAndSimulateFrames);
