import SwiftUI
import core_type
import domain
import infra
import common
import data

struct PlotPoseDataView: View {
    let orientation: UIDeviceOrientation
    let flip: Bool
    let poseLandmarksData: PoseLandmarksData
    let poseTrackersData: PoseTrackersData
    let isDrawLandmarksDataEnabled: Bool
    let isDrawTrackersDataEnabled: Bool
    let visibilityThreshold: Double
    let presenceThreshold: Double
    
    var body: some View {
        Canvas { context, size in
            let transform = landmarkToScreenTransform(
                canvasSize: size,
                imageSize: CGSize(
                    width: CGFloat(poseLandmarksData.imageSize.width),
                    height: CGFloat(poseLandmarksData.imageSize.height)
                ),
                orientation: orientation,
                flip: flip
            )
            let filteredLandmarks = poseLandmarksData.landmarks.map { landmarks in
                landmarks.filter { (_, landmark) in
                    landmark.visibility > visibilityThreshold &&
                    landmark.presence > presenceThreshold
                }
            }
            
            if isDrawLandmarksDataEnabled {
                for landmarks in filteredLandmarks {
                    drawBones(context: context, landmarks: landmarks, transform: transform)
                    drawJoints(context: context, landmarks: landmarks, transform: transform, isOutline: true)
                    drawJoints(context: context, landmarks: landmarks, transform: transform, isOutline: false)
                }
            }
            if isDrawTrackersDataEnabled {
                for (landmarks, trackers) in zip(filteredLandmarks, poseTrackersData.trackers) {
                    drawTrackerAttitudes(context: context, landmarks: landmarks, trackers: trackers, transform: transform)
                }
            }
        }
    }
    
    private func landmarkToScreenTransform(
        canvasSize: CGSize,
        imageSize: CGSize,
        orientation: UIDeviceOrientation,
        flip: Bool
    ) -> CGAffineTransform {
        let imageSize = CGSize(
            width: CGFloat(poseLandmarksData.imageSize.width),
            height: CGFloat(poseLandmarksData.imageSize.height)
        )
        let rotatedSize = orientation.isPortrait ?
        imageSize :
        CGSize(width: imageSize.height, height: imageSize.width)
        let scaleFactor = max(
            canvasSize.width / rotatedSize.width,
            canvasSize.height / rotatedSize.height
        )
        let transform = switch orientation {
        case .portrait:
            CGAffineTransform.identity
                .translatedBy(
                    x: -(rotatedSize.width * scaleFactor - canvasSize.width) / 2,
                    y: -(rotatedSize.height * scaleFactor - canvasSize.height) / 2
                )
                .scaledBy(
                    x: rotatedSize.width * scaleFactor,
                    y: rotatedSize.height * scaleFactor
                )
        case .portraitUpsideDown:
            CGAffineTransform.identity
                .translatedBy(
                    x: -(rotatedSize.width * scaleFactor - canvasSize.width) / 2,
                    y: -(rotatedSize.height * scaleFactor - canvasSize.height) / 2
                )
                .scaledBy(
                    x: rotatedSize.width * scaleFactor,
                    y: rotatedSize.height * scaleFactor
                )
                .translatedBy(x: 1, y: 1)
                .rotated(by: .pi)
        case .landscapeLeft:
            CGAffineTransform.identity
                .translatedBy(
                    x: -(rotatedSize.width * scaleFactor - canvasSize.width) / 2,
                    y: -(rotatedSize.height * scaleFactor - canvasSize.height) / 2
                )
                .scaledBy(
                    x: rotatedSize.width * scaleFactor,
                    y: rotatedSize.height * scaleFactor
                )
                .translatedBy(x: 0, y: 1)
                .rotated(by: -.pi / 2)
        case .landscapeRight:
            CGAffineTransform.identity
                .translatedBy(
                    x: -(rotatedSize.width * scaleFactor - canvasSize.width) / 2,
                    y: -(rotatedSize.height * scaleFactor - canvasSize.height) / 2
                )
                .scaledBy(
                    x: rotatedSize.width * scaleFactor,
                    y: rotatedSize.height * scaleFactor
                )
                .translatedBy(x: 1, y: 0)
                .rotated(by: .pi / 2)
        default:
            CGAffineTransform.identity
        }
        
        if flip {
            return transform
                .translatedBy(x: 1, y: 0)
                .scaledBy(x: -1, y: 1)
        }
        
        return transform
    }
    
    private func drawBones(
        context: GraphicsContext,
        landmarks: [PoseLandmarkType: PoseLandmarkData],
        transform: CGAffineTransform
    ) {
        let bones: [(PoseLandmarkType, PoseLandmarkType)] = [
            (.leftShoulder, .rightShoulder),
            (.leftShoulder, .leftElbow),
            (.leftElbow, .leftWrist),
            (.rightShoulder, .rightElbow),
            (.rightElbow, .rightWrist),
            (.leftShoulder, .leftHip),
            (.rightShoulder, .rightHip),
            (.leftHip, .rightHip),
            (.leftHip, .leftKnee),
            (.leftKnee, .leftAnkle),
            (.rightHip, .rightKnee),
            (.rightKnee, .rightAnkle),
            (.leftAnkle, .leftHeel),
            (.leftAnkle, .leftFootIndex),
            (.rightAnkle, .rightHeel),
            (.rightAnkle, .rightFootIndex)
        ]
        
        for (start, end) in bones {
            guard let startLandmark = landmarks[start],
                  let endLandmark = landmarks[end]
            else { continue }
            
            var path = Path()
            path.move(to: CGPoint(
                x: CGFloat(startLandmark.normalizedLandmark.x),
                y: CGFloat(startLandmark.normalizedLandmark.y)
            ))
            path.addLine(to: CGPoint(
                x: CGFloat(endLandmark.normalizedLandmark.x),
                y: CGFloat(endLandmark.normalizedLandmark.y)
            ))
            context.stroke(path.applying(transform), with: .color(.white), lineWidth: 3)
        }
    }
    
