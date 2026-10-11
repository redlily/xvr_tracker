class JointLine {
    constructor(start, end) {
        this.start = start;
        this.end = end;
    }
}

let POSE_LANDMARK_LINES = [
    new JointLine("mouth_left", "mouth_right"),
    new JointLine("left_shoulder", "right_shoulder"),
    new JointLine("left_hip", "right_hip"),
    new JointLine("nose", "left_eye_inner"),
    new JointLine("left_eye_inner", "left_eye"),
    new JointLine("left_eye", "left_eye_outer"),
    new JointLine("left_eye_outer", "left_ear"),
    new JointLine("left_shoulder", "left_elbow"),
    new JointLine("left_elbow", "left_wrist"),
    new JointLine("left_wrist", "left_thumb"),
    new JointLine("left_wrist", "left_index"),
    new JointLine("left_wrist", "left_pinky"),
    new JointLine("left_index", "left_pinky"),
    new JointLine("left_shoulder", "left_hip"),
    new JointLine("left_hip", "left_knee"),
    new JointLine("left_knee", "left_ankle"),
    new JointLine("left_ankle", "left_heel"),
    new JointLine("left_ankle", "left_foot_index"),
    new JointLine("left_heel", "left_foot_index"),
    new JointLine("nose", "right_eye_inner"),
    new JointLine("right_eye_inner", "right_eye"),
    new JointLine("right_eye", "right_eye_outer"),
    new JointLine("right_eye_outer", "right_ear"),
    new JointLine("right_shoulder", "right_elbow"),
    new JointLine("right_elbow", "right_wrist"),
    new JointLine("right_wrist", "right_thumb"),
    new JointLine("right_wrist", "right_index"),
    new JointLine("right_wrist", "right_pinky"),
    new JointLine("right_index", "right_pinky"),
    new JointLine("right_shoulder", "right_hip"),
    new JointLine("right_hip", "right_knee"),
    new JointLine("right_knee", "right_ankle"),
    new JointLine("right_ankle", "right_heel"),
    new JointLine("right_ankle", "right_foot_index"),
    new JointLine("right_heel", "right_foot_index")
]

class LandmarkType {
    constructor(landmarkId, alignment) {
        this.landmarkId = landmarkId;
        this.alignment = alignment;
    }
}

let LANDMARK_TYPES = {
    "nose": new LandmarkType(0, "center"),
    "left_eye_inner": new LandmarkType(1, "left"),
    "left_eye": new LandmarkType(2, "left"),
    "left_eye_outer": new LandmarkType(3, "left"),
    "right_eye_inner": new LandmarkType(4, "right"),
    "right_eye": new LandmarkType(5, "right"),
    "right_eye_outer": new LandmarkType(6, "right"),
    "left_ear": new LandmarkType(7, "left"),
    "right_ear": new LandmarkType(8, "right"),
    "mouth_left": new LandmarkType(9, "left"),
    "mouth_right": new LandmarkType(10, "right"),
    "left_shoulder": new LandmarkType(11, "left"),
    "right_shoulder": new LandmarkType(12, "right"),
    "left_elbow": new LandmarkType(13, "left"),
    "right_elbow": new LandmarkType(14, "right"),
    "left_wrist": new LandmarkType(15, "left"),
    "right_wrist": new LandmarkType(16, "right"),
    "left_pinky": new LandmarkType(17, "left"),
    "right_pinky": new LandmarkType(18, "right"),
    "left_index": new LandmarkType(19, "left"),
    "right_index": new LandmarkType(20, "right"),
    "left_thumb": new LandmarkType(21, "left"),
    "right_thumb": new LandmarkType(22, "right"),
    "left_hip": new LandmarkType(23, "left"),
    "right_hip": new LandmarkType(24, "right"),
    "left_knee": new LandmarkType(25, "left"),
    "right_knee": new LandmarkType(26, "right"),
    "left_ankle": new LandmarkType(27, "left"),
    "right_ankle": new LandmarkType(28, "right"),
    "left_heel": new LandmarkType(29, "left"),
    "right_heel": new LandmarkType(30, "right"),
    "left_foot_index": new LandmarkType(31, "left"),
    "right_foot_index": new LandmarkType(32, "right"),
}

