// web/app.ts

// Define the expected data structure
interface FrameData {
    timestamp: number;
    fps: number;
    resolution: string;
    // Static Base64 image data to simulate the processed frame
    base64Frame: string; 
}

// Mock data (A small red square image, demonstrating the data format)
const MOCK_FRAME_DATA: FrameData = {
    timestamp: Date.now(),
    fps: 15.5,
    resolution: "640x480",
    base64Frame: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAIAAAACCAYAAABytg0kAAAAD0lEQVR42mP8z8AARgYAAwIAAZwAB0Y6D+cAAAAASUVORK5CYII=" 
};

/**
 * Updates the HTML DOM with new frame data.
 */
function updateViewer(data: FrameData): void {
    console.log(`Web Viewer: Received simulated frame data. FPS: ${data.fps}`);

    const imageElement = document.getElementById('frame-image') as HTMLImageElement;
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
function connectAndSimulateFrames(): void {
    console.log("MOCK: Starting connection simulation for frame stream...");
    
    // Delay to simulate connection time, then update
    setTimeout(() => {
        updateViewer(MOCK_FRAME_DATA);
        console.log("MOCK: Viewer updated with static frame and statistics.");
    }, 1500);
}

// Entry point: start the simulation once the page is fully loaded
document.addEventListener('DOMContentLoaded', connectAndSimulateFrames);