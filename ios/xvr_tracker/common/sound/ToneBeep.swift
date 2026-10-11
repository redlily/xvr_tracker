import Foundation
import AVFoundation

public final actor ToneBeep {
    public enum Tone {
        case none(duration: Double)
        case sin(
            frequency: Double,
            duration: Double,
            gain: Double = 1.0,
            attack: Double = 0.005,
            release: Double = 0.005
        )
        case square(
            frequency: Double,
            duration: Double,
            gain: Double = 1.0,
            attack: Double = 0.005,
            release: Double = 0.005,
            step: Int = 4
        )
        
        func getDuration(sampleRate: Double) -> AVAudioFrameCount {
            let duration = switch self {
            case .none(let duration),
                    .sin(_, let duration, _, _, _),
                    .square(_, let duration, _, _, _, _):
                duration
            }
            return AVAudioFrameCount(sampleRate * duration)
        }
    }
    
    private let engine = AVAudioEngine()
    private let node = AVAudioPlayerNode()
    private let format: AVAudioFormat
    
    public init() {
        let outputFormat = engine.mainMixerNode.outputFormat(forBus: 0)
        format = AVAudioFormat(
            commonFormat: outputFormat.commonFormat,
            sampleRate: outputFormat.sampleRate,
            channels: 1,
            interleaved: false
        ) ?? outputFormat
        
        engine.attach(node)
        engine.connect(node, to: engine.mainMixerNode, format: format)
        
        let session = AVAudioSession.sharedInstance()
        do {
            try session.setCategory(.playback)
            try session.setActive(true)
            try engine.start()
        } catch {
            print("Engine start error: \(error)")
        }
    }
    
    public func beep(tones: [Tone]) {
        guard !tones.isEmpty else { return }
        
        let sampleRate = format.sampleRate
        let totalFrames = tones.reduce(AVAudioFrameCount(0)) { $0 + $1.getDuration(sampleRate: sampleRate) }
        
        guard let buffer = AVAudioPCMBuffer(
            pcmFormat: format,
            frameCapacity: totalFrames
        ) else {
            return
        }
        buffer.frameLength = totalFrames
        
        guard let channelData = buffer.floatChannelData else {
            return
        }
        
        let channelCount = Int(buffer.format.channelCount)
        guard channelCount > 0 else {
            return
        }
        
        var frameOffset: AVAudioFrameCount = 0
        
        for tone in tones {
            let frameCount = tone.getDuration(sampleRate: sampleRate)
            
            switch tone {
            case .none:
                for channel in 0..<channelCount {
                    let ptr = channelData[channel]
                    for i in 0..<Int(frameCount) {
                        ptr[Int(frameOffset) + i] = 0.0
                    }
                }
                
            case .sin(let frequency, _, let gain, let attack, let release):
                let thetaInc = 2.0 * .pi * frequency / sampleRate
                let attackFrames = Int(sampleRate * attack)
                let releaseFrames = Int(sampleRate * release)
                for index in 0..<Int(frameCount) {
                    let sample = sin(thetaInc * Double(index))
                    let fade = if index < attackFrames {
                        Double(index) / Double(attackFrames)
                    } else if index > Int(frameCount) - releaseFrames {
                        Double(Int(frameCount) - index) / Double(releaseFrames)
                    } else {
                        1.0
                    }
                    for channel in 0..<channelCount {
                        channelData[channel][Int(frameOffset) + index] = Float(sample * gain * fade)
                    }
                }
                
            case .square(let frequency, _, let gain, let attack, let release, let step):
                let thetaInc = 2.0 * .pi * frequency / sampleRate
                let attackFrames = Int(sampleRate * attack)
                let releaseFrames = Int(sampleRate * release)
                for index in 0..<Int(frameCount) {
                    let sample = stride(from: 1, through: step * 2, by: 2).reduce(0.0) {
                        let k = Double($1)
                        return $0 + sin(thetaInc * Double(index) * k) / k
                    }
                    let fade = if index < attackFrames {
                        Double(index) / Double(attackFrames)
                    } else if index > Int(frameCount) - releaseFrames {
                        Double(Int(frameCount) - index) / Double(releaseFrames)
                    } else {
                        1.0
                    }
                    for channel in 0..<channelCount {
                        channelData[channel][Int(frameOffset) + index] = Float(sample * gain * fade)
                    }
                }
            }
            
            frameOffset += frameCount
        }
        
        node.stop()
        node.scheduleBuffer(buffer, at: nil, options: [], completionHandler: nil)
        node.play()
    }
}

extension ToneBeep {
    public func emergency() async {
        let tones: [Tone] = [
            .square(frequency: 1046.5, duration: 0.125, gain: 0.5, attack: 0.01, release: 0.01, step: 5),
            .none(duration: 0.01),
            .square(frequency: 1046.5, duration: 0.125, gain: 0.5, attack: 0.01, release: 0.01, step: 5),
            .none(duration: 0.01),
            .square(frequency: 1046.5, duration: 0.125, gain: 0.5, attack: 0.01, release: 0.01, step: 5)
        ]
        beep(tones: tones)
    }
}

