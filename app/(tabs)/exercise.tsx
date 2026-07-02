import { View, Text, ScrollView } from "react-native";
import { useLocalSearchParams } from "expo-router";
import { exercisesDatabase } from "../../data/exerciseDatabase";
import {
  getPersonalRecord,
  getTimesPerformed,
  getTotalVolume,
  getLastWorkout,
  getBestSet,
} from "../../utils/exerciseStats";
import { loadWorkouts } from "../../storage/workoutStorage";
import React from "react";
import InfoCard from "../../components/InfoCard";

export default function ExerciseScreen() {
  const { name } = useLocalSearchParams();

  const exerciseName = Array.isArray(name) ? name[0] : name;

  const exercise = exercisesDatabase
    .flatMap((group) => group.exercises)
    .find((ex) => ex.name === exerciseName);

  const [personalRecord, setPersonalRecord] =
    React.useState<number | null>(null);

  const [timesPerformed, setTimesPerformed] =
    React.useState<number | null>(null);

  const [totalVolume, setTotalVolume] =
    React.useState<number | null>(null);

  const [lastWorkout, setLastWorkout] =
    React.useState<any | null>(null);

  const [bestSet, setBestSet] =
    React.useState<any | null>(null);

  React.useEffect(() => {
    async function loadStats() {
      const workouts = await loadWorkouts();

      const record = getPersonalRecord(
        workouts || [],
        exerciseName
      );

      const times = getTimesPerformed(
        workouts || [],
        exerciseName
      );

      const volume = getTotalVolume(
        workouts || [],
        exerciseName
      );

      const last = getLastWorkout(
        workouts || [],
        exerciseName
      );

      const best = getBestSet(
        workouts || [],
        exerciseName
      );

      setPersonalRecord(record);
      setTimesPerformed(times);
      setTotalVolume(volume);
      setLastWorkout(last);
      setBestSet(best);
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
            marginBottom: 25,
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
   
             <InfoCard title="💪 Equipment"
        value={exercise.equipment} />
        
      
        <InfoCard title="📊 Difficulty"
          value={exercise.difficulty} />
          
        <InfoCard title="📝 Description"
        value={exercise.description} />
           

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
          🏅 Personal Record: {personalRecord ?? "--"} kg
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

          {lastWorkout.exercise.sets.map(
            (set: any, index: number) => (
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
            )
          )}
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
              marginBottom: 8,
            }}
          >
            {bestSet.weight} kg × {bestSet.reps}
          </Text>
        </View>
      )}
    </ScrollView>
  );
}