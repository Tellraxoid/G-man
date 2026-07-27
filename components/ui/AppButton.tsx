import {
    StyleProp,
    Text,
    TextStyle,
    TouchableOpacity,
    ViewStyle
} from "react-native";
import { Colors } from "../../constants/theme";

type AppButtonProps = {
  title: string;
  onPress: () => void;
  style?: StyleProp<ViewStyle>;
  textStyle?: StyleProp<TextStyle>;
};

export default function AppButton({
  title,
  onPress,
  style,
  textStyle,
}: AppButtonProps) {
  return (
    <TouchableOpacity
      onPress={onPress}
      style={[
        {
          backgroundColor: Colors.accent,
          paddingVertical: 14,
          paddingHorizontal: 20,
          borderRadius: 12,
          alignItems: "center",
        },
        style,
      ]}
    >
      <Text
        style={[
          {
            color: Colors.text,
            fontSize: 16,
            fontWeight: "600",
          },
          textStyle,
        ]}
      >
        {title}
      </Text>
    </TouchableOpacity>
  );
}
