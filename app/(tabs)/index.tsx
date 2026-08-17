import { useEffect, useState } from "react";
import { Text, View } from "react-native";

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

  function getWeekStart(date: Date) {
    const result = new Date(
      date.getFullYear(),
      date.getMonth(),
      date.getDate(),
    );

    const day = result.getDay();
    const daysFromMonday = day === 0 ? 6 : day - 1;

    result.setDate(result.getDate() - daysFromMonday);

    return result.getTime();
  }

  function getWeeklyAverage() {
    if (workouts.length === 0) {
      return 0;
    }

    const weeklyCounts = new Map<number, number>();

    workouts.forEach((workout) => {
      const weekStart = getWeekStart(new Date(workout.date));

      weeklyCounts.set(weekStart, (weeklyCounts.get(weekStart) ?? 0) + 1);
    });

    const counts = Array.from(weeklyCounts.values());

    const total = counts.reduce((sum, count) => sum + count, 0);

    return total / counts.length;
  }

  function getTrainingStreak() {
    if (workouts.length === 0) {
      return 0;
    }

    const uniqueWeeks = Array.from(
      new Set(workouts.map((workout) => getWeekStart(new Date(workout.date)))),
    ).sort((a, b) => b - a);

    if (uniqueWeeks.length === 0) {
      return 0;
    }

    const currentWeek = getWeekStart(new Date());
    const latestWorkoutWeek = uniqueWeeks[0];

    const oneWeek = 7 * 24 * 60 * 60 * 1000;

    if (
      latestWorkoutWeek !== currentWeek &&
      latestWorkoutWeek !== currentWeek - oneWeek
    ) {
      return 0;
    }

    let streak = 1;

    for (let i = 0; i < uniqueWeeks.length - 1; i++) {
      const difference = (uniqueWeeks[i] - uniqueWeeks[i + 1]) / oneWeek;

      if (difference === 1) {
        streak++;
      } else {
        break;
      }
    }

    return streak;
  }

  function getThisWeekWorkouts() {
    const currentWeek = getWeekStart(new Date());

    return workouts.filter(
      (workout) => getWeekStart(new Date(workout.date)) === currentWeek,
    ).length;
  }

  const trainingStreak = getTrainingStreak();
  const thisWeekWorkouts = getThisWeekWorkouts();
  const weeklyAverage = getWeeklyAverage();

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

      {/* Statistics */}

      <View
        style={{
          flexDirection: "row",
          gap: 10,
          marginBottom: 20,
        }}
      >
        {/* Streak */}

        <View
          style={{
            flex: 1,
            backgroundColor: Colors.card,
            padding: 15,
            borderRadius: 15,
          }}
        >
          <Text
            style={{
              color: Colors.accent,
              fontSize: 13,
              fontWeight: "bold",
              marginBottom: 8,
            }}
          >
            🔥 Streak
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 23,
              fontWeight: "bold",
            }}
          >
            {trainingStreak}w
          </Text>
        </View>

        {/* This Week */}

        <View
          style={{
            flex: 1,
            backgroundColor: Colors.card,
            padding: 15,
            borderRadius: 15,
          }}
        >
          <Text
            style={{
              color: Colors.accent,
              fontSize: 13,
              fontWeight: "bold",
              marginBottom: 8,
            }}
          >
            🏋️ This Week
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 23,
              fontWeight: "bold",
            }}
          >
            {thisWeekWorkouts}
          </Text>
        </View>

        {/* Weekly Average */}

        <View
          style={{
            flex: 1,
            backgroundColor: Colors.card,
            padding: 15,
            borderRadius: 15,
          }}
        >
          <Text
            style={{
              color: Colors.accent,
              fontSize: 13,
              fontWeight: "bold",
              marginBottom: 8,
            }}
          >
            📊 Avg / Week
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 23,
              fontWeight: "bold",
            }}
          >
            {weeklyAverage.toFixed(1)}
          </Text>
        </View>
      </View>

      {/* Add Workout */}

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

      {/* Workout List */}

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
