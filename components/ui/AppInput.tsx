import { StyleProp, TextInput, TextInputProps, TextStyle } from "react-native";

import { Colors } from "../../constants/theme";

type AppInputProps = TextInputProps & {
  style?: StyleProp<TextStyle>;
};

export default function AppInput({
  style,
  placeholderTextColor = Colors.textSecondary,
  ...props
}: AppInputProps) {
  return (
    <TextInput
      {...props}
      placeholderTextColor={placeholderTextColor}
      style={[
        {
          backgroundColor: Colors.card,
          color: Colors.text,
          paddingHorizontal: 15,
          paddingVertical: 14,
          borderRadius: 15,
          fontSize: 16,
        },
        style,
      ]}
    />
  );
}