let TRACKER_ALIGNMENT_TYPES = {
    "head": "center",
    "chest": "center",
    "left_elbow": "left",
    "right_elbow": "right",
    "waist": "center",
    "left_knee": "left",
    "right_knee": "right",
    "left_feet": "left",
    "right_feet": "right"
}

class Matrix4x4 {
    constructor(
        m11, m12, m13, m14,
        m21, m22, m23, m24,
        m31, m32, m33, m34,
        m41, m42, m43, m44) {
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m14 = m14;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m24 = m24;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.m34 = m34;
        this.m41 = m41;
        this.m42 = m42;
        this.m43 = m43;
        this.m44 = m44;
    }

    static scale(x, y, z) {
        if (arguments.length === 1) {
            return new Matrix4x4(
                x, 0, 0, 0,
                0, x, 0, 0,
                0, 0, x, 0,
                0, 0, 0, 1
            );
        } else {
            return new Matrix4x4(
                x, 0, 0, 0,
                0, y, 0, 0,
                0, 0, z, 0,
                0, 0, 0, 1
            );
        }
    }

    static translate(x = 0, y = 0, z = 0) {
        return new Matrix4x4(
            1, 0, 0, x,
            0, 1, 0, y,
            0, 0, 1, z,
            0, 0, 0, 1
        )
    }
}

class Vector3 {
    constructor(x, y, z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

function mulMatrix4x4AndVector3(m, v) {
    let x = m.m11 * v.x + m.m12 * v.y + m.m13 * v.z + m.m14;
    let y = m.m21 * v.x + m.m22 * v.y + m.m23 * v.z + m.m24;
    let z = m.m31 * v.x + m.m32 * v.y + m.m33 * v.z + m.m34;
    let w = m.m41 + m.m42 + m.m43 + m.m44;
    return new Vector3(x / w, y / w, z / w);
}

function mulMatrix4x4(m1, m2) {
    let m11 = m1.m11 * m2.m11 + m1.m12 * m2.m21 + m1.m13 * m2.m31 + m1.m14 * m2.m41;
    let m12 = m1.m11 * m2.m12 + m1.m12 * m2.m22 + m1.m13 * m2.m32 + m1.m14 * m2.m42;
    let m13 = m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13 * m2.m33 + m1.m14 * m2.m43;
    let m14 = m1.m11 * m2.m14 + m1.m12 * m2.m24 + m1.m13 * m2.m34 + m1.m14 * m2.m44;
    let m21 = m1.m21 * m2.m11 + m1.m22 * m2.m21 + m1.m23 * m2.m31 + m1.m24 * m2.m41;
    let m22 = m1.m21 * m2.m12 + m1.m22 * m2.m22 + m1.m23 * m2.m32 + m1.m24 * m2.m42;
    let m23 = m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23 * m2.m33 + m1.m24 * m2.m43;
    let m24 = m1.m21 * m2.m14 + m1.m22 * m2.m24 + m1.m23 * m2.m34 + m1.m24 * m2.m44;
    let m31 = m1.m31 * m2.m11 + m1.m32 * m2.m21 + m1.m33 * m2.m31 + m1.m34 * m2.m41;
    let m32 = m1.m31 * m2.m12 + m1.m32 * m2.m22 + m1.m33 * m2.m32 + m1.m34 * m2.m42;
    let m33 = m1.m31 * m2.m13 + m1.m32 * m2.m23 + m1.m33 * m2.m33 + m1.m34 * m2.m43;
    let m34 = m1.m31 * m2.m14 + m1.m32 * m2.m24 + m1.m33 * m2.m34 + m1.m34 * m2.m44;
    let m41 = m1.m41 * m2.m11 + m1.m42 * m2.m21 + m1.m43 * m2.m31 + m1.m44 * m2.m41;
    let m42 = m1.m41 * m2.m12 + m1.m42 * m2.m22 + m1.m43 * m2.m32 + m1.m44 * m2.m42;
    let m43 = m1.m41 * m2.m13 + m1.m42 * m2.m23 + m1.m43 * m2.m33 + m1.m44 * m2.m43;
    let m44 = m1.m41 * m2.m14 + m1.m42 * m2.m24 + m1.m43 * m2.m34 + m1.m44 * m2.m44;
    return new Matrix4x4(
        m11, m12, m13, m14,
        m21, m22, m23, m24,
        m31, m32, m33, m34,
        m41, m42, m43, m44
    );
}

const BACKGROUND_COLOR = "#ffffff";

const SPLIT_LINE_COLOR = "#616161";
const SPLIT_LINE_WIDTH = 2;

const POSE_LINE_COLOR = "#616161";
const POSE_LINE_WIDTH = 2;
const LEFT_LANDMARK_COLOR = "#FF8080";
const CENTER_LANDMARK_COLOR = "#404040"
const RIGHT_LANDMARK_COLOR = "#0080FF";

const params = new URLSearchParams(window.location.search);

console.log(window.location.search)

const data_path = params.get("data_path");

const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");

resizeCanvas();
window.addEventListener("resize", resizeCanvas);

var poseLandmarksData = null
var poseTrackersData = null

function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    draw();
}

function draw() {
    ctx.fillStyle = BACKGROUND_COLOR;
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    drawLandmarks(
        0, canvas.height / 2, canvas.width / 2, canvas.height / 2,
        new Matrix4x4(-1, 0, 0, 0,
            0, -1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        ));
    drawLandmarks(
        canvas.width / 2, canvas.height / 2, canvas.width / 2, canvas.height / 2,
        new Matrix4x4(
            0, 0, 1, 0,
            0, -1, 0, 0,
            1, 0, 0, 0,
            0, 0, 0, 1
        ));
    drawLandmarks(
        canvas.width / 2, 0, canvas.width / 2, canvas.height / 2,
        new Matrix4x4(
            0, 0, 1, 0,
            -1, 0, 0, 0,
            0, -1, 0, 0,
            0, 0, 0, 1
        ));


    ctx.strokeStyle = SPLIT_LINE_COLOR;
    ctx.lineWidth = SPLIT_LINE_WIDTH;

    ctx.beginPath();
    ctx.moveTo(0, canvas.height / 2);
    ctx.lineTo(canvas.width, canvas.height / 2);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, canvas.height);
    ctx.stroke();
}

