import { useEffect, useState } from "react";

import { Workout } from "../../types/workout";

import WorkoutCard from "../../components/WorkoutCard";
import {
  loadWorkouts,
  saveWorkouts,
} from "../../storage/workoutStorage";

import {
  View,
  Text,
  TouchableOpacity,
  TextInput,
} from "react-native";


export default function Index() {
  const [workouts, setWorkouts] = useState<Workout[]>([]);

  const [newWorkout, setNewWorkout] =
    useState("");

  // Load workouts on app start
  useEffect(() => {
    async function fetchWorkouts() {
      const workoutsData =
        await loadWorkouts(
        );
      setWorkouts(workoutsData);
    }
    fetchWorkouts();
  }, []);

  // Save workouts when state changes
  useEffect(() => {
    saveWorkouts(workouts);
  }, [workouts]);

 

  function addWorkout() {
    if (newWorkout.trim() === "") return;

    const workoutObject = {
      id: Date.now().toString(),
      name: newWorkout,
      date: new Date().toISOString(),
      exercises: [],
    };

    const updatedWorkouts = [
      ...workouts,
      workoutObject,
    ];

    setWorkouts(updatedWorkouts);

    setNewWorkout("");
  }

  function deleteWorkout(indexToDelete) {
    const updatedWorkouts =
      workouts.filter(
        (_, index) =>
          index !== indexToDelete
      );

    setWorkouts(updatedWorkouts);
  }

  return (
    <View
      style={{
        flex: 1,
        backgroundColor: "#111",
        paddingTop: 80,
        paddingHorizontal: 20,
      }}
    >
      <Text
        style={{
          color: "white",
          fontSize: 32,
          fontWeight: "bold",
          marginBottom: 30,
        }}
      >
        Gym Diary
      </Text>

      <TextInput
        value={newWorkout}
        onChangeText={setNewWorkout}
        placeholder="Workout name"
        placeholderTextColor="#888"
        style={{
          backgroundColor: "#222",
          color: "white",
          padding: 15,
          borderRadius: 15,
          marginBottom: 15,
          fontSize: 16,
        }}
      />

      <TouchableOpacity
        onPress={addWorkout}
        style={{
          backgroundColor: "#22c55e",
          padding: 15,
          borderRadius: 15,
          alignItems: "center",
          marginBottom: 30,
        }}
      >
        <Text
          style={{
            color: "white",
            fontSize: 18,
            fontWeight: "bold",
          }}
        >
          + Add Workout
        </Text>
      </TouchableOpacity>

      <Text
        style={{
          color: "#888",
          marginBottom: 15,
        }}
      >
        Total workouts: {workouts.length}
      </Text>

      {workouts.map((workout, index) => (
        <WorkoutCard
          key={workout.id}
          workout={workout}
          onDelete={() =>
            deleteWorkout(index)
          }
        />
      ))}
    </View>
  );
}
