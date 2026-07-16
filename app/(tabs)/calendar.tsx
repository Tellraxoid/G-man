import {View, Text} from 'react-native';
import {useState, useEffect} from 'react';
import {loadWorkouts} from '../../storage/workoutStorage';
import {Workout} from '../../types/workout';

export default function CalendarScreen() {
    const [workouts, setWorkouts] = 
    useState<Workout[]>([]);

      const [selectedWorkout, setSelectedWorkout] = 
    useState<Workout | null>(null);
    
    const days = Array.from(
        {length: 31},
         (_, index) => index+1);


    useEffect(() => {
        async function fetchWorkouts() {
            const data = await loadWorkouts();
            setWorkouts(data || []);
        }
        fetchWorkouts();
    }, []);

    const workoutDates = workouts.map((workout) =>
         workout.date
    .split('T')[0]);

    return (
      <View style={{ flex: 1, padding: 20 }}>
        <Text
          style={{
            color: 'green',
            fontSize: 32,
            fontWeight: 'bold',
            marginBottom: 20,
            marginTop: 20,
          }}
        >
          July 2026
        </Text>
        <View
          style={{
            flexDirection: 'row',
            flexWrap: 'wrap',
            gap: 10,
          }}
        >
          {days.map((day) => {
            const dateString = `2026-07-${day.toString().padStart(2, '0')}`;
            const hasWorkout = workoutDates.includes(dateString);
            return (
              <View
                key={day}
                style={{
                  width: 40,
                  height: 40,
                  backgroundColor: hasWorkout ? '#4CAF50' : '#222',
                  justifyContent: 'center',
                  alignItems: 'center',
                  borderRadius: 5,
                }}
              >
                <Text
                  style={{
                    color: 'white',
                    fontSize: 16,
                  }}
                >
                  {day}
                </Text>
              </View>
            );
          })}
        </View>
      </View>
    );
}
