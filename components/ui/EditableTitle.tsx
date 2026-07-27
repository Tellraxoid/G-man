import { useEffect, useState } from "react";
import { Text, TouchableOpacity } from "react-native";

import { Colors } from "../../constants/theme";
import AppInput from "./AppInput";

type EditableTitleProps = {
  title: string;
  onSave: (newTitle: string) => void;
};

export default function EditableTitle({ title, onSave }: EditableTitleProps) {
  const [isEditing, setIsEditing] = useState(false);

  const [value, setValue] = useState(title);

  useEffect(() => {
    setValue(title);
  }, [title]);

  function startEditing() {
    setValue(title);
    setIsEditing(true);
  }

  function finishEditing() {
    const trimmedValue = value.trim();

    if (trimmedValue !== "" && trimmedValue !== title) {
      onSave(trimmedValue);
    }

    setIsEditing(false);
  }

  function cancelEditing() {
    setValue(title);
    setIsEditing(false);
  }

  if (isEditing) {
    return (
      <AppInput
        value={value}
        onChangeText={setValue}
        autoFocus
        returnKeyType="done"
        onSubmitEditing={finishEditing}
        onBlur={cancelEditing}
        style={{
          fontSize: 30,
          fontWeight: "bold",
          marginBottom: 20,
        }}
      />
    );
  }

  return (
    <TouchableOpacity
      onPress={startEditing}
      style={{
        marginBottom: 20,
      }}
    >
      <Text
        style={{
          color: Colors.text,
          fontSize: 32,
          fontWeight: "bold",
        }}
      >
        {title}
      </Text>

      <Text
        style={{
          color: Colors.textSecondary,
          marginTop: 5,
          fontSize: 13,
        }}
      >
        Tap to edit
      </Text>
    </TouchableOpacity>
  );
}
