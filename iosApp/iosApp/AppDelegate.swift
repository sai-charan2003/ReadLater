//
//  AppDelegate.swift
//  iosApp
//
//  Created by Sai Charan on 8/21/25.
//

import UIKit
import GoogleSignIn


class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        // Use the Swift SDK singleton
        let handled = GIDSignIn.sharedInstance.handle(url)
        return handled
    }
}
