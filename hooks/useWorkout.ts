import { useState } from "react";

import { loadWorkouts, saveWorkouts } from "../storage/workoutStorage";
import { Workout } from "../types/workout";
import { findWorkoutById } from "../utils/workoutHelpers";

export function useWorkout() {
  const [workout, setWorkout] = useState<Workout | null>(null);

  async function loadWorkoutById(id: string) {
    const workouts = await loadWorkouts();
    const foundWorkout = findWorkoutById(workouts, id);

    setWorkout(foundWorkout);
  }

  async function saveUpdatedWorkout(
    updatedWorkouts: Workout[],
    workoutId: string,
  ) {
    await saveWorkouts(updatedWorkouts);

    setWorkout(findWorkoutById(updatedWorkouts, workoutId));
  }

  async function addExercise(workoutId: string, exerciseName: string) {
    const trimmedName = exerciseName.trim();

    if (trimmedName === "") {
      return;
    }

    try {
      const workouts = await loadWorkouts();

      const updatedWorkouts = workouts.map((item) =>
        item.id === workoutId
          ? {
              ...item,
              exercises: [
                ...item.exercises,
                {
                  id: Date.now().toString(),
                  name: trimmedName,
                  sets: [],
                },
              ],
            }
          : item,
      );

      await saveUpdatedWorkout(updatedWorkouts, workoutId);
    } catch (error) {
      console.log("Error adding exercise", error);
    }
  }

  async function addSet(workoutId: string, exerciseId: string) {
    try {
      const workouts = await loadWorkouts();

      const updatedWorkouts = workouts.map((item) => {
        if (item.id !== workoutId) {
          return item;
        }

        const updatedExercises = item.exercises.map((exercise) => {
          if (exercise.id !== exerciseId) {
            return exercise;
          }

          return {
            ...exercise,
            sets: [
              ...exercise.sets,
              {
                weight: 0,
                reps: 0,
              },
            ],
          };
        });

        return {
          ...item,
          exercises: updatedExercises,
        };
      });

      await saveUpdatedWorkout(updatedWorkouts, workoutId);
    } catch (error) {
      console.log("Error adding set", error);
    }
  }

  async function updateSet(
    workoutId: string,
    exerciseId: string,
    setIndex: number,
    field: string,
    value: number,
  ) {
    try {
      const workouts = await loadWorkouts();

      const updatedWorkouts = workouts.map((item) => {
        if (item.id !== workoutId) {
          return item;
        }

        const updatedExercises = item.exercises.map((exercise) => {
          if (exercise.id !== exerciseId) {
            return exercise;
          }

          const updatedSets = exercise.sets.map((set, index) => {
            if (index !== setIndex) {
              return set;
            }

            return {
              ...set,
              [field]: value,
            };
          });

          return {
            ...exercise,
            sets: updatedSets,
          };
        });

        return {
          ...item,
          exercises: updatedExercises,
        };
      });

      await saveUpdatedWorkout(updatedWorkouts, workoutId);
    } catch (error) {
      console.log("Error updating set", error);
    }
  }

  async function deleteSet(
    workoutId: string,
    exerciseId: string,
    setIndex: number,
  ) {
    try {
      const workouts = await loadWorkouts();

      const updatedWorkouts = workouts.map((item) => {
        if (item.id !== workoutId) {
          return item;
        }

        const updatedExercises = item.exercises.map((exercise) => {
          if (exercise.id !== exerciseId) {
            return exercise;
          }

          return {
            ...exercise,
            sets: exercise.sets.filter((_, index) => index !== setIndex),
          };
        });

        return {
          ...item,
          exercises: updatedExercises,
        };
      });

      await saveUpdatedWorkout(updatedWorkouts, workoutId);
    } catch (error) {
      console.log("Error deleting set", error);
    }
  }

  async function deleteExercise(workoutId: string, exerciseId: string) {
    try {
      const workouts = await loadWorkouts();

      const updatedWorkouts = workouts.map((item) => {
        if (item.id !== workoutId) {
          return item;
        }

        return {
          ...item,
          exercises: item.exercises.filter(
            (exercise) => exercise.id !== exerciseId,
          ),
        };
      });

      await saveUpdatedWorkout(updatedWorkouts, workoutId);
    } catch (error) {
      console.log("Error deleting exercise", error);
    }
  }
  async function updateExerciseName(
    workoutId: string,
    exerciseId: string,
    exerciseName: string,
  ) {
    const trimmedName = exerciseName.trim();

    if (trimmedName === "") {
      return;
    }

    try {
      const workouts = await loadWorkouts();

      const updatedWorkouts = workouts.map((item) => {
        if (item.id !== workoutId) {
          return item;
        }

        const updatedExercises = item.exercises.map((exercise) =>
          exercise.id === exerciseId
            ? {
                ...exercise,
                name: trimmedName,
              }
            : exercise,
        );

        return {
          ...item,
          exercises: updatedExercises,
        };
      });

      await saveUpdatedWorkout(updatedWorkouts, workoutId);
    } catch (error) {
      console.log("Error updating exercise name", error);
    }
  }
  async function updateWorkoutName(workoutId: string, workoutName: string) {
    const trimmedName = workoutName.trim();

    if (trimmedName === "") {
      return;
    }

    try {
      const workouts = await loadWorkouts();

      const updatedWorkouts = workouts.map((item) =>
        item.id === workoutId
          ? {
              ...item,
              name: trimmedName,
            }
          : item,
      );

      await saveUpdatedWorkout(updatedWorkouts, workoutId);
    } catch (error) {
      console.log("Error updating workout name", error);
    }
  }

  return {
    workout,
    loadWorkoutById,
    addExercise,
    addSet,
    updateSet,
    deleteSet,
    deleteExercise,
    updateWorkoutName,
    updateExerciseName,
  };
}
