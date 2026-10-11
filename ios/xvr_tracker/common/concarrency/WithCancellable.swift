//
//  WithCancellable.swift
//  common
//
//  Created by 久野修平 on 2026/01/23.
//

import Foundation

enum TimeoutPolicy {
    case timeout(ms: Int)
    case never
}

func withCancellableCheckedContinuation(
    _ action: () -> Void,
    onCancel: () -> Void,
    timeoutPolicy: TimeoutPolicy = .never
) async {
    
}
