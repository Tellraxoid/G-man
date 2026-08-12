function getMatchingExercises(workouts: any[], exerciseName: string) {
  return workouts.flatMap((workout) =>
    (workout.exercises ?? []).filter(
      (exercise) => exercise.name === exerciseName,
    ),
  );
}

export function getExercisePR(workouts: any[], exerciseName: string) {
  const matchingExercises = getMatchingExercises(workouts, exerciseName);

  const allSets = matchingExercises.flatMap((exercise) => exercise.sets ?? []);

  if (allSets.length === 0) {
    return null;
  }

  return allSets.reduce((bestSet, currentSet) =>
    currentSet.weight > bestSet.weight ? currentSet : bestSet,
  );
}

export function getPersonalRecord(workouts: any[], exerciseName: string) {
  const bestSet = getExercisePR(workouts, exerciseName);

  return bestSet ? bestSet.weight : null;
}

export function getTimesPerformed(workouts: any[], exerciseName: string) {
  return getMatchingExercises(workouts, exerciseName).length;
}

export function getTotalVolume(workouts: any[], exerciseName: string) {
  const matchingExercises = getMatchingExercises(workouts, exerciseName);

  const allSets = matchingExercises.flatMap((exercise) => exercise.sets ?? []);

  return allSets.reduce((total, set) => total + set.weight * set.reps, 0);
}

export function getLastWorkout(workouts: any[], exerciseName: string) {
  const sortedWorkouts = [...workouts].sort(
    (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime(),
  );

  for (const workout of sortedWorkouts) {
    const exercise = (workout.exercises ?? []).find(
      (exercise) => exercise.name === exerciseName,
    );

    if (exercise) {
      return {
        exercise,
        date: workout.date,
      };
    }
  }

  return null;
}

export function getBestSet(workouts: any[], exerciseName: string) {
  return getExercisePR(workouts, exerciseName);
}
export function getExerciseHistory(workouts: any[], exerciseName: string) {
  return workouts
    .map((workout) => {
      const exercise = (workout.exercises ?? []).find(
        (exercise) => exercise.name === exerciseName,
      );

      if (!exercise) {
        return null;
      }

      const bestSet =
        exercise.sets.length > 0
          ? exercise.sets.reduce((best, current) =>
              current.weight > best.weight ? current : best,
            )
          : null;

      return {
        workoutId: workout.id,
        date: workout.date,
        sets: exercise.sets,
        bestSet,
      };
    })
    .filter(Boolean);
}
