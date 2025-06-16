package ru.practicum.user.model.UserDto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    @NotBlank(message = "Пользователь должен иметь email")
    @Email(message = "email не соответствует формату")
    private String email;
    @NotBlank(message = "Пользователь должен иметь не пустое имя")
    private String name;
}