    private func drawJoints(
        context: GraphicsContext,
        landmarks: [PoseLandmarkType: PoseLandmarkData],
        transform: CGAffineTransform,
        isOutline: Bool
    ) {
        for (type, data) in landmarks {
            let center = CGPoint(
                x: CGFloat(data.normalizedLandmark.x),
                y: CGFloat(data.normalizedLandmark.y)
            ).applying(transform)
            let color: Color
            if isOutline {
                color = .white
            } else {
                switch type.position {
                case .left:
                    color = Color(red: 1.0, green: 0.5, blue: 0.5)
                case .center:
                    color = Color(red: 0.25, green: 0.25, blue: 0.25)
                case .right:
                    color = Color(red: 0.0, green: 0.5, blue: 1.0)
                }
            }
            let radius = isOutline ? 5.0 : 3.0
            
            context.fill(
                Path(ellipseIn: CGRect(
                    x: center.x - radius,
                    y: center.y - radius,
                    width: radius * 2,
                    height: radius * 2
                )),
                with: .color(color)
            )
        }
    }
    
    private func drawTrackerAttitudes(
        context: GraphicsContext,
        landmarks: [PoseLandmarkType: PoseLandmarkData],
        trackers: [PoseTrackerType: PoseTrackerData],
        transform: CGAffineTransform
    ) {
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .waist,
            startType: .leftHip,
            endType: .rightHip,
            landmarkPosition: 0.5,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .chest,
            startType: .leftShoulder,
            endType: .rightShoulder,
            landmarkPosition: 0.5,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .head,
            startType: .leftEar,
            endType: .rightEar,
            landmarkPosition: 0.5,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .leftKnee,
            startType: .leftHip,
            endType: .leftKnee,
            landmarkPosition: 0.8,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .rightKnee,
            startType: .rightHip,
            endType: .rightKnee,
            landmarkPosition: 0.8,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .leftFeet,
            startType: .leftAnkle,
            endType: .leftFootIndex,
            landmarkPosition: 0.5,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .rightFeet,
            startType: .rightAnkle,
            endType: .rightFootIndex,
            landmarkPosition: 0.5,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .leftElbow,
            startType: .leftShoulder,
            endType: .leftElbow,
            landmarkPosition: 0.8,
            transform: transform
        )
        drawAttitude(
            context: context,
            landmarks: landmarks,
            trackers: trackers,
            trackerType: .rightElbow,
            startType: .rightShoulder,
            endType: .rightElbow,
            landmarkPosition: 0.8,
            transform: transform
        )
    }
    
    private func drawAttitude(
        context: GraphicsContext,
        landmarks: [PoseLandmarkType: PoseLandmarkData],
        trackers: [PoseTrackerType: PoseTrackerData],
        trackerType: PoseTrackerType,
        startType: PoseLandmarkType,
        endType: PoseLandmarkType,
        landmarkPosition: Double,
        transform: CGAffineTransform
    ) {
        guard let trackerData = trackers[trackerType],
              let startLandmark = landmarks[startType],
              let endLandmark = landmarks[endType] else { return }
        
        let position = startLandmark.normalizedLandmark * (1 - landmarkPosition) +
        endLandmark.normalizedLandmark * landmarkPosition
        let offset = CGPoint(
            x: CGFloat(position.x),
            y: CGFloat(position.y)
        ).applying(transform)
        
        let rotation = Matrix4x4.rotation(axis: .z, angle: atan2(Double(transform.b), Double(transform.a)))
        
        let attitude = rotation * trackerData.attitude.unityToScreen()
        let axisX = attitude.columnVector3(0)
        let axisY = attitude.columnVector3(1)
        let axisZ = attitude.columnVector3(2)
        
        let axisLength: CGFloat = 33
        
        drawAxis(context: context, offset: offset, axis: axisX, color: .white, width: 5, length: axisLength)
        drawAxis(context: context, offset: offset, axis: axisY, color: .white, width: 5, length: axisLength)
        drawAxis(context: context, offset: offset, axis: axisZ, color: .white, width: 5, length: axisLength)
        
        drawAxis(context: context, offset: offset, axis: axisX, color: .red, width: 3, length: 30)
        drawAxis(context: context, offset: offset, axis: axisY, color: .green, width: 3, length: 30)
        drawAxis(context: context, offset: offset, axis: axisZ, color: .blue, width: 3, length: 30)
    }
    
    private func drawAxis(
        context: GraphicsContext,
        offset: CGPoint,
        axis: Vector3,
        color: Color,
        width: CGFloat,
        length: CGFloat
    ) {
        let endPoint = CGPoint(
            x: offset.x + CGFloat(axis.x) * length,
            y: offset.y + CGFloat(axis.y) * length
        )
        
        var path = Path()
        path.move(to: offset)
        path.addLine(to: endPoint)
        
        context.stroke(path, with: .color(color), style: StrokeStyle(lineWidth: width, lineCap: .round))
    }
}
