//
//  AppDelegate.swift
//  iosApp
//
//  Created by Sai Charan on 8/21/25.
//

import UIKit
import GoogleSignIn
import ComposeApp


class AppDelegate: NSObject, UIApplicationDelegate {
    
    func applicationDidFinishLaunching(_ application: UIApplication) {
        MainViewControllerKt.MainViewController()
        
    }

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
