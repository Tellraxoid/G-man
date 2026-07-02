import {
    View,
    Text,
    TouchableOpacity,
  } from "react-native";    
  import {router} from "expo-router";

  export default function WorkoutCard({
     workout,
     onDelete, 
  }) {
    return (
        <View   
        style={{
            padding: 20,
            backgroundColor: "#222",    
            borderRadius: 15,
            marginBottom: 15,
            flexDirection: "row",
            justifyContent: "space-between",
            alignItems: "center",
        }}
        >
           <TouchableOpacity
  onPress={() =>
    router.push({
      pathname: "/workout",
      params: { id: workout.id },
    })
  }
  style={{ flex: 1 }}
>
  <View>
    <Text
      style={{
        color: "white",
        fontSize: 18,
        fontWeight: "bold",
      }}
    >
      {workout.name}
    </Text>

    <Text
      style={{
        color: "#888",
        fontSize: 14,
        marginTop: 5,
      }}
    >
      {new Date(workout.date).toLocaleDateString("en-US", {
        month: "long",
        day: "numeric",
        year: "numeric",
      })}
    </Text>
    <Text
      style={{
        color: "#888",
        fontSize: 14,
        marginTop: 5,
      }}
    >
      Total Exercises: {workout.exercises.length}
    </Text>
    </View>
</TouchableOpacity>

          <TouchableOpacity
            onPress={() => onDelete(workout.id)}
            style={{
              backgroundColor: "red",
              paddingHorizontal: 12,
              borderRadius: 10,
              paddingVertical: 8,
              marginLeft: 15,
            }}
          >
           
            <Text
              style={{
                color: "white",
                fontWeight: "bold",
              }}
            >
              Delete Workout
            </Text>
          </TouchableOpacity>
        </View>
      );
    }