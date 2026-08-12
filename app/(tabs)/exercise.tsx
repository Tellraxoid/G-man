import { useLocalSearchParams } from "expo-router";
import React from "react";
import { ScrollView, Text, View } from "react-native";

import InfoCard from "../../components/InfoCard";
import { exercisesDatabase } from "../../data/exerciseDatabase";
import { loadWorkouts } from "../../storage/workoutStorage";
import {
  getBestSet,
  getExerciseHistory,
  getLastWorkout,
  getTimesPerformed,
  getTotalVolume,
} from "../../utils/exerciseStats";

type ExerciseSet = {
  weight: number;
  reps: number;
};

type LastWorkout = {
  date: string;
  exercise: {
    sets: ExerciseSet[];
  };
};

type ExerciseHistoryItem = {
  workoutId: string;
  date: string;
  sets: ExerciseSet[];
  bestSet: ExerciseSet | null;
};

export default function ExerciseScreen() {
  const { name } = useLocalSearchParams();

  const exerciseName = Array.isArray(name) ? name[0] : name;

  const exercise = exercisesDatabase
    .flatMap((group) => group.exercises)
    .find((ex) => ex.name === exerciseName);

  const [timesPerformed, setTimesPerformed] = React.useState<number | null>(
    null,
  );

  const [totalVolume, setTotalVolume] = React.useState<number | null>(null);

  const [lastWorkout, setLastWorkout] = React.useState<LastWorkout | null>(
    null,
  );

  const [bestSet, setBestSet] = React.useState<ExerciseSet | null>(null);

  const [history, setHistory] = React.useState<ExerciseHistoryItem[]>([]);

  React.useEffect(() => {
    async function loadStats() {
      if (!exerciseName) {
        return;
      }

      const workouts = await loadWorkouts();
      const workoutData = workouts || [];

      const times = getTimesPerformed(workoutData, exerciseName);

      const volume = getTotalVolume(workoutData, exerciseName);

      const last = getLastWorkout(workoutData, exerciseName);

      const best = getBestSet(workoutData, exerciseName);

      const exerciseHistory = getExerciseHistory(workoutData, exerciseName);

      setTimesPerformed(times);
      setTotalVolume(volume);
      setLastWorkout(last);
      setBestSet(best);

      setHistory(exerciseHistory as ExerciseHistoryItem[]);
    }

    loadStats();
  }, [exerciseName]);

  if (!exercise) {
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: "#111",
          paddingTop: 80,
          paddingHorizontal: 20,
        }}
      >
        <Text
          style={{
            color: "white",
            fontSize: 32,
            fontWeight: "bold",
          }}
        >
          Exercise not found
        </Text>
      </View>
    );
  }

  return (
    <ScrollView
      style={{
        flex: 1,
        backgroundColor: "#111",
      }}
      contentContainerStyle={{
        paddingTop: 80,
        paddingHorizontal: 20,
        paddingBottom: 120,
      }}
    >
      <Text
        style={{
          color: "white",
          fontSize: 32,
          fontWeight: "bold",
          marginBottom: 25,
        }}
      >
        {exercise.name}
      </Text>

      <InfoCard title="💪 Equipment" value={exercise.equipment} />

      <InfoCard title="📊 Difficulty" value={exercise.difficulty} />

      <InfoCard title="📝 Description" value={exercise.description} />

      <View
        style={{
          backgroundColor: "#222",
          padding: 20,
          borderRadius: 15,
          marginTop: 15,
        }}
      >
        <Text
          style={{
            color: "#22c55e",
            fontSize: 16,
            fontWeight: "bold",
            marginBottom: 15,
          }}
        >
          🏆 Your Stats
        </Text>

        <Text
          style={{
            color: "white",
            fontSize: 16,
            marginBottom: 8,
          }}
        >
          🏅 Personal Record:{" "}
          {bestSet ? `${bestSet.weight} kg × ${bestSet.reps}` : "--"}
        </Text>

        <Text
          style={{
            color: "white",
            fontSize: 16,
            marginBottom: 8,
          }}
        >
          🔥 Total Volume: {totalVolume ?? "--"} kg
        </Text>

        <Text
          style={{
            color: "white",
            fontSize: 16,
          }}
        >
          🔄 Times Performed: {timesPerformed ?? "--"}
        </Text>
      </View>

      {lastWorkout && (
        <View
          style={{
            backgroundColor: "#222",
            padding: 20,
            borderRadius: 15,
            marginTop: 15,
          }}
        >
          <Text
            style={{
              color: "#22c55e",
              fontSize: 16,
              fontWeight: "bold",
              marginBottom: 15,
            }}
          >
            📅 Last Workout
          </Text>

          <Text
            style={{
              color: "white",
              fontSize: 16,
              marginBottom: 5,
            }}
          >
            {new Date(lastWorkout.date).toLocaleDateString()}
          </Text>

          <Text
            style={{
              color: "#888",
              fontSize: 14,
              marginBottom: 15,
            }}
          >
            {new Date(lastWorkout.date).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            })}
          </Text>

          {lastWorkout.exercise.sets.map((set, index) => (
            <Text
              key={index}
              style={{
                color: "white",
                fontSize: 16,
                marginBottom: 8,
              }}
            >
              Set {index + 1}: {set.weight} kg × {set.reps}
            </Text>
          ))}
        </View>
      )}

      {bestSet && (
        <View
          style={{
            backgroundColor: "#222",
            padding: 20,
            borderRadius: 15,
            marginTop: 15,
          }}
        >
          <Text
            style={{
              color: "#22c55e",
              fontSize: 16,
              fontWeight: "bold",
              marginBottom: 15,
            }}
          >
            🥇 Best Set
          </Text>

          <Text
            style={{
              color: "white",
              fontSize: 16,
            }}
          >
            {bestSet.weight} kg × {bestSet.reps}
          </Text>
        </View>
      )}

      {history.length > 0 && (
        <View
          style={{
            backgroundColor: "#222",
            padding: 20,
            borderRadius: 15,
            marginTop: 15,
          }}
        >
          <Text
            style={{
              color: "#22c55e",
              fontSize: 16,
              fontWeight: "bold",
              marginBottom: 15,
            }}
          >
            📈 History
          </Text>

          {history
            .slice()
            .sort(
              (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime(),
            )
            .map((item) => (
              <View
                key={item.workoutId}
                style={{
                  marginBottom: 15,
                }}
              >
                <Text
                  style={{
                    color: "white",
                    fontSize: 16,
                    fontWeight: "bold",
                    marginBottom: 4,
                  }}
                >
                  {new Date(item.date).toLocaleDateString("en-US", {
                    day: "numeric",
                    month: "short",
                    year: "numeric",
                  })}
                </Text>

                {item.bestSet ? (
                  <Text
                    style={{
                      color: "#888",
                      fontSize: 14,
                    }}
                  >
                    Best: {item.bestSet.weight} kg × {item.bestSet.reps}
                  </Text>
                ) : (
                  <Text
                    style={{
                      color: "#888",
                      fontSize: 14,
                    }}
                  >
                    No sets
                  </Text>
                )}
              </View>
            ))}
        </View>
      )}
    </ScrollView>
  );
}
