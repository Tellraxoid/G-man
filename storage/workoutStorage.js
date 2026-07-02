import AsyncStorage from "@react-native-async-storage/async-storage";

export async function loadWorkouts(){
    try {
        const savedWorkouts =
        await AsyncStorage.getItem(
            "workouts"
        );
        if (!savedWorkouts) {
            return [];
        }
        return JSON.parse(savedWorkouts);
    } catch (error) {
        console.error("Error loading workouts:", error);
        return [];
    }
}

export async function saveWorkouts(workouts) {
    try {
        await AsyncStorage.setItem(
            "workouts",
            JSON.stringify(workouts)
        );
    } catch (error) {
        console.error("Error saving workouts:", error);
    }
}