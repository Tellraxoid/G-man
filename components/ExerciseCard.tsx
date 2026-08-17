import { Colors } from "@/constants/theme";
import { Text, TextInput, TouchableOpacity, View } from "react-native";

import { calculateVolume } from "../utils/calculateVolume";

import EditableTitle from "./ui/EditableTitle";

type HistoricalPR = {
  weight: number;
  reps: number;
} | null;

type PreviousWorkout = {
  date: string;
  sets: ExerciseSet[];
} | null;

type ExerciseSet = {
  weight: number;
  reps: number;
};

type Exercise = {
  id: string;
  name: string;
  sets: ExerciseSet[];
};

type ExerciseCardProps = {
  exercise: Exercise;

  onAddSet: (exerciseId: string) => void;

  onUpdateSet: (
    exerciseId: string,
    setIndex: number,
    field: "weight" | "reps",
    value: number,
  ) => void;

  onDeleteSet: (exerciseId: string, setIndex: number) => void;

  onDeleteExercise: (exerciseId: string) => void;

  onUpdateExerciseName: (exerciseId: string, newName: string) => void;

  historicalPR?: HistoricalPR;
  previousPR?: HistoricalPR;
  previousWorkout?: PreviousWorkout;
};

export default function ExerciseCard({
  exercise,
  onAddSet,
  onUpdateSet,
  onDeleteSet,
  onDeleteExercise,
  onUpdateExerciseName,
  historicalPR,
  previousPR,
  previousWorkout,
}: ExerciseCardProps) {
  const volume = calculateVolume(exercise.sets);
  const currentBestWeight =
    exercise.sets.length > 0
      ? Math.max(...exercise.sets.map((set) => set.weight))
      : 0;

  const isNewPR =
    currentBestWeight > 0 &&
    (!previousPR || currentBestWeight > previousPR.weight);
  return (
    <View
      style={{
        padding: 20,
        backgroundColor: Colors.card,
        borderRadius: 15,
        marginBottom: 15,
      }}
    >
      <EditableTitle
        title={exercise.name}
        onSave={(newName) => onUpdateExerciseName(exercise.id, newName)}
      />

      <Text
        style={{
          color: Colors.textSecondary,
          fontSize: 14,
          marginBottom: 10,
        }}
      >
        Volume: {volume} kg
      </Text>
      {isNewPR && (
        <Text
          style={{
            color: Colors.accent,
            fontSize: 16,
            fontWeight: "bold",
            marginBottom: 10,
          }}
        >
          🏆 NEW PR!
        </Text>
      )}
      {previousWorkout && (
        <View
          style={{
            backgroundColor: Colors.background,
            padding: 12,
            borderRadius: 10,
            marginBottom: 10,
          }}
        >
          <Text
            style={{
              color: Colors.textSecondary,
              fontSize: 13,
              marginBottom: 6,
            }}
          >
            Previous Workout
          </Text>

          <Text
            style={{
              color: Colors.text,
              fontSize: 14,
              marginBottom: 8,
            }}
          >
            {new Date(previousWorkout.date).toLocaleDateString("en-US", {
              month: "short",
              day: "numeric",
            })}
          </Text>

          {previousWorkout.sets.map((set, index) => (
            <Text
              key={index}
              style={{
                color: Colors.textSecondary,
                fontSize: 14,
                marginBottom: 3,
              }}
            >
              Set {index + 1}: {set.weight} kg × {set.reps}
            </Text>
          ))}
        </View>
      )}
      {historicalPR ? (
        <Text
          style={{
            color: Colors.accent,
            fontSize: 14,
            fontWeight: "bold",
            marginBottom: 10,
          }}
        >
          🏆 Best Ever: {historicalPR.weight} kg × {historicalPR.reps}
        </Text>
      ) : (
        <Text
          style={{
            color: Colors.textSecondary,
            fontSize: 14,
            marginBottom: 10,
          }}
        >
          No PR yet
        </Text>
      )}

      {exercise.sets.map((set, index) => (
        <View
          key={index}
          style={{
            flexDirection: "row",
            gap: 10,
            marginBottom: 10,
          }}
        >
          <TextInput
            value={set.weight.toString()}
            onChangeText={(text) =>
              onUpdateSet(exercise.id, index, "weight", Number(text) || 0)
            }
            placeholder="Weight"
            placeholderTextColor={Colors.textSecondary}
            keyboardType="numeric"
            style={{
              backgroundColor: Colors.background,
              color: Colors.text,
              padding: 10,
              borderRadius: 10,
              flex: 1,
              textAlign: "center",
            }}
          />

          <TextInput
            value={set.reps.toString()}
            onChangeText={(text) =>
              onUpdateSet(exercise.id, index, "reps", Number(text) || 0)
            }
            placeholder="Reps"
            placeholderTextColor={Colors.textSecondary}
            keyboardType="numeric"
            style={{
              backgroundColor: Colors.background,
              color: Colors.text,
              padding: 10,
              borderRadius: 10,
              flex: 1,
              textAlign: "center",
            }}
          />

          <TouchableOpacity
            onPress={() => onDeleteSet(exercise.id, index)}
            style={{
              backgroundColor: Colors.deleteButton,
              paddingHorizontal: 12,
              borderRadius: 10,
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                color: Colors.text,
                fontWeight: "bold",
              }}
            >
              X
            </Text>
          </TouchableOpacity>
        </View>
      ))}

      <TouchableOpacity
        onPress={() => onAddSet(exercise.id)}
        style={{
          backgroundColor: Colors.addButton,
          padding: 12,
          borderRadius: 12,
          marginTop: 15,
          alignItems: "center",
        }}
      >
        <Text
          style={{
            color: Colors.text,
            fontWeight: "bold",
          }}
        >
          + Add Set
        </Text>
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => onDeleteExercise(exercise.id)}
        style={{
          backgroundColor: Colors.deleteButton,
          padding: 12,
          borderRadius: 10,
          marginTop: 15,
          alignItems: "center",
        }}
      >
        <Text
          style={{
            color: Colors.text,
            fontWeight: "bold",
          }}
        >
          Delete Exercise
        </Text>
      </TouchableOpacity>
    </View>
  );
}
