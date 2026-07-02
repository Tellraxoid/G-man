import {Tabs} from "expo-router";

export default function TabsLayout() {
    return (    
        <Tabs
            screenOptions={{
                headerShown: false,
                tabBarStyle: {
                    backgroundColor: "#111",
                    borderTopColor: "#222",
                },
                tabBarActiveTintColor: "#fff",
                tabBarInactiveTintColor: "#888",
            }}  
        >   
            <Tabs.Screen
                name="index"
                options={{
                    title: "Workouts",
                }}
            />
            <Tabs.Screen
                name="exercises"
                options={{
                    title: "Exercises",
                }}
            />
            <Tabs.Screen
                name="settings"
                options={{
                    title: "Settings",
                }}
            />
            <Tabs.Screen
                name="exercise"
                options={{
                    href:null,
                }}
            />
        </Tabs> 
    );
}