import {View, Text} from 'react-native';

export default function SettingsScreen() {
    return (
        <View
            style={{
                flex: 1,
                backgroundColor: "#111",
                justifyContent: "center",
                alignItems: "center",
            }}
        >
            <Text
                style={{
                    color: "white",
                    fontSize: 24,
                    fontWeight: "bold",
                }}
            >
                Settings
            </Text>
        </View>
    );
}