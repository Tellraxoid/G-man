export function findWorkoutById(workouts, id) {
    return workouts.find(workout => workout.id === id);
}