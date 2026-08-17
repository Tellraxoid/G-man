import { useEffect, useState } from "react";
import { ScrollView, Text, TouchableOpacity, View } from "react-native";

import { router } from "expo-router";
import AppCard from "../../components/ui/AppCard";
import { Colors } from "../../constants/theme";
import { loadWorkouts } from "../../storage/workoutStorage";
import { Workout } from "../../types/workout";

const weekDays = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

export default function CalendarScreen() {
  const [workouts, setWorkouts] = useState<Workout[]>([]);

  const [selectedWorkouts, setSelectedWorkouts] = useState<Workout[]>([]);

  const [selectedDay, setSelectedDay] = useState<number | null>(null);

  const [currentDate, setCurrentDate] = useState(new Date());

  useEffect(() => {
    async function fetchWorkouts() {
      const data = await loadWorkouts();
      setWorkouts(data || []);
    }

    fetchWorkouts();
  }, []);

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const today = new Date();

  const isCurrentMonth =
    today.getFullYear() === year && today.getMonth() === month;

  const daysInMonth = new Date(year, month + 1, 0).getDate();

  const firstDayOfMonth = new Date(year, month, 1).getDay();

  const startingOffset = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1;

  const days = Array.from({ length: daysInMonth }, (_, index) => index + 1);

  const emptyDays = Array.from({ length: startingOffset }, (_, index) => index);

  const workoutDates = workouts.map(
    (workout) => new Date(workout.date).toISOString().split("T")[0],
  );

  function createDateString(day: number) {
    return `${year}-${String(month + 1).padStart(
      2,
      "0",
    )}-${String(day).padStart(2, "0")}`;
  }

  function selectWorkout(day: number) {
    setSelectedDay(day);

    const dateString = createDateString(day);

    const workoutsForDay = workouts.filter(
      (workout) =>
        new Date(workout.date).toISOString().split("T")[0] === dateString,
    );

    setSelectedWorkouts(workoutsForDay);
  }

  function goToPreviousMonth() {
    setSelectedWorkouts([]);
    setSelectedDay(null);

    setCurrentDate(new Date(year, month - 1, 1));
  }

  function goToNextMonth() {
    setSelectedWorkouts([]);
    setSelectedDay(null);

    setCurrentDate(new Date(year, month + 1, 1));
  }

  const monthTitle = currentDate.toLocaleDateString("en-US", {
    month: "long",
    year: "numeric",
  });

  return (
    <ScrollView
      style={{
        flex: 1,
        backgroundColor: Colors.background,
      }}
      contentContainerStyle={{
        paddingTop: 60,
        paddingHorizontal: 20,
        paddingBottom: 40,
      }}
    >
      <View
        style={{
          flexDirection: "row",
          justifyContent: "space-between",
          alignItems: "center",
          marginTop: 20,
          marginBottom: 25,
        }}
      >
        <TouchableOpacity
          onPress={goToPreviousMonth}
          style={{
            backgroundColor: Colors.card,
            width: 42,
            height: 42,
            borderRadius: 10,
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <Text
            style={{
              color: Colors.text,
              fontSize: 24,
              fontWeight: "bold",
            }}
          >
            ←
          </Text>
        </TouchableOpacity>

        <Text
          style={{
            color: Colors.accent,
            fontSize: 26,
            fontWeight: "bold",
          }}
        >
          {monthTitle}
        </Text>

        <TouchableOpacity
          onPress={goToNextMonth}
          style={{
            backgroundColor: Colors.card,
            width: 42,
            height: 42,
            borderRadius: 10,
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <Text
            style={{
              color: Colors.text,
              fontSize: 24,
              fontWeight: "bold",
            }}
          >
            →
          </Text>
        </TouchableOpacity>
      </View>

      <View
        style={{
          flexDirection: "row",
          marginBottom: 10,
        }}
      >
        {weekDays.map((day) => (
          <View
            key={day}
            style={{
              width: "14.2857%",
              alignItems: "center",
            }}
          >
            <Text
              style={{
                color: Colors.textSecondary,
                fontSize: 13,
                fontWeight: "bold",
              }}
            >
              {day}
            </Text>
          </View>
        ))}
      </View>

      <View
        style={{
          flexDirection: "row",
          flexWrap: "wrap",
        }}
      >
        {emptyDays.map((item) => (
          <View
            key={`empty-${item}`}
            style={{
              width: "14.2857%",
              aspectRatio: 1,
            }}
          />
        ))}

        {days.map((day) => {
          const dateString = createDateString(day);

          const hasWorkout = workoutDates.includes(dateString);

          const isToday = isCurrentMonth && today.getDate() === day;

          const isSelected = selectedDay === day;

          return (
            <View
              key={day}
              style={{
                width: "14.2857%",
                aspectRatio: 1,
                padding: 3,
              }}
            >
              <TouchableOpacity
                onPress={() => selectWorkout(day)}
                style={{
                  flex: 1,

                  backgroundColor: hasWorkout ? Colors.accent : Colors.card,

                  borderRadius: 10,

                  justifyContent: "center",
                  alignItems: "center",

                  borderWidth: isSelected ? 3 : isToday ? 2 : 0,

                  borderColor: isSelected ? Colors.text : Colors.accent,
                }}
              >
                <Text
                  style={{
                    color: Colors.text,
                    fontSize: 15,

                    fontWeight:
                      hasWorkout || isToday || isSelected ? "bold" : "normal",
                  }}
                >
                  {day}
                </Text>
              </TouchableOpacity>
            </View>
          );
        })}
      </View>

      {selectedWorkouts.length > 0 && (
        <View
          style={{
            marginTop: 25,
            gap: 15,
          }}
        >
          {selectedWorkouts.map((selectedWorkout) => (
            <TouchableOpacity
              key={selectedWorkout.id}
              activeOpacity={0.7}
              onPress={() => {
                router.push({
                  pathname: "/workout",
                  params: {
                    id: selectedWorkout.id,
                  },
                });
              }}
            >
              <AppCard>
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
                  {new Date(selectedWorkout.date).toLocaleTimeString("en-US", {
                    hour: "2-digit",
                    minute: "2-digit",
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
            </TouchableOpacity>
          ))}
        </View>
      )}
    </ScrollView>
  );
}
