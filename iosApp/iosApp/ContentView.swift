import SwiftUI
import shared
import KMMViewModelSwiftUI

struct OtherContentView: View {
    @StateViewModel var dualViewModel: DualViewModel = DualViewModel()
    
    var body: some View {
        NavigationView {
            listView()
            .navigationBarTitle("SpaceX Launches")
            .navigationBarItems(trailing:
                Button("Load Data") {
                    self.dualViewModel.retrieveLaunches()
            })
        }
    }
    
    private func listView() -> AnyView {
        
        let resource : Resource = dualViewModel.launchesResource.value as! Resource
        
        if (resource is Resource.Loading) {
            return AnyView(Text("Loading...").multilineTextAlignment(.center))
        } else if (resource is Resource.Content) {
            let content = resource as! Resource.Content
            return AnyView(List(content.launches) { launch in
                    RocketLaunchRow(rocketLaunch: launch)
                })
        } else if (resource is Resource.Error) {
            return AnyView(Text("Failed to load data").multilineTextAlignment(.center))
        } else {
            return AnyView(Text("").multilineTextAlignment(.center))
        }
    }
}

extension RocketLaunch: Identifiable { }
