import {
  Text,
  View,
  TouchableOpacity,
  TextInput,
} from "react-native";
import { calculateVolume } from "../utils/calculateVolume";
import { getPR } from "../utils/prUtils";
export default function ExerciseCard({
  exercise,
  onAddSet,
  onUpdateSet,
  onDeleteSet,
  onDeleteExercise,
}) {
  const volume = calculateVolume(exercise.sets);
      const pr = getPR(exercise.sets);
  return (
    
    <View
      style={{
        padding: 20,
        backgroundColor: "#222",
        borderRadius: 15,
        marginBottom: 15,
      }}
    >
      <Text
        style={{
          color: "white",
          fontSize: 18,
          fontWeight: "bold",
          marginBottom: 10,
        }}
      >
        {exercise.name}
      </Text>
<Text style={{
        color: "#888",
        fontSize: 14,
        marginBottom: 10,
      }}>
        Volume: {volume} kg
      </Text>
      <Text style={{
        color: "#888",
        fontSize: 14,
        marginBottom: 10,
      }}>
        PR: {pr} kg
      </Text>
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
              onUpdateSet(
                exercise.id,
                index,
                "weight",
                Number(text) || 0
              )
            }
            placeholder="Weight"
            placeholderTextColor="#888"
            keyboardType="numeric"
            style={{
              backgroundColor: "#333",
              color: "white",
              padding: 10,
              borderRadius: 10,
              flex: 1,
              textAlign: "center",
            }}
          />

          <TextInput
            value={set.reps.toString()}
            onChangeText={(text) =>
              onUpdateSet(
                exercise.id,
                index,
                "reps",
                Number(text) || 0
              )
            }
            placeholder="Reps"
            placeholderTextColor="#888"
            keyboardType="numeric"
            style={{
              backgroundColor: "#333",
              color: "white",
              padding: 10,
              borderRadius: 10,
              flex: 1,
              textAlign: "center",
            }}
          />

          <TouchableOpacity
            onPress={() =>
              onDeleteSet(
                exercise.id,
                index
              )
            }
            style={{
              backgroundColor: "red",
              paddingHorizontal: 12,
              borderRadius: 10,
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                color: "white",
                fontWeight: "bold",
              }}
            >
              X
            </Text>
          </TouchableOpacity>
        </View>
      ))}

      <TouchableOpacity
        onPress={() => onDeleteExercise(exercise.id)}
        style={{
          backgroundColor: "red",
          paddingHorizontal: 12,
          borderRadius: 10,
          marginTop: 15,
          alignItems: "center",
        }}
      >
        <Text
          style={{
            color: "white",
            fontWeight: "bold",
          }}
        >
          Delete Exercise
        </Text>
      </TouchableOpacity>

      <TouchableOpacity
        onPress={() => onAddSet(exercise.id)}
        style={{
          backgroundColor: "#444",
          padding: 12,
          borderRadius: 12,
          marginTop: 15,
          alignItems: "center",
        }}
      >
        <Text
          style={{
            color: "white",
            fontWeight: "bold",
          }}
        >
          + Add Set
        </Text>
      </TouchableOpacity>
    </View>
  );
}