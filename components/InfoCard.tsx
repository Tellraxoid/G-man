import {View, Text, StyleSheet} from 'react-native';

type InfoCardProps = {
  title: string;
  value: string;
};

export default function InfoCard({
    title, value}: InfoCardProps) {
    return (
        <View style={{
            backgroundColor: "#222",
            padding: 20,
            borderRadius: 15,
            marginBottom: 15,
        }}>
            <Text style={{
                color: "white",
                fontSize: 18,
                fontWeight: "bold",
                marginBottom: 5,
            }}>
                {title}
            </Text>

            <Text style={{
                color: "white",
                fontSize: 16,
            }}>
                {value}
            </Text>
        </View>
    );
}

const styles = StyleSheet.create({
    title: {
        fontSize: 18,
        fontWeight: 'bold',
        marginBottom: 5,
    },
    value: {
        fontSize: 16,
        color: '#666',
    },
});