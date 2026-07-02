import {View, Text} from 'react-native';

export default function CalendarScreen() {
    const days = Array.from(
        {length: 31},
         (_, index) => index+1);
  return (
    <View
      style={{
        flex: 1,
        backgroundColor: '#206126',
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
        July 2026
        </Text>
        <View
        style={{
            flexDirection: 'row',
            flexWrap: 'wrap',   
            gap: 10,
        }}
        >
        {days.map((day) => (
            <View
                key={day}
                style={{
                    width: 40,
                    height: 40,
                    backgroundColor: '#222',
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
        ))}
        </View>  
      
    </View>
  );
}