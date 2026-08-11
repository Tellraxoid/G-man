import { useEffect, useState } from "react";
import {
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Text,
  View,
} from "react-native";

import { useLocalSearchParams } from "expo-router";

import ExerciseCard from "../components/ExerciseCard";
import AppButton from "../components/ui/AppButton";
import AppCard from "../components/ui/AppCard";
import AppInput from "../components/ui/AppInput";
import EditableTitle from "../components/ui/EditableTitle";
import { Colors } from "../constants/theme";
import { useWorkout } from "../hooks/useWorkout";
import { loadWorkouts } from "../storage/workoutStorage";
import { Workout } from "../types/workout";
import { calculateVolume } from "../utils/calculateVolume";
import { getExercisePR } from "../utils/exerciseStats";

export default function WorkoutScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();

  const {
    workout,
    loadWorkoutById,
    addExercise,
    addSet,
    updateSet,
    deleteSet,
    deleteExercise,
    updateWorkoutName,
    updateExerciseName,
  } = useWorkout();

  const [workoutHistory, setWorkoutHistory] = useState<Workout[]>([]);

  const [exerciseName, setExerciseName] = useState("");

  useEffect(() => {
    if (id) {
      loadWorkoutById(id);
    }
  }, [id]);

  useEffect(() => {
    async function loadHistory() {
      const data = await loadWorkouts();
      setWorkoutHistory(data || []);
    }

    loadHistory();
  }, [workout]);

  function handleAddExercise() {
    const trimmedName = exerciseName.trim();

    if (!id || trimmedName === "") {
      return;
    }

    addExercise(id, trimmedName);
    setExerciseName("");
  }

  if (!workout) {
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: Colors.background,
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Text
          style={{
            color: Colors.text,
            fontSize: 16,
          }}
        >
          Loading...
        </Text>
      </View>
    );
  }

  const totalVolume = workout.exercises.reduce(
    (total, exercise) => total + calculateVolume(exercise.sets),
    0,
  );

  const totalExercises = workout.exercises.length;

  const totalSets = workout.exercises.reduce(
    (total, exercise) => total + exercise.sets.length,
    0,
  );

  const previousWorkouts = workoutHistory.filter(
    (item) => item.id !== workout.id,
  );

  return (
    <KeyboardAvoidingView
      style={{
        flex: 1,
        backgroundColor: Colors.background,
      }}
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <ScrollView
        style={{
          flex: 1,
          backgroundColor: Colors.background,
        }}
        contentContainerStyle={{
          paddingTop: 80,
          paddingHorizontal: 20,
          paddingBottom: 100,
        }}
        keyboardShouldPersistTaps="handled"
      >
        <Text
          style={{
            color: Colors.textSecondary,
            fontSize: 16,
            marginBottom: 8,
          }}
        >
          {new Date(workout.date).toLocaleDateString("en-US", {
            day: "numeric",
            month: "long",
            year: "numeric",
          })}
        </Text>

        <EditableTitle
          title={workout.name}
          onSave={(newTitle) => updateWorkoutName(id, newTitle)}
        />

        <AppCard
          style={{
            marginBottom: 20,
          }}
        >
          <Text
            style={{
              color: Colors.accent,
              fontSize: 16,
              fontWeight: "bold",
              marginBottom: 10,
            }}
          >
            Workout Stats
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 14,
              marginBottom: 4,
            }}
          >
            Total Volume: {totalVolume} kg
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 14,
              marginBottom: 4,
            }}
          >
            Total Exercises: {totalExercises}
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 14,
            }}
          >
            Total Sets: {totalSets}
          </Text>
        </AppCard>

        <Text
          style={{
            color: Colors.textSecondary,
            fontSize: 16,
            marginBottom: 20,
          }}
        >
          {new Date(workout.date).toLocaleTimeString("en-US", {
            hour: "2-digit",
            minute: "2-digit",
          })}
        </Text>

        <AppInput
          value={exerciseName}
          onChangeText={setExerciseName}
          placeholder="Exercise name"
          returnKeyType="done"
          onSubmitEditing={handleAddExercise}
          style={{
            marginBottom: 15,
          }}
        />

        <AppButton
          title="+ Add Exercise"
          onPress={handleAddExercise}
          style={{
            marginBottom: 25,
          }}
        />

        {workout.exercises.map((exercise) => {
          const historicalPR = getExercisePR(workoutHistory, exercise.name);

          const previousPR = getExercisePR(previousWorkouts, exercise.name);

          return (
            <ExerciseCard
              key={exercise.id}
              exercise={exercise}
              historicalPR={historicalPR}
              previousPR={previousPR}
              onAddSet={(exerciseId) => addSet(id, exerciseId)}
              onUpdateSet={(exerciseId, setIndex, field, value) =>
                updateSet(id, exerciseId, setIndex, field, value)
              }
              onDeleteSet={(exerciseId, setIndex) =>
                deleteSet(id, exerciseId, setIndex)
              }
              onDeleteExercise={(exerciseId) => deleteExercise(id, exerciseId)}
              onUpdateExerciseName={(exerciseId, newName) =>
                updateExerciseName(id, exerciseId, newName)
              }
            />
          );
        })}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}
