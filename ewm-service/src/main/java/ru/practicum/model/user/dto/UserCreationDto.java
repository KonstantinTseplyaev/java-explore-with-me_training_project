package ru.practicum.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDto {
    @Length(min = 6, max = 254)
    @Email
    @NotBlank
    private String email;
    @Length(min = 2, max = 250)
    @NotBlank
    private String name;
    @Length(min = 10, max = 50)
    @NotBlank
    private String password;
    private String confirmPassword;
}
