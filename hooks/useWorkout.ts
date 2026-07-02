import { useState } from "react";

import { Workout } from "../types/workout";

import {
  loadWorkouts,
  saveWorkouts,
} from "../storage/workoutStorage";

import {
  findWorkoutById,
} from "../utils/workoutHelpers";

export function useWorkout() {

  const [workout, setWorkout] =
    useState<Workout | null>(null);

  async function loadWorkoutById(
    id: string
  ) {

    const workouts =
      await loadWorkouts();

    const foundWorkout =
      findWorkoutById(
        workouts,
        id
      );

    setWorkout(foundWorkout);
  }

  async function addExercise(
    workoutId: string,
    exerciseName: string
  ) {

    if (
      exerciseName.trim() === ""
    ) {
      return;
    }

    const workouts =
      await loadWorkouts();

    const updatedWorkouts =
      workouts.map((item) =>
        item.id === workoutId
          ? {
              ...item,
              exercises: [
                ...item.exercises,
                {
                  id: Date.now().toString(),
                  name: exerciseName,
                  sets: [],
                },
              ],
            }
          : item
      );

    await saveWorkouts(
      updatedWorkouts
    );

    setWorkout(
      findWorkoutById(
        updatedWorkouts,
        workoutId
      )
    );
  }

  async function addSet(
    workoutId: string,
    exerciseId: string
  ) {

    try {

      const workouts =
        await loadWorkouts();

      const updatedWorkouts =
        workouts.map((item) => {

          if (
            item.id === workoutId
          ) {

            const updatedExercises =
              item.exercises.map(
                (exercise) => {

                  if (
                    exercise.id ===
                    exerciseId
                  ) {

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
                  }

                  return exercise;
                }
              );

            return {
              ...item,
              exercises:
                updatedExercises,
            };
          }

          return item;
        });

      await saveWorkouts(
        updatedWorkouts
      );

      setWorkout(
        findWorkoutById(
          updatedWorkouts,
          workoutId
        )
      );

    } catch (error) {

      console.log(
        "Error adding set",
        error
      );
    }
  }

  async function updateSet(
    workoutId: string,
    exerciseId: string,
    setIndex: number,
    field: string,
    value: number
  ) {

    try {

      const workouts =
        await loadWorkouts();

      const updatedWorkouts =
        workouts.map((item) => {

          if (
            item.id === workoutId
          ) {

            const updatedExercises =
              item.exercises.map(
                (exercise) => {

                  if (
                    exercise.id ===
                    exerciseId
                  ) {

                    const updatedSets =
                      exercise.sets.map(
                        (set, index) => {

                          if (
                            index === setIndex
                          ) {

                            return {
                              ...set,
                              [field]: value,
                            };
                          }

                          return set;
                        }
                      );

                    return {
                      ...exercise,
                      sets: updatedSets,
                    };
                  }

                  return exercise;
                }
              );

            return {
              ...item,
              exercises:
                updatedExercises,
            };
          }

          return item;
        });

      await saveWorkouts(
        updatedWorkouts
      );

      setWorkout(
        findWorkoutById(
          updatedWorkouts,
          workoutId
        )
      );

    } catch (error) {

      console.log(
        "Error updating set",
        error
      );
    }
  }

  async function deleteSet(
    workoutId: string,
    exerciseId: string,
    setIndex: number
  ) {

    try {

      const workouts =
        await loadWorkouts();

      const updatedWorkouts =
        workouts.map((item) => {

          if (
            item.id === workoutId
          ) {

            const updatedExercises =
              item.exercises.map(
                (exercise) => {

                  if (
                    exercise.id ===
                    exerciseId
                  ) {

                    return {
                      ...exercise,
                      sets:
                        exercise.sets.filter(
                          (_, index) =>
                            index !==
                            setIndex
                        ),
                    };
                  }

                  return exercise;
                }
              );

            return {
              ...item,
              exercises:
                updatedExercises,
            };
          }

          return item;
        });

      await saveWorkouts(
        updatedWorkouts
      );

      setWorkout(
        findWorkoutById(
          updatedWorkouts,
          workoutId
        )
      );

    } catch (error) {

      console.log(
        "Error deleting set",
        error
      );
    }
  }

  async function deleteExercise(
    workoutId: string,
    exerciseId: string
  ) {

    try {

      const workouts =
        await loadWorkouts();

      const updatedWorkouts =
        workouts.map((item) => {

          if (
            item.id === workoutId
          ) {

            return {
              ...item,
              exercises:
                item.exercises.filter(
                  (exercise) =>
                    exercise.id !==
                    exerciseId
                ),
            };
          }

          return item;
        });

      await saveWorkouts(
        updatedWorkouts
      );

      setWorkout(
        findWorkoutById(
          updatedWorkouts,
          workoutId
        )
      );

    } catch (error) {

      console.log(
        "Error deleting exercise",
        error
      );
    }
  }

  return {
    workout,
    setWorkout,
    loadWorkoutById,
    addExercise,
    addSet,
    updateSet,
    deleteSet,
    deleteExercise,
  };
}