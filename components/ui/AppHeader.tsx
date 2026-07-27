import { Text, View } from "react-native";
import { Colors } from "../../constants/theme";

type AppHeaderProps = {
  title: string;
  subtitle?: string;
};

export default function AppHeader({ title, subtitle }: AppHeaderProps) {
  return (
    <View style={{ marginBottom: 24 }}>
      <Text
        style={{
          color: Colors.text,
          fontSize: 32,
          fontWeight: "bold",
        }}
      >
        {title}
      </Text>

      {subtitle && (
        <Text
          style={{
            color: Colors.textSecondary,
            fontSize: 14,
            marginTop: 6,
          }}
        >
          {subtitle}
        </Text>
      )}
    </View>
  );
}
