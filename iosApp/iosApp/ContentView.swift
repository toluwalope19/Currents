import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        let newsApiKey = Bundle.main.object(forInfoDictionaryKey: "NEWS_API_KEY") as? String ?? ""
        let claudeApiKey = Bundle.main.object(forInfoDictionaryKey: "CLAUDE_API_KEY") as? String ?? ""
        return MainViewControllerKt.MainViewController(newsApiKey: newsApiKey,  claudeApiKey: claudeApiKey,)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}