import { useEffect, useState } from "react";
import { ScrollView, Text, TouchableOpacity, View } from "react-native";
import AppCard from "../../components/ui/AppCard";
import { Colors } from "../../constants/theme";
import { loadWorkouts } from "../../storage/workoutStorage";
import { Workout } from "../../types/workout";

export default function CalendarScreen() {
  const [workouts, setWorkouts] = useState<Workout[]>([]);

  const [selectedWorkout, setSelectedWorkout] = useState<Workout | null>(null);

  useEffect(() => {
    async function fetchWorkouts() {
      const data = await loadWorkouts();
      setWorkouts(data || []);
    }
    fetchWorkouts();
  }, []);

  const workoutDates = workouts.map(
    (workout) => new Date(workout.date).toISOString().split("T")[0],
  );

  const days = Array.from({ length: 31 }, (_, index) => index + 1);
  function selectWorkout(day: number) {
    const dateString = `2026-07-${day.toString().padStart(2, "0")}`;
    const workout = workouts.find(
      (w) => new Date(w.date).toISOString().split("T")[0] === dateString,
    );
    setSelectedWorkout(workout || null);
  }
  return (
    <ScrollView
      style={{
        flex: 1,
        backgroundColor: Colors.background,
      }}
      contentContainerStyle={{
        paddingTop: 60,
        paddingHorizontal: 20,
        paddingBottom: 20,
      }}
    >
      <Text
        style={{
          color: Colors.accent,
          fontSize: 32,
          fontWeight: "bold",
          marginBottom: 20,
          marginTop: 20,
        }}
      >
        July 2026
      </Text>
      <View
        style={{
          flexDirection: "row",
          flexWrap: "wrap",
          gap: 10,
        }}
      >
        {days.map((day) => {
          const dateString = `2026-07-${day.toString().padStart(2, "0")}`;
          const hasWorkout = workoutDates.includes(dateString);
          return (
            <TouchableOpacity
              key={day}
              onPress={() => selectWorkout(day)}
              style={{
                width: 40,
                height: 40,
                backgroundColor: hasWorkout ? Colors.accent : Colors.card,
                justifyContent: "center",
                alignItems: "center",
                borderRadius: 5,
              }}
            >
              <Text
                style={{
                  color: Colors.text,
                  fontSize: 16,
                }}
              >
                {day}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
      {selectedWorkout && (
        <AppCard style={{ marginTop: 20 }}>
          <Text
            style={{
              color: Colors.accent,
              fontSize: 20,
              fontWeight: "bold",
              marginBottom: 5,
            }}
          >
            🏋️ {selectedWorkout.name}
          </Text>

          <Text
            style={{
              color: Colors.textSecondary,
              fontSize: 14,
              marginBottom: 15,
            }}
          >
            {new Date(selectedWorkout.date).toLocaleDateString("en-US", {
              year: "numeric",
              month: "long",
              day: "numeric",
            })}
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 16,
              fontWeight: "bold",
              marginBottom: 10,
            }}
          >
            Exercises
          </Text>

          {selectedWorkout.exercises.length === 0 ? (
            <Text
              style={{
                color: Colors.textSecondary,
                fontSize: 14,
              }}
            >
              No exercises for this workout
            </Text>
          ) : (
            selectedWorkout.exercises.map((exercise) => (
              <Text
                key={exercise.id}
                style={{
                  color: Colors.textSecondary,
                  fontSize: 14,
                  marginBottom: 6,
                }}
              >
                • {exercise.name}
              </Text>
            ))
          )}
        </AppCard>
      )}
    </ScrollView>
  );
}
