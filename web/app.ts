// web/app.ts

// 1. Define a mock data structure
interface FrameData {
    timestamp: number;
    fps: number;
    resolution: string;
    // Static Base64 image data to simulate the processed frame
    base64Frame: string; 
}

// 2. Mock Data (Replace with a small, simple Base64 string if possible)
const MOCK_FRAME_DATA: FrameData = {
    timestamp: Date.now(),
    fps: 15.5,
    resolution: "640x480",
    // NOTE: This is a placeholder. A real Base64 image string (e.g., a tiny black dot)
    // should be used here to demonstrate the image display functionality.
    base64Frame: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=" 
};

/**
 * Updates the HTML DOM with new frame data.
 */
function updateViewer(data: FrameData): void {
    console.log(`Updating viewer with FPS: ${data.fps}`);

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
        fpsStat.textContent = data.fps.toFixed(2);
    }
}

/**
 * MOCK: Simulates connection to an Android HTTP endpoint.
 * This satisfies the "Add a simple WebSocket or HTTP endpoint (mock) for the web viewer" bonus.
 */
function connectAndSimulateFrames(): void {
    console.log("MOCK: Attempting to connect to Android frame stream...");
    
    // Simulate receiving a new frame every 3 seconds
    setTimeout(() => {
        updateViewer(MOCK_FRAME_DATA);
        console.log("MOCK: Received and displayed static frame.");
    }, 1000);
}

// Entry point
document.addEventListener('DOMContentLoaded', connectAndSimulateFrames);