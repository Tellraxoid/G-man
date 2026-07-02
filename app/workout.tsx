import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
} from "react-native";

import { useEffect, useState } from "react";

import { useLocalSearchParams } from "expo-router";

import ExerciseCard from "../components/ExerciseCard";

// Adjusted import path to correctly resolve the hooks module from the app directory
import { useWorkout } from "../hooks/useWorkout";

import { calculateVolume } from "../utils/calculateVolume";

export default function WorkoutScreen() {
  const { id } = useLocalSearchParams();

  const {
    workout,
    setWorkout,
    loadWorkoutById,
    addExercise,
    addSet,
    updateSet,
    deleteSet,
    deleteExercise,
  } = useWorkout();

  const [exerciseName, setExerciseName] =
    useState("");

  useEffect(() => {
    loadWorkoutById(id as string);
  }, []);

 

  // Loading screen
  if (!workout) {
       return (
        <View
          style={{
            backgroundColor: "#111",
            flex: 1,
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <Text>Loading...</Text>
        </View>
      );
  }

  const totalVolume = workout.exercises.reduce(
    (total, exercise) => total + calculateVolume(exercise.sets),
    0
  );

const totalExercises =
  workout.exercises.length;

const totalSets =
  workout.exercises.reduce(
    (total, exercise) =>
      total + exercise.sets.length,
    0
  );



  // Main screen
  return (
   
    <KeyboardAvoidingView
      style={{
        flex: 1,
        backgroundColor: "#111",
      }}
      behavior={
        Platform.OS === "ios"
          ? "padding"
          : "height"
      }
    >
           
      <ScrollView
        style={{
          backgroundColor: "#111",
          flex: 1,
        }}
        contentContainerStyle={{
          paddingTop: 80,
          paddingHorizontal: 20,
          paddingBottom: 100,
        }}
      >
       <Text
          style={{
            color: "white", 
            fontSize: 18,
            marginBottom: 20,
          }}
        >
          {new Date(workout.date).toLocaleDateString("en-US", {
            day: "numeric",
            month: "long",
            year: "numeric",
          })}
        </Text>
               <Text
          style={{
            color: "white",
            fontSize: 32,
            fontWeight: "bold",
            marginBottom: 20,
          }}
        >
                {workout.name}
        </Text>
       <View
          style={{
            backgroundColor: "#222",
            padding: 15,
            borderRadius: 15, 
            marginBottom: 20,
          }}
        >
          <Text
            style={{
              color: "#22c55e",
              fontSize: 16,
              fontWeight: "bold",
              marginBottom: 10,
            }}
          >
            Workout Stats
              </Text>
            <Text
            style={{
              color: "white",
              fontSize: 14, 
            }}
          >
            Total Volume: {totalVolume} kg
          </Text>
          <Text
            style={{
              color: "white",
              fontSize: 14,
            }}
          >
            Total Exercises: {totalExercises}
          </Text>
          <Text
            style={{
              color: "white",
              fontSize: 14,
            }}
          >
            Total Sets: {totalSets}
          </Text>
        </View>

        <Text
          style={{
            color: "white",
            fontSize: 18,
            marginBottom: 20,
          }}>
          {new Date(workout.date).toLocaleTimeString("en-US", {
            hour: "2-digit",
            minute: "2-digit",
          })}
        </Text>
        <TextInput
          value={exerciseName}
          onChangeText={setExerciseName}
          placeholder="Exercise name"
          placeholderTextColor="#888"
          style={{
            backgroundColor: "#222",
            color: "white",
            padding: 15,
            borderRadius: 15,
            marginBottom: 15,
          }}
        />

        <TouchableOpacity
          onPress={() => {
            addExercise(
              id as string,
              exerciseName
            );

            setExerciseName("");
          }}
          style={{
            backgroundColor: "#22c55e",
            padding: 15,
            borderRadius: 15,
            alignItems: "center",
            marginBottom: 25,
          }}
        >
          <Text
            style={{
              color: "white",
              fontWeight: "bold",
              fontSize: 16,
            }}
          >
            + Add Exercise
          </Text>
        </TouchableOpacity>

        {workout.exercises.map(
          (exercise) => (
            <ExerciseCard
              key={exercise.id}
              exercise={exercise}
              onAddSet={(
                exerciseId
              ) =>
                addSet(
                  id as string,
                  exerciseId
                )
              }
              onUpdateSet={
                (exerciseId,
                   setIndex, 
                   field,
                    value) =>
                  updateSet(
                    id as string,
                    exerciseId,
                    setIndex,
                    field,
                    value
                  )
              }
              onDeleteSet={
                (exerciseId, setIndex) =>
                  deleteSet(
                    id as string,
                    exerciseId,
                    setIndex
                  )
              }
              onDeleteExercise={
                ( exerciseId) =>
                  deleteExercise(
                    id as string,
                    exerciseId
                  )
              }
            />
          )
        )}
      </ScrollView>
    </KeyboardAvoidingView>
  );
}