Matrix4x4.prototype.mul = function (v) {
    if (v instanceof Matrix4x4) {
        return mulMatrix4x4(this, v);
    } else if (v instanceof Vector3) {
        return mulMatrix4x4AndVector3(this, v);
    }
}

Matrix4x4.prototype.mulScale = function (x, y, z) {
    if (arguments.length === 1) {
        return mulMatrix4x4(this, Matrix4x4.scale(x));
    } else {
        return mulMatrix4x4(this, Matrix4x4.scale(x, y, z));
    }
}

Matrix4x4.prototype.mulTranslate = function (x = 0, y = 0, z = 0) {
    return mulMatrix4x4(this, Matrix4x4.translate(x, y, z));
}

function drawLandmarks(x, y, width, height, matrix) {
    if (poseLandmarksData == null) return;

    let scale = Math.min(width, height) / 2;
    let poseLandmarkMatrix = Matrix4x4.translate(width / 2, height / 2)
        .mul(matrix)
        .mulScale(scale);

    ctx.save();

    ctx.beginPath();
    ctx.rect(x, y, width, height);
    ctx.clip();
    ctx.translate(x, y);

    if (poseLandmarksData != null) {
        poseLandmarksData.landmarks.forEach((landmarks) => {
            POSE_LANDMARK_LINES.forEach((value) => {
                let start = landmarks[value.start]
                let end = landmarks[value.end]
                if (start != null && end != null) {
                    let startPosition = poseLandmarkMatrix.mul(new Vector3(
                        start.worldLandmark.x,
                        start.worldLandmark.y,
                        start.worldLandmark.z
                    ));
                    let endPosition = poseLandmarkMatrix.mul(new Vector3(
                        end.worldLandmark.x,
                        end.worldLandmark.y,
                        end.worldLandmark.z
                    ));

                    ctx.beginPath();
                    ctx.strokeStyle = POSE_LINE_COLOR;
                    ctx.lineWidth = POSE_LINE_WIDTH;
                    ctx.moveTo(startPosition.x, startPosition.y);
                    ctx.lineTo(endPosition.x, endPosition.y);
                    ctx.stroke();
                }
            });

            for (let key in landmarks) {
                let landmark = landmarks[key];
                let worldLandmark = landmark.worldLandmark;
                let position = poseLandmarkMatrix.mul(new Vector3(
                    worldLandmark.x,
                    worldLandmark.y,
                    worldLandmark.z));

                ctx.beginPath()
                ctx.arc(
                    position.x, position.y,
                    4,
                    0, Math.PI * 2);
                ctx.fillStyle = POSE_LINE_COLOR;
                ctx.fill();
            }

            for (let key in landmarks) {
                let landmarkType = LANDMARK_TYPES[key];
                let landmark = landmarks[key];
                let worldLandmark = landmark.worldLandmark;
                let position = poseLandmarkMatrix.mul(new Vector3(
                    worldLandmark.x,
                    worldLandmark.y,
                    worldLandmark.z));

                ctx.beginPath()
                ctx.arc(
                    position.x, position.y,
                    2,
                    0, Math.PI * 2);
                switch (landmarkType.alignment) {
                    case "left":
                        ctx.fillStyle = LEFT_LANDMARK_COLOR;
                        break;
                    case "center":
                        ctx.fillStyle = CENTER_LANDMARK_COLOR;
                        break;
                    case "right":
                        ctx.fillStyle = RIGHT_LANDMARK_COLOR;
                        break
                }
                ctx.fill();
            }
        });
    }

    let poseTrackerMatrix = Matrix4x4.translate(width / 2, height / 2)
        .mul(matrix)
        .mulScale(scale);


    if (poseTrackersData != null) {
        let modificationMatrix = poseTrackersData.modificationMatrix

        poseTrackersData.trackers.forEach((trackers) => {
            for (let key in trackers) {
                let tracker = trackers[key];
                let position = poseTrackerMatrix.mul(new Vector3(
                    tracker.attitude["m03"],
                    tracker.attitude["m13"],
                    tracker.attitude["m23"]
                ));

                ctx.beginPath()
                ctx.arc(
                    position.x, position.y,
                    8,
                    0, Math.PI * 2);
                ctx.fillStyle = POSE_LINE_COLOR;
                ctx.fill();
            }

            for (let key in trackers) {
                let trackerAlignmentType = TRACKER_ALIGNMENT_TYPES[key];
                let tracker = trackers[key];
                let position = poseTrackerMatrix.mul(new Vector3(
                    tracker.attitude["m03"],
                    tracker.attitude["m13"],
                    tracker.attitude["m23"]
                ));

                ctx.beginPath()
                ctx.arc(
                    position.x, position.y,
                    7,
                    0, Math.PI * 2);
                switch (trackerAlignmentType) {
                    case "left":
                        ctx.fillStyle = LEFT_LANDMARK_COLOR;
                        break;
                    case "center":
                        ctx.fillStyle = CENTER_LANDMARK_COLOR;
                        break;
                    case "right":
                        ctx.fillStyle = RIGHT_LANDMARK_COLOR;
                        break
                }
                ctx.fill();
            }
        });
    }

    ctx.restore();
}

const socket = new WebSocket(`${data_path != null ? `ws://${data_path}` : "."}/data`)

socket.addEventListener("open", (event) => {
    socket.send(`open ${event.data}`);
});

socket.addEventListener("close", (event) => {
    console.log(`close ${event.data}`);
});

socket.addEventListener("message", (event) => {
    let data = JSON.parse(String(event.data));
    if (data.channel === "pose_landmarks_data") {
        poseLandmarksData = data.data;
    } else if (data.channel === "pose_trackers_data") {
        poseTrackersData = data.data;
    }
    draw()
});

socket.addEventListener("error", (event) => {
    console.log(`error ${event.data}`);
});
