import { useEffect, useState } from "react";
import { View } from "react-native";
import WorkoutCard from "../../components/WorkoutCard";
import AppButton from "../../components/ui/AppButton";
import AppHeader from "../../components/ui/AppHeader";
import AppInput from "../../components/ui/AppInput";
import { Colors } from "../../constants/theme";
import { loadWorkouts, saveWorkouts } from "../../storage/workoutStorage";
import { Workout } from "../../types/workout";

export default function Index() {
  const [workouts, setWorkouts] = useState<Workout[]>([]);
  const [newWorkout, setNewWorkout] = useState("");

  useEffect(() => {
    async function fetchWorkouts() {
      const workoutsData = await loadWorkouts();
      setWorkouts(workoutsData || []);
    }

    fetchWorkouts();
  }, []);

  useEffect(() => {
    saveWorkouts(workouts);
  }, [workouts]);

  function addWorkout() {
    if (newWorkout.trim() === "") return;

    const workoutObject: Workout = {
      id: Date.now().toString(),
      name: newWorkout.trim(),
      date: new Date().toISOString(),
      exercises: [],
    };

    setWorkouts([...workouts, workoutObject]);
    setNewWorkout("");
  }

  function deleteWorkout(indexToDelete: number) {
    const updatedWorkouts = workouts.filter(
      (_, index) => index !== indexToDelete,
    );

    setWorkouts(updatedWorkouts);
  }

  return (
    <View
      style={{
        flex: 1,
        backgroundColor: Colors.background,
        paddingTop: 80,
        paddingHorizontal: 20,
      }}
    >
      <AppHeader
        title="Gym Diary"
        subtitle={`Total workouts: ${workouts.length}`}
      />

      <AppInput
        value={newWorkout}
        onChangeText={setNewWorkout}
        placeholder="Workout name"
        style={{
          marginBottom: 15,
        }}
        onSubmitEditing={addWorkout}
        returnKeyType="done"
      />

      <AppButton
        title="+ Add Workout"
        onPress={addWorkout}
        style={{
          marginBottom: 30,
        }}
        textStyle={{
          fontSize: 18,
          fontWeight: "bold",
        }}
      />

      {workouts.map((workout, index) => (
        <WorkoutCard
          key={workout.id}
          workout={workout}
          onDelete={() => deleteWorkout(index)}
        />
      ))}
    </View>
  );
}
