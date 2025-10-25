//
//  ShareViewController.swift
//  iosApp
//
//  Created by Sai Charan on 10/24/25.
//

import UIKit
import Social
import MobileCoreServices

class ShareViewController: SLComposeServiceViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        if let item = extensionContext?.inputItems.first as? NSExtensionItem,
           let attachment = item.attachments?.first {
            if attachment.hasItemConformingToTypeIdentifier("public.url") {
                attachment.loadItem(forTypeIdentifier: "public.url", options: nil) { data, error in
                    if let url = data as? URL {
                        print(url)
                    }
                }
            }
        }
    }
}

