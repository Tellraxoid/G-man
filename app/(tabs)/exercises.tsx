import {View, Text, ScrollView, TouchableOpacity} from 'react-native';

import {router} from 'expo-router';
import exercise from './exercise';
import {exercisesDatabase} from '../../data/exerciseDatabase';

export default function ExercisesScreen() {
  return (
    <ScrollView
       style={{
         flex: 1,
          backgroundColor: '#111',
           padding: 16 }}
           contentContainerStyle={{ 
            paddingBottom: 100,
            paddingTop: 80,
            paddingHorizontal: 20,
          }}
          >
            <Text 
            style={{
                color: 'white',
                fontSize: 32,
                fontWeight: 'bold',
                marginBottom: 20,
            }}
            >
            Exercises
            </Text>
            {exercisesDatabase.map((group, index) => (
              <View
                key={group.muscle}
                style={{
                  marginBottom: 20,
                  backgroundColor: '#222',
                  padding: 15,
                  borderRadius: 15,
                }}
              >
                <Text
                  style={{
                    color: 'white',
                    fontSize: 24,
                    fontWeight: 'bold',
                    marginBottom: 10,
                  }}
                >
                  {group.muscle}
                </Text>

                {group.exercises.map((exercise) => (
                  <TouchableOpacity
                    key={exercise.name}
                    onPress={() =>
                      router.push({
                        pathname: "/exercise",
                        params: { name: exercise.name },
                      })
                    }
                  >
                    <Text
                      style={{
                        color: 'lightgray',
                        fontSize: 18,
                        marginBottom: 5,
                      }}
                    >
                      {exercise.name}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            ))}
          </ScrollView>
        );
      }