import { ReactNode } from "react";
import { StyleProp, View, ViewStyle } from "react-native";
import { Colors } from "../../constants/theme";

type AppCardProps = {
  children: ReactNode;
  style?: StyleProp<ViewStyle>;
};

export default function AppCard({ children, style }: AppCardProps) {
  return (
    <View
      style={[
        {
          backgroundColor: Colors.card,
          padding: 20,
          borderRadius: 15,
        },
        style,
      ]}
    >
      {children}
    </View>
  );
}
