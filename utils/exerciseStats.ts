function getMatchingExercises(workouts: any[], exerciseName: string) {
    return workouts.flatMap((workout) =>
        workout.exercises.filter((exercise) => 
            exercise.name === exerciseName));
}

export function getPersonalRecord(
    workouts: any[],
    exerciseName: string
) {
    const matchingExercises = getMatchingExercises(workouts, exerciseName);
    const allSets = matchingExercises.flatMap((exercise) =>
         exercise.sets);
    if (allSets.length === 0) {
        return null;
    }
    return Math.max(...allSets.map((set) => set.weight));
}

export function getTimesPerformed(
    workouts: any[],
    exerciseName: string
) {
    const matchingExercises = getMatchingExercises(workouts, exerciseName);
    return matchingExercises.length;
}

export function getTotalVolume(
    workouts: any[],
    exerciseName: string    
) {
    const matchingExercises = getMatchingExercises(workouts, exerciseName);
    const allSets = matchingExercises.flatMap((exercise) =>
         exercise.sets);
    return allSets.reduce((total, set) => total + (set.weight * set.reps), 0);
}

export function getLastWorkout(
    workouts: any[],
    exerciseName: string
) {
    const sortedExercises = [...workouts].sort(
        (a, b) => 
        new Date(b.date).getTime() 
        - new Date(a.date).getTime());
    for (const workout of sortedExercises) {
                const exercise = workout.exercises.find(
                    (ex) => ex.name === exerciseName);
        if (exercise) {
            return {exercise, date: workout.date};
        }
    }
    return null;
}

export function getBestSet(
    workouts: any[],
     exerciseName: string
    ) {
    const matchingExercises = workouts.flatMap((workout) =>
        workout.exercises.filter((exercise) => 
            exercise.name === exerciseName)
    );
    const allSets = matchingExercises.flatMap((exercise) =>
         exercise.sets);
    if (allSets.length === 0) {
        return null;
    }
    return allSets.reduce((best, current) => 
         current.weight > best.weight ? current : best
    );
}