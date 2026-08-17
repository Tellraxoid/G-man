import { router } from "expo-router";
import { Text, TouchableOpacity, View } from "react-native";

import { Colors } from "../constants/theme";
import { Workout } from "../types/workout";
import AppCard from "./ui/AppCard";

type WorkoutCardProps = {
  workout: Workout;
  onDelete: (workoutId: string) => void;
};

export default function WorkoutCard({ workout, onDelete }: WorkoutCardProps) {
  const formattedDate = new Date(workout.date).toLocaleDateString("en-US", {
    month: "long",
    day: "numeric",
    year: "numeric",
  });

  const totalExercises = workout.exercises.length;

  const totalSets = workout.exercises.reduce(
    (total, exercise) => total + exercise.sets.length,
    0,
  );

  const totalVolume = workout.exercises.reduce(
    (total, exercise) =>
      total +
      exercise.sets.reduce(
        (exerciseTotal, set) => exerciseTotal + set.weight * set.reps,
        0,
      ),
    0,
  );

  function openWorkout() {
    router.push({
      pathname: "/workout",
      params: {
        id: workout.id,
      },
    });
  }

  function handleDelete() {
    onDelete(workout.id);
  }

  return (
    <AppCard
      style={{
        marginBottom: 15,
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <TouchableOpacity
        onPress={openWorkout}
        style={{
          flex: 1,
        }}
      >
        <View>
          <Text
            style={{
              color: Colors.text,
              fontSize: 18,
              fontWeight: "bold",
            }}
          >
            {workout.name}
          </Text>

          <Text
            style={{
              color: Colors.textSecondary,
              fontSize: 14,
              marginTop: 5,
            }}
          >
            {formattedDate}
          </Text>

          <Text
            style={{
              color: Colors.textSecondary,
              fontSize: 14,
              marginTop: 5,
            }}
          >
            {totalExercises} exercises · {totalSets} sets
          </Text>

          <Text
            style={{
              color: Colors.accent,
              fontSize: 14,
              fontWeight: "bold",
              marginTop: 5,
            }}
          >
            Volume: {totalVolume} kg
          </Text>
        </View>
      </TouchableOpacity>

      <TouchableOpacity
        onPress={handleDelete}
        style={{
          backgroundColor: Colors.deleteButton,
          paddingHorizontal: 12,
          paddingVertical: 8,
          borderRadius: 10,
          marginLeft: 15,
        }}
      >
        <Text
          style={{
            color: Colors.text,
            fontWeight: "bold",
          }}
        >
          Delete
        </Text>
      </TouchableOpacity>
    </AppCard>
  );
}
